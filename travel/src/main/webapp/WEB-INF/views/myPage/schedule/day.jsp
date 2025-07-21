<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class="row mt-4">
	<div class="col-md-4 pe-2">
		<div class="row pt-1">
			<div class="col-2 text-start ps-0 pe-0">
				<span class="btn btn-sm" onclick="changeDate('${today}');">오늘</span>
			</div>
			<div class="col text-center fw-bold">
				<span class="btn btn-sm ps-0 pe-0" onclick="changeDate('${preMonth}');"><i class="bi bi-chevron-left"></i></span>
				<span class="text-dark">${year}년 ${month}월</span>
				<span class="btn btn-sm ps-0 pe-0" onclick="changeDate('${nextMonth}');"><i class="bi bi-chevron-right"></i></span>
			</div>
			<div class="col-2 text-end ps-0 pe-0">
				&nbsp;
			</div>
		</div>
		
		<div class="row">
			<table id="smallCalendar" class="table table-bordered">
				<tr class="text-center bg-light">
					<td class="xs-1 text-danger">일</td>
					<td class="xs-1">월</td>
					<td class="xs-1">화</td>
					<td class="xs-1">수</td>
					<td class="xs-1">목</td>
					<td class="xs-1">금</td>
					<td class="xs-1 text-primary">토</td>
				</tr>
						   		
				<c:forEach var="row" items="${days}" >
					<tr>
						<c:forEach var="d" items="${row}">
							<td class="text-center">
								${d}
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</table>			       
		</div>
		
	</div>
	
	<div class="col ps-5">
		<div class="row">
			<div class="col form-control-plaintext">
				<span class="fw-bold">
					<i class="bi bi-calendar2-date"></i> ${year}년 ${month}월 ${day}일 일정
				</span>
			</div>
		</div>
		
		<div class="row">
			<c:choose>
				<c:when test="${dto != null}">
					<table class="table table-border date-schedule">
						<tr style="border-top: 2px solid #212529;">
							<td class="col-2 table-light">제목</td>
							<td>${dto.subject}</td>
						</tr>
						<tr>
							<td class="col-2 table-light">전체일정</td>
							<td>${dto.do_date} ~ ${dto.end_date}</td>
						</tr>
						<tr>
							<td class="col-2 table-light">일정색상</td>
							<td>
								<input type="color" name="color" id="form-color" value="${dto.color}" title="일정 분류 색상 선택" disabled style="width: 50px; height: 35px; border: none; cursor: pointer; border-radius: 6px;">
							</td>
						</tr>
						<tr>
							<td class="col-2 table-light">등록일</td>
							<td>${dto.reg_date}</td>
						</tr>
						<tr>
							<td class="col-2 table-light">메모</td>
							<td>
								<span style="white-space: pre;">${dto.memo}</span>
							</td>
						</tr>
						<tr>
							<td class="text-end" colspan="2" style="border-bottom: none;">
								<button type="button" id="btnUpdate" class="btn btn-light" data-date="${date}" data-num="${dto.num}">수정</button>
								<button type="button" id="btnDelete" class="btn btn-light" data-date="${date}" data-num="${dto.num}">삭제</button>
								
								<input type="hidden" name="subject" value="${dto.subject}">
								<input type="hidden" name="color" value="${dto.color}">
								<input type="hidden" name="do_date" value="${dto.do_date}">
								<input type="hidden" name="end_date" value="${dto.end_date}">
								<input type="hidden" name="memo" value="${dto.memo}">
								
							</td>
						</tr>
					</table>
				</c:when>
				<c:otherwise>
					<div style="text-align:center; color:#666;">	
						<img src="https://cdn.pixabay.com/photo/2016/03/31/18/14/calendar-1293684_960_720.png" alt="No Schedule" style="width:150px; margin-bottom:10px;">
						<p>등록된 일정이 없습니다.</p>
					</div>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${list.size()>1}">
				<div class="row mb-1">
					<div class="col form-control-plaintext">
						<span class="fw-bold">
							<i class="bi bi-calendar2-date"></i> ${year}년 ${month}월 ${day}일 다른 일정
						</span>
					</div>
				</div>
				
				<table class="table table-border">
					<thead>
						<tr class="text-center table-light"> 
							<th class="col-2">분류</th>
							<th>제목</th>
							<th class="col-2">등록일</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="vo" items="${list}">
							<c:if test="${dto != null and dto.num != vo.num}">
								<tr>
									<td class="text-center">
											여행일정
									</td>
									<td>
										<div class="daySubject" data-date="${date}" data-num="${vo.num}">
											${vo.subject}
										</div>
									</td>
									<td class="text-center">${vo.reg_date}</td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			
			</c:if>
		</div>		
		
	</div>
</div>
