package com.fxg.learning.spring.event.listener;

import com.fxg.learning.spring.event.event.UserCommitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionCommitListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT ,classes = UserCommitEvent.class)
	public void onUserAdd(UserCommitEvent event){
		String threadName = Thread.currentThread()
				.getName();
		logger.info("listener accept event:{},current thread name :{}",event,threadName);
	}
}
