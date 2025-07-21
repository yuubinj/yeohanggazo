package com.travel.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import com.travel.mvc.annotation.RequestMapping;
import com.travel.dao.MemberDAO;
import com.travel.model.MemberDTO;
import com.travel.model.SessionInfo;
import com.travel.mvc.annotation.Controller;
import com.travel.mvc.annotation.RequestMethod;
import com.travel.mvc.annotation.ResponseBody;
import com.travel.mvc.view.ModelAndView;
import com.travel.util.FileManager;
import com.travel.util.MyMultipartFile;

@Controller
public class MemberController {
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public String loginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 폼
		return "member/login";
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public ModelAndView loginSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 처리
		MemberDAO dao = new MemberDAO();
		HttpSession session = req.getSession();
		
		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");
		
		MemberDTO dto = dao.loginMember(userId, userPwd);
		if(dto != null) {
			// 로그인 성공한 경우
			// 회원 이름, 권한등을 세션에 저장
			
			session.setMaxInactiveInterval(20 * 60);
			
			// 세션에 저장할 내용
			SessionInfo info = new SessionInfo();
			info.setMemberIdx(dto.getMemberIdx());
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());
			info.setAvatar(dto.getProfile_photo());
			info.setUserLevel(dto.getUserLevel());
			info.setLocationName(dto.getLocationName());
			info.setRegister_date(dto.getRegister_date());
			info.setEmail(dto.getEmail());
			info.setTel(dto.getTel());
			
			// 세션에 member 라는 이름으로 로그인 정보 저장
			session.setAttribute("member", info);
			
			// 로그인 전 주소가 존재하는 경우 로그인 전 주소로 리다이렉트
			String preLoginURI = (String)session.getAttribute("preLoginURI");

			session.removeAttribute("preLoginURI");
			
			if(preLoginURI != null && preLoginURI.contains("/tour/apiIds")) {
				return new ModelAndView("redirect:/location/main");
	        }
			
			if(preLoginURI != null) {
				return new ModelAndView(preLoginURI);
			}
			
