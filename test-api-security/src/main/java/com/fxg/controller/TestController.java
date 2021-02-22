package com.fxg.controller;

import com.fxg.api.security.annotation.Decrypt;
import com.fxg.api.security.annotation.Encrypt;
import com.fxg.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * test aop-security
 */

@RestController
@RequestMapping("/test")
public class TestController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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
