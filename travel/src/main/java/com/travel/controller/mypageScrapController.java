package com.travel.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.travel.dao.ScrapDAO;
import com.travel.model.ScrapDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
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
	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo)session.getAttribute("member"); 
	    try {
	        int size = 9;
	        String pageNo = req.getParameter("pageNo");
	        int current_page = 1;
	        if(pageNo != null) {
	            current_page = Integer.parseInt(pageNo);
	        }
	        
	        int offset = (current_page - 1) * size;
	        if(offset < 0) offset = 0;
	        
	        List<ScrapDTO> list = dao.mypageListScrap(offset, size, info.getUserId());
	        int total_count = dao.dataCount(info.getUserId());
	        
	        mav.addObject("total_count", total_count);
	        mav.addObject("size", size);
	        mav.addObject("list", list);
	        
	        
	    } catch (Exception e) {
	        resp.sendError(406);
	        throw e;
	    }
	    
	    return mav;
	}

	
	@RequestMapping(value = "/myPage/scrap/details", method = RequestMethod.GET)
	public void apiScrapDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    // 1. 요청 파라미터 받기
	    String contentId = req.getParameter("contentId");

	    // 2. 외부 API URL 조립 (반드시 http:// 붙이기)
	    String apiUrl = "http://apis.data.go.kr/B551011/KorService2/detailCommon2"
	        + "?serviceKey=vZe%2B%2F38iaAWk%2FCcax8T8D8GkUu0fsUpUfbqcKsm6%2BuRPCCFffelICypoPk9hpC2zaFLYI9V6ve%2FiT85gNC2ckQ%3D%3D"
	        + "&MobileOS=ETC"
	        + "&MobileApp=AppTest"
	        + "&_type=json"
	        + "&contentId=" + contentId;

	    try {
	        // 3. URL 객체 생성 및 연결
	        URL url = new URL(apiUrl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");

	        int responseCode = conn.getResponseCode();
	        BufferedReader br;

	        if (responseCode == 200) {
	            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        } else {
	            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }

	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            sb.append(line);
	        }
	        br.close();

	        // 4. JSON 결과를 응답에 바로 출력
	        resp.setContentType("application/json;charset=UTF-8");
	        PrintWriter out = resp.getWriter();
	        out.print(sb.toString());
	        out.flush();

	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "잘못된 API 주소");
	    } catch (IOException e) {
	        e.printStackTrace();
	        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "API 호출 실패");
	    }
	}
}
