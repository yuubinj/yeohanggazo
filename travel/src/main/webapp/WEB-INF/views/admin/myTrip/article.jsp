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
.min-h-150 {
   min-height: 150px;
}
</style>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-app"></i> 내 여행 자랑하기 </h3>
				</div>
				
				<div class="body-main">

					<table class="table board-article">
						<thead>
							<tr>
								<td colspan="2" align="center">
									${dto.subject}
								</td>
							</tr>
						</thead>
						
						<tbody>
							<tr>
								<td width="50%">
									이름 : ${dto.userName}
								</td>
								<td width="50%" align="right">
									${dto.reg_date} | 조회 ${dto.hitCount}
								</td>
							</tr>
							
							<tr>
								<td style="border-bottom: none; width: 100%" colspan="2" valign="top" >
									<c:forEach var="vo" items="${dto.listFileSavename}" varStatus="voStatus">
												<img src="${pageContext.request.contextPath}/uploads/myTrip/${vo}"
												 	class="img-fluid img-thumbnail h-auto" style="width: 33%">
									</c:forEach>
								</td>
							</tr>
							<tr>
								<td colspan="2" valign="top">
									${dto.content}
								</td>
							</tr>
							
							<tr style="border-top: none;">
								<td colspan="2" class="text-center p-3">
									<button type="button" class="btn btn-outline-primary btnSendMyTripLike" title="좋아요"><i class="bi ${isUserLiked ? 'bi-hand-thumbs-up-fill':'bi-hand-thumbs-up'}"></i>&nbsp;&nbsp;<span id="myTripLikeCount">${dto.myTripLikeCount}</span></button>
								</td>
							</tr>
							
							<tr>
								<td colspan="2">
									이전글 :
									<c:if test="${not empty prevDto}">
										<a href="${pageContext.request.contextPath}/admin/myTrip/article?${query}&num=${prevDto.num}">${prevDto.subject}</a>
									</c:if>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									다음글 :
									<c:if test="${not empty nextDto}">
										<a href="${pageContext.request.contextPath}/admin/myTrip/article?${query}&num=${nextDto.num}">${nextDto.subject}</a>
									</c:if>	
								</td>
							</tr>
						</tbody>
					</table>
					
					<table class="table table-borderless mb-2">
						<tr>
							<td width="50%">
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.userId}">
										<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/myTrip/update?num=${dto.num}&pageNo=${pageNo}';">수정</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn btn-light" disabled>수정</button>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userLevel >= 51}">
							    		<button type="button" class="btn btn-light" onclick="deleteOk();">삭제</button>
							    	</c:when>
							    	<c:otherwise>
										<button type="button" class="btn btn-light" disabled>삭제</button>
									</c:otherwise>
							    </c:choose>
							</td>
							<td class="text-end">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/myTrip/list';">리스트</button>
							</td>
						</tr>
					</table>

					<div class="reply">
						<div id="listReply">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</main>

<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userLevel >= 51}">
	<script type="text/javascript">
		function deleteOk(){
			if(confirm('게시글을 삭제하시겠습니까 ? ')){
				let params = 'num=${dto.num}&${query}';
				let url = '${pageContext.request.contextPath}/admin/myTrip/delete?' + params;
				location.href = url;
			}
		}
	</script>
</c:if>

<script type="text/javascript">
function login() {
	location.href = '${pageContext.request.contextPath}/member/login';
}

function sendAjaxRequest(url, method, params, responseType, fn, file = false){
	const settings = {
			type: method,
			data: params,
			dataType: responseType,
			success: function(data){
				fn(data);
			},
			beforeSend: function(xhr){
				xhr.setRequestHeader('AJAX', true); // 로그인 필터에서 AJAX 요청인지 확인
			},
			complete: function(){
				
			},
			error: function(xhr){
				// 로그인 필터에서 로그인이 되어 있지 않으면 403 에러를 던짐
				if(xhr.status ==  403){
					login();
					return;
				} else if(xhr.status === 406){
					alert('요청 처리가 실패했습니다.');
					return false;
				}
				
				console.log(xhr.responseText);
			}
	};
	
	if(file) {
		settings.processData = false;
		settings.contentType = false;
	}
	
	$.ajax(url, settings);
}


//댓글 리스트
$(function(){
	listPage(1);
});

function listPage(page){
	let url = '${pageContext.request.contextPath}/admin/myTrip/listReply';
	let num = '${dto.num}';
	let params = {num:num, pageNo: page};
	let selector = '#listReply';
	
	const fn = function(data){
		$(selector).html(data);
	};
	
	sendAjaxRequest(url, 'get', params, 'text', fn);
}


