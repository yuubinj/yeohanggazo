package com.travel.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class WebAppInit implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 서버가 시작되는 시점에 호출
		
		/*
		  - DefaultServlet
		    : 정적인 리소스(*.css, *.js 등)를 처리하며, /로 매핑되어 있다.
		    : 작성하는 서블릿을 / 로 매핑하면 모든 URL 요청이 서블릿으로 처리 되며
		       *.js나 *.css파일에 대한 처리를 하는 핸들러가 없기 때문에 404 에러가 발생한다.
		  - ServletRegistration getServletRegistration(servletName)
		    : 서블릿의 세부정보를 반환
		  - Set<String> addMapping(String... urlPatterns)
		    : 서블릿에 대해 지정된 URL 패턴을 사용하여 서블릿 매핑을 추가
		 */
		// resource 관련 uri는 default 서블릿으로 위임
		String[] uris = new String[]{"/dist/*", "/uploads/*", "*.css", "*.js", "*.html"};
		sce.getServletContext().getServletRegistration("default").addMapping(uris);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// 서버가 종료되기 직전에 호출
	}
}
