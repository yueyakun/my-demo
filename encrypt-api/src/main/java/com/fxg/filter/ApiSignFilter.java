package com.fxg.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ApiSignFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ServletRequest requestWrapper = null;
		if(request instanceof HttpServletRequest) {
			requestWrapper = new RequestWrapper((HttpServletRequest) request);
		}
		//获取请求中的流如何，将取出来的字符串，再次转换成流，然后把它放入到新request对象中。
		// 在chain.doFiler方法中传递新的request对象
		if(requestWrapper == null) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(requestWrapper, response);
		}
	}

	@Override
	public void destroy() {

	}
}
