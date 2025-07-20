package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.model.FaqDTO;
import com.travel.model.InquiryDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;

public class InquiryDAO {
	private Connection conn = DBConn.getConnection();
	
	// 데이터 추가 - 질문 추가
	public void insertQuestion(InquiryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO inquiry(num, userId, secret, categoryNum, subject, question, reg_date) "
					+ " VALUES(inquiry_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setInt(2, dto.getSecret());
			pstmt.setLong(3, dto.getCategoryNum());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getQuestion());

			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 카테고리에 따른 데이터 개수
	public int dataCount(long categoryNum) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM inquiry iq "
					+ " JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum "
					+ " WHERE fc.enabled = 1 ";
			if(categoryNum != 0) {
				sql += " AND iq.categoryNum = ?";
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
	
	// 검색에서의 데이터 개수
	public int dataCount(long categoryNum, String schType, String kwd) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM inquiry iq "
					+ " JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum "
					+ " WHERE fc.enabled = 1 ";
			if(schType.equals("all")) {
				sql += " AND (INSTR(subject, ?) >= 1 OR INSTR(question, ?) >= 1 ) ";
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\.|\\/)", "");
				sql += " AND TO_CHAR(reg_date, 'YYYY-MM-DD') = ? ";
			} else {
				sql += " AND INSTR(" + schType + ", ?) >= 1";
			}
			if(categoryNum != 0) {
				sql += " AND iq.categoryNum = ?";
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
	
	// 카테고리 출력
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
	
	// 문의 리스트
	public List<InquiryDTO> listInquiry(long categoryNum, int offset, int size) {
		List<InquiryDTO> list = new ArrayList<InquiryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, iq.userId, userName, fc.categoryNum, category, subject, question, ");
			sb.append(" 	TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, answerId, secret ");
			sb.append(" FROM inquiry iq ");
			sb.append(" JOIN member1 m1 ON iq.userId = m1.userId ");
			sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
			sb.append(" WHERE fc.enabled = 1 ");
			if(categoryNum != 0) {
				sb.append(" AND fc.categoryNum = ? ");
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
				InquiryDTO dto = new InquiryDTO();
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setQuestion(rs.getString("question"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerId(rs.getString("answerId"));
				dto.setSecret(rs.getInt("secret"));
				
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
	
	// 검색에서의 문의 리스트
	public List<InquiryDTO> listInquiry(long categoryNum, int offset, int size, String schType, String kwd) {
		List<InquiryDTO> list = new ArrayList<InquiryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, iq.userId, userName, fc.categoryNum, category, subject, question, ");
			sb.append(" 	TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, answerId, secret ");
			sb.append(" FROM inquiry iq ");
			sb.append(" JOIN member1 m1 ON iq.userId = m1.userId ");
			sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
			sb.append(" WHERE fc.enabled = 1 ");
			if(schType.equals("all")) {
				sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(question, ?) >= 1 ) ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\.|\\/)", "");
				sb.append(" AND TO_CHAR(reg_date, 'YYYY-MM-DD') = ? ");
			} else {
				sb.append(" AND INSTR(" + schType + ", ?) >= 1");
			}
			if(categoryNum != 0) {
				sb.append(" AND fc.categoryNum = ? ");
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
				InquiryDTO dto = new InquiryDTO();
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setSubject(rs.getString("subject"));
				dto.setQuestion(rs.getString("question"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerId(rs.getString("answerId"));
				dto.setSecret(rs.getInt("secret"));
				
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
	
	// 해당 문의 보기
	public InquiryDTO findById(long num) {
		InquiryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT iq.num, iq.userId, m1.userName, iq.categoryNum, iq.subject, iq.question, TO_CHAR(iq.reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" 	iq.answerId, m2.userName answerName, iq.answer, TO_CHAR(iq.answer_date, 'YYYY-MM-DD') answer_date, iq.secret ");
			sb.append(" FROM inquiry iq ");
			sb.append(" JOIN member1 m1 ON iq.userId = m1.userId ");
			sb.append(" LEFT OUTER JOIN member1 m2 ON iq.answerId = m2.userId ");
			sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
			sb.append(" WHERE iq.num = ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new InquiryDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setCategoryNum(rs.getLong("categoryNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setQuestion(rs.getString("question"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerId(rs.getString("answerId"));
				dto.setAnswerName(rs.getString("answerName"));
				dto.setAnswer(rs.getString("answer"));
				dto.setAnswer_date(rs.getString("answer_date"));
				dto.setSecret(rs.getInt("secret"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	// 이전 문의 보기
	public InquiryDTO findByPrev(long categoryNum, long num, String schType, String kwd) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		InquiryDTO dto = null;
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append(" SELECT num, subject, secret, userId ");
				sb.append(" FROM inquiry iq ");
				sb.append(" JOIN member1 m ON m.userId = i.userId ");
				sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
				sb.append(" WHERE (fc.enabled = 1 AND num > ? ) AND ");				
				if(schType.equals("all")) {
					sb.append(" (INSTR(subject, ?) >= 1 OR INSTR(question, ?) >= 1 ) ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\.|\\/)", "");
					sb.append(" TO_CHAR(reg_date, 'YYYY-MM-DD') = ? ");
				} else {
					sb.append(" INSTR(" + schType + ", ?) >= 1");
				}
				if(categoryNum != 0) {
					sb.append(" AND fc.categoryNum = ? ");
				}
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				if(schType.equals("all")) {
					pstmt.setString(2, kwd);
					pstmt.setString(3, kwd);
					if(categoryNum != 0) {
						pstmt.setLong(4, categoryNum);
					}
				} else {
					pstmt.setString(2, kwd);
					if(categoryNum != 0) {
						pstmt.setLong(3, categoryNum);
					}
				}
				
			} else {
				sb.append(" SELECT num, subject, secret, userId ");
				sb.append(" FROM inquiry iq ");
				sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
				sb.append(" WHERE (fc.enabled = 1 AND num > ? ) ");
				if(categoryNum != 0) {
					sb.append(" AND fc.categoryNum = ? ");
				}
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
				if(categoryNum != 0) {
					pstmt.setLong(2, categoryNum);
				}
			}

			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new InquiryDTO();
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setSecret(rs.getInt("secret"));
				dto.setUserId(rs.getString("userId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}
	
	// 다음 문의 보기
	public InquiryDTO findByNext(long categoryNum, long num, String schType, String kwd) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		InquiryDTO dto = null;
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append(" SELECT num, subject, secret, userId ");
				sb.append(" FROM inquiry iq ");
				sb.append(" JOIN member1 m ON m.userId = i.userId ");
				sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
				sb.append(" WHERE (fc.enabled = 1 AND num < ? ) AND ");				
				if(schType.equals("all")) {
					sb.append(" (INSTR(subject, ?) >= 1 OR INSTR(question, ?) >= 1 ) ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\.|\\/)", "");
					sb.append(" TO_CHAR(reg_date, 'YYYY-MM-DD') = ? ");
				} else {
					sb.append(" INSTR(" + schType + ", ?) >= 1");
				}
				if(categoryNum != 0) {
					sb.append(" AND fc.categoryNum = ? ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				if(schType.equals("all")) {
					pstmt.setString(2, kwd);
					pstmt.setString(3, kwd);
					if(categoryNum != 0) {
						pstmt.setLong(4, categoryNum);
					}
				} else {
					pstmt.setString(2, kwd);
					if(categoryNum != 0) {
						pstmt.setLong(3, categoryNum);
					}
				}
				
			} else {
				sb.append(" SELECT num, subject, secret, userId ");
				sb.append(" FROM inquiry iq ");
				sb.append(" JOIN faqCategory fc ON fc.categoryNum = iq.categoryNum ");
				sb.append(" WHERE (fc.enabled = 1 AND num < ? ) ");
				if(categoryNum != 0) {
					sb.append(" AND fc.categoryNum = ? ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
				if(categoryNum != 0) {
					pstmt.setLong(2, categoryNum);					
				}
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new InquiryDTO();
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setSecret(rs.getInt("secret"));
				dto.setUserId(rs.getString("userId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}
	
	// 데이터 수정 - 질문 수정
	public void updateQuestion(InquiryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = " UPDATE inquiry SET subject = ?, secret = ?, categoryNum = ?, question = ? "
					+ " WHERE num = ? AND userId = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setInt(2, dto.getSecret());
			pstmt.setLong(3, dto.getCategoryNum());
			pstmt.setString(4, dto.getQuestion());
			pstmt.setLong(5, dto.getNum());
			pstmt.setString(6, dto.getUserId());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 데이터 수정 - 질문에 대한 답 등록
	public void updateAnswer(InquiryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			sql = "UPDATE inquiry SET answerId = ?, answer = ?, ";
			if(dto.getAnswer().length() == 0) {
				sql += " answer_date = NULL ";
			} else {
				sql += " answer_date = SYSDATE ";
			}
			sql += " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getAnswerId());
			pstmt.setString(2, dto.getAnswer());
			pstmt.setLong(3, dto.getNum());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 데이터 삭제 - 질문 삭제
	public void deleteQuestion(long num, String userId, int userLevel) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			if(userLevel >= 99) {
				sql = "DELETE FROM inquiry WHERE num = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			} else {
				sql = "DELETE FROM inquiry WHERE num = ? AND userId = ? ";
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
	
	/*
	// 데이터 삭제 - 질문에 대한 답 삭제
	public void deleteAnswer(long num, String userId, int userLevel) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			if(userLevel >= 99) {
				sql = "UPDATE inquiry SET answerId = null, answer = null, answer_date = NULL "
						+ " WHERE num = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	*/
}
