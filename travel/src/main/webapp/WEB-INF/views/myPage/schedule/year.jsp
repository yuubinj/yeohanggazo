<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class="row mt-4 mb-3">
	<div class="col text-start">
		&nbsp;
	</div>
	<div class="col fs-5 fw-bold text-center">
		<span class="btn" onclick="changeYear(${year-1});"><i class="bi bi-chevron-left"></i></span>
		<span class="text-dark align-middle">${year}년도</span>
		<span class="btn" onclick="changeYear(${year+1});"><i class="bi bi-chevron-right"></i></span>
	</div>
	<div class="col fs-6 text-end">
		<span class="btn btn-sm" onclick="changeYear(${todayYear});">오늘</span>
	</div>
</div>

<table id="yearCalendar" class="table table-borderless">
	<c:forEach var="m" begin="1" end="12" step="3">
		<tr class="text-center">
			<td class="text-dark fw-bold" colspan="7">${m}월</td>
			<td class="sm-1">&nbsp;</td>
			<td class="text-dark fw-bold" colspan="7">${m+1}월</td>
			<td class="sm-1">&nbsp;</td>
			<td class="text-dark fw-bold" colspan="7">${m+2}월</td>
		</tr>
		
		<tr class="text-center bg-light">
			<td class="sm-1 text-danger border">일</td>
			<td class="sm-1 border">월</td>
			<td class="sm-1 border">화</td>
			<td class="sm-1 border">수</td>
			<td class="sm-1 border">목</td>
			<td class="sm-1 border">금</td>
			<td class="sm-1 text-primary border">토</td>
			<td class="sm-1 bg-white">&nbsp;</td>
			
			<td class="sm-1 text-danger border">일</td>
			<td class="sm-1 border">월</td>
			<td class="sm-1 border">화</td>
			<td class="sm-1 border">수</td>
			<td class="sm-1 border">목</td>
			<td class="sm-1 border">금</td>
			<td class="sm-1 text-primary border">토</td>
			<td class="sm-1 bg-white">&nbsp;</td>
			
			<td class="sm-1 text-danger border">일</td>
			<td class="sm-1 border">월</td>
			<td class="sm-1 border">화</td>
			<td class="sm-1 border">수</td>
			<td class="sm-1 border">목</td>
			<td class="sm-1 border">금</td>
			<td class="sm-1 text-primary border">토</td>
		</tr>
		
		<c:forEach var="row" begin="0" end="5">
			<tr class="text-center">
				<c:forEach var="i" begin="0" end="2">
					<c:forEach var="col" begin="0" end="6">
						<td class="sm-1 border">
							<c:if test="${not empty days[m-1+i][row][col]}">${days[m-1+i][row][col]}</c:if>
							<c:if test="${empty days[m-1+i][row][col]}">&nbsp;</c:if>
						</td>
					</c:forEach>
					<c:if test="${i<2}"><td class="sm-1">&nbsp;</td></c:if>
				</c:forEach>
			</tr>
		</c:forEach>
		<tr class="border-0">
			<td colspan="7">&nbsp;</td>
			<td class="sm-1">&nbsp;</td>
			<td colspan="7">&nbsp;</td>
			<td class="sm-1">&nbsp;</td>
			<td colspan="7">&nbsp;</td>
		</tr>
	</c:forEach>
</table>
