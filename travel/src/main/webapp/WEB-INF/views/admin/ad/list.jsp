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
</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-app"></i> 광고글 관리 </h3>
				</div>
				
				<div class="body-main">
					
					<div class="row board-list-header">
						<div class="col-auto me-auto dataCount">
							${dataCount}개(${pageNo}/${total_page} 페이지)
						</div>
						<div class="col-auto">&nbsp;</div>
					</div>
					
					<div class="row g-3">
						<c:forEach var="dto" items="${list}" varStatus="status">
							<div class="col-md-4">
								<div class="border rounded d-flex flex-column p-1 cursor-pointer"
										onclick="location.href='${articleUrl}&num=${dto.num}';">
									<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}" class="img-fluid" style="height: 215px;">
									<p class="text-center text-truncate flex-grow-1 m-0 p-2">${dto.subject}</p>
								</div>
							</div>
						</c:forEach>
					</div>
									
					<div class="page-navigation">
						${dataCount == 0 ? "등록된 게시글이 없습니다." : paging}
					</div>
		
					<div class="row board-list-footer">
						<div class="col">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/ad/list';" title="새로고침"><i class="bi bi-arrow-counterclockwise"></i></button>
						</div>
						<div class="col-6 text-center">
							<form class="row" name="searchForm">
								<div class="col-auto p-1">
									<select name="schType" class="form-select">
										<option value="userName" ${schType=="userName"?"selected":""}>작성자</option>
										<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
										<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
									</select>
								</div>
								<div class="col-auto p-1">
									<input type="text" name="kwd" value="${kwd}" class="form-control">
								</div>
								<div class="col-auto p-1">
									<button type="button" class="btn btn-light" onclick="searchList()"> <i class="bi bi-search"></i> </button>
								</div>
							</form>
						</div>
						<div class="col text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/ad/write';">글올리기</button>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
// 검색 키워드 입력란에서 엔터를 누른 경우 서버 전송 막기 
window.addEventListener('DOMContentLoaded', () => {
	const inputEL = document.querySelector('form input[name=kwd]'); 
	inputEL.addEventListener('keydown', function (evt) {
	    if(evt.key === 'Enter') {
	    	evt.preventDefault();
	    	
	    	searchList();
	    }
	});
});

function searchList() {
	const f = document.searchForm;
	if(! f.kwd.value.trim()) {
		return;
	}
	
	// form 요소는 FormData를 이용하여 URLSearchParams 으로 변환
	const formData = new FormData(f);
	let params = new URLSearchParams(formData).toString();
	
	let url = '${pageContext.request.contextPath}/admin/ad/list';
	location.href = url + '?' + params;
}
</script>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>