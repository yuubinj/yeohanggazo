package com.travel.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
	// 핸들러 지원 여부
	public boolean supports(Object handler);
	public void handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception;
}
