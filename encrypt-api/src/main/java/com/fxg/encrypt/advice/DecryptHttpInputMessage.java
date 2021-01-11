package com.fxg.encrypt.advice;

import com.fxg.encrypt.SecretKeyConfig;
import com.fxg.encrypt.annotation.Decrypt;
import com.fxg.encrypt.interceptor.AESKeyHandler;
import com.fxg.encrypt.util.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DecryptHttpInputMessage implements HttpInputMessage {


	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private HttpHeaders headers;
	private InputStream body;


	public DecryptHttpInputMessage(HttpInputMessage inputMessage, SecretKeyConfig secretKeyConfig, Decrypt decrypt)
			throws Exception {

		String aesKey = AESKeyHandler.get();
		logger.info("接收到aesKey:{}", aesKey);

		boolean showLog = secretKeyConfig.isShowLog();

		if (StringUtils.isEmpty(aesKey)) {
			throw new IllegalArgumentException("aesKey is null");
		}

		this.headers = inputMessage.getHeaders();
		String content = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines()
				.collect(Collectors.joining(System.lineSeparator()));

		if (showLog) {
			logger.info("Encrypted data received：{}", content);
		}

		String decryptBody;
		// 如果未加密
		if (content.startsWith("{")) {
			// 必须加密
			if (decrypt.required()) {
				logger.error("not support unencrypted content:{}", content);
				throw new RuntimeException("not support unencrypted content");
			}
			logger.info("Unencrypted without decryption:{}", content);
			decryptBody = content;
		} else {
			StringBuilder json = new StringBuilder();
			content = content.replaceAll(" ", "+");
			if (!StringUtils.isEmpty(content)) {
				String[] contents = content.split("\\|");
				for (String value : contents) {
					value = AESUtil.decrypt(value, aesKey);
					json.append(value);
				}
			}
			decryptBody = json.toString();
			if (showLog) {
				logger.info("After decryption：{}", decryptBody);
			}
		}

		this.body = new ByteArrayInputStream(decryptBody.getBytes());
	}

	@Override
	public InputStream getBody() {
		return body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
}
