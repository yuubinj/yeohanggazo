package com.travel.mvc;

import java.lang.reflect.Method;
import java.util.Map;

import com.travel.mvc.annotation.ResponseBody;
import com.travel.mvc.view.JsonView;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HandlerExecution {
	private final Object handler;
	private final Method method;

	public HandlerExecution(final Object handler, final Method method) {
		this.handler = handler;
		this.method = method;
	}

	public void handle(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
		Class<?> cls = method.getReturnType();

		if (cls.getSimpleName().equals("void")) {
			// 리턴 타입이 void 인 경우
			method.invoke(handler, req, resp);
		} else if (cls.getSimpleName().equals("Map")) {
			// 리턴 타입이 Map 인 경우
			@SuppressWarnings("unchecked")
			Map<String, Object> model = (Map<String, Object>) method.invoke(handler, req, resp);

			if (method.isAnnotationPresent(ResponseBody.class)) {
				// JSON 반환
				JsonView jsonView = new JsonView();
				jsonView.render(model, req, resp);
			} else {
				// URI 가 jsp 이름
				String uri = req.getRequestURI();
				String cp = req.getContextPath();
				String viewName = uri.substring(cp.length() + 1);

				if (viewName.lastIndexOf(".") != -1) {
					viewName = viewName.substring(0, viewName.lastIndexOf("."));
				}
				ModelAndView modelAndView = new ModelAndView(viewName, model);
				modelAndView.renderView(req, resp);
			}
		} else if (cls.getSimpleName().equals("String")) {
			// 리턴 타입이 String 인 경우
			String viewName = (String) method.invoke(handler, req, resp);
			ModelAndView modelAndView = new ModelAndView(viewName);
			modelAndView.renderView(req, resp);
		} else {
			// 리턴 타입이 ModelAndView 인 경우
			ModelAndView modelAndView = (ModelAndView) method.invoke(handler, req, resp);
			modelAndView.renderView(req, resp);
		}
	}
}
