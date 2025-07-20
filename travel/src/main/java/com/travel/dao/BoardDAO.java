package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travel.model.BbsReplyDTO;
import com.travel.model.BoardDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;

public class BoardDAO {
    private Connection conn = DBConn.getConnection();

    // 게시물 추가
    public void insertBoard(BoardDTO dto) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
    	
        try {
        	sql = "INSERT INTO bbs (num, categoryNum, userId, subject, content, hitCount, reg_date, block, saveFilename, originalFilename)"
        			+ " VALUES (bbs_seq.NEXTVAL, ?, ?, ?, ?, 0, SYSDATE, 0, ?, ?)";
        	
        	pstmt = conn.prepareStatement(sql);
        	
            pstmt.setLong(1, dto.getCategoryNum());
            pstmt.setString(2, dto.getUserId());
            pstmt.setString(3, dto.getSubject());
            pstmt.setString(4, dto.getContent());
            pstmt.setString(5, dto.getSaveFilename());
            pstmt.setString(6, dto.getOriginalFilename());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
    }

    // 게시물 수정
    public void updateBoard(BoardDTO dto) throws SQLException {
        PreparedStatement pstmt = null;
        String sql;
        try {
            sql = "UPDATE bbs SET categoryNum=?, subject=?, content=?, saveFilename=?, originalFilename=?, reg_date=SYSDATE WHERE num=? AND userId = ?";
            
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setLong(1, dto.getCategoryNum());
            pstmt.setString(2, dto.getSubject());
            pstmt.setString(3, dto.getContent());
            pstmt.setString(4, dto.getSaveFilename());
            pstmt.setString(5, dto.getOriginalFilename());
            pstmt.setLong(6, dto.getNum());
            pstmt.setString(7, dto.getUserId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            DBUtil.close(pstmt);
        }
    }

    // 게시물 삭제
    public void deleteBoard(long num, String userId, int userLevel) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
    	
        try {
        	if (userLevel >= 51) {
				sql = "DELETE FROM bbs WHERE num = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			} else {
				sql = "DELETE FROM bbs WHERE num = ? AND userId = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				pstmt.setString(2, userId);
				
				pstmt.executeUpdate();
			}
            
        } catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
    }

