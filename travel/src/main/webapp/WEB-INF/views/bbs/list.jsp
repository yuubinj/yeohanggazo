<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>자유게시판 - 글 보기</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">

<style type="text/css">
/* 전체 배경 */
body {
    background-color: #f8f9fc;
}

/* 리스트 전체 박스 카드화 */
.board-list-wrapper {
    background-color: #ffffff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
}

/* 제목 스타일 */
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

/* 부트스트랩 기본 선 제거 */
.body-title h3 {
	border-bottom: none !important;
}
/* 부트스트랩 기본 그림자 제거 */
/* 테이블 상단 검은 테두리 제거 및 부드러운 구분선 유지 */
.table.board-list {
    border: none !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
    box-shadow: none !important;
}
.table.board-list thead th {
    border-top: none !important;
    border-bottom: 1px solid #dee2e6 !important;
    box-shadow: none !important;
}

/* 테이블 헤더와 셀 테두리 제거 + 연한선*/
.table.board-list th,
.table.board-list td {
    border-width: 1px 0; /* 위아래만 선, 좌우는 없음 */
    border-style: solid;
    border-color: #dee2e6;
}
/* thead 하단 구분선도 제거 + 연한선*/
.table.board-list thead {
     border-bottom: 1px solid #dee2e6;
}
/* 테이블 외곽선 구분선도 제거*/
.table.board-list {
    border-collapse: collapse;
    border: none !important;
}


/* 카테고리 배지 */
.category-badge {
    display: inline-block;
    border-radius: 6px; /* 살짝만 둥글게 */
    padding: 4px 10px;
    font-size: 13px;
    font-weight: 500;
    width: 100px;
    text-align: center;
    white-space: nowrap;       /* 줄바꿈 막기 */
}

/* 카테고리별 색상 */
.category-1 {
    background-color: #ffe6e9;
    color: #ff6b81;
    border: 1px solid #ff6b81;
}

.category-2 {
    background-color: #d1f4e0;
    color: #198754;
    border: 1px solid #198754;
}

.category-3 {
    background-color: #d6e8ff;
    color: #0d6efd;
    border: 1px solid #0d6efd;
}

.category-4 {
    background-color: #eeeeee;
    color: #6c757d;
    border: 1px solid #6c757d;
}

/* 테이블 전체 스타일 */
.table.board-list {
    border: none;
    border-radius: 8px;
    overflow: hidden;
    background-color: #fff;
    font-size: 14px;
    box-shadow: 0 0 4px rgba(0,0,0,0.05);
}


/* 셀 공통 스타일 */
.table td, .table th {
    vertical-align: middle;
    white-space: nowrap;
    text-align: center;
    padding: 12px 10px;
    border-top: 1px solid #e8e8e8;
    border-bottom: 1px solid #e8e8e8;
}

/* 번호 열 정렬 */
.table td:first-child {
    text-align: center;
}

/* 제목 컬럼만 좌측 정렬 */
.table td.left {
    text-align: left;
}

/* 제목 줄이 늘어날 수 있도록 */
.text-wrap .flex-grow-1 {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
}

/* 말머리 + 제목 간격 */
.text-wrap .category-badge + .flex-grow-1 {
    margin-left: 10px;
}

/* 좋아요 / 첨부 아이콘 정렬 */
.table td:nth-last-child(-n+2) {
    text-align: center;
}

/* 검색폼 정돈 */
form[name='searchForm'] select,
form[name='searchForm'] input {
    height: 38px;
}

/* 글쓰기 버튼 영역 여백 */
.board-list-footer {
    margin-top: 20px;
}

/* 페이징 영역 여백 */
.page-navigation {
    margin-top: 20px;
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
					<h3 class="section-title"><i class="bi bi-lightbulb-fill" style="color: #f1c40f;"></i> 자유게시판 </h3>
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
				
				<div class="body-main board-list-wrapper">
					<table class="table table-hover board-list">
						<thead class="table-light">
							<tr>
								<th width="60">말머리</th>
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
									    <c:when test="${dto.category == '공지'}">
									      <span class="category-badge category-1">공지</span>
									    </c:when>
									    <c:otherwise>
									      ${dataCount - (page-1) * size - status.index}
									    </c:otherwise>
									  </c:choose>
									</td>
									<td class="left">
										<div class="text-wrap">
											<c:url var="articleLink" value="/bbs/article">
											    <c:param name="num" value="${dto.num}" />
											    <c:param name="categoryNum" value="${dto.categoryNum}" /> <!-- 글의 카테고리 -->
    											<c:param name="listCategoryNum" value="${categoryNum}" /> <!-- 리스트에서 선택한 카테고리 -->
											    <c:param name="page" value="${page}" />
											    <c:if test="${not empty schType}">
											        <c:param name="schType" value="${schType}" />
											    </c:if>
											    <c:if test="${not empty kwd}">
											        <c:param name="kwd" value="${kwd}" />
											    </c:if>
											</c:url>
											<div class="d-flex align-items-center gap-3">
												<a href="${articleLink}" class="d-flex align-items-center gap-3 text-reset text-decoration-none">
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
