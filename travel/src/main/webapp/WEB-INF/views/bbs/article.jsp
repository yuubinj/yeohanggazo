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
					<h3><i class="bi bi-book-half"></i> 
						<c:choose>
							<c:when test="${categoryNum == 1}">공지</c:when>
						  	<c:when test="${categoryNum == 2}">현지인 추천</c:when>
						  	<c:when test="${categoryNum == 3}">리뷰</c:when>
						  	<c:when test="${categoryNum == 4}">기타</c:when>
						  	<c:otherwise>전체</c:otherwise>
						</c:choose> 게시글
					</h3>
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
								<td align="right">
									${dto.reg_date} | 조회 ${dto.hitCount}
								</td>
							</tr>
							
							<tr>
								<td colspan="2" valign="top" height="200" style="border-bottom:none;">
									${dto.content}
								</td>
							</tr>
							<tr>
								<td colspan="2" class="text-center" style="border-bottom: none;">
									<div class="d-flex justify-content-center align-items-center">
										<div class="flex-grow-1 text-center">
											<c:choose>
												<c:when test="${sessionScope.member != null}">
													<button type="button" class="btn btn-outline-secondary btnSendBoardLike" title="좋아요">
														<i class="bi ${isUserLiked ? 'bi-hand-thumbs-up-fill' : 'bi-hand-thumbs-up'}"></i>&nbsp;&nbsp;
														<span id="likeCount">${dto.boardLikeCount}</span>
													</button>
												</c:when>
												<c:otherwise>
													<button type="button" class="btn btn-outline-secondary" title="로그인 후 이용 가능"
														onclick="alert('로그인이 필요한 기능입니다.'); location.href='${pageContext.request.contextPath}/member/login';">
														<i class="bi bi-hand-thumbs-up"></i>&nbsp;&nbsp;
														<span id="likeCount">${dto.boardLikeCount}</span>
													</button>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<c:if test="${not empty dto.saveFilename}">
										<p class="border text-secondary mb-1 p-2">
											<i class="bi bi-folder2-open"></i>
											<a href="${pageContext.request.contextPath}/bbs/download?num=${dto.num}">${dto.originalFilename}</a>
										</p>
									</c:if>
								</td>
							</tr>
							
							<tr>
							  <td colspan="2">
							    다음글 :
							    <c:if test="${not empty nextDto}">
							      <c:url var="nextLink" value="/bbs/article">
							        <c:param name="num" value="${nextDto.num}" />
							        <c:param name="categoryNum" value="${categoryNum}" />
							        <c:param name="page" value="${page}" />
							        <c:if test="${not empty schType}">
							          <c:param name="schType" value="${schType}" />
							        </c:if>
							        <c:if test="${not empty kwd}">
							          <c:param name="kwd" value="${kwd}" />
							        </c:if>
							      </c:url>
							      <a href="${nextLink}">[${nextDto.category}] ${nextDto.subject}</a>
							    </c:if>
							  </td>
							</tr>
							
							<tr>
							  <td colspan="2">
							    이전글 :
							    <c:if test="${not empty prevDto}">
							      <c:url var="prevLink" value="/bbs/article">
							        <c:param name="num" value="${prevDto.num}" />
							        <c:param name="categoryNum" value="${categoryNum}" />
							        <c:param name="page" value="${page}" />
							        <c:if test="${not empty schType}">
							          <c:param name="schType" value="${schType}" />
							        </c:if>
							        <c:if test="${not empty kwd}">
							          <c:param name="kwd" value="${kwd}" />
							        </c:if>
							      </c:url>
							      <a href="${prevLink}">[${prevDto.category}] ${prevDto.subject}</a>
							    </c:if>
							  </td>
							</tr>
						</tbody>
					</table>
					
					<table class="table table-borderless mb-2">
						<tr>
							<td width="50%">
								<c:if test="${sessionScope.member != null}">
									<c:if test="${sessionScope.member.userId == dto.userId}">
										<!-- 본인 글일 경우 수정/삭제 가능 -->
										<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/bbs/update?num=${dto.num}&categoryNum=${categoryNum}&page=${page}';">수정</button>
										<button type="button" class="btn btn-light" onclick="deleteBoard();">삭제</button>
									</c:if>
									<c:if test="${sessionScope.member.userId != dto.userId && sessionScope.member.userLevel >= 51}">
										<!-- 관리자만 삭제 가능 -->
										<button type="button" class="btn btn-light" onclick="deleteBoard();">삭제</button>
									</c:if>
								</c:if>
							</td>
							<td class="text-end">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/bbs/list?${query}';">리스트</button>
							</td>
						</tr>
					</table>

					<!-- 댓글 영역 -->
					<div class="reply">
						<form name="replyForm" method="post">
							<div class="form-header">
								<span class="bold">댓글</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
							</div>
							
							<table class="table table-borderless reply-form">
								<tr>
									<td>
										<textarea class="form-control" name="content"></textarea>
									</td>
								</tr>
								<tr>
									<td class="text-end">
										<c:choose>
											<c:when test="${sessionScope.member != null}">
												<button type="button" class="btn btn-light btnSendReply">댓글 등록</button>
											</c:when>
											<c:otherwise>
												<button type="button" class="btn btn-light" onclick="alert('로그인이 필요한 기능입니다.'); location.href='${pageContext.request.contextPath}/member/login';">댓글 등록</button>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</table>
						</form>
						<div id="listReply"></div>
					</div>

				</div>
			</div>
		</div>
	</div>
