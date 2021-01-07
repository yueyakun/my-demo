package com.fxg.configs;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fxg.encrypt.interceptor.SignInterceptor;
import com.fxg.formatter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

/**
 * Web 相关配置
 */
@Configuration("default-fxg-web-config")
public class WebConfig implements WebMvcConfigurer {

	/**
	 * 配置 fastjson 为 json 转换器
	 *
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jsonConverter());
		converters.add(stringConverter());
	}

	@Bean
	public FastJsonHttpMessageConverter jsonConverter() {
		FastJsonHttpMessageConverter jsonConverter = new FastJsonHttpMessageConverter();
		jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
		FastJsonConfig config = new FastJsonConfig();
		config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteMapNullValue);
		jsonConverter.setFastJsonConfig(config);
		return jsonConverter;
	}

	@Bean
	public StringHttpMessageConverter stringConverter() {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
		return stringConverter;
	}

	/**
	 * 配置日期时间相关的 formatter
	 *
	 * @param registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateFormatParser parser = new DateFormatParser();
		DateFormatter dateFormatter = new DateFormatter(parser);
		LocalDateFormatter localDateFormatter = new LocalDateFormatter(parser);
		LocalDateTimeFormatter localDateTimeFormatter = new LocalDateTimeFormatter(parser);
		LocalTimeFormatter localTimeFormatter = new LocalTimeFormatter(parser);
		registry.addFormatter(dateFormatter);
		registry.addFormatter(localDateFormatter);
		registry.addFormatter(localDateTimeFormatter);
		registry.addFormatter(localTimeFormatter);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(signInterceptor());
	}

	@Bean
	public SignInterceptor signInterceptor() {
		return new SignInterceptor();
	}
}
