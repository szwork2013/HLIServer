package com.hli.controller;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jdom.Document;
import org.jdom.Element;
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
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;
import com.hli.result.Result;
import com.hli.result.ResultData;
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
		params = new LinkedMultiValueMap<String, String>();
		UriComponents uriComponents;
		String baseUrl = "";
		//M12 handling
		if (couponReq.getProvider() == 1) {
			params.add("goods_code", couponReq.getGoods_code());
			params.add("goods_count", couponReq.getGoods_count());
			params.add("send_phone", couponReq.getSend_phone());
			params.add("recv_phone", couponReq.getRecv_phone());
			params.add("tr_id", couponReq.getTr_id());
			params.add("userid", "banyoungmo");
			params.add("sell_price", couponReq.getSell_price());
			params.add("msg", couponReq.getMessage());

			baseUrl = "http://web6.m12.co.kr:12101/app/dev/order_send.php";
			uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
			String value = uriComponents.getQuery();
			String key = "HLINTNLE54A3I2O1";
			String initVector = "J0S9O8T7USJFDLSX";
			logger.debug(value);

			try {

				IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
				SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

				byte[] encrypted = cipher.doFinal(value.getBytes());
				String strpara = Base64.encodeBase64String(encrypted);
				params.clear();
				params.add("marketcode", "HLINTNL01");
				params.add("strpara", strpara);
				uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
				logger.debug(uriComponents.toUriString());
				String strResult = restTemplate.getForObject(
						uriComponents.toUriString(),
						String.class);
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(new StringReader(strResult));
				Element rootNode = document.getRootElement();
				logger.debug(rootNode.getName());
				String resultCode = rootNode.getChild("RESULT_CODE", rootNode.getNamespace()).getText();
				String resultMsg = rootNode.getChild("STATUS_CODE", rootNode.getNamespace()).getText();
				logger.debug(resultCode);
				logger.debug(resultMsg);
				result.setResult(Integer.parseInt(resultCode));
				result.setMsg(resultMsg);
			}

			catch(Exception e) {
				e.printStackTrace();
				return new Result(100, "Coupon failed");
			}
			return result;
		}
		//Coup handling
		else if (couponReq.getProvider() == 2) {
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


			//logger.debug(params.toString());
			baseUrl = "http://issuev3apitest.m2i.kr:9999/serviceapi_02.asmx/ServiceCreateSendMuch";
			try {
				uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
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

		}
		else {
			result.setResult(100);
			result.setMsg("Unknown provider");
		}
		return result;
	}
}
