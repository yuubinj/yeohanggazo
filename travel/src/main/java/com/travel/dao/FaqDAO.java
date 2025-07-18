package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.model.FaqDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;


public class FaqDAO {
	private Connection conn = DBConn.getConnection();
	
	public List<FaqDTO> listCategory(int enabled) {
		List<FaqDTO> list = new ArrayList<FaqDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT categoryNum, category, orderNo, enabled "
					+ " FROM faqCategory ";
			if(enabled == 1) {
				sql += " WHERE enabled = 1 ";
			}
			sql += " ORDER BY orderNo ";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FaqDTO dto = new FaqDTO();
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setEnabled(rs.getInt("enabled"));
				
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

	public void insertFaqCategory(FaqDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO faqCategory(categoryNum, category, orderNo, enabled) "
					+ " VALUES (faqCategory_seq.NEXTVAL, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getCategory());
			pstmt.setInt(2, dto.getOrderNo());
			pstmt.setInt(3, dto.getEnabled());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void updateFaqCategory(FaqDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE faqCategory SET category = ?, enabled = ?, orderNo = ?"
					+ "  WHERE categoryNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getCategory());
			pstmt.setInt(2, dto.getEnabled());
			pstmt.setInt(3, dto.getOrderNo());
			pstmt.setLong(4, dto.getCategoryNum());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteFaqCategory(long categoryNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM faqCategory WHERE categoryNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, categoryNum);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void insertFaq(FaqDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO faq(num, categoryNum, memberIdx, subject, content, reg_date) "
					+ " VALUES ( faq_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getCategoryNum());
			pstmt.setLong(2, dto.getMemberIdx());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCount(long categoryNum) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM faq f "
					+ " JOIN faqCategory c ON f.categoryNum = c.categoryNum "
					+ " WHERE c.enabled = 1 ";
			if(categoryNum != 0) {
				sql += " AND f.categoryNum = ? ";
			}
								
			pstmt = conn.prepareStatement(sql);
			
			if(categoryNum != 0) {
				pstmt.setLong(1, categoryNum);
			}
			
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
	
	public List<FaqDTO> listFaq(long categoryNum, int offset, int size) {
		List<FaqDTO> list = new ArrayList<FaqDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, f.memberIdx, userId, userName, subject, ");
			sb.append("     content, f.categoryNum, category, ");
			sb.append("     TO_CHAR(f.reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM faq f ");
			sb.append(" JOIN member1 m1 ON f.memberIdx = m1.memberIdx ");
			sb.append(" JOIN faqCategory c ON f.categoryNum = c.categoryNum ");
			sb.append(" WHERE c.enabled = 1 ");
			if(categoryNum != 0) {
				sb.append("  AND f.categoryNum = ? ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			if(categoryNum != 0) {
				pstmt.setLong(1, categoryNum);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			} else {
				pstmt.setInt(1, offset);
				pstmt.setInt(2, size);
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FaqDTO dto = new FaqDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setMemberIdx(rs.getLong("memberIdx"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setReg_date(rs.getString("reg_date"));
				
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
	
	public int dataCount(long categoryNum, String schType, String kwd) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM faq f "
					+ " JOIN faqCategory c ON f.categoryNum = c.categoryNum "
					+ " WHERE c.enabled = 1 ";
			if(schType.equals("all")) { // subject 또는 content
				sql += " AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 )";
			} else { // subject, content
				sql += " AND INSTR(" + schType + ", ?) >= 1";
			}
			if(categoryNum != 0) {
				sql += " AND f.categoryNum = ? ";
			}
								
			pstmt = conn.prepareStatement(sql);
			
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
				if(categoryNum != 0) {
					pstmt.setLong(3, categoryNum);
				}
			} else {
				pstmt.setString(1, kwd);
				if(categoryNum != 0) {
					pstmt.setLong(2, categoryNum);
				}
			}
			
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
	
	public List<FaqDTO> listFaq(long categoryNum, int offset, int size, String schType, String kwd) {
		List<FaqDTO> list = new ArrayList<FaqDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, f.memberIdx, userId, userName, subject, ");
			sb.append("     content, f.categoryNum, category, ");
			sb.append("     TO_CHAR(f.reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM faq f ");
			sb.append(" JOIN member1 m1 ON f.memberIdx = m1.memberIdx ");
			sb.append(" JOIN faqCategory c ON f.categoryNum = c.categoryNum ");
			sb.append(" WHERE c.enabled = 1 ");
			if(schType.equals("all")) {
				sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
			} else {
				sb.append(" AND INSTR(" + schType + ", ?) >= 1");
			}
			if(categoryNum != 0) {
				sb.append("  AND  f.categoryNum = ? ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
				if(categoryNum != 0) {
					pstmt.setLong(3, categoryNum);
					pstmt.setInt(4, offset);
					pstmt.setInt(5, size);
				} else {
					pstmt.setInt(3, offset);
					pstmt.setInt(4, size);
				}
			} else {
				pstmt.setString(1, kwd);
				if(categoryNum != 0) {
					pstmt.setLong(2, categoryNum);
					pstmt.setInt(3, offset);
					pstmt.setInt(4, size);
				} else {
					pstmt.setInt(2, offset);
					pstmt.setInt(3, size);
				}
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				FaqDTO dto = new FaqDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setMemberIdx(rs.getLong("memberIdx"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setReg_date(rs.getString("reg_date"));
				
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
	
	public FaqDTO findById(long num) {
		FaqDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT f.num, f.memberIdx, userId, userName, subject, "
					+ "   content, f.categoryNum, category, reg_date "
					+ " FROM faq f "
					+ " JOIN member1 m ON f.memberIdx = m.memberIdx "
					+ " JOIN faqCategory c ON f.categoryNum = c.categoryNum "
					+ " WHERE f.num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new FaqDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMemberIdx(rs.getLong("memberIdx"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setReg_date(rs.getString("reg_date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}
	
	public void updateFaq(FaqDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE faq SET subject = ?, content = ?, categoryNum = ? "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getCategoryNum());
			pstmt.setLong(4, dto.getNum());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteFaq(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM faq WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
}
