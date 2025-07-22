package com.travel.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ModalDetailController extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@RequestMapping(value = "/location/itemDetail", method = RequestMethod.GET)
	public void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String serviceKey = "WCeKhkV7DfWYcRlxKIqm8I6d7c%2BD0CwC8vJ7B%2Fh7J4RSGz4zihS23dOZ3u3nb%2FsPzC563o%2FiLbqGCCZx%2FWe9tQ%3D%3D";
        String contentId = req.getParameter("contentId");

        StringBuilder ub = new StringBuilder("https://apis.data.go.kr/B551011/KorService2/detailCommon2");
        ub.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        ub.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + "TravelApp");
        ub.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + "ETC");
        ub.append("&" + URLEncoder.encode("contentId", "UTF-8") + "=" + contentId);
        ub.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + "json");
        
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
		
}

