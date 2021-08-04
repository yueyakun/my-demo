package com.yue.service;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MyMessageHandler implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public void onMessage(Message message) {
        //解析message属性
        MessageProperties messageProperties = message.getMessageProperties();
        //解析body消息体
        String body = new String(message.getBody());

        logger.info("-----------------触发自定义监听器-------------------");
        logger.info("messageProperties:" + messageProperties.toString());
        logger.info("body:" + body);
    }
}
