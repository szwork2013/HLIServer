package com.hli.service;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.MapSellerGoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;
import com.hli.domain.SendVO;

public interface AdminService {
	public int modifyGoodsCommission(GoodsVO goods);
	public int addGoods(GoodsVO goods);
	public int modifyGoods(GoodsVO goods);
	public GoodsVO getGoods(GoodsVO goods);
	public void saveGoods(GoodsVO goods);
	
	//상품관리
	public List<GoodsVO> getGoodsList(SearchVO search);
	public int countGoods(SearchVO search);
	
	//관리자 화면 사용자 관리
	public long addManager(ManagerVO manager) throws PersistenceException;
	public long modifyManager(ManagerVO manager) throws PersistenceException;
	public long removeManager(ManagerVO manager) throws PersistenceException;
	public int countManager(SearchVO search);
	public ManagerVO getManager(ManagerVO manager);
	public List<ManagerVO> getManagerList(SearchVO search);
	
	//판매업체 관리
	public int addSeller(SellerVO seller) throws PersistenceException;
	public int modifySeller(SellerVO seller) throws PersistenceException;
	public int removeSeller(SellerVO seller) throws PersistenceException;
	public int countSeller(SearchVO search);
	public List<SellerVO> getSellerList(SearchVO search);
	public SellerVO getSeller(SellerVO seller);
	public int modifyMapSellerGoods(MapSellerGoodsVO map) throws PersistenceException;
	public List<MapSellerGoodsVO> getGoodsOfSeller(SearchVO search);
	public int countGoodsOfSeller(SearchVO search);
	
	//open api
	public List<SellerGoodsVO> getAllGoodsOfSeller(SearchVO search);
	
	//발송 정보
	public long addSend(SendVO send);
	public long addTestSend(SendVO send);
	public List<SendVO> getSendList(SearchVO search);
	public List<SendVO> getTestSendList(SearchVO search); 
	public int countSendList(SearchVO search);
	public int countTestSendList(SearchVO search);
}
