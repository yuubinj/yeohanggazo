package com.travel.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class MyUtil {
	/**
	 * 전체 페이지 수 구하기
	 * 
	 * @param dataCount 총 데이터 개수
	 * @param size      한화면에 출력할 목록 수
	 * @return 전체 페이지 수
	 */
	public int pageCount(int dataCount, int size) {
		if (dataCount <= 0 || size <= 0) {
			return 0;
		}

		return dataCount / size + (dataCount % size > 0 ? 1 : 0);
	}

	/**
	 * 페이징(paging) 처리(GET 방식)
	 * 
	 * @param current_page 현재 표시되는 페이지 번호
	 * @param total_page   전체 페이지 수
	 * @param list_url     링크를 설정할 주소
	 * @return 페이징 처리 결과
	 */
	public String paging(int current_page, int total_page, String list_url) {
		StringBuilder sb = new StringBuilder();

		int numPerBlock = 10;
		int currentPageSetup;
		int n, page;

		if (current_page < 1 || total_page < current_page || list_url == null) {
			return "";
		}

		// list_url += list_url.indexOf("?") != -1 ? "&" : "?";
		list_url += list_url.contains("?") ? "&" : "?";

		// currentPageSetup : 표시할첫페이지-1
		currentPageSetup = (current_page / numPerBlock) * numPerBlock;
		if (current_page % numPerBlock == 0) {
			currentPageSetup = currentPageSetup - numPerBlock;
		}

		sb.append("<div class='paginate'>");
		// 처음페이지, 이전(10페이지 전)
		n = current_page - numPerBlock;
		if (total_page > numPerBlock && currentPageSetup > 0) {
			sb.append("<a href='" + list_url + "page=1' title='처음'>&#x226A</a>");
			sb.append("<a href='" + list_url + "page=" + n + "' title='이전'>&#x003C</a>");
		}

		// 페이징
		page = currentPageSetup + 1;
		while (page <= total_page && page <= (currentPageSetup + numPerBlock)) {
			if (page == current_page) {
				sb.append("<span>" + page + "</span>");
			} else {
				sb.append("<a href='" + list_url + "page=" + page + "'>" + page + "</a>");
			}
			page++;
		}

		// 다음(10페이지 후), 마지막페이지
		n = current_page + numPerBlock;
		if (n > total_page) {
			n = total_page;
		}
		if (total_page - currentPageSetup > numPerBlock) {
			sb.append("<a href='" + list_url + "page=" + n + "' title='다음'>&#x003E</a>");
			sb.append("<a href='" + list_url + "page=" + total_page + "' title='마지막'>&#x226B</a>");
		}
		sb.append("</div>");

		return sb.toString();
	}
	
	public String pagingNo(int current_page, int total_page, String list_url) {
		StringBuilder sb = new StringBuilder();
		
		int numPerBlock = 10;
		int currentPageSetup;
		int n, page;
		
		if (current_page < 1 || total_page < current_page || list_url == null) {
			return "";
		}
		
		// list_url += list_url.indexOf("?") != -1 ? "&" : "?";
		list_url += list_url.contains("?") ? "&" : "?";
		
		// currentPageSetup : 표시할첫페이지-1
		currentPageSetup = (current_page / numPerBlock) * numPerBlock;
		if (current_page % numPerBlock == 0) {
			currentPageSetup = currentPageSetup - numPerBlock;
		}
		
		sb.append("<div class='paginate'>");
		// 처음페이지, 이전(10페이지 전)
		n = current_page - numPerBlock;
		if (total_page > numPerBlock && currentPageSetup > 0) {
			sb.append("<a href='" + list_url + "pageNo=1' title='처음'>&#x226A</a>");
			sb.append("<a href='" + list_url + "pageNo=" + n + "' title='이전'>&#x003C</a>");
		}
		
		// 페이징
		page = currentPageSetup + 1;
		while (page <= total_page && page <= (currentPageSetup + numPerBlock)) {
			if (page == current_page) {
				sb.append("<span>" + page + "</span>");
			} else {
				sb.append("<a href='" + list_url + "pageNo=" + page + "'>" + page + "</a>");
			}
			page++;
		}
		
		// 다음(10페이지 후), 마지막페이지
		n = current_page + numPerBlock;
		if (n > total_page) {
			n = total_page;
		}
		if (total_page - currentPageSetup > numPerBlock) {
			sb.append("<a href='" + list_url + "pageNo=" + n + "' title='다음'>&#x003E</a>");
			sb.append("<a href='" + list_url + "pageNo=" + total_page + "' title='마지막'>&#x226B</a>");
		}
		sb.append("</div>");
		
		return sb.toString();
	}

	/**
	 * javascript로 페이징(paging) 처리 : javascript 지정 함수 호출
	 * 
	 * @param current_page 현재 표시되는 페이지 번호
	 * @param total_page   전체 페이지 수
	 * @param methodName   호출할 자바 스크립트 함수명
	 * @return 페이징 처리 결과
	 */
	public String pagingMethod(int current_page, int total_page, String methodName) {
		StringBuilder sb = new StringBuilder();

		int numPerBlock = 10; // 리스트에 나타낼 페이지 수
		int currentPageSetUp;
		int n, page;

		if (current_page < 1 || total_page < current_page) {
			return "";
		}

		// currentPageSetup : 표시할첫페이지-1
		currentPageSetUp = (current_page / numPerBlock) * numPerBlock;
		if (current_page % numPerBlock == 0) {
			currentPageSetUp = currentPageSetUp - numPerBlock;
		}

		sb.append("<div class='paginate'>");

		// 처음페이지, 이전(10페이지 전)
		n = current_page - numPerBlock;
		if ((total_page > numPerBlock) && (currentPageSetUp > 0)) {
			sb.append("<a onclick='" + methodName + "(1);' title='처음'>&#x226A</a>");
			sb.append("<a onclick='" + methodName + "(" + n + ");' title='이전'>&#x003C</a>");
		}

		// 페이지징
		page = currentPageSetUp + 1;
		while ((page <= total_page) && (page <= currentPageSetUp + numPerBlock)) {
			if (page == current_page) {
				sb.append("<span>" + page + "</span>");
			} else {
				sb.append("<a onclick='" + methodName + "(" + page + ");'>" + page + "</a>");
			}
			page++;
		}

		// 다음(10페이지 후), 마지막 페이지
		n = current_page + numPerBlock;
		if (n > total_page)
			n = total_page;
		if (total_page - currentPageSetUp > numPerBlock) {
			sb.append("<a onclick='" + methodName + "(" + n + ");' title='다음'>&#x003E</a>");
			sb.append("<a onclick='" + methodName + "(" + total_page + ");' title='마지막'>&#x226B</a>");
		}
		sb.append("</div>");

		return sb.toString();
	}

	// 화면에 표시할 페이지를 중앙에 출력
	public String pagingUrl(int current_page, int total_page, String list_url) {
		StringBuilder sb = new StringBuilder();

		int numPerBlock = 10;
		int n, page;

		if (current_page < 1 || total_page < current_page || list_url == null) {
			return "";
		}

		list_url += list_url.contains("?") ? "&" : "?";

		page = 1; // 출력할 시작 페이지
		if (current_page > (numPerBlock / 2) + 1) {
			page = current_page - (numPerBlock / 2);

			n = total_page - page;
			if (n < numPerBlock) {
				page = total_page - numPerBlock + 1;
			}

			if (page < 1)
				page = 1;
		}

		sb.append("<div class='paginate'>");

		// 처음페이지
		if (page > 1) {
			sb.append("<a href='" + list_url + "page=1' title='처음'>&#x226A</a>");
		}

		// 이전(한페이지 전)
		n = current_page - 1;
		if (current_page > 1) {
			sb.append("<a href='" + list_url + "page=" + n + "' title='이전'>&#x003C</a>");
		}

		n = page;
		while (page <= total_page && page < n + numPerBlock) {
			if (page == current_page) {
				sb.append("<span>" + page + "</span>");
			} else {
				sb.append("<a href='" + list_url + "page=" + page + "'>" + page + "</a>");
			}
			page++;
		}

		// 다음(한페이지 다음)
		n = current_page + 1;
		if (current_page < total_page) {
			sb.append("<a href='" + list_url + "page=" + n + "' title='다음'>&#x003E</a>");
		}

		// 마지막페이지
		if (page <= total_page) {
			sb.append("<a href='" + list_url + "page=" + total_page + "' title='마지막'>&#x226B</a>");
		}

		sb.append("</div>");

		return sb.toString();
	}

	// 화면에 표시할 페이지를 중앙에 출력 : javascript 함수 호출
	public String pagingFunc(int current_page, int total_page, String methodName) {
		StringBuilder sb = new StringBuilder();

		int numPerBlock = 10;
		int n, page;

		if (current_page < 1 || total_page < current_page) {
			return "";
		}

		page = 1; // 출력할 시작 페이지
		if (current_page > (numPerBlock / 2) + 1) {
			page = current_page - (numPerBlock / 2);

			n = total_page - page;
			if (n < numPerBlock) {
				page = total_page - numPerBlock + 1;
			}

			if (page < 1)
				page = 1;
		}

		sb.append("<div class='paginate'>");

		// 처음페이지
		if (page > 1) {
			sb.append("<a onclick='" + methodName + "(1);' title='처음'>&#x226A</a>");
		}

		// 이전(한페이지 전)
		n = current_page - 1;
		if (current_page > 1) {
			sb.append("<a onclick='" + methodName + "(" + n + ");' title='이전'>&#x003C</a>");
		}

		n = page;
		while (page <= total_page && page < n + numPerBlock) {
			if (page == current_page) {
				sb.append("<span>" + page + "</span>");
			} else {
				sb.append("<a onclick='" + methodName + "(" + page + ");' >" + page + "</a>");
			}
			page++;
		}

		// 다음(한페이지 다음)
		n = current_page + 1;
		if (current_page < total_page) {
			sb.append("<a onclick='" + methodName + "(" + n + ");' title='다음'>&#x003E</a>");
		}

		// 마지막페이지
		if (page <= total_page) {
			sb.append("<a onclick='" + methodName + "(" + total_page + ");' title='마지막'>&#x226B</a>");
		}

		sb.append("</div>");

		return sb.toString();
	}
	
	/**
	 * 문자열을 주소형식으로 인코딩
	 * @param str 인코딩할 문자열
	 * @return 주소형식으로 인코딩된 문자열
	 */
	public String encodeUrl(String str) {
		 if (str == null) {
			 return null;
		 }
		 
		 try {
			str = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		}
		
		return str;
	}

	/**
	 * 주소 형식의 문자열을 디코딩
	 * @param str 디코딩할 인코딩된 문자열
	 * @return 디코딩된 문자열
	 */
	public String decodeUrl(String str) {
		Pattern pattern = Pattern.compile(".*%[0-9a-fA-F]{2}.*");
		
		 if (str == null) {
			 return null;
		 }
		 
		 try {
			  if (! str.contains("%")) { // % 가 없으면 디코딩하지 않아도 됨
				  return str;
			  }
			  
			   // 인코딩 패턴이 유효한 경우만 디코딩 시도
			  if (pattern.matcher(str).matches()) {
				  return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
			  }
		} catch (IllegalArgumentException e) {
			// 잘못된 % 인코딩 형식(%가 있지만 2자리 16진수가 아닌 경우)이 존재하는 경우
		} catch (UnsupportedEncodingException e) {
		}
		
		return str;
	}
	
	/**
	 * 특수문자를 HTML 문자로 변경 및 엔터를 <br> 로 변경
	 * 
	 * @param str 변경할 문자열
	 * @return HTML 문자로 변경된 문자열
	 */
	public String htmlSymbols(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}

		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("<", "&lt;");

		str = str.replaceAll("\n", "<br>");
		str = str.replaceAll("\\s", "&nbsp;"); // \\s가 엔터도 변경하므로 \n보다 뒤에

		return str;
	}

	/**
	 * E-Mail 검사
	 * 
	 * @param email 검사 할 E-Mail
	 * @return E-Mail 검사 결과
	 */
	public boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}

		return Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());
	}
}
