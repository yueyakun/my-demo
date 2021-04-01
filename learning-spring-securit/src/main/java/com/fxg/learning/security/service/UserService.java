package com.fxg.learning.security.service;

import com.fxg.learning.security.domain.User;
import com.fxg.learning.security.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 用户信息 service 实现类
 * </p>
 */
@Service
@Validated
public class UserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UserMapper userMapper;


	public User selectById(@NotNull Integer id) {
		return userMapper.selectById(id);
	}

	public User selectByName(@NotBlank String name) {
		return userMapper.loadUserByUsername(name);
	}
}
