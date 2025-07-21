package com.travel.controller;

import java.io.IOException;
import java.util.List;

import com.travel.dao.AdDAO;
import com.travel.dao.BoardDAO;
import com.travel.dao.InquiryDAO;
import com.travel.dao.MyTripDAO;
import com.travel.model.AdDTO;
import com.travel.model.BoardDTO;
import com.travel.model.FaqDTO;
import com.travel.model.InquiryDTO;
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
			List<AdDTO> listAd = adDao.listAd(0, 6);
			/*
			BoardDAO boardDao = new BoardDAO();
			List<BoardDTO> listBoard = boardDao.listBoard(0, 5);
			
			MyTripDAO myTripDao = new MyTripDAO();
			List<MyTripDTO> listmyTrip = myTripDao.listMyTrip(0, 5);
			*/
			String selectedCateCommu = req.getParameter("cateCommu");
			
			mav.addObject("selectedCateCommu", selectedCateCommu);
			
			/*
			NoticeDAO noticeDao = new NoticeDAO();
			List<NoticeDTO> listNotice = noticeDao.listNotice(0, 5);
			
			LectureDAO lectureDao = new LectureDAO();
			List<LectureDTO> listLecture = lectureDao.listLecture(); // 카테고리 상관없이 최신의 강좌 5개
			*/
			
			mav.addObject("listAd", listAd);
			//mav.addObject("listBoard", listBoard);
			//mav.addObject("listmyTrip", listmyTrip);
			// mav.addObject("listNotice", listNotice);
			// mav.addObject("listLecture", listLecture);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value="/home/communityList", method = RequestMethod.GET)
	public ModelAndView listCommu(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("home/communityList");
		
		try {
			String categoryName = req.getParameter("categoryName");
			
			if(categoryName.equals("bbs")) {
				BoardDAO boardDao = new BoardDAO();
				List<BoardDTO> listBoard = boardDao.listBoard(0, 5);
				String listUrl = "bbs/list";
				String articleUrl = "bbs/article?page=1";
				
				mav.addObject("list", listBoard);
				mav.addObject("listUrl", listUrl);
				mav.addObject("articleUrl", articleUrl);
			} else if(categoryName.equals("myTrip")) {
				MyTripDAO myTripDao = new MyTripDAO();
				List<MyTripDTO> listmyTrip = myTripDao.listMyTrip(0, 5);
				String listUrl = "myTrip/list";
				String articleUrl = "myTrip/article?page=1";
				
				mav.addObject("list", listmyTrip);
				mav.addObject("listUrl", listUrl);
				mav.addObject("articleUrl", articleUrl);
			} else if(categoryName.equals("inquiry")) {
				InquiryDAO inquiryDao = new InquiryDAO();
				List<InquiryDTO> listInquiry = inquiryDao.listInquiry(0, 0, 5);
				String listUrl = "inquiry/main";
				String articleUrl = "inquiry/article?page=1&categoryNum=0";
				
				mav.addObject("categoryName", categoryName);
				mav.addObject("list", listInquiry);
				mav.addObject("listUrl", listUrl);
				mav.addObject("articleUrl", articleUrl);
			}

			/*
			NoticeDAO noticeDao = new NoticeDAO();
			List<NoticeDTO> listNotice = noticeDao.listNotice(0, 5);
			
			LectureDAO lectureDao = new LectureDAO();
			List<LectureDTO> listLecture = lectureDao.listLecture(); // 카테고리 상관없이 최신의 강좌 5개
			 */

			// mav.addObject("listNotice", listNotice);
			// mav.addObject("listLecture", listLecture);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
}
