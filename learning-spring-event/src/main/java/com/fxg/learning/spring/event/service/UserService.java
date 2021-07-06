package com.fxg.learning.spring.event.service;

import com.fxg.learning.spring.event.domain.User;
import com.fxg.learning.spring.event.event.UserCommitEvent;
import com.fxg.learning.spring.event.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public User add (User user){
		userMapper.insert(user);
		eventPublisher.publishEvent(new UserCommitEvent(user));
		return user;
	}

}
