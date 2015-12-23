package com.hli.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hli.domain.CouponReqVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerVO;
import com.hli.result.ResultDataTotal;

@RestController
public class CouponController {
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	private RestTemplate restTemplate;
	private Map<String, Object> params;
	
/*	@RequestMapping("/api/getGoods")
    public ResultDataTotal<List<SellerVO>> getSellerList(@RequestBody SellerVO seller) {
		logger.debug("/api/getGoods--------------------------------------------------");
		List<SellerVO> sellerList = adminService.getSellerList(search);
		
		int total = adminService.countManager(search);
		
		return new ResultDataTotal<List<SellerVO>>(0, "success", sellerList, total);
	}*/
	
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
