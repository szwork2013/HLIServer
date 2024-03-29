package com.hli.controller;

import java.io.StringReader;
import java.util.List;

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
import com.hli.domain.GoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;
import com.hli.domain.SendVO;
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
	
	//판매업체에게 상품을 공급해주는 REST API - 테스트상품
	@RequestMapping("/api/dev/getAllGoodsOfSeller")
    public ResultData<List<SellerGoodsVO>> getAllTestGoodsOfSeller(@RequestBody SellerVO inSeller, HttpServletRequest request) {
		logger.debug("/api/dev/getAllGoodsOfSeller-----------------------------------------------");
		return getAllGoodsOfSeller(inSeller, request, false);
	}
	
	//판매업체에게 상품을 공급해주는 REST API - 실상품
	@RequestMapping("/api/getAllGoodsOfSeller")
    public ResultData<List<SellerGoodsVO>> getAllGoodsOfSeller(@RequestBody SellerVO inSeller, HttpServletRequest request) {
		logger.debug("/api/getAllGoodsOfSeller---------------------------------------------------");
		return getAllGoodsOfSeller(inSeller, request, true);
	}
	
    private ResultData<List<SellerGoodsVO>> getAllGoodsOfSeller(SellerVO inSeller, HttpServletRequest request, boolean isReal) {
		//판매업체 mid와 password가 일치하는지 확인
		SellerVO seller = adminService.getSeller(inSeller);
		if(seller == null) {
			return new ResultData(100, "입력정보가 일치하지 않습니다");
		}
		
		//등록된 IP인지 확인
		String allowed_ip = seller.getAllowed_ip();
		if(!allowed_ip.contains(request.getRemoteAddr())) {
			logger.debug("ip:" + request.getRemoteAddr());
			return new ResultData(200, "허용된 IP가 아닙니다.");
		}
		
		//상품리스트 가져오기
		SearchVO search = new SearchVO();
		search.setSeller_id(seller.getSeller_id());
		search.setReal(isReal);  //실상품과 테스트 상품구분
		List<SellerGoodsVO> goodsList = adminService.getAllGoodsOfSeller(search);
		
		return new ResultData<List<SellerGoodsVO>>(0, "success", goodsList);
	}
	
	
	//쿠폰 발송 OPEN API=====================================================================
	// 테스트 발송
	@RequestMapping("/dev/api/sendCoupon")
	public Result testSendCoupon(@RequestBody CouponReqVO couponReq, HttpServletRequest request) {
		logger.debug("/dev/api/sendCoupon---------------------------------------------------");
		return send(couponReq, request, false);
	}
	// 실 발송
	@RequestMapping("/api/sendCoupon")
	public Result SendCoupon(@RequestBody CouponReqVO couponReq, HttpServletRequest request) {
		logger.debug("/api/sendCoupon---------------------------------------------------");
		return send(couponReq, request, true);
	}
	
	//쿠폰 재발송 OPEN API=====================================================================
	// 테스트 재발송
	@RequestMapping("/dev/api/resendCoupon")
	public Result testReSendCoupon(@RequestBody SendVO sendReq, HttpServletRequest request) {
		logger.debug("/dev/api/resendCoupon---------------------------------------------------");
		return resend(sendReq, request, false);
	}
	// 실 재발송
	@RequestMapping("/api/resendCoupon")
	public Result ReSendCoupon(@RequestBody SendVO sendReq, HttpServletRequest request) {
		logger.debug("/api/resendCoupon---------------------------------------------------");
		return resend(sendReq, request, true);
	}
	
	
	public Result send(CouponReqVO couponReq, HttpServletRequest request, boolean isReal) {
		SendVO sendVO = new SendVO();
		
		if(couponReq.getMid() == null || couponReq.getPassword() == null) {
			return new Result(100, "필수파라메터 부족");
		}
		
		//판매업체 mid와 password가 일치하는지 확인
		SellerVO inSeller = new SellerVO();
		inSeller.setMid(couponReq.getMid());
		inSeller.setPassword(couponReq.getPassword());
		SellerVO seller = adminService.getSeller(inSeller);
		if(seller == null) {
			return new Result(200, "등록된 판매업체가 아닙니다.");
		}
		sendVO.setSeller_id(seller.getSeller_id());
		
		//등록된 IP인지 확인
		String allowed_ip = seller.getAllowed_ip();
		if(!allowed_ip.contains(request.getRemoteAddr())) {
			logger.debug("ip:" + request.getRemoteAddr());
			return new Result(300, "허용된 IP가 아닙니다.");
		}
		
		//상품 정보 확인
		GoodsVO inGoods = new GoodsVO();
		inGoods.setGoods_code(couponReq.getGoods_code());
		inGoods.setReal(isReal); //실상품 or 테스트 상품 구분
		GoodsVO goods = adminService.getGoods(inGoods);
		if(goods == null) {
			return new Result(400, "등록된 상품코드가 아닙니다.");
		}
		sendVO.setGoods_id(goods.getGoods_id());
		//판매가격은 상품정보의 sell_price로 세팅. 판매업체에게 제공받지 않는다.
		sendVO.setSell_price(goods.getSell_price());
		
		//발송정보 세팅
		sendVO.setGoods_count("1"); //상품 수량은 1로 고정
		sendVO.setRecv_phone(couponReq.getRecv_phone());
		sendVO.setSend_phone(couponReq.getSend_phone());
		sendVO.setTr_id(couponReq.getTr_id());
		sendVO.setMsg(couponReq.getMessage());
		

		restTemplate = new RestTemplate();
		params = new LinkedMultiValueMap<String, String>();
		UriComponents uriComponents;
		String baseUrl = "";
		//M12 handling
		if (goods.getProvider() == 1) {
			params.add("goods_code", couponReq.getGoods_code());
			params.add("goods_count", sendVO.getGoods_count()); //상품 수량 1로 고정
			params.add("send_phone", couponReq.getSend_phone());
			params.add("recv_phone", couponReq.getRecv_phone());
			params.add("tr_id", couponReq.getTr_id());
			params.add("userid", "hlint");
			params.add("sell_price", sendVO.getSell_price()); //상품 판매가격으로 고정
			params.add("msg", couponReq.getMessage());

			if(isReal) {
				baseUrl = "http://web6.m12.co.kr:12101/app/order_send.php";
			} else {
				baseUrl = "http://web6.m12.co.kr:12101/app/dev/order_send.php";
			}
			
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
				String statusCode = rootNode.getChild("STATUS_CODE", rootNode.getNamespace()).getText();
				
				//쿠폰 발송 상태 저장
				sendVO.setResult_code(resultCode);
				sendVO.setStatus_code(statusCode);
				
				sendVO.setReal(isReal);
				logger.debug(sendVO.toString());
				adminService.addSend(sendVO);
				
				return new Result(Integer.parseInt(resultCode), statusCode);
			}

			catch(Exception e) {
				e.printStackTrace();
				return new Result(500, "내부 오류가 발생하였습니다.");
			}
		}
		//Coup handling, 문서 2.18 구현 
		else if (goods.getProvider() == 2) {
			params.add("CODE", "0424");
			params.add("PASS", "hlint123");
			params.add("COUPONCODE", couponReq.getGoods_code());
			params.add("SEQNUMBER", couponReq.getTr_id());
			params.add("QTY", sendVO.getGoods_count());   //상품수량은 1개
			params.add("HP", couponReq.getRecv_phone());
			params.add("CALLBACK", couponReq.getSend_phone());
			params.add("TITLE", "");
			params.add("ADDMSG", couponReq.getMessage());
			params.add("SELPRICE", sendVO.getSell_price()); //상품 가격 세팅


			//logger.debug(params.toString());
			if(isReal) {
				baseUrl = "http://v3api.inumber.co.kr/serviceapi_02.asmx/ServiceCreateSendMuch";
			} else {
				baseUrl = "http://issuev3apitest.m2i.kr:9999/serviceapi_02.asmx/ServiceCreateSendMuch";
			}
			
			try {
				uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
				logger.debug(uriComponents.toUriString());
				String strResult = restTemplate.getForObject(
						uriComponents.toUriString(),
						String.class);
				logger.debug("strResult:" + strResult);
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(new StringReader(strResult));
				Element rootNode = document.getRootElement();
				
				String resultCode = rootNode.getChild("RESULTCODE", rootNode.getNamespace()).getText();
				String resultMsg = rootNode.getChild("RESULTMSG", rootNode.getNamespace()).getText();
				
				//쿠폰 발송 상태 저장
				sendVO.setResult_code(resultCode);
				sendVO.setStatus_code(resultMsg);
				
				//쿠폰번호와 핀번호 저장
				if ("00".equals(resultCode)) {
					Element List = rootNode.getChild("LIST", rootNode.getNamespace());
					List couponList = List.getChildren("GCOUPONLIST", rootNode.getNamespace());
					//한건만 보내므로 루프를 돌리지않고 한건만 저장
					Element node = (Element) couponList.get(0);
					String couponNumber = node.getChildText("COUPONNUMBER", rootNode.getNamespace());
					String pinNumber = node.getChildText("PINNUMBER", rootNode.getNamespace());
					sendVO.setCouponnumber(couponNumber);
					sendVO.setPinnumber(pinNumber);
				}
				sendVO.setReal(isReal);
				
				logger.debug(sendVO.toString());
				adminService.addSend(sendVO);
				
				return new Result(Integer.parseInt(resultCode), resultMsg);
			} 
			catch (Exception e) {
				e.printStackTrace();
				return new Result(100, "Coupon failed");
			}

		}

		return new Result(500, "내부 오류가 발생하였습니다.");
	}
	
	//재발송 로직, 
	public Result resend(SendVO sendReq, HttpServletRequest request, boolean isReal) {
		//상품정보와 거래번호를 받아서 나머지 정보를 세팅한다.
		SendVO sendVO = new SendVO();
		
		//상품 정보 확인
		GoodsVO inGoods = new GoodsVO();
		inGoods.setGoods_id(sendReq.getGoods_id());
		inGoods.setReal(isReal); //실상품 or 테스트 상품 구분
		GoodsVO goods = adminService.getGoods(inGoods);

		sendVO.setGoods_id(goods.getGoods_id());
		sendVO.setSell_price(goods.getSell_price());
		
		//발송정보 세팅
		sendVO.setGoods_count("1"); //상품 수량은 1로 고정
		sendVO.setRecv_phone(sendReq.getRecv_phone());
		sendVO.setSend_phone(sendReq.getSend_phone());
		sendVO.setTr_id(sendReq.getTr_id());
		sendVO.setMsg(sendReq.getMsg());

		restTemplate = new RestTemplate();
		params = new LinkedMultiValueMap<String, String>();
		UriComponents uriComponents;
		String baseUrl = "";
		
		//M12 handling, 아직 재발행 로직 없음
		if (goods.getProvider() == 1) {
			
		}
		//Coup handling, 쿠프 문서 2.33 거래번호로 재발송
		else if (goods.getProvider() == 2) {
			params.add("CODE", "0424");
			params.add("PASS", "hlint123");
			params.add("COUPONCODE", goods.getGoods_code());
			params.add("SEQNUMBER", sendReq.getTr_id());
			//params.add("QTY", sendVO.getGoods_count());   //상품수량은 1개
			params.add("HP", sendReq.getRecv_phone());
			params.add("CALLBACK", sendReq.getSend_phone());
			params.add("TITLE", "");
			params.add("ADDMSG", sendReq.getMsg());
			//params.add("SELPRICE", goods.getSell_price()); //상품 가격 세팅

			//logger.debug(params.toString());
			if(isReal) {
				baseUrl = "http://v3api.inumber.co.kr/ServiceApi.aspx/ServiceCouponSendSeq";
			} else {
				baseUrl = "http://issuev3apitest.m2i.kr:9999/ServiceApi.aspx/ServiceCouponSendSeq";
			}
			
			try {
				uriComponents = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build();
				logger.debug(uriComponents.toUriString());
				String strResult = restTemplate.getForObject(
						uriComponents.toUriString(),
						String.class);
				logger.debug("strResult:" + strResult);
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(new StringReader(strResult));
				Element rootNode = document.getRootElement();
				
				String resultCode = rootNode.getChild("RESULTCODE", rootNode.getNamespace()).getText();
				String resultMsg = rootNode.getChild("RESULTMSG", rootNode.getNamespace()).getText();
				
				//쿠폰 발송 상태 저장
				sendVO.setResult_code(resultCode);
				sendVO.setStatus_code(resultMsg);
				
				//재발송여부 저장
				
				sendVO.setReal(isReal);
				
				logger.debug(sendVO.toString());
				//adminService.addSend(sendVO);
				
				return new Result(Integer.parseInt(resultCode), resultMsg);
			} 
			catch (Exception e) {
				e.printStackTrace();
				return new Result(100, "Coupon failed");
			}

		}

		return new Result(500, "내부 오류가 발생하였습니다.");
	}
}
