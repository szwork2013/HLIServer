package com.hli.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private AdminService adminService;
	
	private RestTemplate restTemplate;
	private Map<String, Object> params;
	
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
	public String sendCoupons(@RequestBody CouponReqVO couponReq) {
		logger.debug("/api/v1/sendCoupons------------------------------------------------------------");
		
		restTemplate = new RestTemplate();
		//coup handling
		if (couponReq.getProvider() == 2) {
			params = new HashMap<>();
			params.put("CODE", "0424");
			params.put("PASS", "hlint123");
			params.put("COUPONCODE", couponReq.getGoods_code());
			params.put("SEQNUMBER", couponReq.getTr_id());
			params.put("QTY", couponReq.getGoods_count());
			params.put("HP", couponReq.getRecv_phone());
			params.put("CALLBACK", couponReq.getSend_phone());
			params.put("ADDMSG", couponReq.getMessage());
			params.put("SELPRICE", couponReq.getSell_price());
		}
		
		Object result = restTemplate.getForEntity(
				"http://issuev3apitest.m2i.kr:9999/serviceapi_02.asmx/ServiceCreateSendMuch",
				Object.class,
				params);
		return result.toString();
	}
}
