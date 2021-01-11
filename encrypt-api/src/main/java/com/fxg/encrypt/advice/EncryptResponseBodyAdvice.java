package com.fxg.encrypt.advice;

import com.fxg.api.HttpResult;
import com.fxg.encrypt.SecretKeyConfig;
import com.fxg.encrypt.annotation.Encrypt;
import com.fxg.encrypt.interceptor.AESKeyHandler;
import com.fxg.encrypt.util.AESUtil;
import com.fxg.encrypt.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<HttpResult<Object>> {


	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SecretKeyConfig secretKeyConfig;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		Method method = returnType.getMethod();
		//解密 body 的前提是：method有 Encrypt 注解且开关打开
		if (Objects.nonNull(method) && method.isAnnotationPresent(Encrypt.class) && secretKeyConfig.isOpen()) {
			return true;
		}
		return false;
	}

	@Override
	public HttpResult<Object> beforeBodyWrite(HttpResult<Object> body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		HttpResult<Object> result = encryptBody(body);
		if (result != null)
			return result;

		return body;
	}

	private HttpResult<Object> encryptBody(HttpResult<Object> body) {

		String aesKey = AESKeyHandler.get();
		log.info("接收到aesKey:{}", aesKey);

		try {
			//取出data
			Object data = body.getData();
			String content = JsonUtils.writeValueAsString(data);
			if (secretKeyConfig.isShowLog()) {
				log.info("Pre-encrypted data：{}", content);
			}
			if (!StringUtils.hasText(aesKey)) {
				throw new RuntimeException("AES_KEY IS EMPTY!");
			}
			String result = AESUtil.encrypt(content, aesKey);
			if (secretKeyConfig.isShowLog()) {
				log.info("After encryption：{}", result);
			}
			body.setData(result);
			return body;
		} catch (Exception e) {
			log.error("Encrypted data exception", e);
		}
		return null;
	}
}
