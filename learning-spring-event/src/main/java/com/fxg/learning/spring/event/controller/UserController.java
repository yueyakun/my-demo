package com.fxg.learning.spring.event.controller;

import com.fxg.learning.spring.event.domain.User;
import com.fxg.learning.spring.event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/add")
	public String addUser() {
		User user = new User();
		user.setName("aaa");
		user.setAge(11);
		userService.add(user);
		return user.toString();
	}
}
