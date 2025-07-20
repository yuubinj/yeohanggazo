package com.travel.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
import com.travel.util.FileManager;
import com.travel.util.MyMultipartFile;
import com.travel.util.MyUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MyTripController {

	@RequestMapping(value = "/myTrip/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		MyUtil util = new MyUtil();
		
		ModelAndView mav = new ModelAndView("myTrip/list");
		
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
			
			String listUrl = cp + "/myTrip/list";
			String articleUrl = cp + "/myTrip/article?pageNo=" + current_page;
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
	
	// 글쓰기 폼
	@RequestMapping(value = "/myTrip/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("myTrip/write");
		mav.addObject("mode", "write");
		
		return mav;
	}
	
	// 글 저장
	@RequestMapping(value = "/myTrip/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		FileManager fileManage = new FileManager();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "myTrip";
		
		try {
			MyTripDTO dto = new MyTripDTO();
			
			dto.setUserId(info.getUserId());
			
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			List<MyMultipartFile> listFile = fileManage.doFileUpload(req.getParts(), pathname);
			dto.setListFile(listFile);

			
			dao.insertMyTrip(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/myTrip/list");
	}
	
	// 글보기
	@RequestMapping(value = "/myTrip/article", method = RequestMethod.GET)
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
				return new ModelAndView("redirect:/myTrip/list?" + query);
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			MyTripDTO prevDto = dao.findByPrev(num, schType, kwd);
			MyTripDTO nextDto = dao.findByNext(num, schType, kwd);
			
			ModelAndView mav = new ModelAndView("myTrip/article");

			List<MyTripDTO> listFile;
			listFile = dao.listMyTripFile(dto.getNum());
			
			List<String> listFileSaveFilename = new ArrayList<String>();
			for(MyTripDTO fileDto : listFile) {
				listFileSaveFilename.add(fileDto.getSaveFilename());
			}
			dto.setListFileSavename(listFileSaveFilename);
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			boolean isUserLiked;
			if(info == null) {
				isUserLiked = false;				
			} else {
				isUserLiked = dao.isUserMyTripLike(num, info.getUserId());								
			}
			
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
		
		return new ModelAndView("redirect:/myTrip/list?" + query);
	}
	
	// 글 수정 폼
	@RequestMapping(value = "/myTrip/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String pageNo = req.getParameter("pageNo");
		try {
			long num = Long.parseLong(req.getParameter("num"));
			MyTripDTO dto = dao.findById(num);
			
			if(dto == null) {
				return new ModelAndView("redirect:/myTrip/list?pageNo=" + pageNo);
			}
			
			if(! dto.getUserId().equals(info.getUserId())) {
				return new ModelAndView("redirect:/myTrip/list?pageNo=" + pageNo);
			}
			
			List<MyTripDTO> listFile = dao.listMyTripFile(num);
			
			ModelAndView mav = new ModelAndView("myTrip/write");
			mav.addObject("dto", dto);
			mav.addObject("listFile", listFile);
			mav.addObject("pageNo", pageNo);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/inquiry/list?pageNo=" + pageNo);
	}
	
	// 글 수정 완료
	@RequestMapping(value = "/myTrip/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		
		String pageNo = req.getParameter("pageNo");

		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "myTrip";
		
		try {
			MyTripDTO dto = new MyTripDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			List<MyMultipartFile> listFile = fileManager.doFileUpload(req.getParts(), pathname);
			dto.setListFile(listFile);
			
			dao.updateMyTrip(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/myTrip/list?pageNo=" + pageNo);
	}
	
	// 수정에서 파일만 삭제
	@RequestMapping(value = "/myTrip/deleteFile")
	public ModelAndView deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일만 삭제
		// 넘어온 파라미터 : 글번호, 파일번호, 페이지번호, size
		MyTripDAO dao = new MyTripDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "myTrip";

		String pageNo = req.getParameter("pageNo");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			long fileNum = Long.parseLong(req.getParameter("fileNum"));
			MyTripDTO dto = dao.findByFileId(fileNum);
			if (dto != null) {
				// 파일삭제
				fileManager.doFiledelete(pathname, dto.getSaveFilename());
				
				// 테이블 파일 정보 삭제
				dao.deleteMyTripFile("one", fileNum);
			}

			// 다시 수정 화면으로
			return new ModelAndView("redirect:/myTrip/update?num=" + num + "&pageNo=" + pageNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/myTrip/list?pageNo=" + pageNo);
	}
	
	// 글 삭제
	@RequestMapping(value = "/myTrip/delete", method = RequestMethod.GET)
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
		
		return new ModelAndView("redirect:/myTrip/list?" + query);
	}
	
	// 게시글 공감 저장 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/myTrip/insertMyTripLike", method = RequestMethod.POST)
	public Map<String, Object> insertMyTripLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 넘어온 파라미터 : 글번호, 공감/공감취소여부
		Map<String, Object> model = new HashMap<String, Object>();
		
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int myTripLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String userLiked = req.getParameter("userLiked");
			
			if(userLiked.equals("true")) {
				// 공감 취소
				dao.deleteMyTripLike(num, info.getUserId());
			} else {
				dao.insertMyTripLike(num, info.getUserId());
			}
			
			myTripLikeCount = dao.countMyTripLike(num);
			
			state = "true";
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		model.put("myTripLikeCount", myTripLikeCount);

		return model;
	}
	

	// 리플 리스트 - AJAX:TEXT
	@RequestMapping(value = "/myTrip/listReply", method = RequestMethod.GET)	
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
			
			if(info == null) {
				replyCount = dao.dataCountReply(num, null, 0);
			} else {
				replyCount = dao.dataCountReply(num, info.getUserId(), info.getUserLevel());				
			}
			total_page = util.pageCount(replyCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MyTripReplyDTO> listReply;
			if(info == null) {
				listReply = dao.listReply(num, offset, size, null, 0);				
			} else {
				listReply = dao.listReply(num, offset, size, info.getUserId(), info.getUserLevel());								
			}
			
			// 엔터를 <br>로
			for(MyTripReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				
				// 유저의 좋아요/싫어요 유무
				if(info == null) {
					dto.setUserReplyLike(-1);
				} else {
					dto.setUserReplyLike(dao.userReplyLike(dto.getReplyNum(), info.getUserId()));					
				}
			}
			
			// 페이징 처리 : AJAX 용
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			ModelAndView mav = new ModelAndView("myTrip/listReply");
			
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

	// 리플 또는 답글 저장 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/myTrip/insertReply", method = RequestMethod.POST)
	public Map<String, Object> insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			MyTripReplyDTO dto = new MyTripReplyDTO();

			long num = Long.parseLong(req.getParameter("num"));
			dto.setNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String parentNum = req.getParameter("parentNum");
			if(parentNum != null) {
				dto.setParentNum(Long.parseLong(parentNum));				
			}
			
			dao.insertReply(dto);
			
			state = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);

		return model;
	}

	// 리플 또는 답글 삭제 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/myTrip/deleteReply", method = RequestMethod.POST)
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

	// 댓글 좋아요 / 싫어요 저장 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/myTrip/insertReplyLike", method = RequestMethod.POST)
	public Map<String, Object> insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
		int disLikeCount = 0;
		
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			int replyLike = Integer.parseInt(req.getParameter("replyLike"));
			String isUserLiked = req.getParameter("isUserLiked");
			String isUserDisliked = req.getParameter("isUserDisliked");
			
			MyTripReplyDTO dto = new MyTripReplyDTO();
			
			dto.setReplyNum(replyNum);
			dto.setReplyLike(replyLike);
			dto.setUserId(info.getUserId());
			dto.setUserReplyLike(replyLike);
			
			if(isUserLiked.equals("true") || isUserDisliked.equals("true")) {
				dao.deleteReplyLike(dto);
				dto.setUserReplyLike(-1);
			} else {
				dao.insertReplyLike(dto);
			}
			
			// 좋아요 싫어요 개수 가져오기
			Map<String , Integer> map = dao.countReplyLike(replyNum);
			
			if(map.containsKey("likeCount")) {
				likeCount = map.get("likeCount");
			}
			if(map.containsKey("disLikeCount")) {
				disLikeCount = map.get("disLikeCount");
			}
			
			state = "true";
			model.put("state", state);
			model.put("userReplyLike", dto.getUserReplyLike());
			model.put("likeCount", likeCount);
			model.put("disLikeCount", disLikeCount);
			

		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				state = "liked";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	// 리플의 답글 리스트 - AJAX:TEXT
	@RequestMapping(value = "/myTrip/listReplyAnswer", method = RequestMethod.GET)
	public ModelAndView listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			long parentNum = Long.parseLong(req.getParameter("parentNum"));
			
			List<MyTripReplyDTO> listReplyAnswer;
			if(info == null) {
				listReplyAnswer = dao.listReplyAnswer(parentNum, null, 0);				
			} else {
				listReplyAnswer = dao.listReplyAnswer(parentNum, info.getUserId(), info.getUserLevel());
			}
			
			for(MyTripReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			ModelAndView mav = new ModelAndView("myTrip/listReplyAnswer");
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
	@RequestMapping(value = "/myTrip/countReplyAnswer", method = RequestMethod.POST)
	public Map<String, Object> countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		MyTripDAO dao = new MyTripDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		int count = 0;
		
		try {
			long parentNum = Long.parseLong(req.getParameter("parentNum"));
			if(info == null) {
				count = dao.dataCountReplyAnswer(parentNum, null, 0);				
			} else {
				count = dao.dataCountReplyAnswer(parentNum, info.getUserId(), info.getUserLevel());				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("count", count);
		return model;
	}

	// 댓글 숨김/표시 : AJAX-JSON
	@ResponseBody
	@RequestMapping(value = "/myTrip/replyShowHide", method = RequestMethod.POST)
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
}
