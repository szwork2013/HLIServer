package com.hli;

import org.junit.Test;

import com.hli.scheduler.HttpScheduler;

public class ScheduleTest {
	
	@Test
	public void productTest() {
		HttpScheduler schedule = new HttpScheduler();
		schedule.getProductOfM12();
	}

}
