package com.travel.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class FileManager {
	private static final int BUFFER_SIZE = 8192;

	/**
	 * 파일 다운로드 메소드
	 * 
	 * @param saveFilename     서버에저장된파일명
	 * @param originalFilename 클라이언트가업로드한파일명
	 * @param pathname         서버에저장된경로
	 * @param resp             HttpServletResponse 객체
	 * @return 다운로드성공여부
	 */
	public boolean doFiledownload(String saveFilename, String originalFilename, String pathname,
			HttpServletResponse resp) {
		boolean flag = false;

		BufferedInputStream bis = null;
		OutputStream os = null;

		try {
			// originalFilename = new String(originalFilename.getBytes("euc-kr"), "8859_1");
			originalFilename = new String(originalFilename.getBytes("utf-8"), "8859_1");
			pathname = pathname + File.separator + saveFilename;
			File f = new File(pathname);
			if (! f.exists()) {
				return flag;
			}

			// 클라이언트에게 전송 할 문서타입을 스트림으로 설정
			resp.setContentType("application/octet-stream");

			// 파일명은 헤더에 실어서 전송
			resp.setHeader("Content-disposition", "attachment;filename=" + originalFilename);

			// 클라이언트에게 파일의 내용을 전송
			byte[] b = new byte[BUFFER_SIZE];
			bis = new BufferedInputStream(new FileInputStream(f));

			// 클라이언트에게 전송할 출력 스트림
			os = resp.getOutputStream();

			int n;
			while ((n = bis.read(b)) != -1) {
				os.write(b, 0, n);
			}
			os.flush();

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e2) {
				}
			}

			if (os != null) {
				try {
					os.close();
				} catch (Exception e2) {
				}
			}
		}

		return flag;
	}

	/**
	 * 단일파일 업로드
	 * 
	 * @param part     클라이언트가 서버로 전송한 파일에 대한 Part 객체
	 * @param pathname 파일을 저장할 경로
	 * @return 업로드된 파일의 정보들을 가지고 있는 List<MyMultipartFile> 객체
	 */
	public MyMultipartFile doFileUpload(Part part, String pathname) {
		MyMultipartFile multiPartFile = null;

		try {
			File f = new File(pathname);
			if (!f.exists()) { // 폴더가 존재하지 않으면
				f.mkdirs();
			}

			String originalFilename = getOriginalFilename(part);
			if (originalFilename == null || originalFilename.length() == 0) {
				return multiPartFile;
			}

			multiPartFile = new MyMultipartFileImpl(part, pathname, originalFilename);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return multiPartFile;
	}

	/**
	 * 다중파일 업로드
	 * 
	 * @param parts    클라이언트가 서버로 전송한 모든 Part 객체
	 * @param pathname 파일을 저장할 경로
	 * @return 업로드된 파일의 정보들을 가지고 있는 List<MyMultipartFile> 객체
	 */
	public List<MyMultipartFile> doFileUpload(Collection<Part> parts, String pathname) {
		List<MyMultipartFile> list = new ArrayList<>();

		try {
			File f = new File(pathname);
			if (! f.exists()) { // 폴더가 존재하지 않으면
				f.mkdirs();
			}

			String originalFilename;
			for (Part part : parts) {
				String contentType = part.getContentType();

				// contentType 가 null 인 경우는 파일이 아닌 경우이다.(<input type="text"... 등)
				if (contentType != null) { // 파일이면
					originalFilename = getOriginalFilename(part);
					if (originalFilename == null || part.getSize() == 0) {
						continue;
					}

					list.add(new MyMultipartFileImpl(part, pathname, originalFilename));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private class MyMultipartFileImpl implements MyMultipartFile {
		private final Part part;
		private final String pathname;
		private final String originalFilename;
		private String saveFilename;

		public MyMultipartFileImpl(Part part, String pathname, String originalFilename)
				throws IOException, IllegalStateException {
			this.part = part;
			this.pathname = pathname;
			this.originalFilename = originalFilename;

			transferTo();
		}

		@Override
		public String getOriginalFilename() {
			return this.originalFilename;
		}

		@Override
		public String getSaveFilename() {
			return this.saveFilename;
		}

		@Override
		public long getSize() {
			return this.part.getSize();
		}

		private void transferTo() throws IOException, IllegalStateException {
			String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));

			saveFilename = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", Calendar.getInstance()) 
					+ System.nanoTime()
					+ fileExt;

			String fullpathname = pathname + File.separator + saveFilename;
			File dest = new File(fullpathname);
			this.part.write(dest.getPath());
		}
	}

	private String getOriginalFilename(Part p) throws Exception {
		try {
			for (String s : p.getHeader("content-disposition").split(";")) {
				if (s.trim().startsWith("filename")) {
					return s.substring(s.indexOf("=") + 1).trim().replace("\"", "");
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return null;
	}

	/**
	 * 파일 삭제
	 * 
	 * @param pathname 파일이 저장된 경로
	 * @param filename 삭제할 파일명
	 * @return 파일 삭제 성공 여부
	 */
	public boolean doFiledelete(String pathname, String filename) {
		String path = pathname + File.separator + filename;

		return doFiledelete(path);
	}

	/**
	 * 파일 삭제
	 * 
	 * @param pathname 파일이 저장된 경로 및 삭제할 파일명
	 * @return 파일 삭제 성공 여부
	 */
	public boolean doFiledelete(String pathname) {
		try {
			File f = new File(pathname);

			if (! f.exists()) { // 파일이 없으면
				return false;
			}

			f.delete();
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
