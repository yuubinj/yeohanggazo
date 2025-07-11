package com.travel.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

public class HandlerMappingRegistry {
	// HandlerMapping이 두개 이상 존재할 수 있으므로
	private final List<HandlerMapping> handlerMappings;

	public HandlerMappingRegistry() {
		handlerMappings = new ArrayList<>();
	}

	public void addHandlerMapping(final HandlerMapping handlerMapping) throws ServletException {
		handlerMapping.initialize();
		handlerMappings.add(handlerMapping);
	}

	public Optional<Object> getHandler(final HttpServletRequest req) {
		return handlerMappings.stream().map(handlerMapping -> handlerMapping.getHandler(req))
				.filter(Objects::nonNull)
				.findFirst(); // .findAny();
	}
}
