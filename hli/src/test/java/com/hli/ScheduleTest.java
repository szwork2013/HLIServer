package com.hli;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import com.hli.scheduler.HttpScheduler;

public class ScheduleTest {
	
	@Test
	public void productTest() {
		HttpScheduler schedule = new HttpScheduler();
		//schedule.getProductOfM12();
		schedule.getProductOfCoupList();
	}

	
}
