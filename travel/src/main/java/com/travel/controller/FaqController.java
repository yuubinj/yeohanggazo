package com.travel.controller;

import java.io.IOException;
import java.util.List;

import com.travel.dao.FaqDAO;
import com.travel.model.FaqDTO;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.view.ModelAndView;
import com.travel.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FaqController {
	@RequestMapping(value = "/faq/main", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("faq/main");
		
		FaqDAO dao = new FaqDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(1);
			
			mav.addObject("listCategory", listCategory);
			
		} catch (Exception e) {
		}
		
		return mav;
	}

	// AJAX-Text
	@RequestMapping(value = "/faq/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("faq/list");
		
		FaqDAO dao = new FaqDAO();
		
		MyUtil util = new MyUtil();
		
		try {
			int size = 5;
			int total_page = 0;
			int dataCount = 0;
			
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}
			
			long categoryNum = 0;
			String strCategoryNum = req.getParameter("categoryNum");
			if(strCategoryNum != null) {
				categoryNum = Long.parseLong(strCategoryNum);
			}
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			if(kwd.length() == 0) {
				dataCount = dao.dataCount(categoryNum);
			} else  {
				dataCount = dao.dataCount(categoryNum, schType, kwd);
			}
			
			if(dataCount != 0) {
				total_page = util.pageCount(dataCount, size);
			}
			
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<FaqDTO> list = null;
			
			if(kwd.length() == 0) {
				list = dao.listFaq(categoryNum, offset, size);
			} else {
				list = dao.listFaq(categoryNum, offset, size, schType, kwd);
			}
			
			for(FaqDTO dto : list) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			String paging = util.pagingMethod(current_page, total_page, "listPage");
		
			mav.addObject("list", list);
			mav.addObject("pageNo", current_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			
		} catch (Exception e) {
			resp.sendError(406);
			throw e;
		}
		
		return mav;
	}
}
