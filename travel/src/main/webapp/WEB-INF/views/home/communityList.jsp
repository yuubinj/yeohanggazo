<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="table table-hover board-list">
	<c:if test="${list.size() > 0}">
		 <div class="px-2" style="display: flex; gap:20px;">
				<c:forEach var="dto" items="${list}">
					<div class="item-box border" style="padding: 5px; cursor:pointer; width: 300px" 
					onclick="location.href='${pageContext.request.contextPath}/${articleUrl}&num=${dto.num}'">
						<p style="font-weight: 500">${dto.subject}</p>
						<p style="font-size: 13px;">${categoryName=='inquiry' ? dto.question : dto.content}</p>
						<p style="font-size: 13px;">작성자 : ${dto.userName}</p>
						<p style="font-size: 13px;">작성일자 : ${dto.reg_date}</p>
					</div>
					
				</c:forEach>
		</div>
		<div class="pt-2 text-end">
			<a href="${pageContext.request.contextPath}/${listUrl}"
				class="text-reset">더보기</a>
		</div>
	</c:if>
 </table>

