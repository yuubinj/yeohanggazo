<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Travel</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp" />

<style type="text/css">
/* 필요한 추가 스타일 있으면 작성 */
</style>
</head>



<!--  contextPath를 dataset으로 추가 -->
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />
</header>

<main>
    <div class="container">
        <div class="body-container row justify-content-center">
            <div class="col-md-10 my-3 p-3">

                <div class="body-title">
                    <h3>
                        <i class="bi bi-app"></i> 축제
                    </h3>
                </div>

                <div class="body-main">
                    <!--  검색 영역 -->
                    <div class="row board-list-header">
                        <div class="col-md-4 text-start">
                            <div class="input-group">
                                <input type="text" id="inputKeyword"
                                    class="form-control rounded me-1" placeholder="검색어(예: 서울)">
                                <button type="button" class="btn btn-light rounded"
                                    onclick="searchList();">
                                    <i class="bi bi-search"></i>
                                </button>
                                <input type="hidden" id="searchKeyword" value="">
                            </div>
                        </div>
                        <div class="col-md-8 text-end"></div>
                    </div>

                    <!--  지역 선택 버튼 그룹 -->
                    <div class="region-button-group my-3">
                        <!-- onclick 제거하고 dataset만 유지 -->
                        <button class="btn btn-outline-primary" data-area-code="" data-area-name="전국">전국</button>
                        <button class="btn btn-outline-primary" data-area-code="1" data-area-name="서울">서울</button>
                        <button class="btn btn-outline-primary" data-area-code="2" data-area-name="인천">인천</button>
                        <!-- 필요 지역 추가 -->
                    </div>

                    <!--  탭 버튼 -->
                    <div class="tab-buttons my-3">
                        <button id="festival-filter-ongoing" class="btn btn-primary" data-type="ongoing">개최중</button>
                        <button class="btn btn-outline-primary" data-type="upcoming">개최예정</button>
                        <button class="btn btn-outline-primary" data-type="finished">종료</button>
                    </div>

                    <!--  축제 카드 리스트 -->
                    <div class="row g-3" id="festival-card-list"></div>
                    <div id="festival-empty-guide" class="text-center text-muted mt-3" style="display:none;">
                        축제 정보가 없습니다.
                    </div>

                    <!--  더보기 버튼 -->
                    <div class="list-footer mt-2 text-center">
                        <button id="festival-more-btn" class="btn btn-light" style="display:none;">
                            더보기 <i class="bi bi-chevron-down"></i>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</main>

<!--  상세보기 모달 -->
<div class="modal fade" id="festivalDetailModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 id="modal-festival-title" class="modal-title">축제 상세정보</h5>
        <button class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div id="modal-festival-image" class="mb-3"></div>
        <p id="modal-festival-period">기간: <span></span></p>
        <p id="modal-festival-addr">주소: <span></span></p>
        <p id="modal-festival-tel">전화: <span></span></p>
        <p id="modal-festival-homepage">홈페이지: <span></span></p>
        <div id="modal-festival-overview" class="mt-3"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

<!--  festival.min.js 로드 -->
<script src="${pageContext.request.contextPath}/dist/js/festival.min.js"></script>

</body>
</html>
