package com.hli.service;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.SearchVO;
import com.hli.persistence.AdminMapper;

@Service
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private AdminMapper adminMapper;

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
		System.out.println("saveGoods:" + inGoods.getGoods_code());
		GoodsVO goods = getGoods(inGoods);
		if(goods == null) {
			//insert
			adminMapper.insertGoods(inGoods);
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

}
