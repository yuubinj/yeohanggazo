<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class="row mt-4 mb-3">
	<div class="col text-start">
		&nbsp;
	</div>
	<div class="col fs-5 fw-bold text-center">
		<span class="btn" onclick="changeMonth(${year}, ${month-1});"><i class="bi bi-chevron-left"></i></span>
		<span class="text-dark align-middle">${year}년 ${month}월</span>
		<span class="btn" onclick="changeMonth(${year}, ${month+1});"><i class="bi bi-chevron-right"></i></span>
	</div>
	<div class="col fs-6 text-end">
		<span class="btn btn-sm" onclick="changeMonth(${todayYear}, ${todayMonth});">오늘</span>
	</div>
</div>

<table id="largeCalendar" class="table table-bordered">
	<tr class="text-center bg-light">
		<td class="text-danger">일</td>
		<td>월</td>
		<td>화</td>
		<td>수</td>
		<td>목</td>
		<td>금</td>
		<td class="text-primary">토</td>
	</tr>

	<c:forEach var="row" items="${days}" >
		<tr align="left" valign="top">
			<c:forEach var="d" items="${row}">
				<td class="p-2">
					${d}
				</td>
			</c:forEach>
		</tr>
	</c:forEach>
</table>
