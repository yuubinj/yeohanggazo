<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">

<style type="text/css">
.category-badge {
    display: inline-block;
    min-width: 90px;
    text-align: left;
    padding: 2px 6px;
    font-size: 0.85em;
    border-radius: 4px;
    font-weight: 500;
    white-space: nowrap;
}

/* 카테고리별 배경색 */
.category-1 { 
	background-color: #dc3545; color: white;	/* 공지 - 빨강 */
}    
.category-2 { 
	background-color: #198754; color: white;	/* 현지인 추천 - 초록 */
	}     /* 현지인 추천 - 초록 */
.category-3 { 
	background-color: #0d6efd; color: white;	/* 리뷰 - 파랑 */
	}     
.category-4 { 
	background-color: #6c757d; color: white;	/* 기타 - 회색 */
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
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-book-half"></i> 자유게시판 </h3>
				</div>
				
				<!-- 카테고리 선택 드롭다운 -->
				<div class="d-flex justify-content-between align-items-center mb-3">
				    <div class="dataCount">
				        ${dataCount}개(${page}/${total_page} 페이지)
				    </div>
				    <form id="categoryForm" method="get" action="${pageContext.request.contextPath}/bbs/list" class="mb-0">
				        <select name="categoryNum" class="form-select w-auto" onchange="document.getElementById('categoryForm').submit();">
				            <option value="0" ${categoryNum == 0 ? "selected" : ""}>전체</option>
				            <option value="1" ${categoryNum == 1 ? "selected" : ""}>공지</option>
				            <option value="2" ${categoryNum == 2 ? "selected" : ""}>현지인 추천</option>
				            <option value="3" ${categoryNum == 3 ? "selected" : ""}>리뷰</option>
				            <option value="4" ${categoryNum == 4 ? "selected" : ""}>기타</option>
				        </select>
				    </form>
				</div>
				
				<div class="body-main">
					<table class="table table-hover board-list">
						<thead class="table-light">
							<tr>
								<th width="60">번호</th>
								<th>제목</th>
								<th width="100">작성자</th>
								<th width="100">작성일</th>
								<th width="70">조회수</th>
								<th width="50">좋아요</th>
								<th width="50">첨부</th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach var="dto" items="${list}" varStatus="status">
								<tr>
									<td>
									  <c:choose>
									    <c:when test="${dto.category == '공지'}">공지</c:when>
									    <c:otherwise>${dataCount - (page-1) * size - status.index}</c:otherwise>
									  </c:choose>
									</td>
									<td class="left">
										<div class="text-wrap">
											<c:url var="articleLink" value="/bbs/article">
											    <c:param name="num" value="${dto.num}" />
											    <c:param name="categoryNum" value="${dto.categoryNum}" />
											    <c:param name="page" value="${page}" />
											    <c:if test="${not empty schType}">
											        <c:param name="schType" value="${schType}" />
											    </c:if>
											    <c:if test="${not empty kwd}">
											        <c:param name="kwd" value="${kwd}" />
											    </c:if>
											</c:url>
											<div class="d-flex align-items-center gap-1">
												<a href="${articleLink}" class="d-flex align-items-center gap-1 text-reset text-decoration-none">
												    <c:if test="${dto.category != '공지'}">
												        <span class="category-badge category-${dto.categoryNum}">[${dto.category}]</span>
												    </c:if>
												    <span class="flex-grow-1">${dto.subject}</span>
												</a>
											</div>
										</div>
									</td>
									<td>${dto.userName}</td>
									<td>${dto.reg_date}</td>
									<td>${dto.hitCount}</td>
									<td>${dto.boardLikeCount}</td>
									<td>
										<c:if test="${not empty dto.saveFilename}">
											<a href="${pageContext.request.contextPath}/bbs/download?num=${dto.num}" class="text-reset"><i class="bi bi-file-arrow-down"></i></a>
										</c:if>
									</td>									
								</tr>
							</c:forEach>
						</tbody>
					</table>
					
					<div class="page-navigation">
						${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
					</div>
		
					<div class="row board-list-footer">
						<div class="col">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/bbs/list?categoryNum=${categoryNum}';" title="새로고침"><i class="bi bi-arrow-counterclockwise"></i></button>
						</div>
						<div class="col-6 d-flex justify-content-center">
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
									<input type="text" name="kwd" value="${kwd}" class="form-control">
									<input type="hidden" name="categoryNum" value="${categoryNum}">
								</div>
								<div class="col-auto p-1">
									<button type="button" class="btn btn-light" onclick="searchList()"> <i class="bi bi-search"></i> </button>
								</div>
							</form>
						</div>
						<div class="col text-end">
							<c:if test="${sessionScope.member.userLevel >= 1}">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/bbs/write?categoryNum=${categoryNum}';">글올리기</button>
							</c:if>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
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
	if (!f.kwd.value.trim()) return;

	const formData = new FormData(f);
	let params = new URLSearchParams(formData).toString();
	let url = '${pageContext.request.contextPath}/bbs/list';
	location.href = url + '?' + params;
}
</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>
</body>
</html>
