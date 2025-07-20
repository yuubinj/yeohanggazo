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
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">
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
	<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>
</header>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">

				<div class="body-title">
					<h3><i class="bi bi-question-octagon"></i> 문의 </h3>
				</div>
				
				<div class="body-main">

					<ul class="nav nav-tabs" id="myTab" role="tablist">
						<li class="nav-item" role="presentation">
							<button class="nav-link ${selectedCategoryNum==vo.categoryNum || selectedCategoryNum == 0 ?'active':''}" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="0">모두</button>
						</li>
						
						<c:forEach var="vo" items="${listCategory}" varStatus="status">
							<li class="nav-item" role="presentation">
								<button class="nav-link ${selectedCategoryNum==vo.categoryNum?'active':''}" id="tab-${status.count}" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="${vo.categoryNum}">${vo.category}</button>
							</li>
						</c:forEach>
		
					</ul>

					<div class="tab-content pt-2" id="nav-tabContent">
						<div class="row py-3">
							<div class="col-3"></div>
							<div class="col-6">

							</div>
							<div class="col-3"></div>
						</div>
						
						<div class="tab-pane fade show active" id="nav-content" role="tabpanel" aria-labelledby="nav-tab-content"></div>
						
						<%-- 문의 등록 --%>
						<div class="row board-list-footer">
						<div class="col">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/inquiry/main';" title="새로고침"><i class="bi bi-arrow-counterclockwise"></i></button>
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
							<button type="button" class="btn btn-light" onclick="categoryManage();">카테고리</button>
						</div>
					</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</main>

<!-- 카테고리 대화상자 -->
<div class="modal fade" id="faqCategoryDialogModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="faqCategoryDialogModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="faqCategoryDialogModalLabel">카테고리</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body pt-1">
			
				<form name="categoryForm" method="post">
					<table class="table table-bordered" >
						<thead class="table-light">
							<tr align="center">
								<th width="170">카테고리</th>
								<th width="120">활성</th>
								<th width="80">출력순서</th>
								<th>변경</th>
							</tr>
						</thead>
						<tbody>
							<tr align="center">
								<td> <input type="text" name="category" class="form-control"> </td>
								<td>
									<select name="enabled" class="form-select">
										<option value="1">활성</option>
										<option value="0">비활성</option>
									</select>
								</td>
								<td> <input type="text" name="orderNo" class="form-control"> </td>
								<td> <button type="button" class="btn btn-light btnCategoryAddOk">등록</button> </td>
							</tr>
						</tbody>
						<tfoot class="category-list"></tfoot>
					</table>
				</form>
			
			</div>
		</div>
	</div>
</div>

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
	const f = document.searchForm;
	const $tab = $('button[role="tab"].active');
	let categoryNum = $tab.attr('data-tab');
	
	let schType = f.schType.value;
	let kwd = f.kwd.value;
	
	let url = '${pageContext.request.contextPath}/admin/inquiry/list';
	let query = 'pageNo='+ page + '&categoryNum=' + categoryNum;
	if( kwd ) {
		query += '&schType=' + schType + '&kwd=' + kwd;
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
}

function searchList() {
	const f = document.searchForm;
	
	let kwd = f.kwd.value.trim();
	f.kwd.value = kwd;
	
	listPage(1);
}

function reloadFaq() {
	resetSearch();
	
	listPage(1);
}

//카테고리 관리
function categoryManage() {
   	$('#faqCategoryDialogModal').modal('show');
   	
   	listAllCategory();
}

// 카테고리 리스트
function listAllCategory() {
	let url = '${pageContext.request.contextPath}/admin/inquiry/listAllCategory';
	
	const fn = function(data) {
		$('.category-list').html(data)
	};
	
	sendAjaxRequest(url, 'get', null, 'text', fn);
}

// 카테고리 등록
$(function(){
	$('#faqCategoryDialogModal').on('click', '.btnCategoryAddOk', function(){
		const $tr = $(this).closest('tr');
		
		let category = $tr.find('input[name=category]').val().trim();
		let enabled = $tr.find('select[name=enabled]').val();
		let orderNo = $tr.find('input[name=orderNo]').val();
		
		if(! category) {
			$tr.find('input[name=category]').focus();
			return false;
		}
		
		if(! /^\d+$/.test(orderNo)) {
			$tr.find('input[name=orderNo]').focus();
			return false;
		}
		
		let url = '${pageContext.request.contextPath}/admin/inquiry/insertCategory';
		let params = {category:category, enabled:enabled, orderNo:orderNo};
		
		const fn = function(data) {
			$('form[name=categoryForm]')[0].reset();
			
			listAllCategory();
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 카테고리 수정
$(function(){
	let $cloneTr = null;
	
	$('#faqCategoryDialogModal').on('click', '.btnCategoryUpdate', function(){
		const $tr = $(this).closest('tr');
		
		$cloneTr = $tr.clone(true); // clone
		
		$tr.find('input').prop('disabled', false);
		$tr.find('select').prop('disabled', false);
		$tr.find('input[name=category]').focus();
		
		$tr.find('.category-modify-btn').hide();
		$tr.find('.category-modify-btnOk').show();		
	});

	// 카테고리 수정 완료
	$('#faqCategoryDialogModal').on('click', '.btnCategoryUpdateOk', function(){
		const $tr = $(this).closest('tr');
		
		let categoryNum = $tr.find('input[name=categoryNum]').val();
		let category = $tr.find('input[name=category]').val().trim();
		let enabled = $tr.find('select[name=enabled]').val();
		let orderNo = $tr.find('input[name=orderNo]').val();
		
		if(! category) {
			$tr.find('input[name=category]').focus();
			return false;
		}
		
		if(! /^[0-9]+$/.test(orderNo)) {
			$tr.find('input[name=orderNo]').focus();
			return false;
		}
		
		let url = '${pageContext.request.contextPath}/admin/inquiry/updateCategory';
		let params = {categoryNum:categoryNum, category:category, enabled:enabled, orderNo:orderNo};
		const fn = function(data){
			let state = data.state;
			if(state === 'false') {
				alert('카테고리 수정이 불가능합니다.');
				return false;
			}
			
			$cloneTr = null;
			
			listAllCategory();
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});

	// 카테고리 수정 취소
	$('#faqCategoryDialogModal').on('click', '.btnCategoryUpdateCancel', function(){
		const $tr = $(this).closest('tr');

		if( $cloneTr ) {
			$tr.replaceWith($cloneTr);
		}
		
		$cloneTr = null;
	});
});

// 카테고리 삭제
$(function(){
	$('#faqCategoryDialogModal').on('click', '.btnCategoryDeleteOk', function(){
		if(! confirm('카테고리를 삭제하시겠습니까 ? ')) {
			return false;
		}
		
		const $tr = $(this).closest('tr');
		let categoryNum = $tr.find('input[name=categoryNum]').val();
		
		let url = '${pageContext.request.contextPath}/admin/inquiry/deleteCategory';
		const fn = function(data) {
			listAllCategory();
		};
		
		sendAjaxRequest(url, 'post', {categoryNum:categoryNum}, 'json', fn);
	});
});


$(function(){
	// 카테고리 대화상자 객체
	const myModalEl = document.getElementById('faqCategoryDialogModal');
	
	myModalEl.addEventListener('show.bs.modal', function(){
		// 모달 대화상자가 보일때
	});

	myModalEl.addEventListener('hidden.bs.modal', function(){
		// 모달 대화상자가 닫할때
		location.href = '${pageContext.request.contextPath}/admin/inquiry/main';
	});
});
</script>

<footer>
	<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>