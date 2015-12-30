package com.hli.controller;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.MapSellerGoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerVO;
import com.hli.domain.SendVO;
import com.hli.result.Result;
import com.hli.result.ResultData;
import com.hli.result.ResultDataTotal;
import com.hli.scheduler.HttpScheduler;
import com.hli.service.AdminService;

@RestController
public class AdminController {
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private HttpScheduler scheduler;
	
	@RequestMapping("/hello")
	public String Hello() {
		return "Hello test";
	}
	
	//M12 상품 업데이트
	@RequestMapping("/admin/api/getM12GoodsList")
	public Result getM12Goods() {
		scheduler.getProductOfM12();
		return new Result(0, "success");
	}
	
	//M12 상품 업데이트
	@RequestMapping("/admin/api/getCoupGoodsList")
	public Result getCoupGoodsList() {
		scheduler.getProductOfCoupList();
		
		return new Result(0, "success");
	}
	
	//관리자화면: 상품관리
	@RequestMapping("/admin/api/getGoodsList")
    public ResultDataTotal<List<GoodsVO>> getGoodsList(@RequestBody SearchVO search) {
		logger.debug("/api/getGoodsList--------------------------------------------------");
		List<GoodsVO> goodsList = adminService.getGoodsList(search);
		
		int total = adminService.countGoods(search);
		
		return new ResultDataTotal<List<GoodsVO>>(0, "success", goodsList, total);
	}
	
	//관리자화면: 사용자 관리=====================================================================
	@RequestMapping("/admin/api/addManager")
    public Result addManager(@RequestBody ManagerVO manager) {
		logger.debug("/api/addManager------------------------------------------------------------");
		
		try {
			long resultCount = adminService.addManager(manager);
			if(resultCount > 0) {
				return new Result(0, "success");
			} else {
				return new Result(100, "insert failed");
			}
		} catch (PersistenceException e) {
			return new Result(100, "insert failed");
		} 
	}
	
	@RequestMapping("/admin/api/modifyManager")
    public Result modifyManager(@RequestBody ManagerVO manager) {
		logger.debug("/api/modifyManager----------------------------------------------------------");
		
		long resultCount = adminService.modifyManager(manager);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "update failed");
		}
	}
	
	@RequestMapping("/admin/api/removeManager")
    public Result removeManager(@RequestBody ManagerVO manager) {
		logger.debug("/api/removeManager----------------------------------------------------------");
		
		long resultCount = adminService.removeManager(manager);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "delete failed");
		}
	}
	
	@RequestMapping("/admin/api/getManager")
    public ResultData<ManagerVO> getManager(@RequestBody ManagerVO inManager) {
		logger.debug("/api/getManager--------------------------------------------------");
		ManagerVO manager = adminService.getManager(inManager);
		
		if(manager == null) {
			return new ResultData<ManagerVO>(100, "fail", manager);
		} else {
			manager.setPass("");
			return new ResultData<ManagerVO>(0, "success", manager);
		}
	}
	
	@RequestMapping("/admin/api/getManagerList")
    public ResultDataTotal<List<ManagerVO>> getManagerList(@RequestBody SearchVO search) {
		logger.debug("/api/getManagerList--------------------------------------------------");
		List<ManagerVO> managerList = adminService.getManagerList(search);
		
		int total = adminService.countManager(search);
		
		return new ResultDataTotal<List<ManagerVO>>(0, "success", managerList, total);
	}
	
	//판매업체 관리=====================================================================
	@RequestMapping("/admin/api/addSeller")
    public Result addSeller(@RequestBody SellerVO seller) {
		logger.debug("/api/addSeller------------------------------------------------------------");
		
		try {
			long resultCount = adminService.addSeller(seller);
			if(resultCount > 0) {
				return new Result(0, "success");
			} else {
				return new Result(100, "insert failed");
			}
		} catch (PersistenceException e) {
			return new Result(100, "insert failed");
		} 
	}
	
	@RequestMapping("/admin/api/modifySeller")
    public Result modifySeller(@RequestBody SellerVO seller) {
		logger.debug("/api/modifySeller----------------------------------------------------------");
		
		long resultCount = adminService.modifySeller(seller);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "update failed");
		}
	}
	
	@RequestMapping("/admin/api/removeSeller")
    public Result removeSeller(@RequestBody SellerVO seller) {
		logger.debug("/api/removeManager----------------------------------------------------------");
		
		long resultCount = adminService.removeSeller(seller);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "delete failed");
		}
	}
	
	@RequestMapping("/admin/api/getSellerList")
    public ResultDataTotal<List<SellerVO>> getSellerList(@RequestBody SearchVO search) {
		logger.debug("/api/getSellerList--------------------------------------------------");
		List<SellerVO> sellerList = adminService.getSellerList(search);
		
		int total = adminService.countManager(search);
		
		return new ResultDataTotal<List<SellerVO>>(0, "success", sellerList, total);
	}
	
	@RequestMapping("/admin/api/getGoodsOfSeller")
    public ResultDataTotal<List<MapSellerGoodsVO>> getGoodsOfSeller(@RequestBody SearchVO search) {
		logger.debug("/api/getGoodsOfSeller--------------------------------------------------");
		List<MapSellerGoodsVO> goodsList = adminService.getGoodsOfSeller(search);
		
		int total = adminService.countGoodsOfSeller(search);
		
		return new ResultDataTotal<List<MapSellerGoodsVO>>(0, "success", goodsList, total);
	}
	
	//발송정보 리스트 가져오기
	@RequestMapping("/admin/api/getSendList")
    public ResultDataTotal<List<SendVO>> getSendList(@RequestBody SearchVO search) {
		logger.debug("/api/getSendList--------------------------------------------------");
		List<SendVO> sendList = adminService.getSendList(search);
		
		int total = adminService.countSendList(search);
		
		return new ResultDataTotal<List<SendVO>>(0, "success", sendList, total);
	}
	//테스트발송정보 리스트 가져오기
	@RequestMapping("/admin/api/getTestSendList")
    public ResultDataTotal<List<SendVO>> getTestSendList(@RequestBody SearchVO search) {
		logger.debug("/api/getTestSendList--------------------------------------------------");
		List<SendVO> sendList = adminService.getTestSendList(search);
		
		int total = adminService.countSendList(search);
		
		return new ResultDataTotal<List<SendVO>>(0, "success", sendList, total);
	}
}
