package com.travel.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/smart/upload")
public class SmartEditorUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static int BUFFER_SIZE = 8192;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();

		String imageSaveInfo = "";
		try {
			String webPath = "/uploads/editor";
			String pathString = session.getServletContext().getRealPath(webPath);
			File f = new File(pathString);
			if (!f.exists()) {
				f.mkdirs();
			}

			String cp = req.getContextPath();

			// 이미지 파일 받기
			if (!"OPTIONS".equals(req.getMethod().toUpperCase())) {
				String filename = req.getHeader("file-name");

				InputStream is = req.getInputStream();
				String saveFilename = doFileUpload(is, filename, pathString);

				imageSaveInfo += "&bNewLine=true";
				imageSaveInfo += "&sFileName=" + filename;
				imageSaveInfo += "&sFileURL=" + cp + webPath + "/" + saveFilename;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		PrintWriter out = resp.getWriter();
		out.print(imageSaveInfo);
	}

	// 서버에 저장할 파일 이름
	protected String generateFilename(String originalFilename) {
		String filename = null;

		if (originalFilename == null || originalFilename.isBlank()) {
			return null;
		}

		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

		long nanoTime = System.nanoTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String formattedDate = sdf.format(new Date(System.currentTimeMillis()));

		// 파일 이름 생성(현재 시간 + nanoTime + 확장자)
		filename = formattedDate + nanoTime + extension;

		return filename;
	}

	// 파일 업로드
	protected String doFileUpload(InputStream is, String originalFilename, String pathString) throws Exception {
		String saveFilename = null;

		// 서버에 저장할 새로운 파일명을 만든다.
		saveFilename = generateFilename(originalFilename);
		if (saveFilename == null) {
			return null;
		}

		String pathname = pathString + File.separator + saveFilename;

		byte[] b = new byte[BUFFER_SIZE];
		int size = 0;

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pathname);

			while ((size = is.read(b)) != -1) {
				fos.write(b, 0, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e2) {
			}
			try {
				is.close();
			} catch (Exception e2) {
			}
		}

		return saveFilename;
	}
}
