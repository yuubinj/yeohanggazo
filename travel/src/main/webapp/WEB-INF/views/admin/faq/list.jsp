<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Bootstrap 5 및 Icons CDN -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<style>
.accordion-body .dropdown {
	position: absolute;
	top: 10px;
	right: 15px;
}
.accordion-body {
	position: relative;
}
.category-fixed {
	position: absolute;
	right: 5%;
	white-space: nowrap;
	color: #888;
	font-size: 0.875rem;
}
</style>

<script>
document.addEventListener("DOMContentLoaded", function () {
	const buttons = document.querySelectorAll(".accordion-button");

	buttons.forEach(btn => {
		btn.addEventListener("click", function (e) {
			const targetSelector = btn.getAttribute("data-bs-target");
			const target = document.querySelector(targetSelector);
			const instance = bootstrap.Collapse.getOrCreateInstance(target);

			// 이미 열려 있는 경우 토글 닫기
			if (target.classList.contains("show")) {
				instance.hide();
				e.preventDefault();
			} else {
				// 다른 열려있는 패널 닫기 (자동 처리됨)
				instance.show();
			}
		});
	});
});
</script>

<c:if test="${list.size() > 0}">
	<div class="accordion accordion-flush mt-2" id="accordionFlush">
		<c:forEach var="dto" items="${list}" varStatus="status">
			<div class="accordion-item" style="border: none;">
				<h2 class="accordion-header mb-1 border" id="flush-heading-${status.index}">
				<button class="accordion-button collapsed bg-light d-flex justify-content-between align-items-center"
					type="button"
					data-bs-toggle="collapse"
					data-bs-target="#flush-collapse-${status.index}"
					aria-expanded="false"
					aria-controls="flush-collapse-${status.index}">
					
					<span class="text-primary fw-bold me-1">Q.</span>
					<span>${dto.subject}</span>
					<span class="position-absolute text-muted small" style="left: 88%; white-space: nowrap;">
						| ${dto.category}
					</span>
				</button>

				</h2>
				<div id="flush-collapse-${status.index}" class="accordion-collapse collapse"
					aria-labelledby="flush-heading-${status.index}" data-bs-parent="#accordionFlush">
					<div class="accordion-body">
						<!-- 우측 상단 드롭다운 버튼 -->
						<div class="dropdown">
							<button class="btn btn-sm" type="button" id="dropdownMenu${status.index}" data-bs-toggle="dropdown" aria-expanded="false">
								<i class="bi bi-three-dots-vertical"></i>
							</button>
							<ul class="dropdown-menu" aria-labelledby="dropdownMenu${status.index}">
								<li><a class="dropdown-item" href="#" onclick="updateFaq('${dto.num}', '${pageNo}'); return false;">수정</a></li>
								<li><a class="dropdown-item" href="#" onclick="deleteFaq('${dto.num}', '${pageNo}'); return false;">삭제</a></li>
							</ul>
						</div>

						<!-- 본문 -->
						<div class="mt-3">
							${dto.content}
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</c:if>

<!-- 페이징 -->
<div class="page-navigation mt-3">
	${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
</div>
