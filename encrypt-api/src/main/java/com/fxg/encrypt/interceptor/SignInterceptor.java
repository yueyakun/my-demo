package com.fxg.encrypt.interceptor;

import com.fxg.api.HttpStatus;
import com.fxg.encrypt.SecretKeyConfig;
import com.fxg.encrypt.filter.RequestWrapper;
import com.fxg.encrypt.util.Base64Util;
import com.fxg.encrypt.util.RSAUtil;
import com.fxg.encrypt.util.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public static final String VERIFY_TIMESTAMP_ERROR_MSG = "Time stamp validation failed! requestTime:{}, currentTime:{},timeOut:{}";
	public static final String VERIFY_SIGNATURE_ERROR_MSG = "Signature verification failed!";

	private static String REDIS_KEY = "trace:request:%s:%s";

	@Autowired
	private SecretKeyConfig secretKeyConfig;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		RequestWrapper requestWrapper;

		// aes时间戳
		String timestamp = request.getHeader("X_TIMESTAMP");
		// aes随机数
		String nonce = request.getHeader("X_NONCE");
		// 加密的aes秘钥
		String encryptedAesKey = request.getHeader("X_EAK");//解密aes秘钥

		/** 若开关关闭 直接返回*/
		if (!secretKeyConfig.isOpen()){
			return true;
		}

		/** 若配置不验签 只解密出aesKey */
		if (!secretKeyConfig.isCheckSign()&&StringUtils.hasText(encryptedAesKey)) {
			if (!StringUtils.isEmpty(encryptedAesKey)) {
				byte[] aesKeyByte = RSAUtil.decrypt(Base64Util.decode(encryptedAesKey),
						secretKeyConfig.getPrivateKey());
				String aesKey = new String(aesKeyByte, StandardCharsets.UTF_8);
				AESKeyHandler.set(aesKey);
			}
			return true;
		}

		/** 下面是验签逻辑 */

		//时间戳和随机数缺失直接返回
		if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(nonce)) {
			logger.warn(VERIFY_SIGNATURE_ERROR_MSG);
			response.setStatus(HttpStatus.ILLEGAL_TIMESTAMP);
			return false;
		}
		// 计算时间差
		long requestTime = Long.parseLong(timestamp);
		long currentTime = System.currentTimeMillis();
		long toleranceTime = currentTime - requestTime;
		// 如果请求时间大于当前时间或者小于最小容忍请求时间, 判定为超时
		if (requestTime > currentTime || secretKeyConfig.getTimeOut() < toleranceTime) {
			logger.error(VERIFY_TIMESTAMP_ERROR_MSG, requestTime, currentTime, secretKeyConfig.getTimeOut());
			response.setStatus(HttpStatus.ILLEGAL_TIMESTAMP);
			return false;
		}
		//如果timestamp和已存在,说明timeout时间内同样的报文之前请求过
		Boolean success = redisTemplate.opsForValue()
				.setIfAbsent(String.format(REDIS_KEY, timestamp, nonce), "", secretKeyConfig.getTimeOut(),
						TimeUnit.MILLISECONDS);
		if (Objects.isNull(success) || !success) {
			response.setStatus(HttpStatus.EXISTED_REQUET);
			return false;
		}

		if (request instanceof RequestWrapper) {
			requestWrapper = (RequestWrapper) request;
		} else {
			requestWrapper = new RequestWrapper(request);
		}

		//验证签名
		boolean right = SignUtil.verifySign(secretKeyConfig.getPrivateKey(), timestamp, nonce, requestWrapper);
		if (right) {
			return true;
		}
		logger.warn(VERIFY_SIGNATURE_ERROR_MSG);
		response.setStatus(HttpStatus.SIGN_FAILED);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		AESKeyHandler.remove();
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
