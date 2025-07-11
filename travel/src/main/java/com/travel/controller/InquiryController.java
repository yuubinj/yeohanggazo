package com.travel.controller;

import java.io.IOException;
import java.util.List;

import com.travel.dao.InquiryDAO;
import com.travel.model.FaqDTO;
import com.travel.model.InquiryDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class InquiryController {
	
	@RequestMapping(value = "/inquiry/main", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("inquiry/main");
		
		InquiryDAO dao = new InquiryDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(1);
			
			mav.addObject("listCategory", listCategory);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	/*
	// 문의 리스트
	@RequestMapping(value = "/inquiry/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 글쓰기 폼
	@RequestMapping(value = "/inquiry/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("inquiry/write");
		mav.addObject("mode", "write");
		
		return mav;
	}
	
	// 글 저장
	@RequestMapping(value = "/inquiry/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InquiryDAO dao = new InquiryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			InquiryDTO dto = new InquiryDTO();
			
			dto.setUserId(info.getUserId());
			
			dto.setSecret(Integer.parseInt(req.getParameter("secret")));
			dto.setCategoryNum(Long.parseLong(req.getParameter("categoryNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setQuestion(req.getParameter("question"));
			
			dao.insertQuestion(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/inquiry/list");
	}
	
	// 글보기
	@RequestMapping(value = "/inquiry/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 글 수정 폼
	@RequestMapping(value = "/inquiry/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 글 수정 완료
	@RequestMapping(value = "/inquiry/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 글 삭제
	@RequestMapping(value = "/inquiry/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	*/
}
