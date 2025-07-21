package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.model.ScrapDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;

public class ScrapDAO {
	private Connection conn = DBConn.getConnection();
	
	public List<String> selectScrapApiId(String userId) throws SQLException {
		List<String> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
		sql = " SELECT apiId FROM scrap WHERE userId = ? ";
		pstmt =  conn.prepareStatement(sql);

		pstmt.setString(1, userId);

		rs = pstmt.executeQuery();

		while(rs.next()) {
		list.add(rs.getString("apiId"));
		}

		} catch (Exception e) {
		e.printStackTrace();
		throw e;
		} finally {
		DBUtil.close(rs);
		DBUtil.close(pstmt);
		}

		return list;

	}
	
	
	
	public List<ScrapDTO> mypageListScrap(int offset, int size, String userId) {
		List<ScrapDTO> list = new ArrayList<ScrapDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT s.userId, s.apiId, s.apiTypeId, s.scrapName, s.scrapAddr, s.scrapImg, ");
			sb.append(" TO_CHAR(s.scrap_date, 'YYYY-MM-DD') scrap_date, s.locationName ");
			sb.append(" FROM SCRAP s ");
			sb.append(" JOIN member1 m ON s.userId = m.userId ");
			sb.append(" WHERE s.userId = ? ");
			sb.append(" ORDER BY s.scrap_date DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ScrapDTO dto = new ScrapDTO();
				
				dto.setApiId(rs.getString("apiId"));
				dto.setApiTypeId(rs.getString("apiTypeId"));
				dto.setScrapName(rs.getString("scrapName"));
				dto.setScrapAddr(rs.getString("scrapAddr"));
				dto.setScrapImg(rs.getString("scrapImg"));
				dto.setScrap_date(rs.getString("scrap_date"));
				dto.setLocationName(rs.getString("locationName"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	public int dataCount(String userId) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try {
			sql = "SELECT COUNT(*) FROM SCRAP WHERE userId = ?  ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return result;
	}
	
	
	public void insertScrap(ScrapDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
		sql = "INSERT INTO scrap(userId, apiId, apiTypeId, scrapName, scrapAddr, scrapImg, scrap_date, locationName) "
		+ " VALUES(?, ?, ?, ?, ?, ?, SYSDATE, ?) ";

		pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, dto.getUserId()); // 로그인한 사람만 스크랩 가능(로그인 정보)
		pstmt.setString(2, dto.getApiId()); // api 고유 아이디(누르면 정보 저장)
		pstmt.setString(3, dto.getApiTypeId()); // api 고유 타입
		pstmt.setString(4, dto.getScrapName()); // api 제목
		pstmt.setString(5, dto.getScrapAddr()); // api 주소
		pstmt.setString(6, dto.getScrapImg()); // api 이미지
		pstmt.setString(7, dto.getLocationName()); // 서울, 경기 등의 지역이름(addr에서 잘라서 가져오기)

		pstmt.executeUpdate();

		} catch (Exception e) {
		e.printStackTrace();
		throw e;
		} finally {
		DBUtil.close(pstmt);
		}
	}
	
	public void deleteScrap(ScrapDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
		sql = " DELETE FROM scrap WHERE userId = ? AND apiId = ?";

		pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, dto.getUserId());
		pstmt.setString(2, dto.getApiId());

		pstmt.executeUpdate();

		} catch (SQLException e) {
		e.printStackTrace();
		throw e;
		} finally {
		DBUtil.close(pstmt);
		}
	}
	
	
	public void mypageDeleteScrap(String userId, String apiId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "delete from scrap where userId = ? and apiId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, apiId);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public ScrapDTO findById(String userId) {
		ScrapDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT s.userId, apiId, apiTypeId, scrapName, scrapAddr, scrapImg, "
					+ " TO_CHAR(scrap_date,'YYYY-MM-DD') scrap_date, locationName "
					+ " FROM SCRAP s "
					+ " JOIN member1 m ON s.userId = m.userId "
					+ " where userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			dto = new ScrapDTO();
			if(rs.next()) {
				dto.setUserId(rs.getString("userId"));
				dto.setApiId(rs.getString("apiId"));
				dto.setApiTypeId(rs.getString("apiTypeId"));
				dto.setScrapName(rs.getString("scrapName"));
				dto.setScrapAddr(rs.getString("scrapAddr"));
				dto.setScrapImg(rs.getString("scrapImg"));
				dto.setScrap_date(rs.getString("scrap_date"));
				dto.setLocationName(rs.getString("locationName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	
}
