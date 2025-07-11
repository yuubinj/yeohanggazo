package com.travel.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

public class AnnotationHandlerMapping implements HandlerMapping {
	private final String[] basePackages;
	private final Map<HandlerKey, HandlerExecution> handlerExecutions;

	public AnnotationHandlerMapping(final String... basePackages) {
		this.basePackages = basePackages;
		this.handlerExecutions = new HashMap<>();
	}

	@Override
	public void initialize() throws ServletException {
		initHandlerExecution(basePackages);
	}

	@Override
	public HandlerExecution getHandler(final HttpServletRequest req) {
		HandlerKey handlerKey = new HandlerKey(req);

		return handlerExecutions.get(handlerKey);
	}

	private void initHandlerExecution(final String[] basePackage) throws ServletException {
		final ControllerScanner controllerScanner = new ControllerScanner();
		Map<Class<?>, Object> controllers = controllerScanner.getControllers(basePackage);

		// List<Method> methods = extractMethods(controllers);

		try {
			for (Class<?> controller : controllers.keySet()) {
				final List<Method> methods = getRequestMappingMethods(controller);
				putSupportedMethodInHandlerExecution(methods, controllers.get(controller));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	private List<Method> extractMethods(final Map<Class<?>, Object> controllers) {
		// 모든 Controller의 모든 메소드
 
		// - 스트림의 flatMap(Function<? super T,? extends Stream<? extends R>> mapper)
		//    : Stream에 있는 어떤 값에 mapper function이 적용될 때 결과가 다른 단일 값이 아닌 스트림(복수개의 값)을 리턴 
		//    : 각각의 값들이 모두 실행되면 여러개의 스트림이 생기며, 이를 하나로 합쳐서 결과로 반환
		//    : Array나 Object로 감싸져 있는 모든 원소를 단일 원소 스트림으로 반환
		// - Stream의 collect(Collector<T, A, R> collector)
		//    : 필터링, 매핑된 요소들을 새로운 컬렉션에 수집해서 리턴

		return controllers.keySet().stream()
				.flatMap(it -> Arrays.stream(it.getMethods()))
				.filter(it -> it.isAnnotationPresent(RequestMapping.class))
				.collect(Collectors.toList());
	}
	*/
	
	private List<Method> getRequestMappingMethods(final Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredMethods())
				.filter(it -> it.isAnnotationPresent(RequestMapping.class))
				.collect(Collectors.toList());
	}

	private void putSupportedMethodInHandlerExecution(final List<Method> methods, final Object handler)
			throws ServletException {
		try {
			for (Method method : methods) {
				putHandlerExecution(method, handler);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void putHandlerExecution(final Method method, final Object handler) throws ServletException {
		final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		final List<HandlerKey> handlerKeys = getHandlerKeys(requestMapping);

		for (HandlerKey handlerKey : handlerKeys) {
			// 동일한 uri가 존재하는 경우 예외를 던짐
			if (handlerExecutions.containsKey(handlerKey)) {
				throw new ServletException(handlerKey.toString() + " : URI 가 중복되었습니다.");
			}

			handlerExecutions.put(handlerKey, new HandlerExecution(handler, method));
		}
	}

	private List<HandlerKey> getHandlerKeys(final RequestMapping requestMapping) {
		if (requestMapping != null) {
			String url = requestMapping.value();
			RequestMethod[] requestMethods = requestMapping.method();
			if (requestMethods.length == 0) {
				requestMethods = new RequestMethod[] { RequestMethod.GET, RequestMethod.POST };
			}

			// Stream의 map(Function<? super T,? extends R> mapper)
			// : Stream에 있는 값들을 하나 하나 다른 값으로 매핑한 후, 결과를 스트림으로 리턴
			return Arrays.stream(requestMethods)
					.map(method -> new HandlerKey(url, method))
					.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

}
