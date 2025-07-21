<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/myPage/layout/headerResources.jsp"/>

<style type="text/css">
.img-person {
	display: inline-block;
	width: 100px;
	height: 100px;
	background-size: cover;
	background-position: center;
	border-radius: 50%;
}
.body-title h3 {
    min-width: 200px;
}

#largeCalendar tr:nth-child(n+2) {
	height: 145px;
}
#largeCalendar tr td {
	width: calc(100% / 7) !important;
}

.textDate { cursor: pointer;  display: block; }

.scheduleSubject, .scheduleMore {
  display: block;
  max-width: 130px;
  font-size: 13px;
  color: #555555;
  cursor: pointer;

}
.scheduleSubject {
  margin: 1.5px 0;
  background: #f8f9fa;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.scheduleMore {
  margin: 0 0 1.5px;
  text-align: right;
}

@media (max-width: 1200px){
  .scheduleSubject, .scheduleMore {
      max-width: 90px;
  }
}

@media (max-width: 768px) {
  /* 화면 너비가 768px 이하일 때 */
  .scheduleSubject, .scheduleMore {
      max-width: 50px;
  }
}

.daySubject {
  cursor: pointer; max-width: 330px; 
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.daySubject:hover { color: #f28011; }

.preMonthDate, .nextMonthDate { color: #aaaaaa; }
.saturdayDate { color: #0000ff; }
.sundayDate{ color: #ff0000; }

.modal {
  z-index: 99999 !important;
}

.modal-backdrop {
  z-index: 99998 !important;
}

</style>

</head>
<body>

<jsp:include page="/WEB-INF/views/myPage/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/myPage/layout/left.jsp"/>
	<div class="wrapper">
		<div class="body-container">
		    <div class="body-container row justify-content-center">
				<div class="col-md-10 my-3 p-3">

					<div class="body-title">
						<h3><i class="bi bi-calendar2-event"></i> 일정관리 </h3>
					</div>
					
					<div class="body-main">
						<ul class="nav nav-tabs" id="myTab" role="tablist">
							<li class="nav-item" role="presentation">
								<button class="nav-link active" id="tab-1" data-bs-toggle="tab" data-bs-target="#nav-1" type="button" role="tab" aria-controls="1" aria-selected="true">월별일정</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="tab-2" data-bs-toggle="tab" data-bs-target="#nav-2" type="button" role="tab" aria-controls="2" aria-selected="true">상세일정</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="tab-3" data-bs-toggle="tab" data-bs-target="#nav-3" type="button" role="tab" aria-controls="3" aria-selected="true">년도</button>
							</li>
						</ul>
		
						<div class="tab-content" id="nav-tabContent">
							<div class="tab-pane fade show active" id="nav-1" role="tabpanel" aria-labelledby="nav-tab-1"></div>
							<div class="tab-pane fade" id="nav-2" role="tabpanel" aria-labelledby="nav-tab-2"></div>
							<div class="tab-pane fade" id="nav-3" role="tabpanel" aria-labelledby="nav-tab-2"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</main>

<!-- Modal -->
<div class="modal fade" id="myDialogModal"
		data-bs-backdrop="static" data-bs-keyboard="false" 
		tabindex="-1" aria-labelledby="imyDialogModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="myDialogModalLabel">스케쥴 등록</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body pt-0 pb-0">
        		<form name="scheduleForm">
        			<table class="table">
							<tr>
								<td class="col-2">제 목</td>
								<td>
									<input type="text" name="subject" id="form-subject" class="form-control">
									<small class="form-control-plaintext help-block">
										* 제목은 필수 입니다.
									</small>
								</td>
							</tr>
							
							<tr>
								<td class="col-2">일정색상</td>
								<td>
									<input type="color" name="color" id="form-color" value="#000" title="일정 분류 색상 선택" style="width: 50px; height: 35px; border: none; cursor: pointer; border-radius: 6px;">
								</td>
							</tr>
	
							
							<tr>
								<td class="col-2">시작일자</td>
								<td>
									<div class="row">
										<div class="col col-sm-4 pe-1">
											<input type="date" name="do_date" id="form-doDate" class="form-control">
										</div>
									</div>
									<small class="form-control-plaintext help-block">
										* 시작날짜는 필수입니다.
									</small>
								</td>
							</tr>
	
							<tr>
								<td class="col-2">종료일자</td>
								<td>
									<div class="row">
										<div class="col col-sm-4 pe-1">
											<input type="date" name="end_date" id="form-endDate" class="form-control">
										</div>
									</div>
									<small class="form-control-plaintext help-block">
										종료일자는 선택사항이며, 시작일자보다 작을 수 없습니다.
									</small>
								</td>
							</tr>
	
							<tr>
								<td class="col-2">메 모</td>
								<td>
									<textarea name="memo" id="form-memo" class="form-control" style="height: 70px; resize: none;"></textarea>
								</td>
							</tr>
							
							<tr>
								<td colspan="2" class="text-center" style="border-bottom: none;">
									<input type="hidden" name="num" id="form-num"  value="0">
									<button type="button" class="btn btn-dark" id="btnScheduleSendOk"> 등록 완료 </button>
									<button type="button" class="btn btn-light" id="btnScheduleSendCancel"> 등록 취소 </button>
								</td>
							</tr>
        			</table>
        		</form>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

$(function(){
    // 실행과 동시에 처음 탭에 출력
    let url = '${pageContext.request.contextPath}/myPage/schedule/month';
    let params = '';
    
    schedule(url, params, '#nav-1');
    
});

$(function(){
    // 탭을 클릭할 때 마다
    $('button[role="tab"]').on('click', function(e){
    	let tab = $(this).attr('aria-controls');
        let selector = '#nav-' + tab;
        
        let url;
        if(tab === '1') {
            url = '${pageContext.request.contextPath}/myPage/schedule/month';
        } else if(tab === '2') {
            url = '${pageContext.request.contextPath}/myPage/schedule/day';
        } else if(tab === '3') {
            url = '${pageContext.request.contextPath}/myPage/schedule/year';
        }

        let params = '';
        schedule(url, params, selector);
    });
    
    // 탭이 변경 될때 마다
    $('button[role="tab"]').on('shown.bs.tab', function(e){
		let tab = $(this).attr('aria-controls');
        let selector = '#nav-' + tab;
		today();
    });
    
});

$('.menu--link2').on('click', function(e){
    e.preventDefault();  // 기본 이동 막기
    
    let tab = $(this).attr('aria-controls');
    if(!tab) return;

    let selector = '#nav-' + tab;
    let url = '';
    if(tab === '1') url = '${pageContext.request.contextPath}/myPage/schedule/month';
    else if(tab === '2') url = '${pageContext.request.contextPath}/myPage/schedule/day';
    else if(tab === '3') url = '${pageContext.request.contextPath}/myPage/schedule/year';
	
    let params = '';
    schedule(url, params, selector);
    
    const tabEl = document.querySelector('#myTab #tab-' + tab);
    if(tabEl) {
        const tabInstance = new bootstrap.Tab(tabEl);
        tabInstance.show();
    }
});



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

function schedule(url, params, selector) {
	const fn = function(data){
		$(selector).html(data);
		today();
	};
	
	sendAjaxRequest(url, 'get', params, 'text', fn);	
}

function today() {
	let date = '${today}';
	$('.textDate').each(function (i) {
        let s = $(this).attr('data-date');
        if(s === date) {
        	$(this).parent().css('background', '#ffffd9');
        }
    });
}

// 월별 - 월을 변경하는 경우
function changeMonth(year, month) {
	let url = '${pageContext.request.contextPath}/myPage/schedule/month';
	let params = 'year=' + year + '&month=' + month;
	
	schedule(url, params, '#nav-1');
}

// 상세 - 날짜를 변경하는 경우
function changeDate(date) {
	let url = '${pageContext.request.contextPath}/myPage/schedule/day';
	let params = 'date=' + date;
	
	schedule(url, params, '#nav-2');
}

// 년도 - 년도를 변경하는 경우
function changeYear(year) {
	let url = '${pageContext.request.contextPath}/myPage/schedule/year';
	let params = 'year=' + year;
	
	schedule(url, params, '#nav-3');
}

//월별 - 스케쥴 제목을 클릭한 경우
$(function(){
	$('body').on('click', '.scheduleSubject', function(){
		let date = $(this).attr('data-date');
		let num = $(this).attr('data-num');
		let url = '${pageContext.request.contextPath}/myPage/schedule/day';
		let params = 'date=' + date + '&num=' + num;
		
		const tabEl = document.querySelector('#myTab #tab-2');
		const tab = new bootstrap.Tab(tabEl);
		tab.show();
		
		schedule(url, params, '#nav-2');
	});
});

// 월별 - more(더보기) 를 클릭 한 경우
$(function(){
	$('body').on('click', '.scheduleMore', function(){
		let date = $(this).attr('data-date');
		let url = '${pageContext.request.contextPath}/myPage/schedule/day';
		let params = 'date=' + date;
		
		const tabEl = document.querySelector('#myTab #tab-2');
		const tab = new bootstrap.Tab(tabEl);
		tab.show();

		schedule(url, params, '#nav-2');
	});
});

// 월별 - 날짜를 클릭한 경우 : 일정 등록
$(function(){
	$('body').on('click', '#largeCalendar .textDate', function(){
		// 폼 reset
		$('form[name=scheduleForm]').each(function(){
			this.reset();
		});
		
// 		$('#form-repeat_cycle').hide();
		$('#form-allDay').prop('checked', true);
		$('#form-allDay').removeAttr('disabled');
		$('#form-endDate').closest('tr').show();
		
		let date = $(this).attr('data-date');
		date = date.substr(0, 4) + '-' + date.substr(4, 2) + '-' + date.substr(6, 2);

		$('form[name=scheduleForm] input[name=do_date]').val(date);
		$('form[name=scheduleForm] input[name=end_date]').val(date);
		
		$('#myDialogModalLabel').html('스케쥴 등록');
		$('#btnScheduleSendOk').attr('data-mode', 'insert');
		$('#btnScheduleSendOk').html(' 등록 완료 ');
		$('#btnScheduleSendCancel').html(' 등록 취소 ');
		
		$('#myDialogModal').modal('show');
	});
});

// 상세일정 - 날짜 클릭
$(function(){
	$('body').on('click', '#smallCalendar .textDate', function(){
		let date = $(this).attr('data-date');
		let url = '${pageContext.request.contextPath}/myPage/schedule/day';
		let params = 'date=' + date;
		
		schedule(url, params, '#nav-2');
	});
});

// 상세일정 - 다른일정 제목 클릭
$(function(){
	$('body').on('click', '.daySubject', function(){
		let date = $(this).attr('data-date');
		let num = $(this).attr('data-num');
		let url = '${pageContext.request.contextPath}/myPage/schedule/day';
		let params = 'date=' + date + '&num=' + num;
		
		schedule(url, params, '#nav-2');
	});
});

// 상세일정 - 수정 버튼
$(function(){
	$('body').on('click', '#btnUpdate', function(){
		let date = $(this).attr('data-date');
		let num = $(this).attr('data-num');
		
		let subject = $('.date-schedule input[name=subject]').val();
// 		let color = $('.date-schedule input[name=color]').val();
		let allDay = $('.date-schedule input[name=allDay]').val();
		let do_date = $('.date-schedule input[name=do_date]').val();
		let end_date = $('.date-schedule input[name=end_date]').val();
		if(! end_date ) end_date = end_date;
		let memo = $('.date-schedule input[name=memo]').val();
		
		$('#form-num').val(num);
		$('#form-subject').val(subject);
// 		$('#form-color').val(color);
		if(allDay === '1') {
			$('#form-allDay').prop('checked', true);
		} else {
			$('#form-allDay').prop('checked', false);
		}
		$('#form-doDate').val(do_date);
		$('#form-endDate').val(end_date);
// 		$('#form-repeat').val(repeat);
// 		$('#form-repeat_cycle').val(repeat_cycle);
// 		if(repeat === '1') {
// 			$('#form-repeat_cycle').show();
// 			$('#form-endDate').closest('tr').hide();
// 		} else {
// 			$('#form-repeat_cycle').val('');
// 			$('#form-repeat_cycle').hide();
// 			$('#form-endDate').closest('tr').show();
// 		}		
		$('#form-memo').val(memo);
		
		$('#myDialogModalLabel').html('스케쥴 수정');
		$('#btnScheduleSendOk').attr('data-mode', 'update');
		$('#btnScheduleSendOk').attr('data-num', num);
		$('#btnScheduleSendOk').attr('data-date', date);
		
		$('#btnScheduleSendOk').html(' 수정 완료 ');
		$('#btnScheduleSendCancel').html(' 수정 취소 ');
		
		$('#myDialogModal').modal('show');
	});
});

// 상세일정 - 삭제 버튼
$(function(){
	$('body').on('click', '#btnDelete', function(){
		if(! confirm('일정을 삭제 하시겠습니까 ? ')) {
			return false;
		}
		
		let date = $(this).attr('data-date');
		let num = $(this).attr('data-num');
		let url = '${pageContext.request.contextPath}/myPage/schedule/delete';
		let params = 'num=' + num;
		
		const fn = function(data) {
			if(data.state === 'true') {
				let url = '${pageContext.request.contextPath}/maPage/schedule/day';
				let params = 'date=' + date;
				schedule(url, params, '#nav-2');
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 년도 - 날짜 클릭
$(function(){
	$('body').on('click', '#yearCalendar .textDate', function(){
		let date = $(this).attr('data-date');
		let url = '${pageContext.request.contextPath}/myPage/schedule/day';
		let params = 'date=' + date;
		
		const tabEl = document.querySelector('#myTab #tab-2');
		const tab = new bootstrap.Tab(tabEl);
		tab.show();
		
		schedule(url, params, '#nav-2');
	});
});

// 등록/수정 대화상자 - 등록완료 버튼
$(function(){
	$('#btnScheduleSendOk').click(function(){
		if(! check()) {
			return false;
		}
		
		let mode = $('#btnScheduleSendOk').attr('data-mode');
		let params = $('form[name=scheduleForm]').serialize();
		let url = '${pageContext.request.contextPath}/myPage/schedule/' + mode;

		const fn = function(data){
			let state = data.state;
			if(state === 'true') {
				if(mode === 'insert') {
					let dd = $('#form-doDate').val().split('-');
					let y = dd[0];
					let m = dd[1];
					if(m.substr(0,1) === '0') m = m.substr(1, 1);
				
				    let url = '${pageContext.request.contextPath}/myPage/schedule/month';
				    let params = 'year=' + y + '&month=' + m;
				    schedule(url, params, '#nav-1');
				} else if(mode === 'update') {
					let num = $('#btnScheduleSendOk').attr('data-num');
					let date = $('#btnScheduleSendOk').attr('data-date');
					
					let url = '${pageContext.request.contextPath}/myPage/schedule/day'
					let params = 'date='+date+'&num='+num;
						
					schedule(url, params, '#nav-2');
				}
			}
			
			$('#myDialogModal').modal('hide');
			
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);		
	});
});

// 등록/수정 대화상자 - 취소 버튼
$(function(){
	$('#btnScheduleSendCancel').click(function(){
		$('#myDialogModal').modal('hide');
	});
});

$(function(){

	$('body').on('change', '#form-doDate', function(){
		$('#form-endDate').val($('#form-doDate').val());
	});

});

// 등록내용 유효성 검사
function check() {
	if(! $('#form-subject').val()) {
		$('#form-subject').focus();
		return false;
	}

	if(! $('#form-doDate').val()) {
		$('#form-doDate').focus();
		return false;
	}

	if($('#form-endDate').val()) {
		let s1 = $('#form-doDate').val().replace('-', '');
		let s2 = $('#form-endDate').val().replace('-', '');
		if(s1 > s2) {
			$('#form-doDate').focus();
			return false;
		}
	}
	
	if($('#form-etime').val()) {
		let s1 = $('#form-stime').val().replace(':', '');
		let s2 = $('#form-etime').val().replace(':', '');
		if(s1 > s2) {
			$('#form-stime').focus();
			return false;
		}
	}	
	
	return true;
}

</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>