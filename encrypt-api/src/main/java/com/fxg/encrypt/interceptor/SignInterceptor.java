package com.fxg.encrypt.interceptor;

import com.fxg.api.HttpStatus;
import com.fxg.configs.SecretKeyConfig;
import com.fxg.util.Base64Util;
import com.fxg.util.SignUtil;
import com.fxg.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

public class SignInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public static final String VERIFY_FAIL_MSG = "The request parameter signature verification failed! cause：{}";

	@Autowired
	private SecretKeyConfig secretKeyConfig;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 签名
		String signStr = request.getHeader("X_SIGN");
		// aes秘钥
		String encryptedAesKey = request.getHeader("X_EAK");
		// aes时间戳
		String timestamp = request.getHeader("X_TIMESTAMP");
		// aes随机数
		String nonce = request.getHeader("X_NONCE");

		if (StringUtils.isEmpty(signStr) || StringUtils.isEmpty(encryptedAesKey) || StringUtils.isEmpty(timestamp)
				|| StringUtils.isEmpty(nonce)) {
			logger.warn(VERIFY_FAIL_MSG, "sing parameters are missing");
			response.setStatus(HttpStatus.SIGN_FAILED);
			return false;
		}

		//校验时间戳
		// TODO: 2021/1/7

		//校验时间戳加随机数，防止重放攻击

		//解密aes秘钥
		byte[] aesKeyByte = RSAUtil.decrypt(Base64Util.decode(encryptedAesKey), secretKeyConfig.getPrivateKey());
		String aesKey = new String(aesKeyByte, StandardCharsets.UTF_8);
		request.setAttribute("aesKey",aesKey);
		//验证签名
		boolean right = SignUtil.verifySign(aesKey,timestamp, nonce, request);
		if (right) {
			return true;
		}
		logger.warn(VERIFY_FAIL_MSG);
		response.setStatus(HttpStatus.SIGN_FAILED);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}
}
