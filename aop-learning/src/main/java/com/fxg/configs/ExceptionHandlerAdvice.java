package com.fxg.configs;

import com.fxg.exception.ExceptionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * 统一异常捕捉
 */
@RestControllerAdvice("com.fxg")
//@ControllerAdvice  这里用 @RestControllerAdvice 注解的话后面的异常处理方法上就不用加 @ResponseBody 注解了
public class ExceptionHandlerAdvice {

	Logger logger = LoggerFactory.getLogger(getClass());

	private String ipAddress;

	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 *
	 */
	public ExceptionHandlerAdvice() {
		try {
			ipAddress = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("获取ip地址失败");
		}
		logger.info("当前主机地址为：{}", ipAddress);
	}

	public ExceptionDescriptor newDescriptor(Exception ex) {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		ExceptionDescriptor descriptor = ExceptionDescriptor.newFromException(ex, this.applicationName, this.ipAddress,
				request.getRequestURI(), null);
		return descriptor;
	}

	//	/**
	//	 * 参数校验异常处理
	//	 *
	//	 * @param e {@link ConstraintViolationException}
	//	 * @return HttpResult<Void>
	//	 */
	//	@ExceptionHandler(value = ConstraintViolationException.class)
	//	public HttpResult<Void> constrainValidationException(ConstraintViolationException e) {
	//
	//		ExceptionDescriptor descriptor = this.newDescriptor(e);
	//		StringBuilder sb = new StringBuilder();
	//
	//		sb.append("校验失败详细信息: \n");
	//
	//		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
	//			sb.append(String.format("校验失败位置:%s,校验规则%s,实际参数值:%s,校验提示信息:%s\n", violation.getPropertyPath(), violation.getConstraintDescriptor().getAnnotation(), violation.getInvalidValue(), violation.getMessage()));
	//		}
	//
	//		descriptor.setInfo(String.format("校验失败详细信息%s", sb.toString()));
	//		logger.info("Validation异常{}", descriptor.toString(), e);
	//		return new HttpResult<>(HttpStatus.PARAM_ERROR, "validation错误", null, Collections.singletonList(descriptor));
	//
	//	}


	/**
	 * 全局异常捕捉处理
	 */
	@ExceptionHandler(value = Exception.class)
	public void globalErrorHandler(Exception e) {
		ExceptionDescriptor descriptor = this.newDescriptor(e);
		logger.info("服务器发生未识别的内部错误{}", descriptor.toString(), e);
		e.printStackTrace();
	}


}
