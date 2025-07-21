<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.scrap-card {
	display: flex;
	flex-direction: column;
	border: 1px solid #ddd;
	padding: 10px;
	margin-bottom: 20px;
	height: 100%;
	background-color: #fff;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.scrap-image {
	width: 100%;
	height: 180px;
	object-fit: cover;
	background-color: #f0f0f0;
	flex-shrink: 0;
}

/* 텍스트 영역을 카드 하단에 고정시키기 위한 영역 */
.scrap-text {
	margin-top: auto; /* 나머지 공간 모두 위로 밀어내서 아래 고정 */
	padding-top: 10px;
}

.scrap-name {
	font-weight: bold;
	font-size: 1.1em;
	margin-bottom: 5px;
}

.scrap-addr {
	color: #666;
	font-size: 0.9em;
}

.scrap-image-wrapper {
	position: relative;
	width: 100%;
	height: 180px;
	background-color: #f0f0f0;
	display: flex;
	align-items: center;
	justify-content: center;
}

.scrap-image {
	width: 100%;
	height: 180px;
	object-fit: cover;
	display: block;
}

.alt-text {
	display: none; /* 기본은 숨김 */
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	color: #999;
	font-size: 1.1em;
	font-weight: bold;
	text-align: center;
	align-items: center;
	justify-content: center;
	display: flex;
}

  .star-btn i {
    color: gold;
    font-size: 24px;
    text-shadow:
      -1px -1px 0 #000,
      1px -1px 0 #000,
      -1px 1px 0 #000,
      1px 1px 0 #000;
  }

</style>

<div class="container mt-3">
	<div class="row">
		<c:forEach var="dto" items="${list}" >
			<div class="col-md-4 d-flex">
				<div class="scrap-card w-100" data-content-id="${dto.apiId}">
					<div class="scrap-image-wrapper" style="position: relative;">
					<c:choose>
					<c:when test="${not empty dto.scrapImg}">
						<img src="${dto.scrapImg}" alt="즐겨찾기 이미지" class="scrap-image"
						onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';" />
					</c:when>
					<c:otherwise>
						<div class="alt-text" style="display: flex;">즐겨찾기 이미지</div>
					</c:otherwise>
					</c:choose>
					
					<button type="button" class="star-btn" data-apiId="${dto.apiId}" title="즐겨찾기 삭제"
					style="position: absolute; top: 8px; right: 8px; background: none; border: none; cursor: pointer; color: gold; font-size: 24px;">
					  <i class="bi bi-star-fill"></i>
					</button>
					</div>
					<div class="scrap-text">
						<div class="scrap-name">${dto.scrapName}</div>
						<div class="scrap-addr">${dto.scrapAddr}</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>

<div class="page-navigation text-center">${dataCount == 0 ? "더 이상 데이터가 없습니다." : paging}
</div>
