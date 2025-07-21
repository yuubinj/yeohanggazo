<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/admin/layout/headerResources.jsp" />
</head>
<body>

	<jsp:include page="/WEB-INF/views/admin/layout/header.jsp" />

	<main>
		<jsp:include page="/WEB-INF/views/admin/layout/left.jsp" />
		<div class="wrapper">
			<div class="body-container">

				<div class="body-main">
					<div id="carouselExampleAutoplaying" class="carousel slide"
						data-bs-ride="carousel">
						<div class="carousel-inner">
							<c:forEach var="dto" items="${list}" varStatus="status">
								<div class="carousel-item ${status.index==0?'active':''}">
									<a href="${pageContext.request.contextPath}/admin/ad/list">
										<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}" class="d-block w-100" style="height: 300px; width: 100%;">
									</a>
								</div>
								<c:if test="${list.size()==0}">
									<img
										src="${pageContext.request.contextPath}/dist/images/bg.png"
										class="d-block w-80" style="max-height: 215px; cursor: pointer;">
								</c:if>
							</c:forEach>
						</div>
						<button class="carousel-control-prev" type="button"
							data-bs-target="#carouselExampleAutoplaying" data-bs-slide="prev">
							<span class="carousel-control-prev-icon" aria-hidden="true"></span>
							<span class="visually-hidden">Previous</span>
						</button>
						<button class="carousel-control-next" type="button"
							data-bs-target="#carouselExampleAutoplaying" data-bs-slide="next">
							<span class="carousel-control-next-icon" aria-hidden="true"></span>
							<span class="visually-hidden">Next</span>
						</button>
					</div>
				</div>

			</div>
		</div>
	</main>

	<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp" />

	<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp" />

</body>
</html>