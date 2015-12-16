package com.hli.persistence;

import com.hli.domain.GoodsVO;

public interface AdminMapper {
	public int insertGoods(GoodsVO goods);
	public int updateGoods(GoodsVO goods);
	public GoodsVO selectGoods(GoodsVO goods);
}
