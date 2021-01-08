package com.fxg.encrypt.advice;

import com.fxg.api.HttpResult;
import com.fxg.configs.SecretKeyConfig;
import com.fxg.encrypt.annotation.Encrypt;
import com.fxg.encrypt.interceptor.AESKeyHandler;
import com.fxg.util.Base64Util;
import com.fxg.util.JsonUtils;
import com.fxg.util.RSAUtil;
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

	private boolean encrypt;

	@Autowired
	private SecretKeyConfig secretKeyConfig;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		Method method = returnType.getMethod();
		if (Objects.isNull(method)) {
			return encrypt;
		}
		encrypt = method.isAnnotationPresent(Encrypt.class) && secretKeyConfig.isOpen();
		return encrypt;
	}

	@Override
	public HttpResult<Object> beforeBodyWrite(HttpResult<Object> body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {

		log.info("接收到aesKey:{}", AESKeyHandler.get());

		if (encrypt) {
			HttpResult<Object> result = encryptBody(body);
			if (result != null)
				return result;
		}

		return body;
	}

	private HttpResult<Object> encryptBody(HttpResult<Object> body) {

		String publicKey = secretKeyConfig.getPublicKey();
		try {
			//取出data
			Object data = body.getData();
			// TODO: 2021/1/4 设置加密标志
			String content = JsonUtils.writeValueAsString(data);
			if (!StringUtils.hasText(publicKey)) {
				throw new NullPointerException("Please configure rsa.encrypt.privateKey parameter!");
			}
			byte[] ByteData = content.getBytes();
			byte[] encodedData = RSAUtil.encrypt(ByteData, publicKey);
			String result = Base64Util.encode(encodedData);
			if (secretKeyConfig.isShowLog()) {
				log.info("Pre-encrypted data：{}，After encryption：{}", content, result);
			}
			body.setData(result);
			return body;
		} catch (Exception e) {
			log.error("Encrypted data exception", e);
		}
		return null;
	}
}
