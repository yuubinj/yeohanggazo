package com.travel.mvc.servlet;

import java.io.IOException;
import java.util.Optional;

import com.travel.mvc.AnnotationHandlerMapping;
import com.travel.mvc.HandlerAdapter;
import com.travel.mvc.HandlerAdapterRegistry;
import com.travel.mvc.HandlerExecutionHandlerAdapter;
import com.travel.mvc.HandlerMappingRegistry;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig(
	// location = "c:/temp",			// 파일을 임시로 저장할 경로(생략가능. 기본값 ""), 지정된 경로가 없으면 업로드가 안됨
	fileSizeThreshold = 1024 * 1024,	// 업로드된 파일이 임시로 서버에 저장되지 않고 메모리에서 스트림으로 바로 전달되는 크기
	maxFileSize = 1024 * 1024 * 5,		// 업로드된 하나의 파일 크기. 기본 용량 제한 없음
	maxRequestSize = 1024 * 1024 * 10	// 폼 전체 용량
)
@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final HandlerMappingRegistry handlerMappingRegistry;
	private final HandlerAdapterRegistry handlerAdapters;
	
	private static final String BASE_PACKAGE = "com.travel.controller";

	public DispatcherServlet() {
		this.handlerMappingRegistry = new HandlerMappingRegistry();
        this.handlerAdapters = new HandlerAdapterRegistry();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		handlerMappingRegistry.addHandlerMapping(new AnnotationHandlerMapping(BASE_PACKAGE));
		handlerAdapters.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		handleRequest(req, resp);
	}

	protected void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// System.out.printf("Method : %s, Request URI : %s\n", req.getMethod(), req.getRequestURI());
			
		final Optional<Object> handler = handlerMappingRegistry.getHandler(req);
        if (handler.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
        	final HandlerAdapter handlerAdapter = handlerAdapters.getHandlerAdapter(handler.get());
            handlerAdapter.handle(req, resp, handler.get());
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
	}

	@Override
	public void destroy() {
	}
}
