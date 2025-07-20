package com.travel.controller.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.FaqDAO;
import com.travel.model.FaqDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.annotation.ResponseBody;
import com.travel.mvc.view.ModelAndView;
import com.travel.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class FaqManageController {
	@RequestMapping(value = "/admin/faq/main", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/faq/main");
		
		FaqDAO dao = new FaqDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(1);
			
			mav.addObject("listCategory", listCategory);
			
		} catch (Exception e) {
		}
		
		return mav;
	}

	// AJAX-Text
	@RequestMapping(value = "/admin/faq/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/faq/list");
		
		FaqDAO dao = new FaqDAO();
		MyUtil util = new MyUtil();
		
		try {
			int size = 10;
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
	
	// AJAX-Text
	@RequestMapping(value = "/admin/faq/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/faq/write");
		
		FaqDAO dao = new FaqDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(1);
			
			mav.addObject("listCategory", listCategory);
			mav.addObject("mode", "write");
			mav.addObject("pageNo", "1");	
		} catch (Exception e) {
			resp.sendError(406);
			throw e;
		}
		
		return mav;
	}
	
	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/write", method = RequestMethod.POST)
	public Map<String, Object> writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FaqDAO dao = new FaqDAO();
		
		HttpSession session =  req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		try {
			
			FaqDTO dto = new FaqDTO();
			dto.setCategoryNum(Long.parseLong(req.getParameter("categoryNum")));
			dto.setMemberIdx(info.getMemberIdx());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dao.insertFaq(dto);
			
			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		
		return model;
	}

	// AJAX-Text
	@RequestMapping(value = "/admin/faq/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/faq/write");
		
		FaqDAO dao = new FaqDAO();
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String page = req.getParameter("pageNo");
			
			FaqDTO dto = dao.findById(num);
			if(dto == null) {
				throw new NullPointerException();
			}
			
			List<FaqDTO> listCategory = dao.listCategory(1);
			
			mav.addObject("dto", dto);
			mav.addObject("listCategory", listCategory);
			mav.addObject("pageNo", page);
			mav.addObject("mode", "update");
			
		} catch (Exception e) {
			resp.sendError(406);
			throw e;
		}
		
		return mav;
	}
	
	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/update", method = RequestMethod.POST)
	public Map<String, Object> updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FaqDAO dao = new FaqDAO();
		
		String state = "false";
		try {
			FaqDTO dto = new FaqDTO();
			
			dto.setCategoryNum(Long.parseLong(req.getParameter("categoryNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setNum(Long.parseLong(req.getParameter("num")));
			
			dao.updateFaq(dto);
			
			state = "true";
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}

	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/delete", method = RequestMethod.POST)
	public Map<String, Object> deleteFaq(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FaqDAO dao = new FaqDAO();
		
		String state = "false";
		try {
			long num = Long.parseLong(req.getParameter("num"));
			dao.deleteFaq(num);
			
			state = "true";
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}
	
	// AJAX-Text
	@RequestMapping(value = "/admin/faq/listAllCategory", method = RequestMethod.GET)
	public ModelAndView listCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/faq/listCategory");

		FaqDAO dao = new FaqDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(0);
			
			mav.addObject("listCategory", listCategory);
			
		} catch (Exception e) {
			resp.sendError(406);
			throw e;
		}
		
		return mav;
	}
	
	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/insertCategory", method = RequestMethod.POST)
	public Map<String, Object> insertCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		String state = "false";
		
		FaqDAO dao = new FaqDAO();
		try {
			FaqDTO dto = new FaqDTO();
			
			dto.setCategory(req.getParameter("category"));
			dto.setOrderNo(Integer.parseInt(req.getParameter("orderNo")));
			dto.setEnabled(Integer.parseInt(req.getParameter("enabled")));
			
			dao.insertFaqCategory(dto);
			
			state = "true";
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}

	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/updateCategory", method = RequestMethod.POST)
	public Map<String, Object> updateCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FaqDAO dao = new FaqDAO();
		
		String state = "false";
		try {
			FaqDTO dto = new FaqDTO();
			
			dto.setCategoryNum(Long.parseLong(req.getParameter("categoryNum")));
			dto.setCategory(req.getParameter("category"));
			dto.setOrderNo(Integer.parseInt(req.getParameter("orderNo")));
			dto.setEnabled(Integer.parseInt(req.getParameter("enabled")));
			
			dao.updateFaqCategory(dto);
			
			state = "true";
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}

	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/faq/deleteCategory", method = RequestMethod.POST)
	public Map<String, Object> deleteCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FaqDAO dao = new FaqDAO();
		
		String state = "false";
		try {
			long categoryNum = Long.parseLong(req.getParameter("categoryNum"));
			dao.deleteFaqCategory(categoryNum);
			state = "true";
		} catch (Exception e) {
		}

		model.put("state", state);
		
		return model;
	}
}
