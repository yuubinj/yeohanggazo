<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="table table-hover board-list">
	<thead class="table-light">
		<tr>
			<th width="">번호</th>
			<th>제목</th>
			<th width="100">작성자</th>
			<th width="100">질문일자</th>
			<th width="80">답변여부</th>
		</tr>
	</thead>
	<c:if test="${list.size() > 0}">
		<tbody>
			<c:forEach var="dto" items="${list}" varStatus="status">
				<tr>
					<td>${dataCount - (pageNo-1) * size - status.index}</td>
					<td class="left">
						<c:choose>
							<c:when test="${dto.secret==1}">
								<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userLevel >= 99}">
									<a href="${articleUrl}&num=${dto.num}" class="text-reset">${dto.subject}</a>
								</c:if>
								<c:if test="${sessionScope.member.userId!=dto.userId && sessionScope.member.userLevel < 99}">
									비밀글 입니다.
								</c:if>
								<i class="bi bi-file-lock2"></i>
							</c:when>
							<c:otherwise>
								<a href="${articleUrl}&num=${dto.num}" class="text-reset">${dto.subject}</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td>${dto.userName}</td>
					<td>${dto.reg_date}</td>
					<td>${not empty dto.answerId?"답변완료":"답변대기"}</td>
				</tr>
			</c:forEach>
		</tbody>
	</c:if>
 </table>
 
<div class="page-navigation">
	${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
</div>
