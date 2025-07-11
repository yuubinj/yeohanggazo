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
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">

<style type="text/css">
.nav-tabs .nav-link {
	min-width: 130px;
	background: #f3f5f7;
	border-radius: 0;
	border-top: 1px solid #dbdddf;
	border-right: 1px solid #dbdddf;
	color: #333;
	font-weight: 600;
}
.nav-tabs .nav-item:first-child .nav-link {
	border-left: 1px solid #dbdddf;
}

.nav-tabs .nav-link.active {
	background: #3d3d4f;
	color: #fff;
}
.tab-pane { min-height: 70px; }

.category-item { color: #0d6efd; font-weight: 500; }
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
					<h3><i class="bi bi-question-octagon"></i> 문의 </h3>
				</div>
				
				<div class="body-main">

					<ul class="nav nav-tabs" id="myTab" role="tablist">
						<li class="nav-item" role="presentation">
							<button class="nav-link active" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="0">모두</button>
						</li>
		
					</ul>

					<div class="tab-content pt-2" id="nav-tabContent">
						<div class="row py-3">
							<div class="col-3"></div>
							<div class="col-6">
								<form class="row" name="searchForm">
									<div class="col input-group">
										<input type="text" name="kwd" value="${kwd}" class="form-control rounded me-1" placeholder="검색 키워드를 입력하세요">
										<button type="button" class="btn btn-light rounded" onclick="searchList()"> <i class="bi bi-search"></i> </button>
										
										<input type="hidden" id="searchType" value="all">
										<input type="hidden" id="searchValue" value="">
									</div>
								</form>
							</div>
							<div class="col-3"></div>
						</div>
						
						<div class="tab-pane fade show active" id="nav-content" role="tabpanel" aria-labelledby="nav-tab-content"></div>
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
			success: function(data) {
				fn(data);
			},
			beforeSend: function(xhr) {
				xhr.setRequestHeader('AJAX', true);
			},
			complete: function () {
			},
			error: function(xhr) {
				if(xhr.status === 403) {
					login();
					return false;
				} else if(xhr.status === 406) {
					alert('요청 처리가 실패 했습니다.');
					return false;
		    	}
		    	
				console.log(xhr.responseText);
			}
	};
	
	if(file) {
		settings.processData = false;  // file 전송시 필수. 서버로전송할 데이터를 쿼리문자열로 변환여부
		settings.contentType = false;  // file 전송시 필수. 서버에전송할 데이터의 Content-Type. 기본:application/x-www-urlencoded
	}
	
	$.ajax(url, settings);
}

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

$(function(){
    listPage(1);
	
	// 탭이 변경될 때 실행
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
	let query = 'pageNo='+ page + '&categoryNum=' + categoryNum;
	if( kwd ) {
		query += '&schType=' + schType 
			+ '&kwd=' + encodeURIComponent(kwd);
	}

	let selector = '#nav-content';
	
	const fn = function(data) {
		$(selector).html(data);
	};
	
	sendAjaxRequest(url, 'get', query, 'text', fn);
}

//검색 폼 초기화
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