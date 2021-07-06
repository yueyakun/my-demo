package com.fxg.learning.spring.event.event;

import com.fxg.learning.spring.event.domain.User;
import org.springframework.context.ApplicationEvent;

public class UserCommitEvent extends ApplicationEvent {
	private User user;
	public UserCommitEvent(User user) {
		super(user);
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserCommitEvent:" + user;
	}
}
