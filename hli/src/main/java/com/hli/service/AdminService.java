package com.hli.service;

import com.hli.domain.GoodsVO;

public interface AdminService {
	public int addGoods(GoodsVO goods);
	public int modifyGoods(GoodsVO goods);
	public int saveGoods(GoodsVO goods);
}
