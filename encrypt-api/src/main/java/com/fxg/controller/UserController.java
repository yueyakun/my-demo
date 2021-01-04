package com.fxg.controller;

import com.fxg.api.HttpResult;
import com.fxg.api.HttpStatus;
import com.fxg.domain.User;
import com.fxg.enums.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
	@PostMapping("/selectByName")
	public HttpResult<User> receiveEncryptBody(@RequestBody User data) {

		logger.info("receive data:{}",data);
		User user = new User();
		user.setNickName("xiaohuihui");
		user.setGender(Gender.MALE);
		user.setBirthday(LocalDate.now());
		user.setId(100);
		return new HttpResult<>(HttpStatus.OK, "ok", user);
	}
}
