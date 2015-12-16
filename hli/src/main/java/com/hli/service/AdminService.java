package com.hli.service;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;

import com.hli.domain.GoodsVO;
import com.hli.domain.ManagerVO;
import com.hli.domain.SearchVO;

public interface AdminService {
	public int addGoods(GoodsVO goods);
	public int modifyGoods(GoodsVO goods);
	public GoodsVO getGoods(GoodsVO goods);
	public void saveGoods(GoodsVO goods);
	
	//관리자 화면 사용자 관리
	public long addManager(ManagerVO manager) throws PersistenceException;
	public long modifyManager(ManagerVO manager) throws PersistenceException;
	public long removeManager(ManagerVO manager) throws PersistenceException;
	public int countManager(SearchVO search);
	public ManagerVO getManager(ManagerVO manager);
	public List<ManagerVO> getManagerList(SearchVO search);
}
