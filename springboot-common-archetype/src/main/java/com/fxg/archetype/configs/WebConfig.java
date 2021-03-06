package com.fxg.archetype.configs;

import com.fxg.archetype.formatter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/**
 * Web 相关配置
 */
@EnableSwagger2
@Configuration("default-fxg-web-config")
public class WebConfig implements WebMvcConfigurer {

	/**
	 * 配置 fastjson 为 json 转换器
	 *
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(stringConverter());
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
		converters.add(mappingJackson2HttpMessageConverter);
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

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.fxg.house"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("house-service 接口文档")
				.description("")
				.termsOfServiceUrl("")
				.version("1.0")
				.build();
	}
}
