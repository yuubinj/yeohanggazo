<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FAQ - 자주하는 질문</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">
<style type="text/css">
body {
    background-color: #f8f9fc;
}

.faq-wrapper {
    background-color: #ffffff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
    margin-top: 20px;
}

.faq-title-section {
    position: relative;
    display: flex;
    align-items: center;
    margin-bottom: 35px;
}
.faq-title-icon {
    font-size: 24px;
    color: #56a5da;
    margin-right: 8px;
}
.faq-title-text {
    font-weight: 600;
    font-size: 22px;
    color: #1e1e1e;
}
.faq-title-underline {
    display: flex;
    align-items: center;
    position: absolute;
    bottom: -6px;
    left: 0;
    width: 100%;
}
.faq-title-underline .line-bold {
    width: 220px;
    height: 4px;
    background-color: #c0c0c0;
    border-radius: 3px;
    margin-right: 4px;
}
.faq-title-underline .line-thin {
    flex-grow: 1;
    height: 1px;
    background-color: #dcdcdc;
}

.faq-tabs {
    border-bottom: none;
    margin-bottom: 1rem;
    justify-content: center;
    flex-wrap: nowrap;
    gap: 12px;
}
.faq-tabs .nav-item {
    margin: 0;
}
.faq-tabs .nav-link {
    min-width: 100px;
    padding: 6px 16px;
    font-weight: 600;
    color: #333;
    border: 1px solid #dee2e6;
    border-radius: 0;
    background-color: #ffffff;
    transition: all 0.2s;
}
.faq-tabs .nav-link.active {
    background-color: #56a5da;
    color: white;
    border-color: #56a5da;
}
.faq-tabs .nav-link:hover {
    background-color: #e2eef6;
    color: #0d6efd;
}

.faq-list .q-icon {
    color: #0d6efd;
    font-weight: 700;
    margin-right: 6px;
}
.faq-list .item {
    border: 1px solid #dfe3e8;
    border-radius: 6px;
    padding: 15px 18px;
    margin-bottom: 12px;
    background-color: #ffffff;
}
.faq-list .item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}
.faq-list .item .text-muted {
    font-size: 13px;
    color: #6c757d;
    margin-top: 4px;
}
.faq-list .item .text-secondary {
    font-size: 14px;
    color: #555;
    margin-top: 10px;
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
				<div class="faq-title-section">
				    <i class="bi bi-question-circle-fill faq-title-icon"></i>
				    <span class="faq-title-text">자주하는 질문</span>
				    <div class="faq-title-underline">
				        <div class="line-bold"></div>
				        <div class="line-thin"></div>
				    </div>
				</div>
				<div class="faq-wrapper">
					<ul class="nav nav-tabs faq-tabs" id="myTab" role="tablist">
						<li class="nav-item" role="presentation">
							<button class="nav-link active" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="0">전체</button>
						</li>
						<c:forEach var="vo" items="${listCategory}" varStatus="status">
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="tab-${status.count}" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="false" data-tab="${vo.categoryNum}">${vo.category}</button>
							</li>
						</c:forEach>
					</ul>
					<div class="tab-content pt-2" id="nav-tabContent">
						<div class="row py-3">
							<div class="col-3"></div>
							<div class="col-6">
								<form class="row" name="searchForm">
									<div class="col input-group">
										<input type="text" name="kwd" value="${kwd}" class="form-control rounded me-1" placeholder="검색 키워드를 입력하세요">
										<button type="button" class="btn btn-outline-secondary rounded" onclick="searchList()"> <i class="bi bi-search"></i> </button>
										<input type="hidden" id="searchType" value="all">
										<input type="hidden" id="searchValue" value="">
									</div>
								</form>
							</div>
							<div class="col-3"></div>
						</div>
						<div class="tab-pane fade show active" id="nav-content" role="tabpanel"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
function sendAjaxRequest(url, method, requestParams, responseType, fn, file = false) {
	const settings = {
		type: method, 
		data: requestParams,
		dataType: responseType,
		success: function(data) {
			fn(data);
		},
		error: function(xhr) {
			console.log(xhr.responseText);
		}
	};

	if (file) {
		settings.processData = false;
		settings.contentType = false;
	}

	$.ajax(url, settings);
}

window.addEventListener('DOMContentLoaded', () => {
	const inputEL = document.querySelector('form input[name=kwd]'); 
	inputEL.addEventListener('keydown', function (evt) {
	    if(evt.key === 'Enter') {
	    	evt.preventDefault();
	    	searchList();
	    }
	});
});

$(function(){
    listPage(1);

    $('button[role="tab"]').on('click', function(e){
    	resetSearch();
    	listPage(1);
    });
});

function listPage(page) {
	const $tab = $('button[role="tab"].active');
	let categoryNum = $tab.attr('data-tab');
	let schType = $('#searchType').val();
	let kwd = $('#searchValue').val();

	let url = '${pageContext.request.contextPath}/faq/list';
	let query = 'pageNo=' + page + '&categoryNum=' + categoryNum;
	if (kwd) {
		query += '&schType=' + schType + '&kwd=' + encodeURIComponent(kwd);
	}

	let selector = '#nav-content';

	const fn = function(data) {
		$(selector).html(data);
	};

	sendAjaxRequest(url, 'get', query, 'text', fn);
}

function resetSearch() {
	const f = document.searchForm;
	f.kwd.value = '';
	$('#searchValue').val('');
}

function searchList() {
	const f = document.searchForm;
	let kwd = f.kwd.value.trim();
	$('#searchValue').val(kwd);
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