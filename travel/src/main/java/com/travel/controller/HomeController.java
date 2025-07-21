package com.travel.controller;

import java.io.IOException;
import java.util.List;

import com.travel.dao.AdDAO;
import com.travel.dao.BoardDAO;
import com.travel.dao.MyTripDAO;
import com.travel.model.AdDTO;
import com.travel.model.BoardDTO;
import com.travel.model.MyTripDTO;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;
import com.travel.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {
	@RequestMapping("/main")
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("home/main");
		
		try {
			AdDAO adDao = new AdDAO();
			List<AdDTO> listAd = adDao.listAd(0, 5);
			
			BoardDAO boardDao = new BoardDAO();
			List<BoardDTO> listBoard = boardDao.listBoard(0, 5);
			
			MyTripDAO myTripDao = new MyTripDAO();
			List<MyTripDTO> listmyTrip = myTripDao.listMyTrip(0, 5);
			
			/*
			NoticeDAO noticeDao = new NoticeDAO();
			List<NoticeDTO> listNotice = noticeDao.listNotice(0, 5);
			
			LectureDAO lectureDao = new LectureDAO();
			List<LectureDTO> listLecture = lectureDao.listLecture(); // 카테고리 상관없이 최신의 강좌 5개
			*/
			
			mav.addObject("listAd", listAd);
			mav.addObject("listBoard", listBoard);
			mav.addObject("listmyTrip", listmyTrip);
			// mav.addObject("listNotice", listNotice);
			// mav.addObject("listLecture", listLecture);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
}
