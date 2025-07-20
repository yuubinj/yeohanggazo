package com.travel.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.travel.util.APISerializer;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class WeatherController {
	
	@RequestMapping(value = "/location/weatherXML", method = RequestMethod.GET)
	public void handleWeatherXML(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String result = null;
		APISerializer serializer = new APISerializer();
		
		String spec = "http://www.kma.go.kr/XML/weather/sfc_web_map.xml";
		
		try {
			result = serializer.receiveToString(spec);
			
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print(result);
			
		} catch (Exception e) {
		}
		
	}

}
