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

	@PostMapping(value = "/common")
	public HttpResult<User> common(@RequestParam String name, @RequestBody User user) {

		logger.info(user.toString());
		return new HttpResult<>(HttpStatus.OK, "ok", user);
	}

	@PostMapping("/sign")
	public String sign(@RequestParam Integer id, @RequestBody User user) {
		logger.info("enter sign method,id:{},user:{}", id, user);
		return "ok";
	}


	@Encrypt
	@GetMapping("/encrypt")
	private User encrypt() {
		User user = new User();
		user.setNickName("encrypt");
		logger.info("enter encrypt method,return user:{}", user);
		return user;
	}

	@Decrypt
	@PostMapping("/decrypt")
	private User decrypt(@RequestBody User user) {
		logger.info("enter decrypt method,param user:{}", user);
		user.setId(1);
		logger.info("enter decrypt method,return user:{}", user);
		return user;
	}
}
