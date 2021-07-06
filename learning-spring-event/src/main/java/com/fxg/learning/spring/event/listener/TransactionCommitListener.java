package com.fxg.learning.spring.event.listener;

import com.fxg.learning.spring.event.event.UserCommitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionCommitListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Async
	@Order(1)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT ,classes = UserCommitEvent.class)
	public void onUserAdd(UserCommitEvent event){
		String threadName = Thread.currentThread()
				.getName();
		logger.info("listener1 accept event:{},current thread name :{}",event.toString(),threadName);
//		throw new RuntimeException("just test");
	}

	@Async
	@Order(2)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT ,classes = UserCommitEvent.class)
	public void onUserAdd2(UserCommitEvent event){
		String threadName = Thread.currentThread()
				.getName();
		logger.info("listener2 accept event:{},current thread name :{}",event.toString(),threadName);
//		throw new RuntimeException("just test");
	}
}
