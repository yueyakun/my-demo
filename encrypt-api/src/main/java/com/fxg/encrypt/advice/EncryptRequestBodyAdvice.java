package com.fxg.encrypt.advice;

import com.fxg.encrypt.SecretKeyConfig;
import com.fxg.encrypt.annotation.Decrypt;
import com.fxg.encrypt.interceptor.AESKeyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

@ControllerAdvice
public class EncryptRequestBodyAdvice implements RequestBodyAdvice {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SecretKeyConfig secretKeyConfig;

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		Method method = methodParameter.getMethod();
		//解密 body 的前提是：method有 Decrypt 注解且开关打开
		if (Objects.nonNull(method) && method.isAnnotationPresent(Decrypt.class) && secretKeyConfig.isOpen()) {
			return true;
		}
		return false;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		log.info("接收到aesKey:{}", AESKeyHandler.get());
			try {
				return new DecryptHttpInputMessage(inputMessage, secretKeyConfig, parameter.getMethod().getAnnotation(Decrypt.class));
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				log.error("Decryption failed", e);
			}
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}
}
