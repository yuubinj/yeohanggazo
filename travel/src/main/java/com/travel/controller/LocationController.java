package com.travel.controller;

import java.io.IOException;

import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LocationController {

	/*
	@RequestMapping(value = "/location/main", method = RequestMethod.GET)
	public ModelAndView showThemePage(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		return new ModelAndView("location/main");
	}
	*/
	
	// 지역 메인 페이지 (전체 탭 포함된 뷰)
	 @RequestMapping(value = "/location/main", method = RequestMethod.GET)
	    public ModelAndView showLocationMain(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {
	        return new ModelAndView("location/main");
	    }

	    // 축제 탭 콘텐츠만 Ajax로 불러올 때 사용 (부분 뷰)
	    @RequestMapping(value = "/location/festivalView", method = RequestMethod.GET)
	    public ModelAndView showFestivalTab(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {
	        return new ModelAndView("location/festival");
	    }
	}