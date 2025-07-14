package com.travel.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HandlerExecutionHandlerAdapter implements HandlerAdapter {
	@Override
	public boolean supports(Object handler) {
		return handler instanceof HandlerExecution;
	}

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		final HandlerExecution handlerExecution = (HandlerExecution) handler;
		handlerExecution.handle(req, resp);
	}
}
