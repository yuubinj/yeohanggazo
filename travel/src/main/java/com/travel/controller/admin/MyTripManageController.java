package com.travel.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.MyTripDAO;
import com.travel.model.MyTripDTO;
import com.travel.model.MyTripReplyDTO;
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
public class MyTripManageController {
	@RequestMapping(value = "/admin/myTrip/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		MyUtil util = new MyUtil();
		
		ModelAndView mav = new ModelAndView("admin/myTrip/list");
		
		try {
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}

			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			int size = 6;
			int dataCount, total_page;

			if (kwd.length() != 0) {
				dataCount = dao.dataCount(schType, kwd);
			} else {
				dataCount = dao.dataCount();
			}
			total_page = util.pageCount(dataCount, size);

			if (current_page > total_page) {
				current_page = total_page;
			}

			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MyTripDTO> list;
			if (kwd.length() != 0) {
				list = dao.listMyTrip(offset, size, schType, kwd);
			} else {
				list = dao.listMyTrip(offset, size);
			}
			
			for(MyTripDTO dto : list) {
				List<MyTripDTO> listFile;
				listFile = dao.listMyTripFile(dto.getNum());
				int myTripLikeCount = dao.countMyTripLike(dto.getNum());
				dto.setMyTripLikeCount(myTripLikeCount);
				
				int myTripReplyCount = dao.countMyTripReply(dto.getNum());
				dto.setMyTripReplyCount(myTripReplyCount);
				
				List<String> listFileSaveFilename = new ArrayList<String>();
				for(MyTripDTO fileDto : listFile) {
					dto.setFileNum(fileDto.getFileNum());
					dto.setSaveFilename(fileDto.getSaveFilename());
					dto.setOriginalFilename(fileDto.getOriginalFilename());
					dto.setFileSize(fileDto.getFileSize());
					
					listFileSaveFilename.add(fileDto.getSaveFilename());
				}
				dto.setListFileSavename(listFileSaveFilename);
			}
			
			String cp = req.getContextPath();
			String query = "";
			if(kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}
			
			String listUrl = cp + "/admin/mytrip/list";
			String articleUrl = cp + "/admin/mytrip/article?pageNo=" + current_page;
			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			mav.addObject("list", list);
			mav.addObject("pageNo", current_page);
			mav.addObject("total_page", total_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("size", size);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("paging", paging);
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	// 글보기
	@RequestMapping(value = "/admin/myTrip/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyUtil util = new MyUtil();
		MyTripDAO dao = new MyTripDAO();
		
		String pageNo = req.getParameter("pageNo");
		String query = "pageNo=" + pageNo;

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
			
			dao.updateHitCount(num);
			
			MyTripDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/admin/myTrip/list?" + query);
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			MyTripDTO prevDto = dao.findByPrev(num, schType, kwd);
			MyTripDTO nextDto = dao.findByNext(num, schType, kwd);
			
			ModelAndView mav = new ModelAndView("admin/myTrip/article");

			List<MyTripDTO> listFile;
			listFile = dao.listMyTripFile(dto.getNum());
			
			List<String> listFileSaveFilename = new ArrayList<String>();
			for(MyTripDTO fileDto : listFile) {
				listFileSaveFilename.add(fileDto.getSaveFilename());
			}
			dto.setListFileSavename(listFileSaveFilename);
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			boolean isUserLiked = dao.isUserMyTripLike(num, info.getUserId());
			
			mav.addObject("dto", dto);
			mav.addObject("pageNo", pageNo);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			mav.addObject("isUserLiked", isUserLiked);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/myTrip/list?" + query);
	}
	
	// 리플 리스트 - AJAX:TEXT
	@RequestMapping(value = "/admin/myTrip/listReply", method = RequestMethod.GET)	
	public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		MyUtil util = new MyUtil(); 
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}
			
			int size = 5;
			int total_page = 0;
			int replyCount = 0;
			
			replyCount = dao.dataCountReply(num, info.getUserId(), info.getUserLevel());
			total_page = util.pageCount(replyCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MyTripReplyDTO> listReply = dao.listReply(num, offset, size, info.getUserId(), info.getUserLevel());
			
			// 엔터를 <br>로
			for(MyTripReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				
				// 유저의 좋아요/싫어요 유무
				dto.setUserReplyLike(dao.userReplyLike(dto.getReplyNum(), info.getUserId()));
			}
			
			// 페이징 처리 : AJAX 용
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			ModelAndView mav = new ModelAndView("admin/myTrip/listReply");
			
			mav.addObject("listReply", listReply);
			mav.addObject("pageNo", current_page);
			mav.addObject("replyCount", replyCount);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(406);
			throw e;
		}
	}
	
	// 리플의 답글 리스트 - AJAX:TEXT
	@RequestMapping(value = "/admin/myTrip/listReplyAnswer", method = RequestMethod.GET)
	public ModelAndView listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			long parentNum = Long.parseLong(req.getParameter("parentNum"));
			
			List<MyTripReplyDTO> listReplyAnswer = dao.listReplyAnswer(parentNum, info.getUserId(), info.getUserLevel());
			
			for(MyTripReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			ModelAndView mav = new ModelAndView("admin/myTrip/listReplyAnswer");
			mav.addObject("listReplyAnswer", listReplyAnswer);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(406);
			throw e;
		}
	}

	// 리플의 답글 개수 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/admin/myTrip/countReplyAnswer", method = RequestMethod.POST)
	public Map<String, Object> countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		int count = 0;
		
		try {
			long parentNum = Long.parseLong(req.getParameter("parentNum"));
			count = dao.dataCountReplyAnswer(parentNum, info.getUserId(), info.getUserLevel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("count", count);
		return model;
	}

	// 댓글 숨김/표시 : AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/admin/myTrip/replyShowHide", method = RequestMethod.POST)
	public Map<String, Object> replyShowHide(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "true";
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			int showReply = Integer.parseInt(req.getParameter("showReply"));
			
			dao.updateReplyShowHide(replyNum, showReply, info.getUserId());
			
		} catch (Exception e) {
			state = "false";
			e.printStackTrace();
		}
		
		model.put("state", state);

		return model;
	}
	
	@RequestMapping(value = "/admin/myTrip/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String pageNo = req.getParameter("pageNo");
		String query = "pageNo=" + pageNo;
		
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
			
			dao.deleteMyTrip(num, info.getUserId(), info.getUserLevel());
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/myTrip/list?" + query);
	}
	
	// 리플 또는 답글 삭제 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/admin/myTrip/deleteReply", method = RequestMethod.POST)
	public Map<String, Object> deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			dao.deleteReply(replyNum, info.getUserId(), info.getUserLevel());
			
			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.put("state", state);
		
		return model;
	}
}
