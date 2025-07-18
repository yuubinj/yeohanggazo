package com.travel.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.dao.BoardDAO;
import com.travel.model.BbsReplyDTO;
import com.travel.model.BoardDTO;
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
import jakarta.servlet.http.Part;

@Controller
public class BoardController {
// 게시물 리스트
	@RequestMapping(value = "/bbs/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("bbs/list");
		
		BoardDAO dao = new BoardDAO();
		MyUtil util = new MyUtil();

		try {
			// 카테고리 파라미터 받기
			String categoryNumParam = req.getParameter("categoryNum");
	        int categoryNum = 0; // 0 = 전체 보기
	        if (categoryNumParam != null && !categoryNumParam.equals("")) {
	            categoryNum = Integer.parseInt(categoryNumParam);
	        }
			
	        // 페이지 파라미터 받기
	        String page = req.getParameter("page");
	        int current_page = 1;
	        if (page != null) {
	            current_page = Integer.parseInt(page);
	        }

	        // 검색 파라미터 받기
	        String schType = req.getParameter("schType");
	        String kwd = req.getParameter("kwd");
	        if (schType == null) {
	            schType = "all";
	            kwd = "";
	        }
	        kwd = util.decodeUrl(kwd);

	        // 게시물 수 계산
	        int dataCount;
	        if (kwd.length() == 0) {
	            dataCount = (categoryNum == 0) ? dao.dataCount() : dao.dataCount(categoryNum);
	        } else {
	            dataCount = (categoryNum == 0) ? dao.dataCount(schType, kwd) : dao.dataCount(categoryNum, schType, kwd);
	        }

	        // 페이지 계산
	        int size = 10;
	        int total_page = util.pageCount(dataCount, size);
	        if (current_page > total_page) current_page = total_page;

	        int offset = (current_page - 1) * size;
	        if (offset < 0) offset = 0;

	        // 게시물 목록 조회
	        List<BoardDTO> list;
	        if (kwd.length() == 0) {
	            list = (categoryNum == 0)
	                    ? dao.listBoard(offset, size)
	                    : dao.listBoard(offset, size, categoryNum);
	        } else {
	            list = (categoryNum == 0)
	                    ? dao.listBoard(offset, size, schType, kwd)
	                    : dao.listBoard(categoryNum, offset, size, schType, kwd);
	        }

	        // 쿼리스트링 생성
	        String query = "";
	        if (kwd.length() != 0) {
	            query = "schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
	        }

	        // URL 생성
	        String cp = req.getContextPath();
	        String listUrl = cp + "/bbs/list?categoryNum=" + categoryNum;
	        String articleUrl = cp + "/bbs/article?categoryNum=" + categoryNum + "&page=" + current_page;
	        if (query.length() != 0) {
	            listUrl += "&" + query;
	            articleUrl += "&" + query;
	        }

	        String paging = util.paging(current_page, total_page, listUrl);

	        // JSP에 전달할 속성
	        mav.addObject("list", list);
	        mav.addObject("categoryNum", categoryNum);
	        mav.addObject("page", current_page);
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

	@RequestMapping(value = "/bbs/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    String categoryNum = req.getParameter("categoryNum");

	    // 비회원이거나 userLevel이 1 미만이면 접근 차단
	    if (info == null || info.getUserLevel() < 1) {
	        return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum);
	    }

	    ModelAndView mav = new ModelAndView("bbs/write");
	    mav.addObject("categoryNum", categoryNum);
	    mav.addObject("mode", "write");

	    return mav;
	}

	@RequestMapping(value = "/bbs/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
	    BoardDAO dao = new BoardDAO();
	    FileManager fileManager = new FileManager();

	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    String categoryNum = req.getParameter("categoryNum");

	    // 비회원이거나 userLevel이 1 미만이면 접근 차단
	    if (info == null || info.getUserLevel() < 1) {
	        return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum);
	    }
		
	 // 파일 저장 경로
	    String root = session.getServletContext().getRealPath("/");
	    String pathname = root + "uploads" + File.separator + "bbs";
		
		try {
			BoardDTO dto = new BoardDTO();		
		
			// userId는 세션에 저장된 정보
			dto.setUserId(info.getUserId());
		
			// 파라미터
			dto.setCategoryNum(Integer.parseInt(categoryNum));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if (multiFile != null) {
				String saveFilename = multiFile.getSaveFilename();
				String originalFilename = multiFile.getOriginalFilename();
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(originalFilename);
				// long size = multiFile.getSize();
			}

			dao.insertBoard(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum);
	}

	@RequestMapping(value = "/bbs/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
		BoardDAO dao = new BoardDAO();
		MyUtil util = new MyUtil();
		
		// 파라미터 수집
		String categoryNum = req.getParameter("categoryNum");
		String page = req.getParameter("page");
		String query = "categoryNum=" + categoryNum + "&page=" + page;
		
		try {
			// 글 번호
			long num = Long.parseLong(req.getParameter("num"));
			
			// 검색 조건 수집 및 디코딩
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");

			if (schType == null) {
				schType = "";
			}
			if (kwd == null) {
				kwd = "";
			}

			kwd = util.decodeUrl(kwd);

			if (!kwd.isEmpty()) {
				query += "&schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}

			// 조회수 증가
			dao.updateHitCount(num);
			
			// 게시물 가져오기
			BoardDTO dto = dao.findById(num);

			
			if(dto == null) {
				return new ModelAndView("redirect:/bbs/list?" + query);
			}
			
			// 이전글/다음글 (categoryNum 조건에 따라 분기)
			int nCategory = Integer.parseInt(categoryNum);
			// 무조건 이 방식으로 통일
			BoardDTO prevDto = dao.findByPrev(nCategory, num, schType, kwd);
			BoardDTO nextDto = dao.findByNext(nCategory, num, schType, kwd);
			
			// 로그인 유저의 게시글 공감 여부
	        HttpSession session = req.getSession();
	        SessionInfo info = (SessionInfo) session.getAttribute("member");

	        String userId = (info != null) ? info.getUserId() : null;
			int userLevel = (info != null) ? info.getUserLevel() : 0;

			boolean isUserLiked = false;
			if (userId != null) {
				isUserLiked = dao.isUserBoardLike(num, userId);
			}
	        // info가 null이면 그대로 false 유지되니까 예외 안 터지고 그대로 페이지 렌더링됨
			
			// JSP로 전달할 속성
			ModelAndView mav = new ModelAndView("bbs/article");
			mav.addObject("categoryNum", categoryNum);
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);
			
			mav.addObject("isUserLiked", isUserLiked);
			
			mav.addObject("userId", userId); // 댓글 JSP에서도 필요할 수 있음
			mav.addObject("userLevel", userLevel); // 혹시 레벨별 구분 필요하면
			mav.addObject("isUserLiked", isUserLiked);

			// 포워딩
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 예외 발생 시 목록으로 이동
		return new ModelAndView("redirect:/bbs/list?" + query);
	}

	@RequestMapping(value = "/bbs/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String categoryNum = req.getParameter("categoryNum");
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			BoardDTO dto = dao.findById(num);
			
			if (dto == null) {
				return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum + "&page=" + page);
			}

			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId())) {
				return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum + "&page=" + page);
			}
			
			ModelAndView mav = new ModelAndView("bbs/write");
			
			mav.addObject("categoryNum", categoryNum);
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/bbs/list?page=");
	}

	@RequestMapping(value = "/bbs/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "bbs";
		
		String categoryNum = req.getParameter("categoryNum");
		String page = req.getParameter("page");

		try {
			BoardDTO dto = new BoardDTO();
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setSaveFilename(req.getParameter("saveFilename"));
			dto.setOriginalFilename(req.getParameter("originalFilename"));
			
			dto.setUserId(info.getUserId());
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if (multiFile != null) {
				if (req.getParameter("saveFilename").length() != 0) {
					// 기존파일 삭제
					fileManager.doFiledelete(pathname, req.getParameter("saveFilename"));
				}

				// 새로운 파일
				String saveFilename = multiFile.getSaveFilename();
				String originalFilename = multiFile.getOriginalFilename();
				// long size = multiFile.getSize();
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(originalFilename);
			}

			dao.updateBoard(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum + "&page=" + page);
	}
	
	@RequestMapping(value = "/bbs/deleteFile")
	public ModelAndView deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일만 삭제
		BoardDAO dao = new BoardDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "bbs";
		
		String categoryNum = req.getParameter("categoryNum");
		String page = req.getParameter("page");
		

		try {
			long num = Long.parseLong(req.getParameter("num"));
			BoardDTO dto = dao.findById(num);
			if (dto == null) {
				return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum + "&page=" + page);
			}

			if (!info.getUserId().equals(dto.getUserId())) {
				return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum + "&page=" + page);
			}

			// 파일삭제
			fileManager.doFiledelete(pathname, dto.getSaveFilename());

			// 파일명과 파일크기 변경
			dto.setOriginalFilename("");
			dto.setSaveFilename("");
			dao.updateBoard(dto);

			ModelAndView mav = new ModelAndView("bbs/write");
			
			mav.addObject("categoryNum", categoryNum);
			mav.addObject("dto", dto);
			mav.addObject("page", page);

			mav.addObject("mode", "update");

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/bbs/list?categoryNum=" + categoryNum +"&page=" + page);
	}

	@RequestMapping(value = "/bbs/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제
		BoardDAO dao = new BoardDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);

			if (kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}
			
			dao.deleteBoard(num, info.getUserId(), info.getUserLevel());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/bbs/list?" + query);
	}
	
	@RequestMapping(value = "/bbs/download")
	public void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 파일 다운로드
		BoardDAO dao = new BoardDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "bbs";
		
		boolean b = false;

		try {
			long num = Long.parseLong(req.getParameter("num"));

			BoardDTO dto = dao.findById(num);
			if (dto != null) {
				b = fileManager.doFiledownload(dto.getSaveFilename(), dto.getOriginalFilename(), pathname, resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ( ! b ) {
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print("<script>alert('파일다운로드가 실패 했습니다.');history.back();</script>");
		}
	}
	
	// 게시글 공감 저장 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/bbs/insertBoardLike", method = RequestMethod.POST)
	public Map<String, Object> insertBoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 넘어온 파라미터 : 글번호, 공감/공감취소여부
		Map<String, Object> model = new HashMap<String, Object>();
		
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int boardLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String userLiked = req.getParameter("userLiked");
			
			if(userLiked.equals("true")) {
				// 공감 취소
				dao.deleteBoardLike(num, info.getUserId());
			} else {
				// 공감
				dao.insertBoardLike(num, info.getUserId());
			}
			
			boardLikeCount = dao.countBoardLike(num);
			
			state = "true";
			
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		model.put("boardLikeCount", boardLikeCount);
		
		return model;
	}

	// 리플 리스트 - AJAX:TEXT
	@RequestMapping(value = "/bbs/listReply", method = RequestMethod.GET)
	public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BoardDAO dao = new BoardDAO();
	    MyUtil util = new MyUtil();

	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    // 비로그인 구분
	    String userId = (info != null) ? info.getUserId() : null;
	    int userLevel = (info != null) ? info.getUserLevel() : 0;

	    try {
	        long num = Long.parseLong(req.getParameter("num"));
	        String pageNo = req.getParameter("pageNo");
	        int current_page = 1;
	        if (pageNo != null) {
	            current_page = Integer.parseInt(pageNo);
	        }

	        int size = 5;
	        int total_page = 0;
	        int replyCount = dao.dataCountReply(num, userId, userLevel);
	        total_page = util.pageCount(replyCount, size);
	        if (current_page > total_page) {
	            current_page = total_page;
	        }

	        int offset = (current_page - 1) * size;
	        if (offset < 0) offset = 0;

	        List<BbsReplyDTO> listReply = dao.listReply(num, offset, size, userId, userLevel);

	        for (BbsReplyDTO dto : listReply) {
	            dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

	        	// 로그인 구분
	            if (userId != null) {
	                dto.setUserReplyLike(dao.userReplyLike(dto.getReplyNum(), userId));
	            }
	        }

	        String paging = util.pagingMethod(current_page, total_page, "listPage");

	        ModelAndView mav = new ModelAndView("bbs/listReply");
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
	@RequestMapping(value = "/bbs/insertReply", method = RequestMethod.POST)
	public Map<String, Object> insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			BbsReplyDTO dto = new BbsReplyDTO();
			
			long num = Long.parseLong(req.getParameter("num"));
			dto.setNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			
			String parentNumStr = req.getParameter("parentNum");
			long parentNum = 0;
			if (parentNumStr != null && !parentNumStr.trim().equals("")) {
			    parentNum = Long.parseLong(parentNumStr);
			}
			dto.setParentNum(parentNum);
			
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
	@RequestMapping(value = "/bbs/deleteReply", method = RequestMethod.POST)
	public Map<String, Object> deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		BoardDAO dao = new BoardDAO();
		
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
	@RequestMapping(value = "/bbs/insertReplyLike", method = RequestMethod.POST)
	public Map<String, Object> insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
		int disLikeCount = 0;
		
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			int replyLike = Integer.parseInt(req.getParameter("replyLike"));
			
			BbsReplyDTO dto = new BbsReplyDTO();
			
			dto.setReplyNum(replyNum);
			dto.setReplyLike(replyLike);
			dto.setUserId(info.getUserId());
			
			dao.insertReplyLike(dto);
			Map<String, Integer> map = dao.countReplyLike(replyNum);
			
			if(map.containsKey("likeCount")) {
				likeCount = map.get("likeCount");
			}
			if(map.containsKey("disLikeCount")) {
				disLikeCount = map.get("disLikeCount");
			}
			
			state = "true";
		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				state = "liked";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.put("state", state);
		model.put("likeCount", likeCount);
		model.put("disLikeCount", disLikeCount);
		
		return model;
	}

	// 리플의 답글 리스트 - AJAX:TEXT
	@RequestMapping(value = "/bbs/listReplyAnswer", method = RequestMethod.GET)
	public ModelAndView listReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

	    BoardDAO dao = new BoardDAO();

	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    String userId = null;
	    int userLevel = 0;

	    if (info != null) {
	        userId = info.getUserId();
	        userLevel = info.getUserLevel();
	    }

	    try {
	        long parentNum = Long.parseLong(req.getParameter("parentNum"));

	        List<BbsReplyDTO> listReplyAnswer = dao.listReplyAnswer(parentNum, userId, userLevel);

	        for (BbsReplyDTO dto : listReplyAnswer) {
	            dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
	        }

	        ModelAndView mav = new ModelAndView("bbs/listReplyAnswer");
	        mav.addObject("listReplyAnswer", listReplyAnswer);

	        return mav;

	    } catch (Exception e) {
	        e.printStackTrace();
	        resp.sendError(500);
	        throw e;
	    }
	}

	// 리플의 답글 개수 - AJAX:JSON
	@ResponseBody
	@RequestMapping(value = "/bbs/countReplyAnswer", method = RequestMethod.POST)
	public Map<String, Object> countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		BoardDAO dao = new BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		int count = 0;
		String userId = null;
		int userLevel = 0;

		if (info != null) {
			userId = info.getUserId();
			userLevel = info.getUserLevel();
		}
		
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
	@RequestMapping(value = "/bbs/replyShowHide", method = RequestMethod.POST)
	public Map<String, Object> replyShowHide(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();

		BoardDAO dao = new BoardDAO();
		
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
