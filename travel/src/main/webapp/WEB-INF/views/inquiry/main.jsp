<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>문의</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">

<style type="text/css">
body {
    background-color: #f8f9fc;
}

.inquiry-section-title {
    position: relative;
    display: inline-block;
    font-weight: 700;
    font-size: 24px;
    padding-bottom: 10px;
    margin-bottom: 8px; 
    color: #2c3e50;
}

.inquiry-title-underline {
    display: flex;
    align-items: center;
    margin-top: 0px;    /* 제목과 선 사이 간격 (좁게) */
    margin-bottom: 48px; /* 선과 아래 박스 사이 간격 (넓게) */
}

.inquiry-title-underline .line-bold {
    width: 150px;
    height: 4px;
    background-color: #c0c0c0;
    border-radius: 3px;
    margin-right: 4px;
}

.inquiry-title-underline .line-thin {
    flex-grow: 1;
    height: 1px;
    background-color: #dcdcdc;
}

.inquiry-board-list-wrapper {
    background-color: #ffffff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
    margin-top: 20px;
}

.inquiry-nav-tabs {
    border-bottom: none;
    margin-bottom: 1rem;
    margin-top: 1rem;
    justify-content: center;
    flex-wrap: nowrap;
    gap: 12px;
}

.inquiry-nav-tabs .nav-item {
    margin: 0;
}

.inquiry-nav-tabs .nav-link {
    min-width: 100px;
    padding: 6px 16px;
    font-weight: 600;
    color: #333;
    border: 1px solid #dee2e6;
    border-radius: 0;
    background-color: #ffffff;
    transition: all 0.2s;
}

.inquiry-nav-tabs .nav-link.active {
    background-color: #56a5da;
    color: white;
    border-color: #56a5da;
}

.inquiry-nav-tabs .nav-link:hover {
    background-color: #e2eef6;
    color: #0d6efd;
}

.inquiry-icon {
    color: #1abc9c;
    font-size: 22px;
    margin-right: 8px;
}

/* 리스트 상단과의 간격 조절 */
.inquiry-board-list-wrapper > .d-flex {
    margin-top: -8px;
}

.inquiry-table {
    width: 100%;
    border-collapse: collapse;
    background-color: #ffffff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
}

.inquiry-table thead {
    background-color: #f1f3f5;
}

.inquiry-table thead th {
    padding: 12px 10px;
    text-align: center;
    font-weight: 600;
    color: #343a40;
    border-bottom: 1px solid #dee2e6;
}

.inquiry-table tbody td {
    padding: 12px 10px;
    text-align: center;
    color: #495057;
    border-bottom: 1px solid #e9ecef;
    vertical-align: middle;
}

.inquiry-table tbody td.subject {
    text-align: left;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
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

				<div class="inquiry-body-title">
				    <h3 class="inquiry-section-title">
				        <i class="bi bi-envelope-fill inquiry-icon"></i> 문의
				    </h3>
				    <div class="inquiry-title-underline">
				        <div class="line-bold"></div>
				        <div class="line-thin"></div>
				    </div>
				</div>

				<div class="inquiry-board-list-wrapper">
					<div class="d-flex justify-content-center mb-3">
						<ul class="nav inquiry-nav-tabs border-0" id="myTab" role="tablist">
							<li class="nav-item" role="presentation">
								<button class="nav-link ${selectedCategoryNum==vo.categoryNum || selectedCategoryNum == 0 ?'active':''}" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="0">모두</button>
							</li>
							<c:forEach var="vo" items="${listCategory}" varStatus="status">
								<li class="nav-item" role="presentation">
									<button class="nav-link ${selectedCategoryNum==vo.categoryNum?'active':''}" id="tab-${status.count}" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="${vo.categoryNum}">${vo.category}</button>
								</li>
							</c:forEach>
						</ul>
					</div>
				
					<!-- 콘텐츠 영역 -->
					<div class="tab-content pt-2" id="nav-tabContent">
				
						<!-- AJAX로 불러올 콘텐츠 -->
						<div class="tab-pane fade show active" id="nav-content" role="tabpanel" aria-labelledby="nav-tab-content"></div>
				
						<!-- 하단 버튼 및 검색창 -->
						<div class="row board-list-footer">
							<div class="col">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/inquiry/main';" title="새로고침"><i class="bi bi-arrow-counterclockwise"></i></button>
							</div>
							<div class="col-6 d-flex justify-content-center">
								<form class="row" name="searchForm">
									<div class="col input-group">
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
										</div>
										<div class="col-auto p-1">
											<button type="button" class="btn btn-light" onclick="searchList()"> <i class="bi bi-search"></i> </button>
										</div>
									</div>
								</form>
							</div>
							<div class="col text-end">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/inquiry/write';">문의등록</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
function login() {
	location.href = '${pageContext.request.contextPath}/member/login';
}
function sendAjaxRequest(url, method, requestParams, responseType, fn, file = false) {
	const settings = {
		type: method, 
		data: requestParams,
		dataType: responseType,
		success: fn,
		beforeSend: xhr => xhr.setRequestHeader('AJAX', true),
		error: xhr => {
			if(xhr.status === 403) return login();
			if(xhr.status === 406) alert('요청 처리가 실패 했습니다.');
			console.log(xhr.responseText);
		}
	};
	if(file) {
		settings.processData = false;
		settings.contentType = false;
	}
	$.ajax(url, settings);
}
window.addEventListener('DOMContentLoaded', () => {
	const inputEL = document.querySelector('form input[name=kwd]'); 
	inputEL.addEventListener('keydown', evt => {
	    if(evt.key === 'Enter') {
	    	evt.preventDefault();
	    	searchList();
	    }
	});
});
$(function(){
	listPage(1);
	$('button[role="tab"]').on('click', function(){
		resetSearch();
		listPage(1);
	});
});
function listPage(page) {
	const f = document.searchForm;
	const $tab = $('button[role="tab"].active');
	let categoryNum = $tab.attr('data-tab');
	let schType = f.schType.value;
	let kwd = f.kwd.value;
	let url = '${pageContext.request.contextPath}/inquiry/list';
	let query = 'pageNo=' + page + '&categoryNum=' + categoryNum;
	if(kwd) query += '&schType=' + schType + '&kwd=' + kwd;
	sendAjaxRequest(url, 'get', query, 'text', data => $('#nav-content').html(data));
}
function resetSearch() {
	document.searchForm.kwd.value = '';
}
function searchList() {
	const f = document.searchForm;
	f.kwd.value = f.kwd.value.trim();
	listPage(1);
}
function reloadFaq() {
	resetSearch();
	listPage(1);
}
</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>
</body>
</html>