    // 전체 게시물 리스트
    public List<BoardDTO> listBoard(int offset, int size) {
    	List<BoardDTO> list = new ArrayList<BoardDTO>();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	StringBuilder sb = new StringBuilder();
    	
    	try {
    		sb.append(" SELECT b.num, b.categoryNum, c.category, m.userName, b.subject, b.content, ");
    		sb.append(" b.saveFilename, b.originalFilename, ");
    		sb.append(" NVL(r.replyCount, 0) replyCount, ");
    		sb.append(" b.hitCount, TO_CHAR(b.reg_date, 'YYYY-MM-DD') reg_date, ");
    		sb.append(" NVL(bl.boardLikeCount, 0) boardLikeCount ");

    		sb.append(" FROM bbs b ");
    		sb.append(" JOIN member1 m ON b.userId = m.userId ");
    		sb.append(" JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append("   SELECT num, COUNT(*) boardLikeCount FROM bbsLike GROUP BY num ");
    		sb.append(" ) bl ON b.num = bl.num ");
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append("   SELECT num, COUNT(*) replyCount FROM bbsReply GROUP BY num ");
    		sb.append(" ) r ON b.num = r.num ");

    		sb.append(" WHERE b.block = 0 ");
    		sb.append(" ORDER BY ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
    		sb.append("   b.num DESC ");
    		sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

    		pstmt = conn.prepareStatement(sb.toString());
    		pstmt.setInt(1, offset);
    		pstmt.setInt(2, size);

    		rs = pstmt.executeQuery();
    		
    		while (rs.next()) {
    			BoardDTO dto = new BoardDTO();
    			dto.setNum(rs.getLong("num"));
    			dto.setCategoryNum(rs.getInt("categoryNum"));
    			dto.setCategory(rs.getString("category"));
    			dto.setUserName(rs.getString("userName"));
    			dto.setSubject(rs.getString("subject"));
    			dto.setContent(rs.getString("content"));
    			dto.setSaveFilename(rs.getString("saveFilename"));
    			dto.setOriginalFilename(rs.getString("originalFilename"));
    			dto.setReplyCount(rs.getInt("replyCount"));
    			dto.setHitCount(rs.getInt("hitCount"));
    			dto.setReg_date(rs.getString("reg_date"));
    			dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
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

    // 카테고리별 전체 게시물 리스트
    public List<BoardDTO> listBoard(int offset, int size, int categoryNum) {
        List<BoardDTO> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append(" SELECT b.num, b.categoryNum, c.category, m.userName, b.subject, b.content, ");
            sb.append(" b.saveFilename, b.originalFilename, ");
            sb.append(" NVL(r.replyCount, 0) replyCount, ");
            sb.append(" b.hitCount, TO_CHAR(b.reg_date, 'YYYY-MM-DD') reg_date, ");
            sb.append(" NVL(bl.boardLikeCount, 0) boardLikeCount ");
            sb.append(" FROM bbs b ");
            sb.append(" JOIN member1 m ON b.userId = m.userId ");
            sb.append(" JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append(" LEFT OUTER JOIN (SELECT num, COUNT(*) replyCount FROM bbsReply GROUP BY num) r ON b.num = r.num ");
            sb.append(" LEFT OUTER JOIN (SELECT num, COUNT(*) boardLikeCount FROM bbsLike GROUP BY num) bl ON b.num = bl.num ");
            sb.append(" WHERE b.block = 0 AND b.categoryNum = ? ");
            sb.append(" ORDER BY ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
    		sb.append("   b.num DESC ");
            sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setInt(1, categoryNum);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, size);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                BoardDTO dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setUserName(rs.getString("userName"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
                dto.setSaveFilename(rs.getString("saveFilename"));
                dto.setOriginalFilename(rs.getString("originalFilename"));
                dto.setReplyCount(rs.getInt("replyCount"));
                dto.setHitCount(rs.getInt("hitCount"));
                dto.setReg_date(rs.getString("reg_date"));
                dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
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

    // 검색에서의 게시물 리스트
    public List<BoardDTO> listBoard(int offset, int size, String schType, String kwd) {
    	List<BoardDTO> list = new ArrayList<BoardDTO>();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	StringBuilder sb = new StringBuilder();

    	try {
    		sb.append(" SELECT b.num, b.categoryNum, c.category, m.userName, b.subject, b.content, ");
    		sb.append(" b.saveFilename, b.originalFilename, ");
    		sb.append(" NVL(r.replyCount, 0) replyCount, ");
    		sb.append(" b.hitCount, TO_CHAR(b.reg_date, 'YYYY-MM-DD') reg_date, ");
    		sb.append(" NVL(bl.boardLikeCount, 0) boardLikeCount ");

    		sb.append(" FROM bbs b ");
    		sb.append(" JOIN member1 m ON b.userId = m.userId ");
    		sb.append(" JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
    		
    		// 댓글 수 LEFT JOIN
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append("     SELECT num, COUNT(*) replyCount FROM bbsReply GROUP BY num ");
    		sb.append(" ) r ON b.num = r.num ");
    		
    		// 공감 수 LEFT JOIN
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append("		SELECT num, COUNT(*) boardLikeCount FROM bbsLike GROUP BY num ");
    		sb.append(" ) bl ON b.num = bl.num ");
    		
    		sb.append(" WHERE b.block = 0 ");
    		if (schType.equals("all")) {
    			sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
    		} else if (schType.equals("reg_date")) {
    			kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
    			sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
    		} else {
    			sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
    		}
    		sb.append(" ORDER BY ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
    		sb.append("   b.num DESC ");
    		sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

    		pstmt = conn.prepareStatement(sb.toString());

    		if (schType.equals("all")) {
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

    		while (rs.next()) {
    			BoardDTO dto = new BoardDTO();

    			dto.setNum(rs.getLong("num"));
    			dto.setCategoryNum(rs.getInt("categoryNum"));
    			dto.setCategory(rs.getString("category"));
    			dto.setUserName(rs.getString("userName"));
    			dto.setSubject(rs.getString("subject"));
    			dto.setContent(rs.getString("content"));
    			dto.setSaveFilename(rs.getString("saveFilename"));
    			dto.setOriginalFilename(rs.getString("originalFilename"));
    			dto.setReplyCount(rs.getInt("replyCount"));
    			dto.setHitCount(rs.getInt("hitCount"));
    			dto.setReg_date(rs.getString("reg_date"));
    			dto.setBoardLikeCount(rs.getInt("boardLikeCount"));

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


    // 카테고리별 검색에서의 게시물 리스트
    public List<BoardDTO> listBoard(int categoryNum, int offset, int size, String schType, String kwd) {
    	List<BoardDTO> list = new ArrayList<BoardDTO>();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	StringBuilder sb = new StringBuilder();
    	
    	try {
    		sb.append(" SELECT b.num, b.categoryNum, c.category, m.userName, b.subject, b.content, ");
    		sb.append(" b.saveFilename, b.originalFilename, ");
            sb.append(" NVL(r.replyCount, 0) replyCount, ");
    		sb.append(" b.hitCount, TO_CHAR(b.reg_date, 'YYYY-MM-DD') reg_date, ");
    		sb.append(" NVL(bl.boardLikeCount, 0) boardLikeCount ");
    		
    		sb.append(" FROM bbs b ");
    		sb.append(" JOIN member1 m ON b.userId = m.userId ");
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append(" 	SELECT num, COUNT(*) replyCount FROM bbsReply GROUP BY num ");
    		sb.append(" ) r ON b.num = r.num ");
    		sb.append(" JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
    		sb.append(" LEFT OUTER JOIN ( ");
    		sb.append("     SELECT num, COUNT(*) boardLikeCount FROM bbsLike GROUP BY num ");
    		sb.append(" ) bl ON b.num = bl.num ");
    		
    		sb.append(" WHERE b.block = 0 AND b.categoryNum = ? ");
    		if (schType.equals("all")) {
    			sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
    		} else if (schType.equals("reg_date")) {
    			kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
    			sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
    		} else {
    			sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
    		}
    		sb.append(" ORDER BY ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
    		sb.append("   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
    		sb.append("   b.num DESC ");
    		sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

    		pstmt = conn.prepareStatement(sb.toString());
    		
    		pstmt.setInt(1, categoryNum);
    		if (schType.equals("all")) {
    			pstmt.setString(2, kwd);
    			pstmt.setString(3, kwd);
    			pstmt.setInt(4, offset);
    			pstmt.setInt(5, size);
    		} else {
    			pstmt.setString(2, kwd);
    			pstmt.setInt(3, offset);
    			pstmt.setInt(4, size);
    		}

    		rs = pstmt.executeQuery();
    		
    		while (rs.next()) {
    			BoardDTO dto = new BoardDTO();

    			dto.setNum(rs.getLong("num"));
    			dto.setCategoryNum(rs.getInt("categoryNum"));
    			dto.setCategory(rs.getString("category"));
    			dto.setUserName(rs.getString("userName"));
    			dto.setSubject(rs.getString("subject"));
    			dto.setContent(rs.getString("content"));
    			dto.setSaveFilename(rs.getString("saveFilename"));
    			dto.setOriginalFilename(rs.getString("originalFilename"));
    			dto.setReplyCount(rs.getInt("replyCount"));
    			dto.setHitCount(rs.getInt("hitCount"));
    			dto.setReg_date(rs.getString("reg_date"));
    			dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
    			
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

    // 데이터 개수
    public int dataCount() {
    	int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM bbs WHERE block = 0 ";
			
			pstmt = conn.prepareStatement(sql);
			
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
    
    // 카테고리별 데이터 개수
    public int dataCount(int categoryNum) {
    	int result = 0;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String sql;
    	
    	try {
    		sql = "SELECT COUNT(*) FROM bbs WHERE block = 0 AND categoryNum = ? ";
    		
    		pstmt = conn.prepareStatement(sql);
    		
    		pstmt.setInt(1, categoryNum);
    		
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
    public int dataCount(String schType, String kwd) {
    	int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM bbs b "
					+ " JOIN member1 m ON b.userId = m.userId "
					+ " WHERE block = 0 ";
			if (schType.equals("all")) {
				sql += "  AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ";
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  AND INSTR(" + schType + ", ?) >= 1 ";
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
    
    // 카테고리별 검색에서의 데이터 개수
    public int dataCount(int categoryNum, String schType, String kwd) {
    	int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM bbs b "
					+ " JOIN member1 m ON b.userId = m.userId "
					+ " WHERE block = 0 AND categoryNum = ? ";
			if (schType.equals("all")) {
				sql += "  AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ";
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  AND INSTR(" + schType + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, categoryNum);
			pstmt.setString(2, kwd);
			if (schType.equals("all")) {
				pstmt.setString(3, kwd);
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

    // 해당 게시물 보기
    public BoardDTO findById(long num) {
    	BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT b.num, b.categoryNum, category, b.userId, userName, subject, content, "
					+ "   saveFilename, originalFilename, reg_date, hitCount, block, "
					+ "   NVL(boardLikeCount, 0) boardLikeCount "
					+ " FROM bbs b "
					+ " JOIN member1 m ON b.userId=m.userId "
					+ " JOIN bbsCategory c ON b.categoryNum = c.categoryNum"
					+ " LEFT OUTER JOIN ( "
					+ "    SELECT num, COUNT(*) boardLikeCount FROM bbsLike "
					+ "    GROUP BY num "
					+ " ) bc ON b.num = bc.num "
					+ " WHERE b.num = ? AND block = 0 ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setCategoryNum(rs.getInt("categoryNum"));
				dto.setCategory(rs.getString("category"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setBlock(rs.getInt("block"));
				
				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
    }
    
    // 정렬 및 검색 기준 통일: 전체조건 + 공지 우선 정렬 + 카테고리, 검색조건 처리
 	 // 다음글
     public BoardDTO findByNext(int categoryNum, long num, String schType, String kwd) {
         BoardDTO dto = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         StringBuilder sb = new StringBuilder();

         try {
             sb.append("SELECT num, category, subject, content FROM ( ");
             sb.append("    SELECT b.num, c.category, b.subject, b.content, ");
             sb.append("           ROW_NUMBER() OVER ( ");
             sb.append("               ORDER BY ");
             sb.append("                   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END ASC, ");
             sb.append("                   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
             sb.append("                   b.num DESC ");
             sb.append("           ) AS rn ");
             sb.append("    FROM bbs b ");
             sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
             sb.append("    JOIN member1 m ON b.userId = m.userId ");
             sb.append("    WHERE b.block = 0 ");

             if (categoryNum > 0) sb.append(" AND b.categoryNum = ? ");
             if (kwd != null && !kwd.isEmpty()) {
                 if ("all".equals(schType)) {
                     sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                 } else if ("reg_date".equals(schType)) {
                     kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
                     sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                 } else {
                     sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
                 }
             }

             sb.append(") ");
             sb.append("WHERE rn = ( ");
             sb.append("    SELECT rn - 1 FROM ( ");
             sb.append("        SELECT b.num, ");
             sb.append("               ROW_NUMBER() OVER ( ");
             sb.append("                   ORDER BY ");
             sb.append("                       CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END ASC, ");
             sb.append("                       CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
             sb.append("                       b.num DESC ");
             sb.append("               ) AS rn ");
             sb.append("        FROM bbs b ");
             sb.append("        WHERE b.block = 0 ");

             if (categoryNum > 0) sb.append(" AND b.categoryNum = ? ");
             if (kwd != null && !kwd.isEmpty()) {
                 if ("all".equals(schType)) {
                     sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                 } else if ("reg_date".equals(schType)) {
                     sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                 } else {
                     sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
                 }
             }

             sb.append("    ) WHERE num = ? ");
             sb.append(")");

             pstmt = conn.prepareStatement(sb.toString());
             int idx = 1;

             if (categoryNum > 0) pstmt.setInt(idx++, categoryNum);
             if (kwd != null && !kwd.isEmpty()) {
                 pstmt.setString(idx++, kwd);
                 if ("all".equals(schType)) pstmt.setString(idx++, kwd);
             }

             if (categoryNum > 0) pstmt.setInt(idx++, categoryNum);
             if (kwd != null && !kwd.isEmpty()) {
                 pstmt.setString(idx++, kwd);
                 if ("all".equals(schType)) pstmt.setString(idx++, kwd);
             }

             pstmt.setLong(idx++, num);

             rs = pstmt.executeQuery();
             if (rs.next()) {
                 dto = new BoardDTO();
                 dto.setNum(rs.getLong("num"));
                 dto.setCategory(rs.getString("category"));
                 dto.setSubject(rs.getString("subject"));
                 dto.setContent(rs.getString("content"));
             }
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             DBUtil.close(rs);
             DBUtil.close(pstmt);
         }

         return dto;
     }	
 	
 	 // 이전글
     public BoardDTO findByPrev(int categoryNum, long num, String schType, String kwd) {
         BoardDTO dto = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         StringBuilder sb = new StringBuilder();

         try {
             sb.append("SELECT num, category, subject, content FROM ( ");
             sb.append("    SELECT b.num, c.category, b.subject, b.content, ");
             sb.append("           ROW_NUMBER() OVER ( ");
             sb.append("               ORDER BY ");
             sb.append("                   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END ASC, ");
             sb.append("                   CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
             sb.append("                   b.num DESC ");
             sb.append("           ) AS rn ");
             sb.append("    FROM bbs b ");
             sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
             sb.append("    JOIN member1 m ON b.userId = m.userId ");
             sb.append("    WHERE b.block = 0 ");

             if (categoryNum > 0) sb.append(" AND b.categoryNum = ? ");
             if (kwd != null && !kwd.isEmpty()) {
                 if ("all".equals(schType)) {
                     sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                 } else if ("reg_date".equals(schType)) {
                     kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
                     sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                 } else {
                     sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
                 }
             }

             sb.append(") ");
             sb.append("WHERE rn = ( ");
             sb.append("    SELECT rn + 1 FROM ( ");
             sb.append("        SELECT b.num, ");
             sb.append("               ROW_NUMBER() OVER ( ");
             sb.append("                   ORDER BY ");
             sb.append("                       CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END ASC, ");
             sb.append("                       CASE WHEN b.categoryNum = 1 THEN b.reg_date ELSE NULL END DESC, ");
             sb.append("                       b.num DESC ");
             sb.append("               ) AS rn ");
             sb.append("        FROM bbs b ");
             sb.append("        WHERE b.block = 0 ");

             if (categoryNum > 0) sb.append(" AND b.categoryNum = ? ");
             if (kwd != null && !kwd.isEmpty()) {
                 if ("all".equals(schType)) {
                     sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                 } else if ("reg_date".equals(schType)) {
                     sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                 } else {
                     sb.append(" AND INSTR(" + schType + ", ?) >= 1 ");
                 }
             }

             sb.append("    ) WHERE num = ? ");
             sb.append(")");

             pstmt = conn.prepareStatement(sb.toString());
             int idx = 1;

             if (categoryNum > 0) pstmt.setInt(idx++, categoryNum);
             if (kwd != null && !kwd.isEmpty()) {
                 pstmt.setString(idx++, kwd);
                 if ("all".equals(schType)) pstmt.setString(idx++, kwd);
             }

             if (categoryNum > 0) pstmt.setInt(idx++, categoryNum);
             if (kwd != null && !kwd.isEmpty()) {
                 pstmt.setString(idx++, kwd);
                 if ("all".equals(schType)) pstmt.setString(idx++, kwd);
             }

             pstmt.setLong(idx++, num);

             rs = pstmt.executeQuery();
             if (rs.next()) {
                 dto = new BoardDTO();
                 dto.setNum(rs.getLong("num"));
                 dto.setCategory(rs.getString("category"));
                 dto.setSubject(rs.getString("subject"));
                 dto.setContent(rs.getString("content"));
             }
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             DBUtil.close(rs);
             DBUtil.close(pstmt);
         }

         return dto;
     }

    
/*
	// 이전글 전체에서
    public BoardDTO findByPrevInAll(long num) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, subject FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, b.subject, ");
            sb.append("           ROW_NUMBER() OVER ( ");
            sb.append("               ORDER BY ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                   b.reg_date DESC, ");
            sb.append("                   b.num DESC ");
            sb.append("           ) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    WHERE b.block = 0 ");
            sb.append(") ");
            sb.append("WHERE rn > ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ");
            sb.append("               ROW_NUMBER() OVER ( ");
            sb.append("                   ORDER BY ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                       b.reg_date DESC, ");
            sb.append("                       b.num DESC ");
            sb.append("               ) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ");
            sb.append("ORDER BY rn ASC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setLong(1, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
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
    
    // 다음글 전체에서
    public BoardDTO findByNextInAll(long num) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, subject FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, b.subject, ");
            sb.append("           ROW_NUMBER() OVER ( ");
            sb.append("               ORDER BY ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                   b.reg_date DESC, ");
            sb.append("                   b.num DESC ");
            sb.append("           ) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    WHERE b.block = 0 ");
            sb.append(") ");
            sb.append("WHERE rn < ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ");
            sb.append("               ROW_NUMBER() OVER ( ");
            sb.append("                   ORDER BY ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                       b.reg_date DESC, ");
            sb.append("                       b.num DESC ");
            sb.append("               ) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ");
            sb.append("ORDER BY rn DESC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setLong(1, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
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
    
    // 카테고리 기준 이전글
    public BoardDTO findByPrevInCategory(long num, int categoryNum) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT * FROM (");
            sb.append("  SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("         ROW_NUMBER() OVER (");
            sb.append("             ORDER BY ");
            sb.append("                 CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                 CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                 b.num DESC ");
            sb.append("         ) rn ");
            sb.append("  FROM bbs b ");
            sb.append("  JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("  WHERE b.block = 0 AND b.categoryNum = ? ");
            sb.append(") ");
            sb.append("WHERE rn > ( ");
            sb.append("  SELECT rn FROM (");
            sb.append("    SELECT b.num, ROW_NUMBER() OVER (");
            sb.append("        ORDER BY ");
            sb.append("            CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("            CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("            b.num DESC ");
            sb.append("    ) rn ");
            sb.append("    FROM bbs b ");
            sb.append("    WHERE b.block = 0 AND b.categoryNum = ? AND b.num = ? ");
            sb.append("  )");
            sb.append(") ORDER BY rn ASC FETCH FIRST 1 ROWS ONLY");

            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setInt(1, categoryNum);
            pstmt.setInt(2, categoryNum);
            pstmt.setLong(3, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }
        return dto;
    }

    // 카테고리 기준 다음글
    public BoardDTO findByNextInCategory(long num, int categoryNum) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT * FROM (");
            sb.append("  SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("         ROW_NUMBER() OVER (");
            sb.append("             ORDER BY ");
            sb.append("                 CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                 CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                 b.num DESC ");
            sb.append("         ) rn ");
            sb.append("  FROM bbs b ");
            sb.append("  JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("  WHERE b.block = 0 AND b.categoryNum = ? ");
            sb.append(") ");
            sb.append("WHERE rn < ( ");
            sb.append("  SELECT rn FROM (");
            sb.append("    SELECT b.num, ROW_NUMBER() OVER (");
            sb.append("        ORDER BY ");
            sb.append("            CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("            CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("            b.num DESC ");
            sb.append("    ) rn ");
            sb.append("    FROM bbs b ");
            sb.append("    WHERE b.block = 0 AND b.categoryNum = ? AND b.num = ? ");
            sb.append("  )");
            sb.append(") ORDER BY rn DESC FETCH FIRST 1 ROWS ONLY");

            pstmt = conn.prepareStatement(sb.toString());
            pstmt.setInt(1, categoryNum);
            pstmt.setInt(2, categoryNum);
            pstmt.setLong(3, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }
        return dto;
    }

    
    // 이전글 검색에서
    public BoardDTO findByPrevInSearch(long num, String schType, String kwd) {
    	BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, category, subject, content FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("           ROW_NUMBER() OVER ( ");
            sb.append("               ORDER BY ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                   CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                   b.num DESC ");
            sb.append("           ) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("    WHERE b.block = 0 ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(") WHERE rn > ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ");
            sb.append("               ROW_NUMBER() OVER ( ");
            sb.append("                   ORDER BY ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN 0 ELSE 1 END, ");
            sb.append("                       CASE WHEN b.categoryNum = 1 THEN b.reg_date END DESC, ");
            sb.append("                       b.num DESC ");
            sb.append("               ) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(" AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ORDER BY rn ASC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());

            int idx = 1;
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setLong(idx, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return dto;
    }
    
    // 다음글 검색에서
    public BoardDTO findByNextInSearch(long num, String schType, String kwd) throws SQLException {
    	BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, category, subject, content FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("           ROW_NUMBER() OVER ( ");
            sb.append("               ORDER BY b.reg_date DESC, b.num DESC ");
            sb.append("           ) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("    WHERE b.block = 0 ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(") WHERE rn < ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ROW_NUMBER() OVER (ORDER BY b.reg_date DESC, b.num DESC) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(" AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ORDER BY rn DESC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());

            int idx = 1;
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setLong(idx, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return dto;
    }
    
    // 이전글 카테고리+검색에서
    public BoardDTO findByPrevInCategorySearch(long num, int categoryNum, String schType, String kwd) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, category, subject, content FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("           ROW_NUMBER() OVER (ORDER BY b.reg_date DESC, b.num DESC) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("    WHERE b.block = 0 AND b.categoryNum = ? ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(") WHERE rn > ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ROW_NUMBER() OVER (ORDER BY b.reg_date DESC, b.num DESC) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 AND b.categoryNum = ? ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(" AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ORDER BY rn ASC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());

            int idx = 1;
            pstmt.setInt(idx++, categoryNum);
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setInt(idx++, categoryNum);
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setLong(idx, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return dto;
    }
    
    // 다음글 카테고리+검색에서
    public BoardDTO findByNextInCategorySearch(long num, int categoryNum, String schType, String kwd) {
        BoardDTO dto = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT num, categoryNum, category, subject, content FROM ( ");
            sb.append("    SELECT b.num, b.categoryNum, c.category, b.subject, b.content, ");
            sb.append("           ROW_NUMBER() OVER (ORDER BY b.reg_date DESC, b.num DESC) AS rn ");
            sb.append("    FROM bbs b ");
            sb.append("    JOIN bbsCategory c ON b.categoryNum = c.categoryNum ");
            sb.append("    WHERE b.block = 0 AND b.categoryNum = ? ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(") WHERE rn < ( ");
            sb.append("    SELECT rn FROM ( ");
            sb.append("        SELECT b.num, ROW_NUMBER() OVER (ORDER BY b.reg_date DESC, b.num DESC) AS rn ");
            sb.append("        FROM bbs b ");
            sb.append("        WHERE b.block = 0 AND b.categoryNum = ? ");

            if (kwd != null && !kwd.isEmpty()) {
                if ("all".equals(schType)) {
                    sb.append(" AND (INSTR(b.subject, ?) >= 1 OR INSTR(b.content, ?) >= 1) ");
                } else if ("reg_date".equals(schType)) {
                    sb.append(" AND TO_CHAR(b.reg_date, 'YYYYMMDD') = ? ");
                } else {
                    sb.append(" AND INSTR(").append(schType).append(", ?) >= 1 ");
                }
            }

            sb.append(" AND b.num = ? ");
            sb.append("    ) ");
            sb.append(") ORDER BY rn DESC FETCH FIRST 1 ROWS ONLY ");

            pstmt = conn.prepareStatement(sb.toString());

            int idx = 1;
            pstmt.setInt(idx++, categoryNum);
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setInt(idx++, categoryNum);
            if (kwd != null && !kwd.isEmpty()) {
                pstmt.setString(idx++, kwd);
                if ("all".equals(schType)) pstmt.setString(idx++, kwd);
            }
            pstmt.setLong(idx, num);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new BoardDTO();
                dto.setNum(rs.getLong("num"));
                dto.setCategoryNum(rs.getInt("categoryNum"));
                dto.setCategory(rs.getString("category"));
                dto.setSubject(rs.getString("subject"));
                dto.setContent(rs.getString("content"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return dto;
    }
*/
    
    // 조회수 증가하기
    public void updateHitCount(long num) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE bbs SET hitCount = hitCount + 1 WHERE num = ?";
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
    public boolean isUserBoardLike(long num, String userId) {
    	boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, userId FROM bbsLike "
					+ " WHERE num = ? AND userId = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
    }

    // 게시물의 공감 추가
    public void insertBoardLike(long num, String userId) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO bbsLike(num, userId) VALUES (?, ?)";
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
    public void deleteBoardLike(long num, String userId) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM bbsLike WHERE num = ? AND userId = ?";
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

    // 게시물의 공감 개수
    public int countBoardLike(long num) {
    	int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM bbsLike WHERE num = ?";
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

    // 게시물의 댓글 및 답글 추가
    public void insertReply(BbsReplyDTO dto) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO bbsReply(replyNum, num, userId, content, "
					+ "   parentNum, reg_date, showReply, block) "
					+ " VALUES (bbsReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, 1, 0)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			pstmt.setLong(4, dto.getParentNum());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
    }

    // 게시물의 댓글 및 답글 수정
    public void updateReply(BbsReplyDTO dto) throws SQLException {
        PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE bbsReply SET content = ? "
					+ " WHERE num=? AND userId=? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getContent());
			pstmt.setLong(2, dto.getNum());
			pstmt.setString(3, dto.getUserId());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
    }

    // 게시물의 댓글 삭제
    public void deleteReply(long replyNum, String userId, int userLevel) throws SQLException {
        PreparedStatement pstmt = null;
		String sql;
		
		try {
			if(userLevel < 51) {
				BbsReplyDTO dto = findByReplyId(replyNum);
				if(dto == null || ( ! userId.equals(dto.getUserId()))) {
					return;
				}
			}

			if(replyNum == 0) {
				return;
			}
			
			sql = "DELETE FROM bbsReply WHERE replyNum = ? OR parentNum = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, replyNum);
			pstmt.setLong(2, replyNum);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}

	}
    
    // 이용자별 댓글
    public BbsReplyDTO findByReplyId(long replyNum) {
    	BbsReplyDTO dto = null;
    	PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyNum, num, r.userId, userName, content, "
					+ "   r.reg_date, parentNum, showReply, block "
					+ " FROM bbsReply r "
					+ " JOIN member1 m ON r.userId = m.userId "
					+ " WHERE replyNum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, replyNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new BbsReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setParentNum(rs.getLong("parentNum"));
				dto.setShowReply(rs.getInt("showReply"));
				dto.setBlock(rs.getInt("block"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
    }
    
    // 관리자용 이용자별 댓글
    public BbsReplyDTO findByReplyId(long replyNum, int userLevel) {
    	BbsReplyDTO dto = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String sql;
    	
    	try {
    		sql = "SELECT replyNum, num, r.userId, userName, content, "
    				+ "   r.reg_date, parentNum, showReply, block "
    				+ " FROM bbsReply r "
    				+ " JOIN member1 m ON r.userId = m.userId "
    				+ " WHERE replyNum = ? ";
    		
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setLong(1, replyNum);
    		
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()) {
    			dto = new BbsReplyDTO();
    			
    			if (userLevel >= 51) {
                    dto.setReplyNum(rs.getLong("replyNum"));
                    dto.setNum(rs.getLong("num"));
                    dto.setUserId(rs.getString("userId"));
                    dto.setUserName(rs.getString("userName"));
                    dto.setContent(rs.getString("content"));
                    dto.setReg_date(rs.getString("reg_date"));
                    dto.setParentNum(rs.getLong("parentNum"));
                    dto.setShowReply(rs.getInt("showReply"));
                    dto.setBlock(rs.getInt("block"));
                } else {
                    dto = null; // 일반 사용자는 null 리턴
                }
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		DBUtil.close(rs);
    		DBUtil.close(pstmt);
    	}
    	return dto;
    }

    // 게시물의 댓글 개수
    public int dataCountReply(long num, String userId, int userLevel) {
    	int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM bbsReply "
					+ " WHERE num = ? AND parentNum = 0 AND block = 0 ";
			
			if(userLevel < 51) {
				sql += " AND (userId = ? OR showReply = 1) ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			if(userLevel < 51) {
				pstmt.setString(2, userId);
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

    // 게시물 댓글 리스트
    public List<BbsReplyDTO> listReply(long num, int offset, int size, String userId, int userLevel) {
    	List<BbsReplyDTO> list = new ArrayList<>();
    	PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.replyNum, r.userId, userName, num, content, r.reg_date, r.showReply, ");
			sb.append("    NVL(answerCount, 0) answerCount,  ");
			sb.append("    NVL(likeCount, 0) likeCount,  ");
			sb.append("    NVL(disLikeCount, 0) disLikeCount  ");
			sb.append(" FROM bbsReply r");
			sb.append(" JOIN member1 m ON r.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("    SELECT parentNum, COUNT(*) answerCount ");
			sb.append("    FROM bbsReply ");
			sb.append("    WHERE parentNum != 0 ");
			if(userLevel < 51) {
				sb.append("   AND ( userId = ? OR showReply = 1 ) ");
			}
			sb.append("    GROUP BY parentNum ");
			sb.append(" ) a ON r.replyNum = a.parentNum ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("    SELECT replyNum, ");
			sb.append("         COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
			sb.append("         COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
			sb.append("    FROM bbsReplyLike ");
			sb.append("    GROUP BY replyNum ");
			sb.append(") b ON r.replyNum = b.replyNum ");
			sb.append(" WHERE num = ? AND r.parentNum = 0 AND r.block = 0 ");
			if(userLevel < 51) {
				sb.append("  AND ( r.userId = ? OR showReply = 1 ) ");
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
				BbsReplyDTO dto = new BbsReplyDTO();
				
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
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
    }

    // 댓글의 답글 리스트
    public List<BbsReplyDTO> listReplyAnswer(long parentNum, String userId, int userLevel) {
    	List<BbsReplyDTO> list = new ArrayList<>();
		
    	PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.replyNum, r.userId, userName, num, content, r.reg_date, parentNum, r.showReply ");
			sb.append(" FROM bbsReply r");
			sb.append(" JOIN member1 m ON r.userId = m.userId ");
			sb.append(" WHERE parentNum = ? AND r.block = 0 ");
			if (userLevel < 51 && userId != null) {
				sb.append(" AND ( r.userId = ? OR showReply = 1 ) ");
	        }
	        sb.append(" ORDER BY r.replyNum DESC ");

	        pstmt = conn.prepareStatement(sb.toString());
	        pstmt.setLong(1, parentNum);
	        if (userLevel < 51 && userId != null) {
	            pstmt.setString(2, userId);
	        }

	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	            BbsReplyDTO dto = new BbsReplyDTO();
	            dto.setReplyNum(rs.getLong("replyNum"));
	            dto.setNum(rs.getLong("num"));
	            dto.setUserId(rs.getString("userId"));
	            dto.setUserName(rs.getString("userName"));
	            dto.setContent(rs.getString("content"));
	            dto.setReg_date(rs.getString("reg_date"));
	            dto.setParentNum(rs.getLong("parentNum"));
	            dto.setShowReply(rs.getInt("showReply"));
	            list.add(dto);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();  // 이걸 반드시 서버 콘솔에서 확인해봐!
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
			sql = "SELECT COUNT(*) FROM bbsReply "
					+ " WHERE parentNum = ? AND block = 0 ";
			if(userLevel < 51) {
				sql += " AND (userId = ? OR showReply = 1) ";
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
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
    }

    // 댓글의 좋아요 추가
    public void insertReplyLike(BbsReplyDTO dto) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO bbsReplyLike(replyNum, userId, replyLike)"
					+ " VALUES(?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getReplyNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setInt(3, dto.getReplyLike());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
    }

    // 댓글의 좋아요 개수
    public Map<String, Integer> countReplyLike(long replyNum) {
    	Map<String, Integer> map = new HashMap<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(DECODE(replyLike, 1, 1)) likeCount, "
					+ "   COUNT(DECODE(replyLike, 0, 1)) disLikeCount "
					+ " FROM bbsReplyLike "
					+ " WHERE replyNum = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				map.put("likeCount", rs.getInt("likeCount"));
				map.put("disLikeCount", rs.getInt("disLikeCount"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return map;
    }

    // 유저의 좋아요 유무 : 1-좋아요, -1:하지않은상태
    public int userReplyLike(long replyNum, String userId) {
    	int result = -1;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyLike FROM bbsReplyLike "
					+ " WHERE replyNum = ? AND userId = ? ";
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

    // 댓글 보이기/숨기기
    public void updateReplyShowHide(long replyNum, int showReply, String userId) throws SQLException {
    	PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE bbsReply SET showReply = ? "
					+ " WHERE replyNum = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, showReply);
			pstmt.setLong(2, replyNum);
			pstmt.setString(3, userId);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
} 
