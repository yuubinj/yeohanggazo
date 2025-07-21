package com.travel.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.ScrapDAO;
import com.travel.model.ScrapDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.annotation.ResponseBody;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class mypageScrapController {
	@RequestMapping(value = "/myPage/scrap/main", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("myPage/scrap/main");
		
		return mav;
	}

	@RequestMapping(value = "/myPage/scrap/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("myPage/scrap/list");
	    
	    ScrapDAO dao = new ScrapDAO();
	    
	    try {
	        int size = 9;
	        String pageNo = req.getParameter("pageNo");
	        int current_page = 1;
	        if(pageNo != null) {
	            current_page = Integer.parseInt(pageNo);
	        }
	        
	        int offset = (current_page - 1) * size;
	        if(offset < 0) offset = 0;
	        
	        List<ScrapDTO> list = dao.mypageListScrap(offset, size);
	        mav.addObject("list", list);
	        
	    } catch (Exception e) {
	        resp.sendError(406);
	        throw e;
	    }
	    
	    return mav;
	}

	
	@ResponseBody
	@RequestMapping(value = "/myPage/scrap/delete", method = RequestMethod.POST)
	public Map<String, Object> deletescrap(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		ScrapDAO dao = null;
		String state = "false";
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			
			String userId = (info.getUserId());
			String apiTypeId = (req.getParameter("apiTypeId"));
			
			if (apiTypeId == null || apiTypeId.trim().isEmpty()) {
		        model.put("state", state);
		        model.put("message", "삭제할 스크랩 정보가 없습니다.");
		        return model;
		    }
			dao =  new ScrapDAO();
			dao.mypageDeleteScrap(userId, apiTypeId);
			
			state = "true";
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}
	
	
	
}
