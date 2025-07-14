package com.travel.mvc;

import java.util.ArrayList;
import java.util.List;

import com.travel.mvc.exception.NotFoundHandlerAdapterException;

public class HandlerAdapterRegistry {
	// 두개 이상의 HandlerAdapter가 존재할 수 있으므로
	private final List<HandlerAdapter> handlerAdapters;

	public HandlerAdapterRegistry() {
		this.handlerAdapters = new ArrayList<>();
	}

	public void addHandlerAdapter(final HandlerAdapter handlerAdapter) {
		handlerAdapters.add(handlerAdapter);
	}

	public HandlerAdapter getHandlerAdapter(final Object handler) throws NotFoundHandlerAdapterException {
		return handlerAdapters.stream()
				.filter(it -> it.supports(handler))
				.findFirst()
				// .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 핸들러입니다."));
				.orElseThrow(() -> new NotFoundHandlerAdapterException("핸들러 어댑터를 찾을 수 없습니다."));
	}
}
