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
<style type="text/css">
.btn-primary {
	background-color: #5f0080;
	border: none;
	height: 50px;
	font-size: 16px;
}
</style>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-6 my-5 p-3">

	            <div class="border mt-5 p-4" style="border-radius: 10px;">
	                <h4 class="text-center fw-bold">${title}</h4>
	                <hr class="mt-4">
	
	                <div class="d-grid pt-3">
	                   <p class="text-center">${message}</p>
	                </div>
	
	                <div class="d-grid">
	                    <button type="button" class="btn btn-lg btn-primary" onclick="location.href='${pageContext.request.contextPath}/';">
	                        메인화면 <i class="bi bi-arrow-counterclockwise"></i>
	                    </button>
	                </div>
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