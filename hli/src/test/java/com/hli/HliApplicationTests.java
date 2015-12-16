package com.hli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hli.scheduler.HttpScheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HliApplication.class)
@WebAppConfiguration
public class HliApplicationTests {
	
	@Autowired
	private HttpScheduler scheduler;

	@Test
	public void contextLoads() {
		scheduler.getProductOfM12();
	}

}
