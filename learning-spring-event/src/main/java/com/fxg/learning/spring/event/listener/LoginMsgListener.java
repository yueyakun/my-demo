package com.fxg.learning.spring.event.listener;

import com.fxg.learning.spring.event.event.UserLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(110)
public class LoginMsgListener implements ApplicationListener<UserLoginEvent> {

	private Logger logger = LoggerFactory.getLogger(this.getClass()
															.getName());

	@Override
	public void onApplicationEvent(UserLoginEvent event) {
		String threadName = Thread.currentThread()
				.getName();
		System.out.println(String.format("login message listener accept event:%s,current thread name :%s", event.toString(), threadName));
//		throw new RuntimeException("just test");
	}
}
