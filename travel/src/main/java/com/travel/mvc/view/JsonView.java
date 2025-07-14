package com.travel.mvc.view;

import java.util.Map;

import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsonView implements View {
	private final static String APPLICATION_JSON = "application/json";
	// private final static String APPLICATION_JSON = "application/json; charset=UTF-8"; 
			// 대부분의 브라우저가 charset=UTF-8를 생략해도 UTF-8로 처리 
	public JsonView() {
	}

	@Override
	public void render(final Map<String, ?> model, final HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		resp.setContentType(APPLICATION_JSON);
		final String body = getBody(model);
		resp.getWriter().write(body);
	}

	private String getBody(final Map<String, ?> model) {
		JSONObject objectMapper = convertMapToJson(model);
		
		return objectMapper.toString();
	}

	private JSONObject convertMapToJson(final Map<String, ?> model) {
	    JSONObject json = new JSONObject();
	    
	    for(Map.Entry<String, ?> entry : model.entrySet()) {
	        json.put(entry.getKey(), entry.getValue());
	    }
	    
	    return json;
	}
}
