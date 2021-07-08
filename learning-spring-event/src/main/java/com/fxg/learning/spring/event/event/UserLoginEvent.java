package com.fxg.learning.spring.event.event;

import com.fxg.learning.spring.event.domain.User;
import org.springframework.context.ApplicationEvent;

public class UserLoginEvent extends ApplicationEvent {
	private String userName;
	public UserLoginEvent(String userName) {
		super(userName);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public String toString() {
		return "userName:" + userName;
	}
}