			// 메인화면으로 리다이렉트
			return new ModelAndView("redirect:/");

		}
		
		// 로그인 실패한 경우 로그인 페이지로 포워딩
		ModelAndView mav = new ModelAndView("member/login");
		
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		mav.addObject("message", msg); // req.setAttribuite("message", msg);
		
		return mav;
	}
	
	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그아웃
		
		HttpSession session = req.getSession();
		
		// member라는 이름으로 세션에 저장된 속성 삭제
		session.removeAttribute("member");
		
		// 세션에 저장된 모든 속성을 지우고, 세션을 초기화
		session.invalidate();
		
		// 메인화면으로 리다이렉트
		return "redirect:/";
	}
	
	@RequestMapping(value = "/member/account", method = RequestMethod.GET)
	public ModelAndView accountForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원가입 폼
		ModelAndView mav = new ModelAndView("member/member");
		mav.addObject("mode", "account");
		return mav;
	}

	@RequestMapping(value = "/member/account", method = RequestMethod.POST)
	public ModelAndView accountSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원 가입
		MemberDAO dao = new MemberDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "member";
		
		String message = "";
		
		try {
			MemberDTO dto = new MemberDTO();
			
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			
			dto.setBirth(req.getParameter("birth"));
			String e1 = req.getParameter("email1");
			String e2 = req.getParameter("email2");
			dto.setEmail(e1 + "@" + e2);
			dto.setTel(req.getParameter("tel"));
			dto.setZip(req.getParameter("zip"));
			dto.setAddr1(req.getParameter("addr1"));
			dto.setAddr2(req.getParameter("addr2"));
			
			// 프로파일 포토
			Part p = req.getPart("selectFile");
			MyMultipartFile multiPart = fileManager.doFileUpload(p, pathname);
			if(multiPart != null) {
				dto.setProfile_photo(multiPart.getSaveFilename());
			}
			
			// 테이블에 저장
			dao.insertMember(dto);
			
			// 회원가입 메시지를 출력하기 위해
			session.setAttribute("mode", "account");
			session.setAttribute("userName", dto.getUserName());
			
			return new ModelAndView("redirect:/member/complete");
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				message = "이미 등록된 아이디 입니다.";
			} else if(e.getErrorCode() == 1400) {
				message = "필수 입력 사항을 입력하지 않았습니다.";
			} else if(e.getErrorCode() == 1840 || e.getErrorCode() == 1861) {
				message = "날짜 형식이 올바르지 않습니다.";
			} else {
				message = "회원 가입이 실패했습니다.";
			}
		} catch (Exception e) {
			message = "회원 가입이 실패했습니다.";
			e.printStackTrace();
		}
		
		ModelAndView mav = new ModelAndView("member/member");
		mav.addObject("mode", "account");
		mav.addObject("message", message);
		return mav;
	}
	
	@RequestMapping(value = "/member/complete", method = RequestMethod.GET)
	public ModelAndView complete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원가입 / 회원 수정 완료후 / 회원탈퇴 완료후
		HttpSession session = req.getSession();
		
		String mode = (String)session.getAttribute("mode");
		String userName = (String)session.getAttribute("userName");
		
		session.removeAttribute("mode");
		session.removeAttribute("userName");
		
		if(mode == null) {
			return new ModelAndView("redirect:/");
		}
		
		String title = null;
		String message = "<b>" + userName + "</b>님 ";

		if(mode.equals("account")) {
			title = "회원가입";
			message += "회원가입이 완료 되었습니다.<br>로그인 하시면 정보를 이용할수 있습니다.";
		} else if(mode.equals("update")) {
			title = "정보수정";
			message += "회원정보가 수정되었습니다.<br>메인 화면으로 이동하시기 바랍니다.";
		} else {
			title = "회원탈퇴";
			message += "회원탈퇴가 완료되었습니다.<br>메인 화면으로 이동하시기 바랍니다.";
		}
		ModelAndView mav = new ModelAndView("member/complete");
		
		mav.addObject("title", title);
		mav.addObject("message", message);
		
		return mav;
	}

	// AJAX - JSON
	@ResponseBody // Map을 JSON 형식의 문자열로 변환하여 응답
	@RequestMapping(value = "/member/userIdCheck", method = RequestMethod.POST)
	public Map<String, Object> userIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		MemberDAO dao = new MemberDAO();
		
		String userId = req.getParameter("userId");
		MemberDTO dto = dao.findById(userId);
		
		String passed = "false";
		if(dto == null) {
			passed = "true";
		}
		
		model.put("passed", passed);
		
		return model;
	}
	
	@RequestMapping(value = "/member/pwd", method = RequestMethod.GET)
	public ModelAndView pwdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 패스워드 확인 폼(정보수정/회원탈퇴)
		ModelAndView mav = new ModelAndView("member/pwd");
		
		String mode = req.getParameter("mode");
		
		mav.addObject("mode", mode);
		
		return mav;
	}

	@RequestMapping(value = "/member/pwd", method = RequestMethod.POST)
	public ModelAndView pwdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 패스워드 확인
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			MemberDTO dto = dao.findById(info.getUserId());
			if(dto == null) {
				session.invalidate();
				return new ModelAndView("redirect:/");
			}
			
			String userPwd = req.getParameter("userPwd");
			String mode = req.getParameter("mode");
			
			if(! dto.getUserPwd().equals(userPwd)) {
				ModelAndView mav = new ModelAndView("member/pwd");
				
				mav.addObject("mode", mode);
				mav.addObject("message", "패스워드가 일치하지 않습니다.");
				
				return mav;
			}
			
			if(mode.equals("delete")) {
				// 회원 탈퇴
				dao.deleteMember(info.getUserId());
				
				session.removeAttribute("member");
				session.setAttribute("mode", "delete");
			    session.setAttribute("userName", info.getUserName());
			    
				return new ModelAndView("redirect:/member/complete");
			} 
			
			// 정보수정 화면
			ModelAndView mav = new ModelAndView("member/member");
			
			mav.addObject("dto", dto);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping(value = "/member/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 회원정보수정완료
		MemberDAO dao = new MemberDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "member";
		
		try {
			MemberDTO dto = new MemberDTO();
			
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			
			dto.setBirth(req.getParameter("birth"));
			String e1 = req.getParameter("email1");
			String e2 = req.getParameter("email2");
			dto.setEmail(e1 + "@" + e2);
			dto.setTel(req.getParameter("tel"));
			dto.setZip(req.getParameter("zip"));
			dto.setAddr1(req.getParameter("addr1"));
			dto.setAddr2(req.getParameter("addr2"));
			
			// 프로파일 포토
			dto.setProfile_photo(req.getParameter("profile_photo"));
			Part p = req.getPart("selectFile");
			MyMultipartFile multiPart = fileManager.doFileUpload(p, pathname);
			if(multiPart != null) {
				// 기존사진 삭제
				if(dto.getProfile_photo().length() != 0) {
					fileManager.doFiledelete(pathname, dto.getProfile_photo());
				}
				
				dto.setProfile_photo(multiPart.getSaveFilename());
			}
			
			dao.updateMember(dto);
			
			info.setAvatar(dto.getProfile_photo());
			
			session.setAttribute("mode", "update");
			session.setAttribute("userName", dto.getUserName());
			
			return new ModelAndView("redirect:/member/complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/");
	}
	
	@ResponseBody
	@RequestMapping(value = "/member/deleteProfile", method = RequestMethod.POST)
	public Map<String, Object> deleteProfilePhoto(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 프로파일 포토 삭제 - AJAX
		Map<String, Object> model = new HashMap<String, Object>();
		
		MemberDAO dao = new MemberDAO();
		FileManager fileManager = new FileManager();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "member";
		
		String state = "false";
		
		try {
			String profile_photo = req.getParameter("profile_photo");
			if(profile_photo != null && profile_photo.length() != 0) {
				fileManager.doFiledelete(pathname, profile_photo);
				
				dao.deleteProfilePhoto(info.getUserId());
				
				info.setAvatar("");
				state = "true";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		
		return model;
	}
	
	@RequestMapping(value = "/member/noAuthorized", method = RequestMethod.GET)
	public String noAuthorized(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 권한이 없는 경우
		return "member/noAuthorized";
	}
}
