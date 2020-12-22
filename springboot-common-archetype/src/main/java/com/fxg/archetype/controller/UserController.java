package com.fxg.archetype.controller;

import com.fxg.archetype.api.HttpResult;
import com.fxg.archetype.api.HttpStatus;
import com.fxg.archetype.domain.User;
import com.fxg.archetype.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */

@RestController
@RequestMapping("/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UserService userService;

	/**
	 * 查询初始化记录
	 */
	@GetMapping("/selectByName")
	public HttpResult<User> selectByName(@RequestParam String name) {
		return new HttpResult<>(HttpStatus.OK, "ok", userService.selectByName(name));
	}
}
