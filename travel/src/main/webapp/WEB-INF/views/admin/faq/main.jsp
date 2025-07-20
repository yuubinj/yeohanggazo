<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>관리자 - FAQ</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="/WEB-INF/views/admin/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
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
.span-icon, .btnCategoryAddOk { cursor: pointer; }

.text-muted {
	color: #888 !important;
}
</style>
</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="wrapper">
		<div class="body-container">
		
		    <div class="body-title">
				<h3><i class="bi bi-question-octagon"></i> 자주하는 질문 </h3>
		    </div>
		    
		    <div class="body-main row">
		    	<div class="col-xxl-9">
		    		
					<ul class="nav nav-tabs" id="myTab" role="tablist">
						<li class="nav-item" role="presentation">
							<button class="nav-link active" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="0">모두</button>
						</li>
		
						<c:forEach var="vo" items="${listCategory}" varStatus="status">
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="tab-${status.count}" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="${vo.categoryNum}">${vo.category}</button>
							</li>
						</c:forEach>
					</ul>
					
					
					<div class="tab-content pt-2" id="nav-tabContent">
						<div class="tab-pane fade show active" id="nav-content" role="tabpanel" aria-labelledby="nav-tab-content"></div>
						
						<div class="row py-3">
							<div class="col">
								<button type="button" class="btn btn-light" onclick="reloadFaq();" title="새로고침"><i class="bi bi-arrow-counterclockwise"></i></button>
							</div>
							<div class="col-6 text-center">
								<form class="row" name="searchForm">
									<div class="col-auto p-1">
										<select name="schType" class="form-select">
											<option value="all" ${schType=="all"?"selected":""}>제목+내용</option>
											<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
											<option value="content" ${schType=="content"?"selected":""}>내용</option>
										</select>
									</div>
									<div class="col-auto p-1">
										<input type="text" name="kwd" value="${kwd}" class="form-control">
									</div>
									<div class="col-auto p-1">
										<button type="button" class="btn btn-light" onclick="searchList()"> <i class="bi bi-search"></i> </button>
										
										<input type="hidden" id="searchType" value="all">
										<input type="hidden" id="searchValue" value="">
									</div>
								</form>
							</div>
							<div class="col text-end">
								<button type="button" class="btn btn-light" onclick="categoryManage();">카테고리</button>
								<button type="button" class="btn btn-light" onclick="writeForm();">글올리기</button>
							</div>
						</div>
					</div>		
		    		
		    	</div>
			</div>
		</div>
	</div>
</main>

<!-- FAQ 등록 및 수정 대화상자 -->
<div class="modal fade" id="myDialogModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="myDialogModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="myDialogModalLabel">자주하는질문</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body pt-1">
			</div>
		</div>
	</div>
</div>

<!-- 카테고리 대화상자 -->
<div class="modal fade" id="faqCategoryDialogModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="faqCategoryDialogModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="faqCategoryDialogModalLabel">FAQ 카테고리</h5>
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

//검색 키워드 입력란에서 엔터를 누른 경우 서버 전송 막기 
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
	// $('#tab-0').addClass('active');
	
	listPage(1);
	
	// 탭이 변경될 때 실행
	$('button[role="tab"]').on('click', function(e){
		searchReset();
    	
		listPage(1);
	});
});

// 검색 폼 초기화
function searchReset() {
	const f = document.searchForm;
	f.schType.value = 'all';
	f.kwd.value = '';
	
	$('#searchType').val('all');
	$('#searchValue').val('');
}

// 리스트 및 페이징
function listPage(page) {
	const $tab = $('button[role="tab"].active');
	let categoryNum = $tab.attr('data-tab');
	let schType = $('#searchType').val();
	let kwd = $('#searchValue').val().trim();
	
	const url = '${pageContext.request.contextPath}/admin/faq/list';
	let params = 'pageNo='+ page + '&categoryNum=' + categoryNum;
	if( kwd ) {
		params += '&schType=' + schType 
			+ '&kwd=' + encodeURIComponent(kwd);
	}
	
	let selector = '#nav-content';
	
	const fn = function(data) {
		$(selector).html(data);
		bindAccordionToggle(); // 토글 열려있을 때 다시 클릭하면 닫히도록 처리
	};
	
	sendAjaxRequest(url, 'get', params, 'text', fn);
}

// 검색
function searchList() {
	const f = document.searchForm;
	
	let schType = f.schType.value;
	let kwd = f.kwd.value.trim();
	
	$('#searchType').val(schType);
	$('#searchValue').val(kwd);
	
	listPage(1);
}

// 새로 고침
function reloadFaq() {
	searchReset();
	
	listPage(1);
}

