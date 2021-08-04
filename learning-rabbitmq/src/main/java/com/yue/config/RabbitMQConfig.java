package com.yue.config;

import com.yue.service.MyMessageHandler;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class RabbitMQConfig {


	@Value("${spring.rabbitmq.host}")
	public String host;

	@Value("${spring.rabbitmq.username}")
	public String username;

	@Value("${spring.rabbitmq.password}")
	public String password;

	@Autowired
	CachingConnectionFactory connectionFactory;

	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost(host);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(password);
		return cachingConnectionFactory;
	}

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Bean(name = "rabbitAdmin")
	@Primary
	public RabbitAdmin rabbitAdmin() {
		RabbitAdmin admin = new RabbitAdmin(this.connectionFactory);
		return admin;
	}

	@Bean
	@Qualifier("test-exchange")
	public Exchange testExchange() {
		Exchange exchange = ExchangeBuilder.directExchange("test-exchange").durable(true).build();
		this.rabbitAdmin.declareExchange(exchange);
		return exchange;
	}

	@Bean
	@Qualifier("test-queue1")
	public Queue testQueue1() {
		Exchange stockExchange = this.testExchange();
		Queue queue = QueueBuilder.durable("test-queue1").withArgument("x-message-ttl", 120000).build();
		this.rabbitAdmin.declareQueue(queue);
		this.rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(stockExchange).with("a.b.c").noargs());
		return queue;
	}

	@Bean
	@Qualifier("test-queue2")
	public Queue testQueue2() {
		Exchange stockExchange = this.testExchange();
		Queue queue = QueueBuilder.durable("test-queue2").withArgument("x-message-ttl", 120000).build();
		this.rabbitAdmin.declareQueue(queue);
		this.rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(stockExchange).with("a.b.d").noargs());
		return queue;
	}

	@Autowired
	private MyMessageHandler myMessageHandler;

	@Bean
	public SimpleMessageListenerContainer mqMessageContainer() throws AmqpException, IOException {
		//生成 监听容器
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		//设置启动监听超时时间
		container.setConsumerStartTimeout(3000L);
		container.setExposeListenerChannel(true);
		//设置确认模式 设置成自动偷偷懒~
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		//监听处理类
		container.setMessageListener(myMessageHandler);

		container.addQueueNames("test-queue1");
		container.addQueueNames("test-queue2");
		container.start();
		return container;
	}

}
