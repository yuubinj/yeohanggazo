<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class="reply-info">
	<span class="reply-count">댓글 ${replyCount}개</span>
	<span>[목록, ${pageNo}/${total_page} 페이지]</span>
</div>

<table class="table table-borderless">
	<c:forEach var="vo" items="${listReply}">
		<tr class="border table-light">
			<td width="50%">
				<div class="row reply-writer">
					<div class="col-1"><i class="bi bi-person-circle text-muted icon"></i></div>
					<div class="col-auto align-self-center">
						<div class="name">${vo.userName}</div>
						<div class="date">${vo.reg_date}</div>
					</div>
				</div>
			</td>
			<td width="50%" align="right" class="align-middle">
				<span class="reply-dropdown"><i class="bi bi-three-dots-vertical"></i></span>
				<div class="reply-menu d-none">
					<c:choose>
					    <c:when test="${not empty sessionScope.member and sessionScope.member.userId == vo.userId}">
					        <div class="deleteReplyAnswer reply-menu-item" data-replyNum="${vo.replyNum}" data-parentNum="${vo.parentNum}">삭제</div>
					        <div class="hideReplyAnswer reply-menu-item" data-replyNum="${vo.replyNum}" data-showReply="${vo.showReply}">${vo.showReply == 1 ? "숨김":"표시"}</div>
					    </c:when>
					    <c:when test="${not empty sessionScope.member and sessionScope.member.userLevel == 99}">
					        <div class="deleteReplyAnswer reply-menu-item" data-replyNum="${vo.replyNum}" data-parentNum="${vo.parentNum}">삭제</div>
					        <div class="blockReplyAnswer reply-menu-item" data-block="${vo.block}" data-block="${vo.block}">${vo.block == 0 ? "해제":"차단"}</div>
					    </c:when>
					    <c:when test="${not empty sessionScope.member}">
					        <!-- 본인이 아니고, 관리자가 아닐 경우 -->
					        <c:if test="${sessionScope.member.userId != vo.userId}">
					            <div class="notifyReplyAnswer reply-menu-item">신고</div>
					        </c:if>
					    </c:when>
					    <c:when test="${empty sessionScope.member}">
					        <div class="reply-menu-item" onclick="if(confirm('이 기능은 로그인 후 이용 가능합니다.\n로그인하시겠습니까?')) location.href='${pageContext.request.contextPath}/member/login';">신고</div>
        					<div class="reply-menu-item" onclick="if(confirm('이 기능은 로그인 후 이용 가능합니다.\n로그인하시겠습니까?')) location.href='${pageContext.request.contextPath}/member/login';">차단</div>
					    </c:when>
					</c:choose>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2" valign="top" class="${vo.showReply==0?'text-primary text-opacity-50':''}">${vo.content}</td>
		</tr>
		<tr>
			<td>
				<button type="button" class="btn btn-light btnReplyAnswerLayout" data-replyNum="${vo.replyNum}">
					답글 <span id="answerCount${vo.replyNum}">${vo.answerCount}</span>
				</button>
			</td>
			<td align="right"></td>
		</tr>
		<tr class="reply-answer d-none">
			<td colspan="2">
				<div class="border rounded replyAnswer-form">
					<input type="hidden" name="num" value="${vo.num}" />
					<input type="hidden" name="parentNum" value="${vo.replyNum}" />
					<div id="listReplyAnswer${vo.replyNum}" class="answer-list"></div>
					<div>
						<textarea class="form-control m-2" name="content"></textarea>
					</div>
					<div class="text-end pe-2 pb-1">
						<c:choose>
							<c:when test="${sessionScope.member != null}">
								<button type="button" class="btn btn-outline-secondary btn-sm btnSendReplyAnswer" data-replyNum="${vo.replyNum}">등록</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn btn-outline-secondary btn-sm" onclick="alert('로그인이 필요한 기능입니다.'); location.href='${pageContext.request.contextPath}/member/login';">등록</button>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</td>
		</tr>
	</c:forEach>
</table>

<div class="page-navigation">
	${paging}
</div>
