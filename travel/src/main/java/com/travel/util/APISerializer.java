package com.travel.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class APISerializer {
	// API 서버의 XML, JSON 데이터를 받아 String 으로 반환(서버에 데이터 접속해서 가져오기)
	public String receiveToString(String spec) throws Exception {
		String result = null;
		
		BufferedReader nbr = null;
		try {
			URL url = URI.create(spec).toURL();
			nbr = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = nbr.readLine()) != null) {
				sb.append(s);
			}
			
			result = sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(nbr != null) {
				try {
					nbr.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}

}
