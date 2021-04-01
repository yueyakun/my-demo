package com.fxg.archetype.controller;

import com.fxg.archetype.api.HttpResult;
import com.fxg.archetype.api.HttpStatus;
import com.fxg.archetype.domain.User;
import com.fxg.archetype.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	@PostMapping(value = "/selectByName")
	public HttpResult<User> selectByName(@RequestParam String name) {
		User user = new User();
		user.setId(1);
		user.setNickName("小灰灰");
		user.setUsername("summer");
		return new HttpResult<>(HttpStatus.OK, "ok", user);
	}

	/**
	 * 查询初始化记录
	 */
	@PostMapping(value = "/add")
	public HttpResult<User> add(@RequestBody User user) {

		logger.info(user.toString());
		return new HttpResult<>(HttpStatus.OK, "ok", user);
	}
}
