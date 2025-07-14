package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.travel.model.MemberDTO;
import com.travel.util.*;

public class MemberDAO {
	private Connection conn = com.travel.util.DBConn.getConnection();
	
	public MemberDTO loginMember(String userId, String userPwd) {
		MemberDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT memberIdx, m1.userId, userPwd, userName, userLevel, profile_photo, "
					+ " register_date, modify_date, zip, addr1, addr2, locationName "
					+ " FROM member1 m1 "
					+ " LEFT OUTER JOIN member2 m2 ON m1.userId = m2.userId "
					+ " WHERE m1.userId = ? AND userPwd = ? AND enabled = 1";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, userPwd);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MemberDTO();
				
				dto.setMemberIdx(rs.getLong("memberIdx"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserPwd(rs.getString("userPwd"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserLevel(rs.getInt("userLevel"));
				dto.setProfile_photo(rs.getString("profile_photo"));
				dto.setRegister_date(rs.getString("register_date"));
				dto.setModify_date(rs.getString("modify_date"));
				dto.setZip(rs.getString("zip"));
				dto.setAddr1(rs.getString("addr1"));
				dto.setAddr2(rs.getString("addr2"));
				dto.setLocationName(rs.getString("locationName"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		
		return dto;
	}
	
	public void insertMember(MemberDTO dto) throws SQLException	{
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			conn.setAutoCommit(false);
			
			// member1 테이블과 member2 에 테이블에 회원정보 추가
			sql = "INSERT INTO member1(memberIdx, userId, userPwd, userName, userLevel, enabled, register_date, modify_date, zip, addr1, addr2, locationName) "
				    + "VALUES(member_seq.NEXTVAL, ?, ?, ?, 1, 1, SYSDATE, SYSDATE, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getUserPwd());
			pstmt.setString(3, dto.getUserName());
			pstmt.setString(4, dto.getZip());
			String addr1 = dto.getAddr1();
			pstmt.setString(5, addr1);
			pstmt.setString(6, dto.getAddr2());
			
			String locationName = addr1.substring(0, 2);
			
			pstmt.setString(7, locationName);
			
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			sql = "INSERT INTO member2(userId, email, birth, profile_photo, tel) "
					+ "VALUES(?, ?,TO_DATE(?, 'YYYY-MM-DD'), ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getEmail());
			pstmt.setString(3, dto.getBirth());
			pstmt.setString(4, dto.getProfile_photo());
			pstmt.setString(5, dto.getTel());
			
			pstmt.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) {
			DBUtil.rollback(conn);
			
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
			}
		}
	}
	
	public MemberDTO findById(String userId) {
		MemberDTO dto = null;
		
		// member1 테이블과 member2 테이블 조인
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT memberIdx, m1.userId, userPwd, userName, userLevel, enabled, register_date, modify_date, "
					+ " email, TO_CHAR(birth,'YYYY-MM-DD') birth, profile_photo, tel, zip, addr1, addr2, locationName "
					+ " FROM member1 m1 "
					+ " LEFT OUTER JOIN member2 m2 ON m1.userId = m2.userId "
					+ " WHERE m1.userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MemberDTO();
				
				dto.setMemberIdx(rs.getLong("memberIdx"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserPwd(rs.getString("userPwd"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserLevel(rs.getInt("userLevel"));
				dto.setEnabled(rs.getInt("enabled"));
				dto.setRegister_date(rs.getString("register_date"));
				dto.setModify_date(rs.getString("modify_date"));
				dto.setZip(rs.getString("zip"));
				dto.setAddr1(rs.getString("addr1"));
				dto.setAddr2(rs.getString("addr2"));
				dto.setLocationName(rs.getString("locationName"));
				
				
				dto.setEmail(rs.getString("email"));
				if(dto.getEmail() != null ) {
					String[] ss = dto.getEmail().split("@");
					if(ss.length == 2) {
						dto.setEmail1(ss[0]);
						dto.setEmail2(ss[1]);
					}
				}
				dto.setBirth(rs.getString("birth"));
				dto.setProfile_photo(rs.getString("profile_photo"));
				dto.setTel(rs.getString("tel"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
		
	}
	
	
	public void updateMember(MemberDTO dto) throws SQLException {
		// member1 테이블과 member2 테이블 수정
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			conn.setAutoCommit(false);
			
			sql = "UPDATE member1 set userPwd = ?, modify_date = SYSDATE, zip = ?, addr1 = ?, addr2 = ?, locationName = ? "
					+ "WHERE userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserPwd());
			pstmt.setString(2, dto.getZip());
			String addr1 = dto.getAddr1();
			pstmt.setString(3, addr1);
			pstmt.setString(4, dto.getAddr2());
			
			String locationName = addr1.substring(0, 2);
			
			pstmt.setString(5, locationName);
			pstmt.setString(6, dto.getUserId());
			
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			sql = "UPDATE member2 set email = ?, birth = TO_DATE(?,'YYYY-MM-DD'), "
					+ "profile_photo = ?, tel = ? "
					+ "WHERE userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getEmail());
			pstmt.setString(2, dto.getBirth());
			pstmt.setString(3, dto.getProfile_photo());
			pstmt.setString(4, dto.getTel());
			pstmt.setString(5, dto.getUserId());
			
			pstmt.executeUpdate();
			
			conn.commit();
		} catch (Exception e) {
			DBUtil.rollback(conn);
			
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
			
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {
			}
		}
		
		
	}
	
	
	public void deleteProfilePhoto(String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "UPDATE member2 SET profile_photo = null WHERE userId = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
}
