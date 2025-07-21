<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<!-- 최근 사진 5개 출력 -->
		<div class="container-fluid mt-3">

			<div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="carousel">
				<div class="carousel-indicators">
					<c:forEach var="dto" items="${listAd}" varStatus="status">
						<button type="button" data-bs-target="#carouselExampleIndicators"
							data-bs-slide-to="${status.index}" class="${status.index==0?'active':''}" aria-current="true"
							aria-label="${dto.subject}"></button>
					</c:forEach>
				</div>
				<div class="carousel-inner">
					<c:forEach var="dto" items="${listAd}" varStatus="status">
						<div class="carousel-item ${status.index==0?'active':''}">
							<a href="${pageContext.request.contextPath}/photo/article?page=1&num=${dto.num}">
								<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}" class="d-block w-100" style="height: 450px;">
							</a>
						</div>
					</c:forEach>
					<c:if test="${listPhoto.size()==0}">
						<img src="${pageContext.request.contextPath}/dist/images/bg.png" class="d-block w-100" style="max-height: 450px;">
					</c:if>
				</div>
				<button class="carousel-control-prev" type="button"
					data-bs-target="#carouselExampleIndicators" data-bs-slide="prev">
					<span class="carousel-control-prev-icon" aria-hidden="true"></span>
					<span class="visually-hidden">Previous</span>
				</button>
				<button class="carousel-control-next" type="button"
					data-bs-target="#carouselExampleIndicators" data-bs-slide="next">
					<span class="carousel-control-next-icon" aria-hidden="true"></span>
					<span class="visually-hidden">Next</span>
				</button>
			</div>

		</div>
		<!-- 게시판 -->
		<div class="col-md-4 p-1">
			<div>
				<div class="fw-semibold pt-2 pb-1">
					<i class="bi bi-app"></i> 게시판
				</div>

				<div class="border px-2">
					<c:forEach var="dto" items="${listBoard}">
						<div class="text-truncate px-2 subject-list">
							<a href="${pageContext.request.contextPath}/bbs/article?page=1&num=${dto.num}">${dto.subject}</a>
						</div>
					</c:forEach>
					<c:forEach var="n" begin="${listBoard.size() + 1}" end="5">
						<div class="text-truncate px-2 subject-list">&nbsp;</div>
					</c:forEach>
				</div>
				<div class="pt-2 text-end">
					<a href="${pageContext.request.contextPath}/bbs/list"
						class="text-reset">더보기</a>
				</div>
			</div>
		</div>

		<!-- 내 여행 자랑하기 -->
		<div class="col-md-4 p-1">
			<div>
				<div class="fw-semibold pt-2 pb-1">
					<i class="bi bi-app"></i> 내 여행 자랑하기
				</div>

				<div class="border px-2">
					<c:forEach var="dto" items="${listmyTrip}">
						<div class="text-truncate px-2 subject-list">
							<a href="${pageContext.request.contextPath}/myTrip/article?page=1&num=${dto.num}">${dto.subject}</a>
						</div>
					</c:forEach>
					<c:forEach var="n" begin="${listmyTrip.size() + 1}" end="5">
						<div class="text-truncate px-2 subject-list">&nbsp;</div>
					</c:forEach>
				</div>
				<div class="pt-2 text-end">
					<a href="${pageContext.request.contextPath}/myTrip/list"
						class="text-reset">더보기</a>
				</div>
			</div>
		</div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>

</body>
</html>