<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<style type="text/css">
.faq-list {
	margin-top: 10px;
}

.faq-item {
	border: 1px solid #dfe3e8;
	border-radius: 6px;
	margin-bottom: 15px;
	overflow: hidden;
	background: #fff;
}

.faq-subject {
	font-size: 16px;
	font-weight: 600;
	cursor: pointer;
	margin: 0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 20px;
	border-bottom: 1px solid #eee;
	color: #222;
	background-color: #fff;
}

.faq-subject .left {
	display: flex;
	align-items: center;
	gap: 8px;
}

.faq-subject .faq-q {
	color: #0d6efd;
	font-weight: bold;
}

.faq-subject .faq-category {
	color: #888;
	font-size: 14px;
	margin-left: 10px;
}

.faq-answer {
	display: none;
	padding: 20px;
	background: #f9f9f9;
	border: 1px solid #ddd;
	border-top: none;
	font-size: 15px;
	line-height: 1.6;
	color: #444;
}

.no-data {
	padding: 30px 0;
	text-align: center;
	color: #888;
	font-size: 16px;
}

.page-navigation {
	margin-top: 20px;
	text-align: center;
}
</style>

<div class="faq-list">
	<c:choose>
		<c:when test="${empty list}">
			<div class="no-data">등록된 게시물이 없습니다.</div>
		</c:when>
		<c:otherwise>
			<c:forEach var="dto" items="${list}">
				<div class="faq-item">
					<p class="faq-subject" onclick="toggleAnswer(this)">
						<span class="left">
							<span class="faq-q">Q.</span>
							<span>${dto.subject}</span>
						</span>
						<span class="faq-category">| ${dto.category}</span>
					</p>
					<div class="faq-answer">
						${dto.content}
					</div>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</div>

<div class="page-navigation">
	${paging}
</div>

<script>
function toggleAnswer(el) {
	const answer = el.nextElementSibling;
	const isVisible = answer.style.display === 'block';

	document.querySelectorAll('.faq-answer').forEach(e => e.style.display = 'none');
	if (!isVisible) {
		answer.style.display = 'block';
	}
}
</script>