// FAQ 등록 폼
function writeForm() {
	$('#myDialogModalLabel').text('자주하는 질문 등록');
	// $('#myDialogModal .modal-dialog').addClass('modal-lg');
	
	/*
	// 로그인이 풀린 경우 이상한 현상
	let url = '${pageContext.request.contextPath}/admin/faq/write';
	$('#myDialogModal .modal-body').load(url); // AJAX-GET
	$('#myDialogModal').modal("show");
	*/
	
	let url = '${pageContext.request.contextPath}/admin/faq/write';
    const fn = function(data) {
    	$('#myDialogModal .modal-body').html(data);
    	$('#myDialogModal').modal("show");
    };
    
    sendAjaxRequest(url, 'get', null, 'text', fn);
}

// FAQ 등록 및 수정 완료
function sendOk(mode, page) {
    const f = document.faqForm;

    if(! f.categoryNum.value) {
        alert('카테고리를 선택하세요. ');
        f.categoryNum.focus();
        return;
    }
    
    let str = f.subject.value.trim();
    if(!str) {
        alert('제목을 입력하세요. ');
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if( !str ) {
        alert('내용을 입력하세요. ');
        f.content.focus();
        return;
    }
    
    const url = '${pageContext.request.contextPath}/admin/faq/' + mode;
    let params = $('form[name=faqForm]').serialize();
    
    const fn = function(data) {
    	$('#myDialogModal .modal-body').empty();
    	$('#myDialogModal').modal('hide');
    	
    	if(mode === 'write') {
    		searchReset();
    		listPage(1);
    	} else {
    		listPage(page);
    	}
    };
    
    sendAjaxRequest(url, 'post', params, 'json', fn);
}

//FAQ 등록 또는 수정 취소
function sendCancel() {
	$('#myDialogModal .modal-body').empty();
	$('#myDialogModal').modal("hide");
}

// FAQ 수정 폼
function updateFaq(num, page) {
	$('#myDialogModalLabel').text('자주하는 질문 수정');
	
	let url = '${pageContext.request.contextPath}/admin/faq/update';
    const fn = function(data) {
    	$('#myDialogModal .modal-body').html(data);
    	$('#myDialogModal').modal("show");
    };
    
    sendAjaxRequest(url, 'get', {num:num, pageNo:page}, 'text', fn);
}

// FAQ 삭제
function deleteFaq(num, page) {
	if(! confirm('게시글을 삭제하시겠습니까 ? ')) {
		return false;
	}
	
	let url = '${pageContext.request.contextPath}/admin/faq/delete';
    const fn = function(data) {
    	listPage(page);
    };
    
    sendAjaxRequest(url, 'post', {num:num}, 'json', fn);
}

// 카테고리 관리
function categoryManage() {
   	$('#faqCategoryDialogModal').modal('show');
   	
   	listAllCategory();
}

// 카테고리 리스트
function listAllCategory() {
	let url = '${pageContext.request.contextPath}/admin/faq/listAllCategory';
	
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
		
		let url = '${pageContext.request.contextPath}/admin/faq/insertCategory';
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
		
		let url = '${pageContext.request.contextPath}/admin/faq/updateCategory';
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
		
		let url = '${pageContext.request.contextPath}/admin/faq/deleteCategory';
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
		location.href = '${pageContext.request.contextPath}/admin/faq/main';
	});
});

// Bootstrap의 Collapse 기능 기반 아코디언에서 이미 열려 있는 아코디언을 다시 클릭했을 때 닫히게 하는 용도
function bindAccordionToggle() {
	const buttons = document.querySelectorAll('.accordion-button');

	buttons.forEach(button => {
		button.addEventListener('click', function (e) {
			const targetSelector = this.getAttribute('data-bs-target');
			const target = document.querySelector(targetSelector);

			if (target.classList.contains('show')) {
				const bsCollapse = bootstrap.Collapse.getInstance(target);
				if (bsCollapse) {
					bsCollapse.hide();
					e.preventDefault(); // 클릭 시 기본 토글 방지
				}
			}
		});
	});
}

// FAQ 리스트 로딩 이후 호출 필요
document.addEventListener('DOMContentLoaded', function() {
	bindAccordionToggle();
});
 
// 같은 질문 제목을 한 번 더 클릭하면 닫히도록 커스텀 구현한 토글 기능
function toggleAnswer(el) {
	const answer = el.nextElementSibling;
	const isOpen = answer.classList.contains('open');

	// 일단 모든 답변 닫기
	document.querySelectorAll('.faq-answer').forEach(e => {
		e.style.display = 'none';
		e.classList.remove('open');
	});

	// 이미 열려있던 걸 다시 클릭했으면 닫기만 하고 끝
	if (!isOpen) {
		answer.style.display = 'block';
		answer.classList.add('open');
	}
}

</script>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>