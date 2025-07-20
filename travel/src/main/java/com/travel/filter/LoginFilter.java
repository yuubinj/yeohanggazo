package com.travel.filter;

import java.io.IOException;

import com.travel.model.SessionInfo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 - Filter
   : 요청(request)과 응답(response)를 가로채어 전후처리를 할 수 있는
     재사용 가능한 컴포넌트
   : 주로 공통 기능을 처리하기 위해 사용
   : 요청(request) 전 - 인증, 권한검사, 인코딩, 로깅 등
   : 응답(response) 전 - 응답압축, 응답내용변경, 보안헤더추가 등
 */
@WebFilter("/*")
public class LoginFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// 필터클래스가 객체를 생성할 때 한번 실행
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 클라이언트가 요청이 있을 때마다 실행
		
		// request 필터
		
		// 로그인 체크
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		HttpSession session = req.getSession();
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		// 세션에 저장된 로그인 정보
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		// 로그인이 되어 있지 않은 경우
		if(info == null && isExcludeUri(req) == false) {
			if(isAjaxRequest(req)) {
				// Ajax 요청이면서 로그인이 되어 있지 않으면 403 에러 코드를 던짐
				resp.sendError(403);
			} else {
				// 로그인 전 주소가 존재하는 경우 로그인 전 주소를 세션에 저장하여
				//  로그인 후 이동
				
				// uri 에서 ContextPath 제거
				if(uri.indexOf(req.getContextPath()) == 0) {
					uri = uri.substring(req.getContextPath().length());
				}
				uri = "redirect:" + uri;
				
				String qs = req.getQueryString();
				if(qs != null) {
					uri += "?" + qs;
				}
				session.setAttribute("preLoginURI", uri);
				
				// 로그인 페이지로 리다이렉트				
				resp.sendRedirect(cp + "/member/login");
			}
			
			return;
		} else if(info != null && uri.indexOf("admin") != -1) {
			// 로그인 되어 있는 상태에서 관리자 메뉴를 접근하는 경우
			if(info.getUserLevel() < 51) {
				resp.sendRedirect(cp + "/member/noAuthorized");
				return;
			}
		}
		
		// 다음 필터 또는 필터의 마지막이면 end-pointer(서블릿 등)를 실행
		// 필터가 일을 하고 서블릿에 넘어감
		chain.doFilter(request, response);
		
		// response 필터
		
	}
	
	@Override
	public void destroy() {
		// 필터 클래스의 객체가 파괴되기 직전 한번 실행
		
	}
	
	// 요청이 AJAX인지를 확인하는 메소드
	private boolean isAjaxRequest(HttpServletRequest req) {
		// AJAX 요청인 경우 헤더에 AJAX 라는 이름에 true 를 실어서 보내도록 구현
		
		String h = req.getHeader("AJAX");
		
		return h != null && h.equals("true");
	}
	
	// 로그인 체크가 필요한지 않은지의 여부 판단
	private boolean isExcludeUri(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		uri = uri.substring(cp.length());
		
		// 로그인이 체크가 필요없는 uri
		String[] uris = {
			"/index.jsp", "/main",
			"/member/login", "/member/logout",
			"/member/account", "/member/userIdCheck", "/member/complete",
			"/notice/list", "/bbs/list", "/bbs/article", "/bbs/listReply","/bbs/listReplyAnswer", "/bbs/countReplyAnswer",
			"/uploads/photo/**",
			"/uploads/myTrip/**",
			"/dist/**", "/inquiry/main", "/inquiry/list", "/inquiry/article",
			"/inquiry/listAllCategory", "/faq/main", "/faq/list", "/myTrip/**",
			 "/theme/main", "/location/main", "/location/weatherXML"
		};
		
		if(uri.length() <= 1) {
			return true;
		}
		
		for(String s : uris) {
			if(s.lastIndexOf("**") != -1) {
				s = s.substring(0, s.lastIndexOf("**"));
				if(uri.indexOf(s) == 0) {
					return true;
				}
			} else if(uri.equals(s)) {
				return true;
			}
		}
		
		return false;
	}
}
