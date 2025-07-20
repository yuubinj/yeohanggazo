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
public class ThemeController {
	
	@RequestMapping(value = "/theme/main", method = RequestMethod.GET)
	public ModelAndView showThemePage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		return new ModelAndView("theme/main");
	}

}
