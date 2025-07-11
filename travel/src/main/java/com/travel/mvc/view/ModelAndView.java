package com.travel.mvc.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ModelAndView {
	private final View view;
	private final Map<String, Object> model;

	public ModelAndView(final String viewName) {
		this.view = new JspView(viewName);
		this.model = new HashMap<>();
	}
	
	public ModelAndView(final View view) {
		this.view = view;
		this.model = new HashMap<>();
	}

	public ModelAndView(final String viewName, final Map<String, Object> model) {
		this.view = new JspView(viewName);
		this.model = model;
	}
	
	public ModelAndView addObject(final String attributeName, final Object attributeValue) {
		model.put(attributeName, attributeValue);
		return this;
	}

	public void renderView(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
		view.render(model, req, resp);
	}

	public Object getObject(final String attributeName) {
		return model.get(attributeName);
	}

	public Map<String, Object> getModel() {
		return Collections.unmodifiableMap(model); 
			// read-only 한 객체를 만든다.
			// 파라미터로 전달 받은 컬렉션 객체에 어떠한 변경이라도 발생하면 예외 발생
	}

	public View getView() {
		return view;
	}
}
