package com.fxg.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * Web 相关配置
 */
@EnableSwagger2
@Configuration("default-fxg-web-config")
public class WebConfig implements WebMvcConfigurer {


	@Bean
	public StringHttpMessageConverter stringConverter() {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
		return stringConverter;
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
