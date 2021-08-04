package com.yue.controller;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("test")
public class TestController {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@GetMapping("send1")
	public String send1(String message){
		rabbitTemplate.convertAndSend("test-exchange", "a.b.c", "hello world");
		return "ok";
	}

	@GetMapping("send2")
	public String send2(String message){
		Object o = rabbitTemplate.convertSendAndReceive("test-exchange", "a.b.d", "hello world");
		return "ok";
	}


}
