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
	public int saveGoods(GoodsVO goods) {
		// TODO Auto-generated method stub
		return 0;
	}

}
