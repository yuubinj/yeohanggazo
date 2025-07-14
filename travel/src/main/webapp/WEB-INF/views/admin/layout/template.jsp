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
</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="wrapper">
		<div class="body-container">
		
		    <div class="body-title">
				<h3><i class="bi bi-app"></i> 제목 </h3>
		    </div>
		    
		    <div class="body-main row">
		    	<div class="col-xxl-9">
		    		<p> 템플릿 입니다. </p>
		    	</div>
			</div>
			
		</div>
	</div>
</main>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>