package com.fxg.learning.spring.event.controller;

import com.fxg.learning.spring.event.domain.User;
import com.fxg.learning.spring.event.event.UserCommitEvent;
import com.fxg.learning.spring.event.event.UserLoginEvent;
import com.fxg.learning.spring.event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@PostMapping(value = "/login")
	public String login(String userName) {
		//todo 登录逻辑

		//发布登录事件
		System.out.println(String.format("login api,username is :%s,current thread name :%s",userName,Thread.currentThread().getName()));
		publisher.publishEvent(new UserLoginEvent(userName));
//		publisher.publishEvent(new UserCommitEvent(new User()));
		return "ok";
	}

	@PostMapping(value = "/add")
	public String addUser() {
		User user = new User();
		user.setName("aaa");
		user.setAge(11);
		userService.add(user);
		return user.toString();
	}
}
