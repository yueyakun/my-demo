package com.fxg.controller;

import com.fxg.annotation.ApiLog;
import com.fxg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 需要admin或者user权限的接口统一放到这个controller里
 */

@RestController
@RequestMapping("/test")
public class TestAopController {

	@Autowired
	private UserService userService;

	@ApiLog
	@GetMapping("/test1")
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