</main>

<c:if test="${sessionScope.member != null && (sessionScope.member.userId == dto.userId || sessionScope.member.userLevel >= 51)}">
	<script type="text/javascript">
		function deleteBoard() {
		    if(confirm('게시글을 삭제 하시겠습니까 ?')) {
			    let params = 'num=${dto.num}&${query}';
			    let url = '${pageContext.request.contextPath}/bbs/delete?' + params;
		    	location.href = url;
		    }
		}
	</script>
</c:if>

<script type="text/javascript">
$(function(){
	const isLoggedIn = "${sessionScope.member != null ? 'true' : 'false'}" === "true";
	const isWriter = "${sessionScope.member != null && sessionScope.member.userId == dto.userId ? 'true' : 'false'}" === "true";
	const isAdmin = "${sessionScope.member != null && sessionScope.member.userLevel >= 99 ? 'true' : 'false'}" === "true";

	if (!isLoggedIn || (!isWriter && !isAdmin)) {
		$('button:contains("수정")').hide();
		$('button:contains("삭제")').hide();
	}
});

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
				    // 댓글 불러올 때는 무시
				    if (url.includes('/listReply')) {
				        console.log("비로그인 상태에서 댓글 불러오기 시도 — 무시");
				        return;
				    }

				    // 그 외 기능은 로그인 유도
				    alert('로그인이 필요한 기능입니다.');
				    location.href = '${pageContext.request.contextPath}/member/login';
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

