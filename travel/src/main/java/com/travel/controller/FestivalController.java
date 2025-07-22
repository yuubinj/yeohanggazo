package com.travel.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller

public class FestivalController {

    //  공공데이터 서비스 키
    private static final String SERVICE_KEY ="서비스키 넣기"; // 개인서비스키 입력

    // 축제 목록 API (전국 + 지역)
    @RequestMapping(value = "/festival/list", method = RequestMethod.GET)
    public void festivalList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String areaCode = req.getParameter("areaCode");
            if (areaCode == null) areaCode = "";

            String pageNoStr = req.getParameter("pageNo");
            int pageNo = (pageNoStr != null && !pageNoStr.isEmpty())
                    ? Integer.parseInt(pageNoStr)
                    : 1;

            String numOfRowsStr = req.getParameter("numOfRows");
            int numOfRows = (numOfRowsStr != null && !numOfRowsStr.isEmpty())
                    ? Integer.parseInt(numOfRowsStr)
                    : 10;

            // 2000년 이후 전체 축제 포함
            String startDate = "20000101";

            StringBuilder urlBuilder =
                    new StringBuilder("https://apis.data.go.kr/B551011/KorService2/searchFestival2");

            urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
            urlBuilder.append("&numOfRows=").append(numOfRows);
            urlBuilder.append("&pageNo=").append(pageNo);
            urlBuilder.append("&MobileOS=ETC&MobileApp=AppTest&_type=json");

            if (!areaCode.isEmpty()) {
                urlBuilder.append("&areaCode=").append(URLEncoder.encode(areaCode, StandardCharsets.UTF_8));
            }

            urlBuilder.append("&eventStartDate=").append(startDate);

            System.out.println("API 요청 URL(list): " + urlBuilder);

            String apiResponse = callExternalApi(urlBuilder.toString());
            out.print(apiResponse);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"축제 데이터를 불러오는 중 오류 발생\", \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            if (out != null) out.close();
        }
    }

     // 축제 상세 API
    @RequestMapping(value = "/festival/detail", method = RequestMethod.GET)
    public void festivalDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String contentId = req.getParameter("contentId");
            if (contentId == null || contentId.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"contentId 파라미터 누락\"}");
                return;
            }

            // 목록에서 내려온 contentTypeId 그대로 사용
            String contentTypeId = req.getParameter("contentTypeId");
            if (contentTypeId == null || contentTypeId.isEmpty()) {
                contentTypeId = "15"; // 기본값: 축제
            }

            StringBuilder urlBuilder =
                    new StringBuilder("https://apis.data.go.kr/B551011/KorService2/detailCommon2");

            urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
            urlBuilder.append("&MobileOS=ETC&MobileApp=festivalApp&_type=json");
            urlBuilder.append("&contentId=").append(URLEncoder.encode(contentId, StandardCharsets.UTF_8));
            urlBuilder.append("&contentTypeId=").append(URLEncoder.encode(contentTypeId, StandardCharsets.UTF_8));

            // 추가 정보 포함
            urlBuilder.append("&defaultYN=Y&firstImageYN=Y&areacodeYN=Y");
            urlBuilder.append("&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y");

            System.out.println("API 요청 URL(detail): " + urlBuilder);

            String apiResponse = callExternalApi(urlBuilder.toString());
            out.print(apiResponse);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"축제 상세 조회 실패\",\"message\":\"" + e.getMessage() + "\"}");
        } finally {
            if (out != null) out.close();
        }
    }

    //API 호출 공통 메서드
    private String callExternalApi(String apiUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd;
        int code = conn.getResponseCode();
        System.out.println("응답코드=" + code);

        rd = new BufferedReader(new InputStreamReader(
                code >= 200 && code <= 300 ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) result.append(line);
        rd.close();
        conn.disconnect();

        return result.toString();
    }

    
     //JSP 페이지 이동
    @RequestMapping(value = "/festival/festivalMain", method = RequestMethod.GET)
    public ModelAndView showFestivalTab(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        return new ModelAndView("festival/festivalMain");
    }
}
