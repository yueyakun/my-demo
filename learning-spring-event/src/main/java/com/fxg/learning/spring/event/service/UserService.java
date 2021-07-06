package com.fxg.learning.spring.event.service;

import com.fxg.learning.spring.event.domain.User;
import com.fxg.learning.spring.event.event.UserCommitEvent;
import com.fxg.learning.spring.event.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public User add (User user){
		logger.info("accept request,current thread name :{}",Thread.currentThread().getName());
		userMapper.insertUser(user);
		eventPublisher.publishEvent(new UserCommitEvent(user));
		logger.info("save complete");
		return user;
	}

}
