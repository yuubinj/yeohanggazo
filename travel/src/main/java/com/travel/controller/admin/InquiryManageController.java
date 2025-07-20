package com.travel.controller.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.FaqDAO;
import com.travel.dao.InquiryDAO;
import com.travel.model.FaqDTO;
import com.travel.model.InquiryDTO;
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
public class InquiryManageController {
	
	@RequestMapping(value = "/admin/inquiry/main", method = RequestMethod.GET)
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/inquiry/main");
		
		InquiryDAO dao = new InquiryDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(1);
			String selectedCategoryNum = req.getParameter("categoryNum");
			
			mav.addObject("listCategory", listCategory);
			mav.addObject("selectedCategoryNum", selectedCategoryNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	// 문의 리스트
	@RequestMapping(value = "/admin/inquiry/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/inquiry/list");
		
		InquiryDAO dao = new InquiryDAO();
		
		MyUtil util = new MyUtil();
		
		try {
			int size = 3;
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
			} else {
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
			
			List<InquiryDTO> list = null;
			
			if(kwd.length() == 0) {
				list = dao.listInquiry(categoryNum, offset, size);
			} else {
				list = dao.listInquiry(categoryNum, offset, size, schType, kwd);
			}
			
			for(InquiryDTO dto : list) {
				dto.setQuestion(dto.getQuestion().replaceAll("\n", "<br>"));
			}
			
			String query = "";
			if(kwd.length() != 0) {
				query = "kwd=" + util.encodeUrl(kwd);
			}
			
			String cp = req.getContextPath();

			String articleUrl = cp + "/admin/inquiry/article?pageNo=" + current_page + "&categoryNum=" + categoryNum;
			if(query.length() != 0) {
				articleUrl += "&" + query;
			}
			
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			mav.addObject("list", list);
			mav.addObject("pageNo", current_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("total_page", total_page);
			mav.addObject("size", size);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("kwd", kwd);
			mav.addObject("paging", paging);

		} catch (Exception e) {
			resp.sendError(410);
			throw e;
		}
		
		return mav;
	}
	
	/*
	// 글쓰기 폼
	@RequestMapping(value = "/admin/inquiry/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/inquiry/write");
		mav.addObject("mode", "write");
		
		return mav;
	}
	
	// 글 저장
	@RequestMapping(value = "/admin/inquiry/write", method = RequestMethod.POST)
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
		
		return new ModelAndView("redirect:/admin/inquiry/list");
	}
	*/
	
	// 글보기
	@RequestMapping(value = "/admin/inquiry/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyUtil util = new MyUtil();
		InquiryDAO dao = new InquiryDAO();
		
		String pageNo = req.getParameter("pageNo");
		long categoryNum = Long.parseLong(req.getParameter("categoryNum"));
		String query = "pageNo=" + pageNo + "&categoryNum=" + categoryNum;

		try {
			long num = Long.parseLong(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";						
			}
			kwd = util.decodeUrl(kwd);
			
			if(kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}
			
			InquiryDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/admin/inquiry/main?" + query);
			}
			dto.setQuestion(dto.getQuestion().replaceAll("\n", "<br>"));
			
			InquiryDTO prevDto = dao.findByPrev(categoryNum, num, schType, kwd);
			InquiryDTO nextDto = dao.findByNext(categoryNum, num, schType, kwd);
			
			ModelAndView mav = new ModelAndView("admin/inquiry/article");
			
			mav.addObject("dto", dto);
			mav.addObject("pageNo", pageNo);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/inquiry/main?" + query);
	}
	
	/*
	// 글 수정 폼
	@RequestMapping(value = "/admin/inquiry/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InquiryDAO dao = new InquiryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String pageNo = req.getParameter("pageNo");
		try {
			long num = Long.parseLong(req.getParameter("num"));
			InquiryDTO dto = dao.findById(num);
			
			if(dto == null) {
				return new ModelAndView("redirect:/admin/inquiry/main?pageNo=" + pageNo);
			}
			
			if(! dto.getUserId().equals(info.getUserId())) {
				return new ModelAndView("redirect:/admin/inquiry/main?pageNo=" + pageNo);
			}
			
			ModelAndView mav = new ModelAndView("admin/inquiry/write");
			mav.addObject("dto", dto);
			mav.addObject("pageNo", pageNo);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/inquiry/main?pageNo=" + pageNo);
	}
	
	// 글 수정 완료
	@RequestMapping(value = "/admin/inquiry/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InquiryDAO dao = new InquiryDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String pageNo = req.getParameter("pageNo");

		try {
			InquiryDTO dto = new InquiryDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setQuestion(req.getParameter("question"));
			dto.setSecret(Integer.parseInt(req.getParameter("secret")));
			dto.setCategoryNum(Long.parseLong(req.getParameter("categoryNum")));
			
			dto.setUserId(info.getUserId());
			
			dao.updateQuestion(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/inquiry/main?pageNo=" + pageNo);
	}
	*/
	
	@RequestMapping(value = "/admin/inquiry/answer", method = RequestMethod.POST)
	public ModelAndView answerSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 답변 완료
		InquiryDAO dao = new InquiryDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("pageNo");
		try {
			InquiryDTO dto = new InquiryDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setAnswer(req.getParameter("answer"));
			dto.setAnswerId(info.getUserId());

			dao.updateAnswer(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/admin/inquiry/main?pageNo=" + page);
	}

	
	// 글 삭제
	@RequestMapping(value = "/admin/inquiry/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InquiryDAO dao = new InquiryDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String pageNo = req.getParameter("pageNo");
		String query = "pageNo=" + pageNo;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String mode = req.getParameter("mode");
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);

			if(kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}
			
			if(mode.equals("answer") && info.getUserLevel() >= 51) {
				// 답변 삭제
				InquiryDTO dto = new InquiryDTO();
				dto.setNum(num);
				dto.setAnswer("");
				dto.setAnswerId("");
				dao.updateAnswer(dto);
			} else if(mode.equals("question")) {
				// 질문 삭제
				dao.deleteQuestion(num, info.getUserId(), info.getUserLevel());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/inquiry/main?" + query);
	}
	/*
	// 카테고리 관리 - 카테고리 보기
	@RequestMapping(value = "/admin/inquiry/listAllCategory", method = RequestMethod.GET)
	public ModelAndView listCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/inquiry/listCategory");

		InquiryDAO dao = new InquiryDAO();
		try {
			List<FaqDTO> listCategory = dao.listCategory(0);
			
			mav.addObject("listCategory", listCategory);
			
		} catch (Exception e) {
			resp.sendError(406);
			throw e;
		}
		
		return mav;
	}
	*/
	// AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/inquiry/insertCategory", method = RequestMethod.POST)
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
	@RequestMapping(value = "/admin/inquiry/updateCategory", method = RequestMethod.POST)
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
	@RequestMapping(value = "/admin/inquiry/deleteCategory", method = RequestMethod.POST)
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