// 게시글 공감 여부
$(function(){
	$('.btnSendBoardLike').click(function(){
		const $i = $(this).find('i');
		let userLiked = $i.hasClass('bi-hand-thumbs-up-fill');
		let msg = userLiked ? '게시글 공감을 취소하시겠습니까 ? ' : '게시글에 공감하십니까 ? ';
		
		if(! confirm( msg )) {
			return false;
		}
		
		let url = '${pageContext.request.contextPath}/bbs/insertBoardLike';
		let num = '${dto.num}';
		let params = {num:num, userLiked:userLiked};
		
		const fn = function(data){
			let state = data.state;
			if(state === 'true') {
				if( userLiked ) {
					$i.removeClass('bi-hand-thumbs-up-fill').addClass('bi-hand-thumbs-up');
				} else {
					$i.removeClass('bi-hand-thumbs-up').addClass('bi-hand-thumbs-up-fill');
				}
				
				let count = data.boardLikeCount;
				$('#likeCount').text(count);
			} else if(state === 'liked') {
				alert('게시글 공감은 한번만 가능합니다. !!!');
			} else if(state === "false") {
				alert('게시물 공감 여부 처리가 실패했습니다. !!!');
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 페이징 처리
$(function(){
	listPage(1);
});

function listPage(page) {
	let url = '${pageContext.request.contextPath}/bbs/listReply';
	let num = '${dto.num}';
	let params = {num:num, pageNo:page};
	let selector = '#listReply';
	
	const fn = function(data){
		$(selector).html(data);
	};

	sendAjaxRequest(url, 'get', params, 'text', fn);
}

// 리플 등록
$(function(){
	$('.btnSendReply').click(function(){
		let num = '${dto.num}';
		const $tb = $(this).closest('table');

		let content = $tb.find('textarea').val().trim();
		if(! content) {
			$tb.find('textarea').focus();
			return false;
		}
		
		let url = '${pageContext.request.contextPath}/bbs/insertReply';
		let params = {num:num, content:content, parentNum:0};
		
		const fn = function(data){
			$tb.find('textarea').val('');
			
			let state = data.state;
			if(state === 'true') {
				listPage(1);
			} else if(state === 'false') {
				alert('댓글을 추가 하지 못했습니다.');
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 삭제, 신고 메뉴
$(function(){
	$('.reply').on('click', '.reply-dropdown', function(){
		const $menu = $(this).next('.reply-menu');
		
		let isHidden = $menu.hasClass('d-none');
		
		if(isHidden) {
			$('.reply-menu').not('.d-none').addClass('d-none');
			
			$menu.removeClass('d-none');
			
			let pos = $(this).offset();
			$menu.offset( {left:pos.left-70, top:pos.top+20} );
		} else {
			$menu.addClass('d-none');
		}
	});
	
	$('.reply').on('click', function(evt) {
		if($(evt.target.parentNode).hasClass('reply-dropdown')) {
			return false;
		}
		
		$('.reply-menu').not('.d-none').addClass('d-none');
	});
});

// 댓글 삭제
$(function(){
	$('.reply').on('click', '.deleteReply', function(){
		if(! confirm('게시물을 삭제하시겠습니까 ? ')) {
		    return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let page = $(this).attr('data-pageNo');
		
		let url = '${pageContext.request.contextPath}/bbs/deleteReply';
		let params = {replyNum:replyNum, mode:'reply'};
		
		const fn = function(data){
			// let state = data.state;
			listPage(page);
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 댓글별 답글 리스트
function listReplyAnswer(parentNum) {
	let url = '${pageContext.request.contextPath}/bbs/listReplyAnswer';
	let params = { parentNum: parentNum };
	let selector = '#listReplyAnswer' + parentNum;
	
	const fn = function(data){
		$(selector).html(data);
	};
	sendAjaxRequest(url, 'get', params, 'text', fn);
}

// 댓글별 답글 개수
function countReplyAnswer(parentNum) {
	let url = '${pageContext.request.contextPath}/bbs/countReplyAnswer';
	let params = 'parentNum=' + parentNum;
	
	const fn = function(data){
		let count = data.count;
		let selector = '#answerCount' + parentNum;
		$(selector).html(count);
	};
	
	sendAjaxRequest(url, 'post', params, 'json', fn);
}

// 답글 버튼(댓글별 답글 등록폼 및 답글리스트)
$(function(){
	$('.reply').on('click', '.btnReplyAnswerLayout', function(){
		const $trReplyAnswer = $(this).closest('tr').next();  // 답글창 tr
		let replyNum = $(this).attr('data-replyNum');

		if( $trReplyAnswer.hasClass('d-none')) {
			listReplyAnswer(replyNum);     // 답글 목록 불러오기
			countReplyAnswer(replyNum);    // 답글 개수 갱신
		}

		$trReplyAnswer.toggleClass('d-none');	
	});
});


// 댓글별 답글 등록
$(function(){
	$('.reply').on('click', '.btnSendReplyAnswer', function(){
		let num = '${dto.num}';
		let replyNum = $(this).attr('data-replyNum');
		const $td = $(this).closest('td');
		
		let content = $td.find('textarea').val().trim();
		if(! content) {
			$td.find('textarea').focus();
			return false;
		}
		
		let url = '${pageContext.request.contextPath}/bbs/insertReply';
		let params = {num:num, content:content, parentNum:replyNum};
		
		const fn = function(data){
			$td.find('textarea').val('');
			
			var state = data.state;
			if(state === 'true') {
				listReplyAnswer(replyNum);
				countReplyAnswer(replyNum);
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 댓글별 답글 삭제
$(function(){
	$('.reply').on('click', '.deleteReplyAnswer', function(){
		if(! confirm('게시물을 삭제하시겠습니까 ? ')) {
		    return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let parentNum = $(this).attr('data-parentNum');
		
		let url = '${pageContext.request.contextPath}/bbs/deleteReply';
		let params = {replyNum:replyNum, mode:'answer'};
		
		const fn = function(data){
			listReplyAnswer(parentNum);
			countReplyAnswer(parentNum);
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

// 댓글 숨김기능
$(function(){
	$('.reply').on('click', '.hideReply', function(){
		let $menu = $(this);
		
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		let msg = '댓글을 숨김 하시겠습니까 ? ';
		if(showReply === '0') {
			msg = '댓글 숨김을 해제 하시겠습니까 ? ';
		}
		if(! confirm(msg)) {
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		let url = '${pageContext.request.contextPath}/bbs/replyShowHide';
		let params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data){
			if(data.state === 'true') {
				let $item = $($menu).closest('tr').next('tr').find('td');
				if(showReply === '1') {
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

// 답글 숨김기능
$(function(){
	$('.reply').on('click', '.hideReplyAnswer', function(){
		let $menu = $(this);
		
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		
		let msg = '댓글을 숨김 하시겠습니까 ? ';
		if(showReply === '0') {
			msg = '댓글 숨김을 해제 하시겠습니까 ? ';
		}
		if(! confirm(msg)) {
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		let url = '${pageContext.request.contextPath}/bbs/replyShowHide';
		let params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data){
			if(data.state === 'true') {
				let $item = $menu.closest('.row').next('div');
				if(showReply === '1') {
					$item.removeClass('text-primary').removeClass('text-opacity-50');
					$menu.attr('data-showReply', '1');
					$menu.html('숨김');
				} else {
					$item.addClass('text-primary').addClass('text-opacity-50');
					$menu.attr('data-showReply', '0');
					$menu.html('표시');
				}
			}
		};
		
		sendAjaxRequest(url, 'post', params, 'json', fn);
	});
});

</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>
</body>
</html>
