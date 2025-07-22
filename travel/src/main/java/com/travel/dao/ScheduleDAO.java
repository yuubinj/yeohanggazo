package com.travel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travel.model.ScheduleDTO;
import com.travel.util.DBConn;
import com.travel.util.DBUtil;


public class ScheduleDAO {
	private Connection conn = DBConn.getConnection();

	public void insertSchedule(ScheduleDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "INSERT INTO schedule(num, userId, subject, color, do_date, "
					+ "  memo, reg_date, end_date) "
					+ " VALUES(schedule_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, ?) ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getColor());
			pstmt.setString(4, dto.getDo_date());
			pstmt.setString(5, dto.getMemo());
			pstmt.setString(6, dto.getEnd_date());
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}

	}

	public List<ScheduleDTO> listMonth(String startDay, String endDay, String userId) {
		List<ScheduleDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT num, subject, do_date, end_date, color ");
			sb.append(" FROM schedule ");
			sb.append(" WHERE userId = ? AND ");
			sb.append(" ( ");
			sb.append("      ( ");
			sb.append("         ( TO_DATE(do_date, 'YYYYMMDD') >= TO_DATE(?, 'YYYYMMDD') ");
			sb.append("             AND TO_DATE(do_date, 'YYYYMMDD') <= TO_DATE(?, 'YYYYMMDD')  ");
			sb.append("          )  OR ( TO_DATE(end_date, 'YYYYMMDD') <= TO_DATE(?, 'YYYYMMDD')  ");
			sb.append("             AND TO_DATE(end_date, 'YYYYMMDD') <= TO_DATE(?, 'YYYYMMDD')  ");
			sb.append("          )");
			sb.append("       )");
			sb.append(" ) ");
			sb.append(" ORDER BY do_date ASC, num DESC ");


			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setString(2, startDay);
			pstmt.setString(3, endDay);
			pstmt.setString(4, startDay);
			pstmt.setString(5, endDay);


			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ScheduleDTO dto = new ScheduleDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setDo_date(rs.getString("do_date"));
				dto.setEnd_date(rs.getString("end_date"));
				dto.setColor(rs.getString("color"));
				
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

	public List<ScheduleDTO> listDay(String date, String userId) {
		List<ScheduleDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT num, subject, do_date, end_date, color,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM schedule");
			sb.append(" WHERE userId = ? AND ");
			sb.append(" ( ");
			sb.append("      ( ");
			sb.append("         TO_DATE(do_date, 'YYYYMMDD') = TO_DATE(?, 'YYYYMMDD') ");
			sb.append("         OR (end_date IS NOT NULL AND TO_DATE(do_date, 'YYYYMMDD') <= TO_DATE(?, 'YYYYMMDD') AND TO_DATE(end_date, 'YYYYMMDD') >= TO_DATE(?, 'YYYYMMDD')) ");
			sb.append("      ) ");
			sb.append(" ) ");
			sb.append(" ORDER BY num DESC ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setString(2, date);
			pstmt.setString(3, date);
			pstmt.setString(4, date);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ScheduleDTO dto = new ScheduleDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setDo_date(rs.getString("do_date"));
				dto.setEnd_date(rs.getString("end_date"));
				dto.setColor(rs.getString("color"));
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

	public ScheduleDTO findById(long num) {
		ScheduleDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		String period, s;

		try {
			sql = "SELECT num, userId, subject, do_date, end_date, "
				+ "      color, memo, reg_date "
				+ "  FROM schedule "
				+ "  WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new ScheduleDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setDo_date(rs.getString("do_date"));
				s = dto.getDo_date().substring(0, 4) + "-" + dto.getDo_date().substring(4, 6) + "-"
						+ dto.getDo_date().substring(6);
				dto.setDo_date(s);
				dto.setEnd_date(rs.getString("end_date"));
				if (dto.getEnd_date() != null && dto.getEnd_date().length() == 8) {
					s = dto.getEnd_date().substring(0, 4) + "-" + dto.getEnd_date().substring(4, 6) + "-"
							+ dto.getEnd_date().substring(6);
					dto.setEnd_date(s);
				}
				
				period = dto.getDo_date();
				dto.setPeriod(period);
				
				dto.setColor(rs.getString("color"));
				dto.setMemo(rs.getString("memo"));
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

	public void updateSchedule(ScheduleDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("UPDATE schedule SET ");
			sb.append("    subject = ?, color = ?, do_date = ?, end_date = ?,memo = ? ");
			sb.append("  WHERE num = ? AND userId = ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getColor());
			pstmt.setString(3, dto.getDo_date());
			pstmt.setString(4, dto.getEnd_date());
			pstmt.setString(5, dto.getMemo());
			pstmt.setLong(6, dto.getNum());
			pstmt.setString(7, dto.getUserId());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}

	}

	public void deleteSchedule(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM schedule WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}

	}
	
	public List<ScheduleDTO> listSchedule(int offset, int size, String userId) {
		List<ScheduleDTO> list = new ArrayList<ScheduleDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String s;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT num, m.userId, subject, color, do_date, end_date, memo, reg_date ");
			sb.append("FROM schedule s ");
			sb.append("JOIN member1 m ON s.userId = m.userId ");
			sb.append("WHERE m.userId = ? and (TO_DATE(do_date, 'YYYY-MM-DD') >= TRUNC(SYSDATE) or TO_DATE(end_date, 'YYYY-MM-DD') >= TRUNC(SYSDATE))");
			sb.append("ORDER BY TO_DATE(do_date, 'YYYY-MM-DD') ASC ");
			sb.append("OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");


			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ScheduleDTO dto = new ScheduleDTO();

				dto.setNum(rs.getLong("num"));
				dto.setUserId(userId);
				dto.setSubject(rs.getString("subject"));
				dto.setColor(rs.getString("color"));
				dto.setDo_date(rs.getString("do_date"));
				s = dto.getDo_date().substring(0, 4) + "-" + dto.getDo_date().substring(4, 6) + "-"
						+ dto.getDo_date().substring(6);
				dto.setDo_date(s);
				dto.setEnd_date(rs.getString("end_date"));
				if (dto.getEnd_date() != null && dto.getEnd_date().length() == 8) {
					s = dto.getEnd_date().substring(0, 4) + "-" + dto.getEnd_date().substring(4, 6) + "-"
							+ dto.getEnd_date().substring(6);
					dto.setEnd_date(s);
				}
				dto.setMemo(rs.getString("memo"));
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
	
}
