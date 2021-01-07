package com.fxg.encrypt.interceptor;

import com.fxg.api.HttpStatus;
import com.fxg.configs.SecretKeyConfig;
import com.fxg.filter.RequestWrapper;
import com.fxg.util.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public static final String VERIFY_FAIL_MSG = "The request parameter signature verification failed! cause：{}";

	@Autowired
	private SecretKeyConfig secretKeyConfig;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		RequestWrapper requestWrapper;

		if (request instanceof RequestWrapper) {
			requestWrapper = (RequestWrapper) request;
		} else {
			requestWrapper = new RequestWrapper(request);
		}


		// aes时间戳
		String timestamp = request.getHeader("X_TIMESTAMP");
		// aes随机数
		String nonce = request.getHeader("X_NONCE");

		if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce)) {
			logger.warn(VERIFY_FAIL_MSG, "sing parameters are missing");
			response.setStatus(HttpStatus.SIGN_FAILED);
			return false;
		}

		//校验时间戳
		// TODO: 2021/1/7

		//校验时间戳加随机数，防止重放攻击

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

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
