package com.hli.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hli.domain.GoodsVO;
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
			adminMapper.updateGoods(goods);
		}
	}

	@Override
	public GoodsVO getGoods(GoodsVO goods) {
		return adminMapper.selectGoods(goods);
	}

}
