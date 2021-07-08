package com.fxg.learning.spring.event.listener;

import com.fxg.learning.spring.event.event.UserLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncLoginLogListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass()
															.getName());

//	@Async
	@Order(105)
	@EventListener(classes = UserLoginEvent.class,condition = "event.userName == 'yyk'")
	public void listener3(UserLoginEvent event) {
		String threadName = Thread.currentThread()
				.getName();
		System.out.println(String.format("login log listener3 accept event:%s,current thread name :%s", event.toString(), threadName));
//		throw new RuntimeException("just test");
	}

	//	@Async
	@Order(115)
	@EventListener(classes = UserLoginEvent.class)
	public void listener4(UserLoginEvent event) {
		String threadName = Thread.currentThread()
				.getName();
		System.out.println(String.format("login log listener4 accept event:%s,current thread name :%s", event.toString(), threadName));
		//		throw new RuntimeException("just test");
	}
}
