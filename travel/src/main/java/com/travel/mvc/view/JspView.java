package com.travel.mvc.view;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JspView implements View {
	private static final String REDIRECT_PREFIX = "redirect:";
	private static final String FORWARD_PREFIX = "/WEB-INF/views/";
	private static final String FORWARD_SUFFIX = ".jsp";

	private final String viewName;

	public JspView(final String viewName) {
		this.viewName = viewName;
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (viewName.startsWith(REDIRECT_PREFIX)) {
        	String cp = req.getContextPath();
        	String uri = cp + viewName.substring(REDIRECT_PREFIX.length());
        	resp.sendRedirect(uri);
        	
            return;
        }
        
        model.keySet().forEach(key -> {
        	req.setAttribute(key, model.get(key));
        });

        final var requestDispatcher = req.getRequestDispatcher(FORWARD_PREFIX + viewName + FORWARD_SUFFIX);
        requestDispatcher.forward(req, resp);
	}
}
