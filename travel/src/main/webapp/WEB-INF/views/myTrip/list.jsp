<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/dist/css/board.css"
	type="text/css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/dist/css/paginate.css"
	type="text/css">

<style type="text/css">
body {
    background-color: #f8f9fc;
}

.body-title h3 {
	border-bottom: none;
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
</style>
</head>
<body>

	<header>
		<jsp:include page="/WEB-INF/views/layout/header.jsp" />
	</header>

	<main>
		<div class="container">
			<div class="body-container row justify-content-center">
				<div class="col-md-10 my-3 p-3">
					<div class="body-title">
						<h3 class="section-title">
							<i class="bi bi-app"></i> 내 여행 자랑하기
						</h3>
					</div>

					<div class="body-main">

						<div class="row board-list-header">
							<div class="col-auto me-auto dataCount">
								${dataCount}개(${pageNo}/${total_page} 페이지)</div>
							<div class="col-auto">&nbsp;</div>
						</div>

						<div class="row g-3">
							<c:forEach var="dto" items="${list}" varStatus="status">
								<div class="col-4">
									<div class="myTrip-item">
										<div class="myTrip-photo">
											<div id="carouselExampleIndicators${status.index}" class="carousel slide"
												data-bs-ride="carousel">
												
												<div class="carousel-inner">
													<c:forEach var="vo" items="${dto.listFileSavename}" varStatus="voStatus">
														<div class="carousel-item ${voStatus.index==0?'active':''}">
															<a href="${pageContext.request.contextPath}/myTrip/article?pageNo=${pageNo}&num=${dto.num}&schType=${schType}&kwd=${kwd}">
																<img src="${pageContext.request.contextPath}/uploads/myTrip/${vo}" class="d-block w-80" style="height: 300px; width: 100%;">
															</a>
															<div class="carousel-caption d-none d-md-block">
																
															</div>
														</div>
														<c:if test="${list.size()==0}">
															<img
																src="${pageContext.request.contextPath}/dist/images/bg.png"
																class="d-block w-80" style="max-height: 215px;">
														</c:if>
													</c:forEach>
												</div>
											</div>
										</div>
										<div class="myTrip-desc">
											<div class="myTrip-desc myTrip-title" onclick="goToArticle(${dto.num})">
												<p style="margin-bottom: 3px;">${dto.subject}</p>
											</div>
											<div class="myTrip-desc myTrip-info">
												<i class="bi bi-hand-thumbs-up" onclick="goToArticle('${dto.num}')"> ${dto.myTripLikeCount}</i>&nbsp;
												<i class="bi bi-card-list" onclick="goToArticle('${dto.num}')"> ${dto.myTripReplyCount}</i>&nbsp;
												<i class="bi bi-eyeglasses" onclick="goToArticle('${dto.num}')"> ${dto.hitCount}</i>
											</div>
										</div>
									</div>			
								</div>
							</c:forEach>
						</div>
					</div>

					<div class="page-navigation">${dataCount == 0 ? "등록된 게시글이 없습니다." : paging}
					</div>

					<div class="row board-list-footer">
						<div class="col">
							<button type="button" class="btn btn-light"
								onclick="location.href='${pageContext.request.contextPath}/myTrip/list';"
								title="새로고침">
								<i class="bi bi-arrow-counterclockwise"></i>
							</button>
						</div>
						<div class="col-6 text-center">
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
							</form>
						</div>
						
						<div class="col text-end">
							<c:choose>
								<c:when test="${sessionScope.member != null}">
									<button type="button" class="btn btn-light"
										onclick="location.href='${pageContext.request.contextPath}/myTrip/write';">글올리기</button>
								</c:when>
								<c:otherwise>
									<button type="button" class="btn btn-light" style="display: none">글올리기</button>
								</c:otherwise>
							</c:choose>
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
	// console.log(params);
	
	let url = '${pageContext.request.contextPath}/myTrip/list';
	location.href = url + '?' + params;
}


function goToArticle(num) {
	let params = 'pageNo=${pageNo}&schType=${schType}&kwd=${kwd}';
	let url = '${pageContext.request.contextPath}/myTrip/article';
	location.href = url + '?' + params + '&num=' + num;
}

</script>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
	</footer>

	<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>