package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.model.MyTripDTO;
import com.travel.model.MyTripReplyDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;
import com.travel.util.MyMultipartFile;

public class MyTripDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertMyTrip(MyTripDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;
		
		try {
			sql = "SELECT myTrip_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = " INSERT INTO myTrip(num, userId, subject, content, hitCount, reg_date) "
					+ " VALUES (?, ?, ?, ?, 0, SYSDATE) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if(dto.getListFile().size() != 0) {
				sql = "INSERT INTO myTripFile(fileNum, num, saveFilename, originalFilename, fileSize) "
						+ " VALUES(myTripFile_seq.NEXTVAL, ?, ?, ?, ?) ";
				pstmt = conn.prepareStatement(sql);
				
				for(MyMultipartFile mf : dto.getListFile()) {
					pstmt.setLong(1, dto.getNum());
					pstmt.setString(2, mf.getSaveFilename());
					pstmt.setString(3, mf.getOriginalFilename());
					pstmt.setLong(4, mf.getSize());
					
					pstmt.executeUpdate();
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
	}
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM myTrip";
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
			sql = "SELECT NVL(COUNT(*), 0) FROM myTrip ";
			if (schType.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + schType + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, kwd);
			if (schType.equals("all")) {
				pstmt.setString(2, kwd);
			}

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
	
	public List<MyTripDTO> listMyTrip(int offset, int size) {
		List<MyTripDTO> list = new ArrayList<MyTripDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT mt.num, mt.userId, userName, subject, hitCount, reg_date ");
			sb.append(" FROM myTrip mt ");
			sb.append(" JOIN member1 m1 ON mt.userId = m1.userId ");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyTripDTO dto = new MyTripDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
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

	public List<MyTripDTO> listMyTrip(int offset, int size, String schType, String kwd) {
List<MyTripDTO> list = new ArrayList<MyTripDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT mt.num, mt.userId, userName, subject, hitCount, reg_date ");
			sb.append(" FROM myTrip mt ");
			sb.append(" JOIN member1 m1 ON mt.userId = m1.userId ");
			if(schType.equals("all")) {
				sb.append(" AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\/|\\.|\\-)", "");
				sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(1, kwd);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyTripDTO dto = new MyTripDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
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
	
	public MyTripDTO findById(long num) {
		MyTripDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT mt.num, mt.userId, userName, subject, content, hitCount, reg_date, "
					+ " NVL(myTripLikeCount, 0) myTripLikeCount "
					+ " FROM myTrip mt "
					+ " JOIN member1 m ON mt.userId = m.userId "
					+ " LEFT OUTER JOIN ( "
					+ " 	SELECT num, COUNT(*) myTripLikeCount FROM myTripLike "
					+ "		GROUP BY num "
					+ " ) mtl ON mt.num = mtl.num "
					+ " WHERE mt.num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyTripDTO();

				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				dto.setMyTripLikeCount(rs.getInt("myTripLikeCount"));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	public MyTripDTO findByPrev(long num, String schType, String kwd) {
		MyTripDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append("SELECT num, subject ");
				sb.append(" FROM myTrip ");
				sb.append(" WHERE ( num > ? ) AND ");
				if(schType.equals("all")) {
					sb.append(" ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\.|\\-|\\/)", "");
					sb.append(" TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append(" ( INSTR(" + schType + ", ?) >= 1 ");
				}
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);
				if(schType.equals("all")) {
					pstmt.setString(3, kwd);
				}
				
			} else {
				sb.append("SELECT num, subject ");
				sb.append(" FROM myTrip ");
				sb.append(" WHERE num > ? ");
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyTripDTO();
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}
	
	public MyTripDTO findByNext(long num, String schType, String kwd) {
		MyTripDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append("SELECT num, subject ");
				sb.append(" FROM myTrip ");
				sb.append(" WHERE num < ? AND ");
				if(schType.equals("all")) {
					sb.append(" ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\.|\\-|\\/)", "");
					sb.append(" TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append(" ( INSTR(" + schType + ", ?) >= 1 ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);
				if(schType.equals("all")) {
					pstmt.setString(3, kwd);
				}
				
			} else {
				sb.append("SELECT num, subject ");
				sb.append(" FROM myTrip ");
				sb.append(" WHERE num < ? ");
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyTripDTO();
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = "UPDATE myTrip SET hitCount = hitCount + 1 "
					+ " WHERE num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, num);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void updateMyTrip(MyTripDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE myTrip SET subject = ?, content = ? "
					+ " WHERE num = ? ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getNum());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;

			if (dto.getListFile() != null && dto.getListFile().size() != 0) {
				sql = "INSERT INTO myTripFile(fileNum, num, saveFilename, originalFilename, fileSize) "
						+ " VALUES (myTripFile_seq.NEXTVAL, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (MyMultipartFile mf: dto.getListFile()) {
					pstmt.setLong(1, dto.getNum());
					pstmt.setString(2, mf.getSaveFilename());
					pstmt.setString(3, mf.getOriginalFilename());
					pstmt.setLong(4, mf.getSize());
					
					pstmt.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteMyTrip(long num, String userId, int userLevel) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if (userLevel >= 99) {
				sql = "DELETE FROM myTrip WHERE num = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			} else {
				sql = "DELETE FROM myTrip WHERE num = ? AND userId = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				pstmt.setString(2, userId);
				
				pstmt.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public List<MyTripDTO> listMyTripFile(long num) {
		List<MyTripDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, saveFilename, originalFilename, fileSize "
					+ " FROM myTripFile WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				MyTripDTO dto = new MyTripDTO();

				dto.setFileNum(rs.getLong("fileNum"));
				dto.setNum(rs.getLong("num"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				dto.setFileSize(rs.getLong("fileSize"));
				
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
	
	public MyTripDTO findByFileId(long fileNum) {
		MyTripDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, saveFilename, originalFilename, fileSize "
					+ " FROM myTripFile WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MyTripDTO();

				dto.setFileNum(rs.getLong("fileNum"));
				dto.setNum(rs.getLong("num"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				dto.setFileSize(rs.getLong("fileSize"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}

	public void deleteMyTripFile(String mode, long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM myTripFile WHERE num = ?";
			} else {
				sql = "DELETE FROM myTripFile WHERE fileNum = ?";
			}
			
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
	
	// 로그인 유저의 게시글 공감 유무
	public boolean isUserMyTripLike(long num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT num, userId FROM myTripLike WHERE num = ? AND userID = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 게시물의 공감 추가
	public void insertMyTripLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO myTripLike(num, userId) VALUES (?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 게시글 공감 삭제
	public void deleteMyTripLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM myTripLike WHERE num = ? AND userId = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int countMyTripReply(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM myTripReply WHERE num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
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
	
	// 게시물의 공감 개수
	public int countMyTripLike(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM myTripLike WHERE num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
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
	
	// 게시물의 댓글 및 답글 추가
	public void insertReply(MyTripReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO myTripReply(replyNum, num, userId, content, reg_date, parentNum, showReply, block) "
					+ " VALUES(myTripReply_seq.NEXTVAL, ?, ?, ?, SYSDATE, ?, 1, 0)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			pstmt.setLong(4, dto.getParentNum());

			pstmt.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}

	// 게시물의 댓글 개수
	public int dataCountReply(long num, String userId, int userLevel) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql; 
		
		try {
			sql = "SELECT COUNT(*) FROM myTripReply "
					+ " WHERE num = ? AND parentNum = 0 AND block = 0 ";
			// 관리자가 아닐 때는 댓글을 단 당사자인지 확인
			if(userLevel < 51) {
				sql += " AND (userId = ? OR showReply = 1) ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			if(userLevel < 99) {
				pstmt.setString(2, userId);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
		
		return result;
	}

	// 게시물 댓글 리스트
	public List<MyTripReplyDTO> listReply(long num, int offset, int size, String userId, int userLevel) {
		List<MyTripReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.replyNum, r.userId, userName, num, content, r.reg_date, r.showReply, ");
			sb.append(" 	NVL(answerCount, 0) answerCount, ");
			sb.append(" 	NVL(likeCount, 0) likeCount, ");
			sb.append(" 	NVL(disLikeCount, 0) disLikeCount ");
			sb.append(" FROM myTripReply r ");
			sb.append(" JOIN member1 m ON r.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append(" 	SELECT parentNum, COUNT(*) answerCount ");
			sb.append(" 	FROM myTripReply ");
			sb.append(" 	WHERE parentNum != 0 ");
			if(userLevel < 51) {
				sb.append("		AND (userId = ? OR showReply = 1 ) ");
			}
			sb.append(" 	GROUP BY parentNum ");
			sb.append(" ) a ON r.replyNum = a.parentNum ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append(" 	SELECT replyNum, ");
			sb.append(" 		COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
			sb.append(" 		COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
			sb.append(" 	FROM myTripReplyLike ");
			sb.append(" 	GROUP BY replyNum ");
			sb.append(" ) b ON r.replyNum = b.replyNum ");
			sb.append(" WHERE num = ? AND r.parentNum = 0 AND r.block = 0 ");
			if(userLevel < 51) {
				sb.append(" AND ( r.userId = ? OR showReply = 1 ) ");
			}
			sb.append(" ORDER BY r.replyNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if(userLevel < 51) {
				pstmt.setString(1, userId);				
				pstmt.setLong(2, num);
				pstmt.setString(3, userId);				
				pstmt.setInt(4, offset);
				pstmt.setInt(5, size);
			} else {
				pstmt.setLong(1, num);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);				
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyTripReplyDTO dto = new MyTripReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setShowReply(rs.getInt("showReply"));
				
				dto.setAnswerCount(rs.getInt("answerCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				dto.setDisLikeCount(rs.getInt("disLikeCount"));
				
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

	public MyTripReplyDTO findByReplyId(long replyNum) {
		MyTripReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT replyNum, num, r.userId, userName, content, r.reg_date, showReply, block "
					+ " FROM myTripReply r "
					+ " JOIN member1 m ON r.userId = m.userId "
					+ " WHERE replyNum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyTripReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setShowReply(rs.getInt("showReply"));
				dto.setBlock(rs.getInt("block"));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	// 게시물의 댓글 삭제
	public void deleteReply(long replyNum, String userId, int userLevel) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if(userLevel < 51) {
				MyTripReplyDTO dto = findByReplyId(replyNum);
				if(dto == null || (! userId.equals(dto.getUserId()))) {
					return;
				}
			}
			
			if(replyNum == 0) {
				return;
			}
			sql = " DELETE FROM myTripReply WHERE replyNum = ? OR parentNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);
			pstmt.setLong(2, replyNum);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 댓글 보이기/숨기기
	public void updateReplyShowHide(long replyNum, int showReply, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " UPDATE myTripReply SET showReply = ? "
					+ " WHERE replyNum = ? AND userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, showReply);
			pstmt.setLong(2, replyNum);
			pstmt.setString(3, userId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	

	// 댓글의 답글 리스트
	public List<MyTripReplyDTO> listReplyAnswer(long parentNum, String userId, int userLevel) {
		List<MyTripReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.replyNum, r.userId, userName, num, content, r.reg_date, parentNum, r.showReply ");
			sb.append(" FROM myTripReply r ");
			sb.append(" JOIN member1 m ON r.userId = m.userId ");
			sb.append(" WHERE parentNum = ? AND r.block = 0 ");
			if(userLevel < 51) {
				sb.append(" AND ( r.userId = ? OR showReply = 1 ) ");
			}
			sb.append(" ORDER BY r.replyNum DESC ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, parentNum);
			if(userLevel < 51) {
				pstmt.setString(2, userId);				
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyTripReplyDTO dto = new MyTripReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setNum(rs.getLong("num"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setParentNum(rs.getLong("parentNum"));
				dto.setShowReply(rs.getInt("showReply"));
				
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
	
	// 댓글의 답글 개수
	public int dataCountReplyAnswer(long parentNum, String userId, int userLevel) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql; 
		
		try {
			sql = "SELECT COUNT(*) FROM myTripReply "
					+ " WHERE parentNum = ? AND block = 0 ";
			if(userLevel < 51) {
				sql += " AND ( userId = ? OR showReply = 1 ) ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, parentNum);
			if(userLevel < 51) {
				pstmt.setString(2, userId);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 댓글의 좋아요 / 싫어요 추가
	public void insertReplyLike(MyTripReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO myTripReplyLike(replyNum, userId, replyLike) VALUES(?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getReplyNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setInt(3, dto.getReplyLike());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 댓글 좋아요/싫어요 삭제
	public void deleteReplyLike(MyTripReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM myTripReplyLike WHERE replyNum = ? AND userId = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getReplyNum());
			pstmt.setString(2, dto.getUserId());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 댓글의 좋아요 / 싫어요 개수
	public Map<String, Integer> countReplyLike(long replyNum) {
		Map<String, Integer> map = new HashMap<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(DECODE(replyLike, 1, 1)) likeCount, "
					+ " 	COUNT(DECODE(replyLike, 0, 1)) disLikeCount "
					+ " FROM myTripReplyLike "
					+ " WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				map.put("likeCount", rs.getInt("likeCount"));
				map.put("disLikeCount", rs.getInt("disLikeCount"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return map;
	}
	
	// 유저의 좋아요/싫어요 유무 : 1-좋아요, 0-싫어요, -1:하지 않은 상태
	public int userReplyLike(long replyNum, String userId) {
		int result = -1;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyLike FROM myTripReplyLike WHERE replyNum = ? AND userId = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, replyNum);
			pstmt.setString(2, userId);
			
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
	
}
