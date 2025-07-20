<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/admin/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">
<style type="text/css">
.min-h-150 {
   min-height: 150px;
}
</style>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>
</header>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-app"></i> 광고글 관리 </h3>
				</div>
				
				<div class="body-main">

					<table class="table board-article">
						<thead>
							<tr>
								<td colspan="2" align="center">
									${dto.subject}
								</td>
							</tr>
						</thead>
						
						<tbody>
							<tr>
								<td width="50%">
									이름 : ${dto.userName}
								</td>
								<td align="right">
									${dto.reg_date}
								</td>
							</tr>
							
							<tr>
								<td width="30px">
									숨김 여부 : ${dto.hide==0? '공개' : '숨김'}		
								</td>
							</tr>
							
							<tr>
								<td colspan="2" style="border-bottom: none;">
									<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}"
									 	class="img-fluid img-thumbnail w-100 h-auto">
								</td>
							</tr>

							<tr>
								<td colspan="2">
									이전글 :
									<c:if test="${not empty prevDto}">
										<a href="${pageContext.request.contextPath}/admin/ad/article?${query}&num=${prevDto.num}">${prevDto.subject}</a>
									</c:if>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									다음글 :
									<c:if test="${not empty nextDto}">
										<a href="${pageContext.request.contextPath}/admin/ad/article?${query}&num=${nextDto.num}">${nextDto.subject}</a>
									</c:if>	
								</td>
							</tr>
						</tbody>
					</table>
					
					<table class="table table-borderless mb-2">
						<tr>
							<td width="50%">
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.userId}">
										<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/ad/update?num=${dto.num}&pageNo=${pageNo}';">수정</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn btn-light" disabled>수정</button>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userLevel >= 51}">
							    		<button type="button" class="btn btn-light" onclick="deleteOk();">삭제</button>
							    	</c:when>
							    	<c:otherwise>
										<button type="button" class="btn btn-light" disabled>삭제</button>
									</c:otherwise>
							    </c:choose>
							</td>
							<td class="text-end">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/ad/list';">리스트</button>
							</td>
						</tr>
					</table>

				</div>
			</div>
		</div>
	</div>
</main>

<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userLevel >= 51}">
	<script type="text/javascript">
		function deleteOk(){
			if(confirm('게시글을 삭제하시겠습니까 ? ')){
				let params = 'num=${dto.num}&${query}';
				let url = '${pageContext.request.contextPath}/admin/ad/delete?' + params;
				location.href = url;
			}
		}
	</script>
</c:if>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>