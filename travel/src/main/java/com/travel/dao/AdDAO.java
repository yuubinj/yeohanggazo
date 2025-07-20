package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.model.AdDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;

public class AdDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertAd(AdDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO ad (num, userId, subject, imageFilename, reg_date, hide) "
					+ " VALUES(ad_seq.NEXTVAL, ?, ?, ?, SYSDATE, 0) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getImageFilename());

			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM ad";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return result;
	}

	public int dataCount(String schType, String kwd) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) "
					+ " FROM ad a "
					+ " JOIN member1 m ON a.userId = m.userId "
					+ " WHERE ";
			if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += " TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += " INSTR(" + schType + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, kwd);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return result;
	}
	
	public List<AdDTO> listAdMain(int offset, int size) {
		List<AdDTO> list = new ArrayList<AdDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, a.userId, userName, subject, imageFilename, ");
			sb.append("     TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM ad a ");
			sb.append(" JOIN member1 m ON a.userId = m.userId ");
			sb.append(" WHERE hide = 0 ");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				AdDTO dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	public List<AdDTO> listAd(int offset, int size) {
		List<AdDTO> list = new ArrayList<AdDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT num, a.userId, userName, subject, imageFilename, ");
			sb.append("     TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM ad a ");
			sb.append(" JOIN member1 m ON a.userId = m.userId ");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				AdDTO dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return list;
	}
	
	public List<AdDTO> listAd(int offset, int size, String schType, String kwd) {
		List<AdDTO> list = new ArrayList<AdDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT num, a.userId, userName, subject, imageFilename, ");
			sb.append("      TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM ad a ");
			sb.append(" JOIN member1 m ON a.userId = m.userId ");
			sb.append(" WHERE ");
			if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append(" INSTR(" + schType + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, kwd);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				AdDTO dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return list;
	}
	
	public AdDTO findById(long num) {
		AdDTO dto = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT num, a.userId, userName, subject, reg_date, imageFilename, hide "
					+ " FROM ad a "
					+ " JOIN member1 m ON a.userId=m.userId  "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHide(rs.getInt("hide"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}

	public AdDTO findByPrev(long num, String schType, String kwd) {
		AdDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM ad a ");
				sb.append(" JOIN member1 m ON a.userId = m.userId ");
				sb.append(" WHERE ( hide = 0 AND num > ? ) ");
				if (schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + schType + ", ?) >= 1 ) ");
				}
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);

			} else {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM ad ");
				sb.append(" WHERE hide = 0 AND num > ? ");
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}

	public AdDTO findByNext(long num, String schType, String kwd) {
		AdDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM ad a ");
				sb.append(" JOIN member1 m ON a.userId = m.userId ");
				sb.append(" WHERE (  hide = 0 AND num < ? ) ");
				if (schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + schType + ", ?) >= 1 ) ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);

			} else {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM ad ");
				sb.append(" WHERE  hide = 0 AND num < ? ");
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new AdDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}
	
	public void updateAd(AdDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE ad SET subject = ?, imageFilename = ?, hide = ? "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getImageFilename());
			pstmt.setInt(3, dto.getHide());
			pstmt.setLong(4, dto.getNum());

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}

	public void deleteAd(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM ad WHERE num = ?";
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
