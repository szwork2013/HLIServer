package com.hli.persistence;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.MapSellerGoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;

public interface AdminMapper {
	public int insertGoods(GoodsVO goods);
	public int updateGoods(GoodsVO goods);
	public GoodsVO selectGoods(GoodsVO goods);
	
	//상품관리
	public List<GoodsVO> selectGoodsList(SearchVO search);
	public int countGoods(SearchVO search);
	
	//관리자 화면 사용자 관리
	public long insertManager(ManagerVO manager) throws PersistenceException;
	public long updateManager(ManagerVO manager) throws PersistenceException;
	public long deleteManager(ManagerVO manager) throws PersistenceException;
	public int countManager(SearchVO search);
	public ManagerVO selectManager(ManagerVO manager);
	public List<ManagerVO> selectManagerList(SearchVO search);
	
	//판매업체 관리
	public int insertSeller(SellerVO Seller) throws PersistenceException;
	public void insertMapSellerGoods(MapSellerGoodsVO map) throws PersistenceException;
	public int countSeller(SearchVO search);
	public List<SellerVO> selectSellerList(SearchVO search);
	public SellerVO selectSeller(SellerVO seller);
	public List<MapSellerGoodsVO> selectGoodsOfSeller(SearchVO search);
	public int countGoodsOfSeller(SearchVO search);
	
	//open api
	public List<SellerGoodsVO> selectAllGoodsOfSeller(SearchVO search);
}
