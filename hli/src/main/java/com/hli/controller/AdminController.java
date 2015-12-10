package com.hli.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	
	@RequestMapping("/admin/hello")
	public String Hello() {
		return "Hello test";
	}
}
