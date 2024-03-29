package com.hli.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.MapSellerGoodsVO;
import com.hli.domain.SearchVO;
import com.hli.domain.SellerGoodsVO;
import com.hli.domain.SellerVO;
import com.hli.domain.SendVO;
import com.hli.persistence.AdminMapper;

@Service
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private AdminMapper adminMapper;
	
	/**
	 * 공급업체 수수료 수정
	 */
	@Override
	public int modifyGoodsCommission(GoodsVO goods) {
		return adminMapper.updateGoodsCommission(goods);
	}

	@Override
	public int addGoods(GoodsVO goods) {
		return adminMapper.insertGoods(goods);
	}

	@Override
	public int modifyGoods(GoodsVO goods) {
		return adminMapper.updateGoods(goods);
	}

	@Override
	public void saveGoods(GoodsVO inGoods) {
		//System.out.println("saveGoods:" + inGoods);
		GoodsVO goods = getGoods(inGoods);
		if(goods == null) {
			//insert
			adminMapper.insertGoods(inGoods);
			//실상품이 추가되면 판매업체의 map에도 추가한다.
			//inGoods에 자동 증가된 goods_id 키값이 들어가 있다.
			if(inGoods.isReal()) {
				List<SellerVO> sellerList = adminMapper.selectSellerList(new SearchVO());
				for(SellerVO seller : sellerList) {
					MapSellerGoodsVO vo = new MapSellerGoodsVO();
					vo.setSeller_id(seller.getSeller_id());
					vo.setCommission("5"); //5퍼센트로 고정
					vo.setGoods_id(inGoods.getGoods_id());
					adminMapper.insertMapSellerGoods(vo);
				}
			}
		} else {
			//update
			inGoods.setGoods_id(goods.getGoods_id());
			adminMapper.updateGoods(inGoods);
		}
	}

	@Override
	public GoodsVO getGoods(GoodsVO goods) {
		return adminMapper.selectGoods(goods);
	}
	
	
	@Override
	public long addManager(ManagerVO manager) throws PersistenceException {
		return adminMapper.insertManager(manager);
	}

	@Override
	public long modifyManager(ManagerVO manager) throws PersistenceException {
		return adminMapper.updateManager(manager);
	}

	@Override
	public long removeManager(ManagerVO manager) throws PersistenceException {
		return adminMapper.deleteManager(manager);
	}

	@Override
	public int countManager(SearchVO search) {
		return adminMapper.countManager(search);
	}

	@Override
	public ManagerVO getManager(ManagerVO manager) {
		return adminMapper.selectManager(manager);
	}

	@Override
	public List<ManagerVO> getManagerList(SearchVO search) {
		return adminMapper.selectManagerList(search);
	}

	@Override
	public List<GoodsVO> getGoodsList(SearchVO search) {
		return adminMapper.selectGoodsList(search);
	}

	@Override
	public int countGoods(SearchVO search) {
		return adminMapper.countGoods(search);
	}

	
	//판매업체 관리
	@Override
	public int addSeller(SellerVO seller) throws PersistenceException {
		//자동생성된 seller의 키는 seller 객체에 담겨 있다.
		int seller_id = adminMapper.insertSeller(seller);

		
		SearchVO search = new SearchVO();
		search.setReal(true);
		List<GoodsVO> goodsList = getGoodsList(search);
		for(GoodsVO goods : goodsList) {
			MapSellerGoodsVO mapVO = new MapSellerGoodsVO();
			mapVO.setSeller_id(seller.getSeller_id());
			mapVO.setGoods_id(goods.getGoods_id());
			mapVO.setCommission("0");
			adminMapper.insertMapSellerGoods(mapVO);
		}
		
		return seller_id;
	}

	@Override
	public int modifySeller(SellerVO seller) throws PersistenceException {
		return adminMapper.updateSeller(seller);
	}

	@Override
	public int removeSeller(SellerVO seller) throws PersistenceException {
		return adminMapper.deleteSeller(seller);
	}

	@Override
	public int countSeller(SearchVO search) {
		return adminMapper.countSeller(search);
	}

	@Override
	public List<SellerVO> getSellerList(SearchVO search) {
		return adminMapper.selectSellerList(search);
	}

	@Override
	public int modifyMapSellerGoods(MapSellerGoodsVO map) throws PersistenceException {
		return adminMapper.updateMapSellerGoods(map);
	}
	
	@Override
	public List<MapSellerGoodsVO> getGoodsOfSeller(SearchVO search) {
		return adminMapper.selectGoodsOfSeller(search);
	}

	@Override
	public int countGoodsOfSeller(SearchVO search) {
		return adminMapper.countGoodsOfSeller(search);
	}

	@Override
	public SellerVO getSeller(SellerVO seller) {
		return adminMapper.selectSeller(seller);
	}

	@Override
	public List<SellerGoodsVO> getAllGoodsOfSeller(SearchVO search) {
		return adminMapper.selectAllGoodsOfSeller(search);
	}

	@Override
	public long addSend(SendVO send) {
		return adminMapper.insertSend(send);
	}

	@Override
	public long addTestSend(SendVO send) {
		return adminMapper.insertTestSend(send);
	}

	@Override
	public List<SendVO> getSendList(SearchVO search) {
		return adminMapper.selectSendList(search);
	}

	@Override
	public List<SendVO> getTestSendList(SearchVO search) {
		return adminMapper.selectTestSendList(search);
	}

	@Override
	public int countSendList(SearchVO search) {
		return adminMapper.countSendList(search);
	}

	@Override
	public int countTestSendList(SearchVO search) {
		return adminMapper.countTestSendList(search);
	}

}
