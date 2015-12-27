package com.hli.controller;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.StringRefAddr;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.exceptions.PersistenceException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.hli.domain.CouponReqVO;
import com.hli.domain.GoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;
import com.hli.result.Result;
import com.hli.result.ResultData;
import com.hli.result.ResultDataTotal;
import com.hli.service.AdminService;

@RestController
public class CouponController {
	private static Logger logger = LoggerFactory.getLogger(CouponController.class);
	
	@Autowired
	private AdminService adminService;
	
	private RestTemplate restTemplate;
	private MultiValueMap<String, String> params;
	
	//판매업체에게 상품을 공급해주는 REST API
	@RequestMapping("/api/getAllGoodsOfSeller")
    public ResultData<List<SellerGoodsVO>> getAllGoodsOfSeller(@RequestBody SellerVO inSeller, HttpServletRequest request) {
		logger.debug("/api/getAllGoodsOfSeller--------------------------------------------------");
		
		//판매업체 mid와 password가 일치하는지 확인
		SellerVO seller = adminService.getSeller(inSeller);
		if(seller == null) {
			return new ResultData(100, "입력정보가 일치하지 않습니다");
		}
		
		//등록된 IP인지 확인
		String allowed_ip = seller.getAllowed_ip();
		if(!allowed_ip.contains(request.getRemoteAddr())) {
			logger.debug("ip:" + request.getRemoteAddr());
			//return new ResultData(200, "허용된 IP가 아닙니다.");
		}
		
		//상품리스트 가져오기
		SearchVO search = new SearchVO();
		search.setSeller_id(seller.getSeller_id());
		List<SellerGoodsVO> goodsList = adminService.getAllGoodsOfSeller(search);
		
		return new ResultData<List<SellerGoodsVO>>(0, "success", goodsList);
	}
	
	//쿠폰 발송 OPEN API=====================================================================
	@RequestMapping("/coupon/api/v1/sendCoupons")
	public Result sendCoupons(@RequestBody CouponReqVO couponReq) {
		logger.debug("/api/v1/sendCoupons------------------------------------------------------------");
		
		restTemplate = new RestTemplate();
		Result result = new Result();
		//coup handling
		if (couponReq.getProvider() == 2) {
			params = new LinkedMultiValueMap<String, String>();
			params.add("CODE", "0424");
			params.add("PASS", "hlint123");
			params.add("COUPONCODE", couponReq.getGoods_code());
			params.add("SEQNUMBER", couponReq.getTr_id());
			params.add("QTY", couponReq.getGoods_count());
			params.add("HP", couponReq.getRecv_phone());
			params.add("CALLBACK", couponReq.getSend_phone());
			params.add("TITLE", "");
			params.add("ADDMSG", couponReq.getMessage());
			params.add("SELPRICE", couponReq.getSell_price());
			
		}
		//logger.debug(params.toString());
		String baseUrl = "http://issuev3apitest.m2i.kr:9999/serviceapi_02.asmx/ServiceCreateSendMuch";
		try {
			UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
			logger.debug(uriComponents.toUriString());
			String strResult = restTemplate.getForObject(
					uriComponents.toUriString(),
					String.class);
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(new StringReader(strResult));
			Element rootNode = document.getRootElement();
			logger.debug(rootNode.getName());
			String resultCode = rootNode.getChild("RESULTCODE", rootNode.getNamespace()).getText();
			String resultMsg = rootNode.getChild("RESULTMSG", rootNode.getNamespace()).getText();
			logger.debug(resultCode);
			logger.debug(resultMsg);
			result.setResult(Integer.parseInt(resultCode));
			result.setMsg(resultMsg);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new Result(100, "Coupon failed");
		}
		
		return result;
	}
}