// 삭제, 신고 메뉴
$(function(){
	$('.reply').on('click', '.reply-dropdown', function(){
		const $menu = $(this).next('.reply-menu');
		let isHidden = $menu.hasClass('d-none');
		
		if(isHidden){
			$('.reply-menu').not('.d-none').addClass('d-none');
			$menu.removeClass('d-none');
			
			let pos = $(this).offset();
			$menu.offset({left:pos.left-70, top:pos.top+20});
		} else {
			$menu.addClass('d-none');
		}
	});
	
	$('body').on('click', function(evt){
		if($(evt.target.parentNode).hasClass('reply-dropdown')){
			return false;
		}
		
		$('.reply-menu').not('.d-none').addClass('d-none');
	})
});

// 댓글 삭제
$(function(){
	$('.reply').on('click', '.deleteReply', function(){
		if(! confirm('게시글을 삭제하시겠습니까 ? ')){
			return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let page = $(this).attr('data-pageNo');
		
		let url = '${pageContext.request.contextPath}/admin/myTrip/deleteReply';
		let params = {replyNum:replyNum};
		
		const fn = function(data){
			listPage(page);
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 답글 버튼(댓글별 답글 등록 폼 및 답글 리스트)
$(function(){
	$('.reply').on('click', '.btnReplyAnswerLayout', function(){
		const $tr = $(this).closest('tr').next();
		
		let replyNum = $(this).attr('data-replyNum');
		
		if($tr.hasClass('d-none')){
			// 답글 리스트
			listReplyAnswer(replyNum);
			
			// 답글 개수
			countReplyAnswer(replyNum);
		}
		
		$tr.toggleClass('d-none');
	})
});

// 댓글별 답글 리스트
function listReplyAnswer(parentNum){
	let url = '${pageContext.request.contextPath}/admin/myTrip/listReplyAnswer';
	let params = {parentNum:parentNum};
	let selector = $('#listReplyAnswer' + parentNum);
	
	const fn = function(data){
		$(selector).html(data);
	};
	
	sendAjaxRequest(url, 'get', params, 'text', fn);
};

// 댓글별 답글 개수
function countReplyAnswer(parentNum){
	let url = '${pageContext.request.contextPath}/admin/myTrip/countReplyAnswer';
	let params = {parentNum:parentNum};
	
	const fn = function(data){
		let count = data.count;
		let selector = '#answerCount' + parentNum;
		$(selector).html(count);
	};
	
	sendAjaxRequest(url, 'post', params, 'json', fn);
};

// 답글 삭제
$(function(){
	$('.reply').on('click', '.deleteReplyAnswer', function(){
		if(! confirm('게시물을 삭제하시겠습니까 ? ')){
			return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let parentNum = $(this).attr('data-parentNum');
		
		let url = '${pageContext.request.contextPath}/admin/myTrip/deleteReply';
		let params = {replyNum:replyNum};
		
		const fn = function(data){
			listReplyAnswer(parentNum);
			countReplyAnswer(parentNum);
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

//댓글 숨김
$(function(){
	$('.reply').on('click', '.hideReply', function(){
		let $menu = $(this);
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		let msg = '게시물을 숨김하시겠습니까 ? ';
		
		if(showReply == '0'){
			msg = '게시물 숨김을 해제 하시겠습니까 ? ';
		}

		if(! confirm(msg)){
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		let url = '${pageContext.request.contextPath}/admin/myTrip/replyShowHide';
		let params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data){
			if(data.state === 'true'){
				let $item = $($menu).closest('tr').next('tr').find('td');
				if(showReply == '1'){
					$item.removeClass('text-primary').removeClass('text-opacity-50');
					$menu.attr('data-showReply', '1');
					$menu.text('숨김');
				} else {
					$item.addClass('text-primary').addClass('text-opacity-50');
					$menu.attr('data-showReply', '0');
					$menu.text('표시');
				}	
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

//답글 숨김
$(function(){
	$('.reply').on('click', '.hideReplyAnswer', function(){
		let $menu = $(this);
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		let msg = '게시물을 숨김하시겠습니까 ? ';
		
		if(showReply == '0'){
			msg = '게시물 숨김을 해제 하시겠습니까 ? ';
		}

		if(! confirm(msg)){
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		let url = '${pageContext.request.contextPath}/admin/myTrip/replyShowHide';
		let params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data){
			if(data.state === 'true'){
				let $item = $($menu).closest('.row').next('div');
				if(showReply == '1'){
					$item.removeClass('text-primary').removeClass('text-opacity-50');
					$menu.attr('data-showReply', '1');
					$menu.text('숨김');
				} else {
					$item.addClass('text-primary').addClass('text-opacity-50');
					$menu.attr('data-showReply', '0');
					$menu.text('표시');
				}
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});
</script>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>