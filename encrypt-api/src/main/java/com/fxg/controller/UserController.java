package com.fxg.controller;

import com.fxg.api.HttpResult;
import com.fxg.api.HttpStatus;
import com.fxg.domain.User;
import com.fxg.encrypt.annotation.Decrypt;
import com.fxg.encrypt.annotation.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 测试
 */

@RestController
@RequestMapping("/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 查询初始化记录
	 */
	@Encrypt
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
	@Decrypt(required = true)
	@PostMapping(value = "/add")
	public HttpResult<User> add(@RequestBody User user) {

		logger.info(user.toString());
		return new HttpResult<>(HttpStatus.OK, "ok", user);
	}
}
