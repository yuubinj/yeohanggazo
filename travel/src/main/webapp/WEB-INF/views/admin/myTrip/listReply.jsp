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
						<c:when test="${sessionScope.member.userId == vo.userId}">
							<div class="deleteReply reply-menu-item" data-replyNum="${vo.replyNum}" data-pageNo="${pageNo}">삭제</div>
							<div class="hideReply reply-menu-item" data-replyNum="${vo.replyNum}" data-showReply="${vo.showReply}">${vo.showReply ==1? "숨김":"표시"}</div>
						</c:when>
						
						<c:when test="${sessionScope.member.userLevel >= 51}">
							<div class="deleteReply reply-menu-item" data-replyNum="${vo.replyNum}" data-pageNo="${pageNo}">삭제</div>
							<div class="blockReply reply-menu-item">차단</div>
						</c:when>
						
						<c:otherwise>
							<div class="blockReply reply-menu-item">차단</div>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2" valign="top" class="${vo.showReply==0?'text-primary text-opacity-50':''}">${vo.content}</td>
		</tr>

		<tr>
			<td>
				<button type="button" class="btn btn-light btnReplyAnswerLayout" data-replyNum="${vo.replyNum}">답글 <span id="answerCount${vo.replyNum}">${vo.answerCount}</span></button>
			</td>
			<td align="right" data-userReplyLike="${vo.userReplyLike}">
				<button type="button" class="btn btn-light btnSendReplyLike" data-replyNum="${vo.replyNum}" data-replyLike="1" title="좋아요"><i class="bi bi-hand-thumbs-up" style="${vo.userReplyLike==1? 'color:#0d6efd;':''}"></i> <span>${vo.likeCount}</span></button>
				<button type="button" class="btn btn-light btnSendReplyLike" data-replyNum="${vo.replyNum}" data-replyLike="0" title="싫어요"><i class="bi bi-hand-thumbs-down"style="${vo.userReplyLike==0? 'color:red;':''}"></i> <span>${vo.disLikeCount}</span></button>	        
			</td>
		</tr>
	
		<tr class="reply-answer d-none">
			<td colspan="2">
				<div class="border rounded">
					<div id="listReplyAnswer${vo.replyNum}" class="answer-list"></div>
				</div>
			</td>
		</tr>
	</c:forEach>
</table>

<div class="page-navigation">
	${paging}
</div>
