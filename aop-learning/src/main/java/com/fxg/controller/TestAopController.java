package com.fxg.controller;

import com.fxg.annotation.ApiLog;
import com.fxg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * test aop
 */

@RestController
@RequestMapping("/test")
public class TestAopController {

	@Autowired
	private UserService userService;

	@ApiLog
	@PostMapping("/test1")
	public String test1() {
		System.out.println("代理public方法：test1");
		System.out.println(userService);
		System.out.println(this);
		this.privateMethod();
		return "ok";
	}

	private void privateMethod() {
		System.out.println("私有方法方法：privateMethod");
		System.out.println(userService);
		System.out.println(this);
	}

	@ApiLog
	@GetMapping("/test2")
	private void test2() {
		System.out.println("代理private方法：test2");
		System.out.println(this.userService);
		System.out.println(this);
		this.privateMethod();
	}
}
