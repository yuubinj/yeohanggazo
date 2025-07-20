package com.travel.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.travel.dao.ScrapDAO;
import com.travel.model.ScrapDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ScrapController {
	
	@RequestMapping(value = "/tour/scrap", method = RequestMethod.POST)
	public void scrap(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain; charset=UTF-8");
		
		ScrapDAO dao = new ScrapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			resp.getWriter().write("not_login");
			return;
		}
		
		try {
			
			ScrapDTO dto = new ScrapDTO();
			
			dto.setUserId(info.getUserId());
			dto.setApiId(req.getParameter("apiId"));
			dto.setApiTypeId(req.getParameter("apiTypeId"));
			dto.setScrapAddr(req.getParameter("scrapAddr"));
			dto.setScrapName(req.getParameter("scrapName"));
			dto.setScrapImg(req.getParameter("scrapImg"));
			
			String locationName;
			String[] result = req.getParameter("scrapAddr").split(" ");
			if(result[0].trim().length() == 4) {
				locationName = result[0].trim().substring(0, 1) + result[0].trim().substring(2, 3);
			} else {
				locationName = result[0].trim().substring(0, 2);
			}
			dto.setLocationName(locationName);
			
			dao.insertScrap(dto);
			
			resp.getWriter().write("success");
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("error");
		}
		
	}
	
	
	@RequestMapping(value = "/tour/unscrap", method = RequestMethod.POST)
	public void unscrap(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain; charset=UTF-8");
		
		ScrapDAO dao = new ScrapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			resp.getWriter().write("not_login");
			return;
		}
		
		try {
			
			ScrapDTO dto = new ScrapDTO();
			
			dto.setUserId(info.getUserId());
			dto.setApiId(req.getParameter("apiId"));
		
			dao.deleteScrap(dto);
			
			resp.getWriter().write("success");
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("error");
		}
		
	}
	
	
	@RequestMapping(value = "/tour/apiIds", method = RequestMethod.GET)
	public void getUserScrapApiIds(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json; charset=UTF-8");
		
		List<String> scrapList = new ArrayList<String>();
		
		ScrapDAO dao = new ScrapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if(info != null) {
			try {
				String userId = info.getUserId();
				
				scrapList = dao.selectScrapApiId(userId);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String json = "[" + scrapList.stream()
			.map(id -> "\"" + id + "\"")
			.collect(Collectors.joining(",")) + "]";

		resp.getWriter().write(json);
	
	}

}
