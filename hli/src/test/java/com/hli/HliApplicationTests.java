package com.hli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hli.domain.GoodsVO;
import com.hli.persistence.AdminMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HliApplication.class)
@WebAppConfiguration
public class HliApplicationTests {
	
	@Autowired
	private AdminMapper adminMapper;

	@Test
	public void contextLoads() {
		GoodsVO inGoods = new GoodsVO();
		inGoods.setGoods_code("GD1109210002");
		inGoods.setReal(false);
		GoodsVO goods = adminMapper.selectGoods(inGoods);
		System.out.println(goods);
		
		inGoods.setReal(true);
		goods = adminMapper.selectGoods(inGoods);
		System.out.println(goods);
	}

}
