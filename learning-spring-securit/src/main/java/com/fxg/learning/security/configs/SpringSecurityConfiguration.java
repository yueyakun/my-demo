package com.fxg.learning.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fxg.learning.security.api.HttpResult;
import com.fxg.learning.security.api.HttpStatus;
import com.fxg.learning.security.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	UserService userService;
	@Autowired
	private ObjectMapper objectMapper;

	@Override

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//		auth.userDetailsService(userService).passwordEncoder(new PasswordEncoder() {
		//			@Override
		//			public String encode(CharSequence charSequence) {
		//				return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
		//			}
		//
		//			/**
		//			 * @param charSequence 明文
		//			 * @param s 密文
		//			 * @return
		//			 */
		//			@Override
		//			public boolean matches(CharSequence charSequence, String s) {
		//				return s.equals(DigestUtils.md5DigestAsHex(charSequence.toString().getBytes()));
		//			}
		//		});
		auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//其他的路径都是登录后即可访问
		http.authorizeRequests()
				// swagger start
				.antMatchers("/configuration/ui")
				.permitAll()
				.antMatchers("/configuration/security")
				.permitAll()
				// Actuator 访问端点信息必须有admin权限
				//				.requestMatchers(EndpointRequest.toAnyEndpoint())
				//				.hasRole("admin")
				// 项目业务接口权限配置
				.antMatchers("/auth/admin/**")
				.hasRole("admin")
				.antMatchers("/auth/user/**")
				.hasRole("user")
				.anyRequest()
				.permitAll()
				.and()
				.formLogin()
				.successHandler((httpServletRequest, httpServletResponse, authentication) -> {
					String value = objectMapper.writeValueAsString(authentication.getPrincipal());
					String token = tokenFactory.getOperator().add(value);
					httpServletResponse.setContentType("application/json;charset=utf-8");
					PrintWriter out = httpServletResponse.getWriter();
					out.write(objectMapper.writeValueAsString(new HttpResult<>(HttpStatus.OK, "登录成功", token)));
					out.flush();
					out.close();
				})
				.failureHandler((httpServletRequest, httpServletResponse, e) -> {
					httpServletResponse.setContentType("application/json;charset=utf-8");
					httpServletResponse.setStatus(Response.SC_FORBIDDEN);
					PrintWriter out = httpServletResponse.getWriter();
					out.write(objectMapper.writeValueAsString(new HttpResult<>(HttpStatus.AUTH_FAIL, "登录失败")));
					out.flush();
					out.close();
				})
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll()
				.and()
				.logout()
				//				.logoutUrl("/logout")//这个是get请求，若要指定为post请求需使用logoutRequestMatcher,两个设置一个即可
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
				.deleteCookies()//删除cookies
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.logoutSuccessHandler((HttpServletRequest req, HttpServletResponse res, Authentication authentication) -> {
					String token = AuthFilter.getToken(req);
					if (!StringUtils.isEmpty(token)) {
						tokenFactory.getOperator().invalidate(token);
					}
					res.setContentType("application/json;charset=utf-8");
					PrintWriter out = res.getWriter();
					out.write(objectMapper.writeValueAsString(new HttpResult<>(HttpStatus.OK, "登出成功")));
					out.flush();
					out.close();
				})
				.permitAll()
				.and()
				.csrf()//添加 CSRF 支持
				.disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 关闭Session
				.and()
				.exceptionHandling()
				// 没有会话
				.authenticationEntryPoint((HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
					response.setContentType("application/json;charset=utf-8");
					PrintWriter out = response.getWriter();
					out.write(objectMapper.writeValueAsString(new HttpResult<>(HttpStatus.UN_LOGIN, "会话失效")));
					out.flush();
					out.close();
				})
				// 无权访问
				.accessDeniedHandler((HttpServletRequest httpServletRequest, HttpServletResponse resp, AccessDeniedException e) -> {
					resp.setContentType("application/json;charset=utf-8");
					PrintWriter out = resp.getWriter();
					out.write(objectMapper.writeValueAsString(new HttpResult<>(HttpStatus.AUTH_FAIL, "权限不足")));
					out.flush();
					out.close();
				});
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/blogimg/**", "/index.html", "/static/**");
	}
}
