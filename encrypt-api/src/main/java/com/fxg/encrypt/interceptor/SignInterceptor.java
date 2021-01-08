package com.fxg.encrypt.interceptor;

import com.fxg.api.HttpStatus;
import com.fxg.configs.SecretKeyConfig;
import com.fxg.filter.RequestWrapper;
import com.fxg.util.Base64Util;
import com.fxg.util.RSAUtil;
import com.fxg.util.SignUtil;
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

	public static final String VERIFY_FAIL_MSG = "The request parameter signature verification failed! cause：{}";

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

		//时间戳和重放校验
		if (secretKeyConfig.isCheckReplay()) {
			// 计算时间差
			int requestTime = Integer.parseInt(timestamp);
			long currentTime = System.currentTimeMillis();
			long toleranceTime = currentTime - requestTime;
			// 如果请求时间小于最小容忍请求时间, 判定为超时
			if (requestTime > currentTime || secretKeyConfig.getTimeOut() < toleranceTime) {
				logger.error("request too early, requestTime:{}, currentTime:{}", requestTime, currentTime);
				response.setStatus(HttpStatus.TOO_EARLY);
				return false;
			}
			//如果timestamp和已存在,说明timeout时间内同样的报文之前请求过
			Boolean success = redisTemplate.opsForValue()
					.setIfAbsent(String.format(REDIS_KEY, timestamp, nonce), "", secretKeyConfig.getTimeOut(),
							TimeUnit.MILLISECONDS);
			if (Objects.isNull(success) || !success) {
				response.setStatus(HttpStatus.TOO_EARLY);
				return false;
			}
		}
		if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce)) {
			logger.warn(VERIFY_FAIL_MSG, "sing parameters are missing");
			response.setStatus(HttpStatus.SIGN_FAILED);
			return false;
		}

		// 开关关闭或者配置不验签就只解密出aesKey
		if (!secretKeyConfig.isOpen() || !secretKeyConfig.isCheckSign()) {
			// 加密的aes秘钥
			String encryptedAesKey = request.getHeader("X_EAK");//解密aes秘钥
			if (!StringUtils.isEmpty(encryptedAesKey)) {
				byte[] aesKeyByte = RSAUtil.decrypt(Base64Util.decode(encryptedAesKey),
						secretKeyConfig.getPrivateKey());
				String aesKey = new String(aesKeyByte, StandardCharsets.UTF_8);
				AESKeyHandler.set(aesKey);
			}
			return true;
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
		logger.warn(VERIFY_FAIL_MSG);
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
