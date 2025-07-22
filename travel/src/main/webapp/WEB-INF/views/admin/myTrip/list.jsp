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
.myTrip-title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 70%;
    cursor: pointer;
}

body {
    background-color: #f8f9fc;
}

.body-title h3 {
	border-bottom: none !important;
}

.section-title {
    position: relative;
    display: inline-block;
    font-weight: 700;
    font-size: 24px;
    padding-bottom: 10px;
    margin-bottom: 24px;
    color: #2c3e50;
}

.section-title::after {
    content: "";
    position: absolute;
    bottom: -5px;
    left: 0;
    width: 200px;
    height: 3px;
    background-color: #ccc;
    border-radius: 6px;
}

.myTrip-list-wrapper {
    background-color: #ffffff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
}

</style>
</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>

	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3 class="section-title">
						<i class="bi bi-airplane"></i> 내 여행 자랑하기
					</h3>
				</div>

				<div class="row board-list-header mb-3">
					<div class="col-auto me-auto dataCount">
						${dataCount}개(${pageNo}/${total_page} 페이지)</div>
					<div class="col-auto">&nbsp;</div>
				</div>
				
				<div class="body-main myTrip-list-wrapper">
					<div class="row board-list-footer">
						<div class="col text-center">
							<form class="row" name="searchForm">
								<div class="col-auto p-1">
									<select name="schType" class="form-select">
										<option value="all" ${schType=="all"?"selected":""}>제목+내용</option>
										<option value="userName" ${schType=="userName"?"selected":""}>작성자</option>
										<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
										<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
										<option value="content" ${schType=="content"?"selected":""}>내용</option>
									</select>
								</div>
								<div class="col-auto p-1">
									<input type="text" name="kwd" value="${kwd}"
										class="form-control">
								</div>
								<div class="col-auto p-1">
									<button type="button" class="btn btn-light"
										onclick="searchList()">
										<i class="bi bi-search"></i>
									</button>
								</div>
								<div class="col-auto p-1">
									<button type="button" class="btn btn-light"
										onclick="location.href='${pageContext.request.contextPath}/admin/myTrip/list';"
										title="새로고침">
										<i class="bi bi-arrow-counterclockwise"></i>
									</button>
								</div>
							</form>
						</div>
					</div>
					
					<div class="row g-3">
						<c:forEach var="dto" items="${list}" varStatus="status">
							<div class="col-4">
								<div id="carouselExampleIndicators${status.index}" class="carousel slide"
									data-bs-ride="carousel">
									<div class="carousel-indicators">
										<c:forEach var="vo" items="${dto.listFileSavename}" varStatus="voStatus">
											<button type="button"
												data-bs-target="#carouselExampleIndicators${status.index}"
												data-bs-slide-to="${voStatus.index}"
												class="${voStatus.index==0?'active':''}" aria-current="true"
												aria-label="${dto.subject}"></button>
										</c:forEach>
									</div>
									<div class="carousel-inner">
										<c:forEach var="vo" items="${dto.listFileSavename}" varStatus="voStatus">
											<div class="carousel-item ${voStatus.index==0?'active':''}">
												<a href="${pageContext.request.contextPath}/admin/myTrip/article?pageNo=${pageNo}&num=${dto.num}&schType=${schType}&kwd=${kwd}">
													<img src="${pageContext.request.contextPath}/uploads/myTrip/${vo}" class="d-block w-80" style="height: 300px; width: 100%;">
												</a>
												<div class="carousel-caption d-none d-md-block">
													<h5>${dto.subject}</h5>
												</div>
											</div>
											<c:if test="${list.size()==0}">
												<img
													src="${pageContext.request.contextPath}/dist/images/bg.png"
													class="d-block w-80" style="max-height: 215px;">
											</c:if>
										</c:forEach>
									</div>
									<i class="bi bi-eyeglasses">${dto.hitCount}</i>
									<i class="bi bi-hand-thumbs-up">${dto.myTripLikeCount}</i>
									<i class="bi bi-card-list">${dto.myTripReplyCount}</i>
									<button class="carousel-control-prev" type="button"
										data-bs-target="#carouselExampleIndicators${status.index}"
										data-bs-slide="prev">
										<span class="carousel-control-prev-icon" aria-hidden="true"></span>
										<span class="visually-hidden">Previous</span>
									</button>
									<button class="carousel-control-next" type="button"
										data-bs-target="#carouselExampleIndicators${status.index}"
										data-bs-slide="next">
										<span class="carousel-control-next-icon" aria-hidden="true"></span>
										<span class="visually-hidden">Next</span>
									</button>
								</div>
	
							</div>
						</c:forEach>
					</div>
					<div class="page-navigation">${dataCount == 0 ? "등록된 게시글이 없습니다." : paging}
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
	console.log(params);
	
	let url = '${pageContext.request.contextPath}/admin/myTrip/list';
	location.href = url + '?' + params;
}
</script>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>