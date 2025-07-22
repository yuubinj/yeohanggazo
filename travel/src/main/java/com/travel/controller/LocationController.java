package com.travel.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import com.travel.dao.ScrapDAO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LocationController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	/*
	 * @RequestMapping(value = "/location/main", method = RequestMethod.GET) public
	 * ModelAndView showThemePage(HttpServletRequest req, HttpServletResponse resp)
	 * throws ServletException, IOException {
	 * 
	 * return new ModelAndView("location/main"); }
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
	
	// 특산물 탭 불러올 때 사용 - 농촌진흥청 api(서울, 세종, 제주 제외)
	@RequestMapping(value = "/location/specialtyView", method = RequestMethod.GET)
	public void showSpecialtyTab(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 프론트엔드에서 보낸 파라미터 받기(isTourApi 값으로 어떤 api를 호출할 지 분리)
		String isTourApi = req.getParameter("isTourApi");
		boolean useTourApi = "true".equalsIgnoreCase(isTourApi);
		
		if(useTourApi) {
			String contentTypeId = req.getParameter("contentTypeId");
	        callTourApi(req, resp, contentTypeId);
		} else {
			callNongsaroApi(req, resp);
		}
		
	}
	
	// 국문관광정보 API 호출 및 JSON 응답 처리
	public void callTourApi(HttpServletRequest req, HttpServletResponse resp, String contentTypeId)
			throws ServletException, IOException {

		String serviceKey = "WCeKhkV7DfWYcRlxKIqm8I6d7c%2BD0CwC8vJ7B%2Fh7J4RSGz4zihS23dOZ3u3nb%2FsPzC563o%2FiLbqGCCZx%2FWe9tQ%3D%3D";
        String areaCode = req.getParameter("areaCode");
        String keyword = req.getParameter("keyword");
        String pageNo = req.getParameter("pageNo");
        
        if(pageNo == null || pageNo.isEmpty()) {
        	pageNo = "1";
        }
        
        
        StringBuilder ub;
        
        if(keyword != null && !keyword.trim().isEmpty()) {
        	ub = new StringBuilder("https://apis.data.go.kr/B551011/KorService2/searchKeyword2");
        	ub.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        	ub.append("&" + URLEncoder.encode("keyword", "UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8"));
        } else {
        	ub = new StringBuilder("https://apis.data.go.kr/B551011/KorService2/areaBasedList2");
        	ub.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        }
        
        ub.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "12");
        ub.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + pageNo);
        ub.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + "ETC");
        ub.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + "TravelApp");
        ub.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + "json");
        ub.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "=" + URLEncoder.encode(contentTypeId, "UTF-8"));
        ub.append("&" + URLEncoder.encode("arrange", "UTF-8") + "=" + "A");
        
        if (areaCode != null && !areaCode.isEmpty()) {
        	ub.append("&" + URLEncoder.encode("areaCode", "UTF-8") + "=" + areaCode);
        }
        
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		StringBuilder sb = new StringBuilder();
		
		try {
        
	        URL url = new URL(ub.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");     
	        conn.setConnectTimeout(5000); // 5초 타임아웃
			conn.setReadTimeout(5000);
	
			int responseCode = conn.getResponseCode();
	
			// 4. API 서버로부터 응답 읽기
			if (responseCode >= 200 && responseCode <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
			}
	
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (rd != null) {
			try {
				rd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			conn.disconnect();
		}
	}

		String jsonResponse = sb.toString();
	        
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(jsonResponse);
		out.flush();
        
	}       
        
        
	public void callNongsaroApi(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {    
        
        String keyword = req.getParameter("keyword");
        String areaName = req.getParameter("areaName");
        
        if (keyword == null) {
        	keyword = "";
        }
        
        String pageNo = req.getParameter("pageNo");
        
        if(pageNo == null || pageNo.isEmpty()) {
        	pageNo = "1";
        }
		
		// 농촌진흥청 API 키와 URL 설정
		String apiKey = "202507097AINBT1BAKZAUVC4VNGDEG";
		StringBuilder ub = new StringBuilder("http://api.nongsaro.go.kr/service/localSpcprd/localSpcprdLst");
		ub.append("?" + URLEncoder.encode("apiKey", "UTF-8") + "=" + apiKey);
		
	    if (keyword != null && !keyword.trim().isEmpty()) {
	    	ub.append("&" + URLEncoder.encode("sText", "UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8"));
	    }
	    
	    if (areaName != null && !areaName.trim().isEmpty()) {
	    	ub.append("&" + URLEncoder.encode("sAreaNm", "UTF-8") + "=" + URLEncoder.encode(areaName, "UTF-8"));
	    }
	    
	    ub.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + pageNo);
        ub.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "12");

		
		String apiUrl = ub.toString();

		HttpURLConnection conn = null;
		BufferedReader rd = null;
		StringBuilder sb = new StringBuilder();

		try {
			// API 서버에 HTTP 요청 보내기
			URL url = new URL(apiUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000); // 5초 타임아웃
			conn.setReadTimeout(5000);

			int responseCode = conn.getResponseCode();

			//  API 서버로부터 응답 읽기
			if (responseCode >= 200 && responseCode <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
			}

			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		String responseXml = sb.toString();

		// 응답을 프론트엔드로 전달
		resp.setContentType("application/xml; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(responseXml);
		out.flush();
	}
	
	// 음식점 탭 불러올 때 사용 
	@RequestMapping(value = "/location/restaurantView", method = RequestMethod.GET)
	public void showRestaurantTab(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
		
		String contentTypeId = req.getParameter("contentTypeId");
		callTourApi(req, resp, contentTypeId);
	}
}
