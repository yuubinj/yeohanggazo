<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<header class="container-fluid header-top fixed-top">
	<div class="container-fluid p-2">
		<div class="row">
			<div class="col-auto d-lg-none align-self-center">
				<button type="button" id="toggleMenu" class="toggle_menu">
					<i class="bi bi-list"></i>
				</button>
			</div>
			<div class="col align-self-center">
				<h2 class="fs-4 fw-bold">관리자 페이지</h2>
			</div>
			<div class="col-auto">
				<div class="row">
					<div class="col-3 align-self-center">
						<c:choose>
							<c:when test="${not empty sessionScope.member.avatar}">
								<span class="img-person" style="background-image: url('${pageContext.request.contextPath}/uploads/member/${sessionScope.member.avatar}');"></span>
							</c:when>
							<c:otherwise>
								<span class="img-person" style="background-image: url('${pageContext.request.contextPath}/dist/images/avatar.png');"></span>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-auto text-end align-self-center ps-3">
						<div class="text-start">
							<span class="fw-semibold" style="font-size: 10px;">관리자</span>
						</div>
						<div class="text-start">
							<span>${sessionScope.member.userName} 님</span>
							&nbsp;<a href="${pageContext.request.contextPath}/"><i class="bi bi-box-arrow-right"></i></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</header>