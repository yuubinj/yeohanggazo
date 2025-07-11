package com.travel.mvc;

import java.util.Objects;

import com.travel.mvc.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;

public class HandlerKey {
	private final String url;
	private final RequestMethod requestMethod;

	public HandlerKey(final HttpServletRequest req) {
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		uri = uri.substring(cp.length());

		this.url = uri;
		// this.url = request.getRequestURI();
		this.requestMethod = RequestMethod.valueOf(req.getMethod()); // 지정된 이름의 열거형 반환
	}

	public HandlerKey(final String url, final RequestMethod requestMethod) {
		this.url = url;
		this.requestMethod = requestMethod;
	}

	@Override
	public String toString() {
		return "[url=" + url + ", method=" + requestMethod + "]";
	}

	@Override
	public int hashCode() {
		// hashCode() 함수가 존재해야 검색 가능
		return Objects.hash(url, requestMethod);
	}

	@Override
	public boolean equals(Object obj) {
		HandlerKey other = (HandlerKey) obj;

		if (obj == null || getClass() != obj.getClass() || requestMethod != other.requestMethod) {
			return false;
		}

		if ((url == null && other.url != null) || !url.equals(other.url)) {
			return false;
		}

		return true;
	}
}
