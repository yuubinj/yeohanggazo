package com.travel.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.travel.dao.AdDAO;
import com.travel.model.AdDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.mvc.annotation.RequestMethod;
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
public class AdManageController {

	@RequestMapping(value = "/admin/ad/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/ad/list");
		
		AdDAO dao = new AdDAO();
		MyUtil util = new MyUtil();
		
		// 게시물 리스트
		try {
			String page = req.getParameter("pageNo");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = util.decodeUrl(kwd);
			
			int dataCount;
			if(kwd.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(schType, kwd);
			}
			
			String cp = req.getContextPath();
			int size = 12;
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<AdDTO> list = null;
			if(kwd.length() == 0) {
				list = dao.listAd(offset, size);
			} else {
				list = dao.listAd(offset, size, schType, kwd);
			}
			
			String query = "";
			if(kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + util.encodeUrl(kwd);
			}
			
			String listUrl = cp + "/admin/ad/list";
			String articleUrl = cp + "/admin/ad/article?pageNo=" + current_page;
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

	@RequestMapping(value = "/admin/ad/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		ModelAndView mav = new ModelAndView("admin/ad/write");
		mav.addObject("mode", "write");
		return mav;
	}

	@RequestMapping(value = "/admin/ad/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 저장
		AdDAO dao = new AdDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "photo";

		try {
			AdDTO dto = new AdDTO();
			
			dto.setUserId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));

			String filename = null;
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if(multiFile != null) {
				filename = multiFile.getSaveFilename();
			}
			
			// filename 이 null 이 아닐 때에만 사진 등록 가능하도록.
			if(filename != null) {
				dto.setImageFilename(filename);
				dao.insertAd(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/admin/ad/list");
	}

	@RequestMapping(value = "/admin/ad/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 보기
		AdDAO dao = new AdDAO();
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("pageNo");
		String query = "pageNo=" + page;
		
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
			
			AdDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/admin/ad/list?pageNo=" + page);
			}
			
			AdDTO prevDto = dao.findByPrev(num, schType, kwd);
			AdDTO nextDto = dao.findByNext(num, schType, kwd);
			
			ModelAndView mav = new ModelAndView("admin/ad/article");
			
			mav.addObject("dto", dto);
			mav.addObject("pageNo", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/admin/ad/list?" + query);
	}

	@RequestMapping(value = "/admin/ad/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		AdDAO dao = new AdDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("pageNo");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			AdDTO dto = dao.findById(num);
			
			if(dto == null) {
				return new ModelAndView("redirect:/admin/ad/list?pageNo=" + page);
			}
			
			if(!dto.getUserId().equals(info.getUserId())) {
				return new ModelAndView("redirect:/admin/ad/list?pageNo=" + page);
			}
			
			ModelAndView mav = new ModelAndView("admin/ad/write");
			
			mav.addObject("dto", dto);
			mav.addObject("pageNo", page);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/ad/list?pageNo=" + page);
	}

	@RequestMapping(value = "/admin/ad/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		AdDAO dao = new AdDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "photo";

		String page = req.getParameter("pageNo");

		try {
			AdDTO dto = new AdDTO();
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setHide(Integer.parseInt(req.getParameter("hide")));
			
			String imageFilename = req.getParameter("imageFilename");
			dto.setImageFilename(imageFilename);
			
			String filename = null;
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			//System.out.println(multiFile);
			if(multiFile != null) {
				filename = multiFile.getSaveFilename();
				fileManager.doFiledelete(pathname, imageFilename);
				dto.setImageFilename(filename);
			}
			
			dao.updateAd(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/admin/ad/list?pageNo=" + page);
	}

	@RequestMapping(value = "/admin/ad/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제 완료
		AdDAO dao = new AdDAO();
		MyUtil util = new MyUtil(); 

		String page = req.getParameter("pageNo");
		String query = "pageNo=" + page;
		
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
			
			dao.deleteAd(num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/admin/ad/list?" + query);
	}
}
