<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Travel</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/specialty.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/tour.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/weather.css" type="text/css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">


<style type="text/css">
.region-button-group {
  display: flex;
  gap: 8px;
  margin: 16px 0;
  flex-wrap: wrap;
}

.region-button-group button {
  background-color: transparent;
  border: 1px solid #ccc;
  border-radius: 18px;
  padding: 6px 16px;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.region-button-group button:hover {
  background-color: #f0f0f0;
}

.region-button-group button.active {
  background-color: #4a72f0;
  color: white;
  border-color: #4a72f0;
}

button.selected {
  background-color: #4f75ff;
  color: white;
  border: none;
}

/* 로딩 오버레이 */
.overlay {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(255, 255, 255, 0.8);
	display: none;
	justify-content: center;
	align-items: center;
	z-index: 1000;
}

.spinner {
	border: 8px solid #f3f3f3;
	border-top: 8px solid #3498db;
	border-radius: 50%;
	width: 60px;
	height: 60px;
	animation: spin 2s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}


/* 검색 결과 카드 */
.search-result-card {
	border: 1px solid #e0e0e0;
	border-radius: 8px;
	padding: 10px 15px;
	margin-bottom: 8px;
	background: #fff;
	transition: 0.2s ease;
	cursor: pointer;
}

.search-result-card:hover {
	background: #f8f9fa;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.search-result-card.selected {
	background: #e9f1ff;
	border-color: #4a72f0;
}

.search-result-name {
	font-weight: 600;
	font-size: 16px;
	color: #333;
	margin-bottom: 3px;
}

.search-result-addr {
	font-size: 13px;
	color: #666;
}


/* 스크랩 */
.tour-item-content {
	position: relative;
}

.scrap-star {
	position: absolute;
	top: 5px;
	right: 8px;
	z-index: 10;
	background-color: rgba(255, 255, 255, 0.8);
	width: 30px;
	height: 30px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
}

.scrap-star i {
	font-size: 18px;
	color: #ffc107;
}

</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
			
				<h3>어디로 떠나볼까요?</h3>
				<h5>지역을 선택하세요</h5>
				
				<p style="font-size: 14px; color: #666;">📍 지역 선택</p>
				
				
				<div class="region-button-group"> 
					  <button onclick="selectRegion(this)" data-area-name="" data-area-code="" class="active">전국</button>
					  <button onclick="selectRegion(this)" data-area-name="서울" data-area-code="1">서울</button>
					  <button onclick="selectRegion(this)" data-area-name="인천광역시" data-area-code="2">인천</button>
					  <button onclick="selectRegion(this)" data-area-name="대전광역시" data-area-code="3">대전</button>
					  <button onclick="selectRegion(this)" data-area-name="대구광역시" data-area-code="4">대구</button>
					  <button onclick="selectRegion(this)" data-area-name="광주광역시" data-area-code="5">광주</button>
					  <button onclick="selectRegion(this)" data-area-name="부산광역시" data-area-code="6">부산</button>
					  <button onclick="selectRegion(this)" data-area-name="울산광역시" data-area-code="7">울산</button>
					  <button onclick="selectRegion(this)" data-area-name="세종" data-area-code="8">세종</button>
					  <button onclick="selectRegion(this)" data-area-name="경기도" data-area-code="31">경기</button>
					  <button onclick="selectRegion(this)" data-area-name="강원특별자치도" data-area-code="32">강원</button>
					  <button onclick="selectRegion(this)" data-area-name="충청북도" data-area-code="33">충북</button>
					  <button onclick="selectRegion(this)" data-area-name="충청남도" data-area-code="34">충남</button>
					  <button onclick="selectRegion(this)" data-area-name="경상북도" data-area-code="35">경북</button>
					  <button onclick="selectRegion(this)" data-area-name="경상남도" data-area-code="36">경남</button>
					  <button onclick="selectRegion(this)" data-area-name="전북특별자치도" data-area-code="37">전북</button>
					  <button onclick="selectRegion(this)" data-area-name="전라남도" data-area-code="38">전남</button>
					  <button onclick="selectRegion(this)" data-area-name="제주" data-area-code="39">제주</button>
				</div>
				
				
				<p style="margin-top: 10px; color: #444;">
				  선택한 지역의 날씨, 맛집, 축제 등 다양한 정보를 확인해보세요 😊
				</p>
				
				<br>
				
				<div class="body-main">

				<ul class="nav nav-tabs" id="myTab" role="tablist">
					<li class="nav-item">
						<button class="nav-link active" id="weather-tab" data-bs-toggle="tab" data-bs-target="#weather-panel" type="button">날씨</button>
					</li>
					<li class="nav-item">
						<button class="nav-link" id="restaurant-tab" data-bs-toggle="tab" data-bs-target="#restaurant-panel" type="button">음식점</button>
					</li>
					<li class="nav-item">
						<button class="nav-link" id="transport-tab" data-bs-toggle="tab" data-bs-target="#transport-panel" type="button">교통</button>
					</li>
					<li class="nav-item">
						<button class="nav-link" id="specialty-tab" data-bs-toggle="tab" data-bs-target="#specialty-panel" type="button">특산물</button>
					</li>
					<li class="nav-item">
						<button class="nav-link" id="tourist-tab" data-bs-toggle="tab" data-bs-target="#tourist-panel" type="button">관광지</button>
					</li>
					<li class="nav-item">
						<button class="nav-link" id="festival-tab" data-bs-toggle="tab" data-bs-target="#festival-panel" type="button">축제</button>
					</li>
				</ul>
				
				    <div class="tab-content pt-3" id="myTabContent">
				    	<!-- 날씨 -->
						<div class="tab-pane fade show active" id="weather-panel">
							<div class="weather-box p-2">
								<!-- JS로 날씨 카드 렌더링 -->
							</div>
						</div>

						<!-- 음식점 -->
						<div class="tab-pane fade" id="restaurant-panel">
							<div class="d-flex gap-2 mb-4">
								<input type="text" id="restaurantSearchKeyword"
									class="form-control" placeholder="음식점 이름을 입력하세요.">
								<button class="btn btn-primary text-nowrap"
									id="restaurantSearchBtn">검색</button>
							</div>
							<div class="text-center my-5 d-none" id="restaurant-spinner">
								<div class="spinner-border" role="status">
									<span class="visually-hidden">Loading...</span>
								</div>
							</div>
							<div class="row g-3" id="restaurant-grid"></div>
							<div class="d-flex justify-content-center mt-4">
							    <button id="restaurant-more-btn" class="btn btn-load-more" style="display: none;">
							        더보기 <i class="bi bi-chevron-down ms-2"></i>
							    </button>
							</div>
						</div>

						<!-- 교통 -->
						<div class="tab-pane fade" id="transport-panel">
							<p>내 위치와 목적지를 검색하여 길찾기를 이용할 수 있습니다.</p>
							<div class="input-group mb-2">
								<input type="text" id="destinationKeyword" class="form-control"
									placeholder="목적지 검색 (예: 서울역)">
								<button class="btn btn-primary" onclick="searchDestination()">검색</button>
							</div>
							<div id="traffic-map"
								style="width: 100%; height: 400px; border: 1px solid #ddd;"></div>
							<div id="search-results" class="search-results mt-3"></div>
							<button id="go-btn" class="btn btn-success mt-2" disabled>길찾기</button>
						</div>

						<!-- 특산물 -->
						<div class="tab-pane fade" id="specialty-panel">
							<div class="d-flex gap-2 mb-4">
								<input type="text" id="specialtySearchKeyword"
									class="form-control" placeholder="특산물 이름(예: 사과)을 입력하세요.">
								<button class="btn btn-primary text-nowrap"
									id="specialtySearchBtn">검색</button>
							</div>
							<div class="text-center my-5 d-none" id="specialty-spinner">
								<div class="spinner-border" role="status">
									<span class="visually-hidden">Loading...</span>
								</div>
							</div>
							<div class="row g-3" id="specialty-grid"></div>
							<div class="d-flex justify-content-center mt-4">
							    <button id="specialty-more-btn" class="btn btn-load-more" style="display: none;">
							        더보기 <i class="bi bi-chevron-down ms-2"></i>
							    </button>
							</div>
						</div>
				            
				        <!-- 관광지 -->
				        <div class="tab-pane fade" id="tourist-panel" role="tabpanel" aria-labelledby="tourist-tab">
				            
				            <div class="row board-list-header">
								<div class="col-md-4 text-start">
									<div class="input-group">
										<input type="text" id="inputKeyword" class="form-control rounded me-2" placeholder="검색어(예: 한옥)">
										<button type="button" class="btn btn-light rounded" onclick="searchList();"><i class="bi bi-search"></i></button>
										<input type="hidden" id="searchKeyword" value="">
									</div>							
								</div>
							</div>				
						
							<div class="row g-3 list-content-tour" data-pageNo="0" data-totalPage="0"></div>
							
							<div class="list-footer mt-2 text-end">
								<span class="more-btn-tour btn btn-light">&nbsp;더보기&nbsp;<i class="bi bi-chevron-down"></i>&nbsp;</span>
							</div>
				            <input type="hidden" id="selectedArea" value="">
				        </div>
				        
				        
				        <!-- 축제 -->
						<div class="tab-pane fade" id="festival-panel">
							<div class="festival-filter-buttons mt-3 mb-3">
								<button id="festival-filter-ongoing"
									class="btn btn-primary btn-sm active me-2" data-type="ongoing">개최중</button>
								<button id="festival-filter-upcoming"
									class="btn btn-outline-primary btn-sm me-2"
									data-type="upcoming">개최예정</button>
								<button id="festival-filter-finished"
									class="btn btn-outline-primary btn-sm" data-type="finished">종료</button>
							</div>
							<div id="festival-empty-guide" class="text-center mb-3"
								style="display: none;">
								<p>현재 공공데이터 API에는 예정된 축제 정보가 부족할 수 있습니다.</p>
								<p>
									더 많은 축제 정보는 한국관광공사 <strong>‘구석구석’</strong> 사이트에서 확인하세요.
								</p>
								<a href="https://korean.visitkorea.or.kr/fes/festViewList.do"
									target="_blank" class="btn btn-primary">구석구석 바로가기</a>
							</div>
							<div class="row g-3 festival-list-container"
								id="festival-card-list">
								<p>축제 정보를 불러오는 중입니다...</p>
							</div>
							<div class="text-center mt-3">
								<button id="festival-more-btn" class="btn btn-light"
									style="display: none;">
									&nbsp;더보기&nbsp;<i class="bi bi-chevron-down"></i>&nbsp;
								</button>
							</div>
						</div>
						
				    </div>

				</div>
				
			</div>
		</div>
	</div>
</main>


<!-- 관광지 상세 모달 -->
<div class="modal fade" id="tourDialogModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="tourDialogModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="tourDialogModalLabel">관광지 상세정보</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body tour-detail">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>

<!-- 음식점·특산물 상세 모달 -->
<div class="modal fade" id="itemDetailModal" tabindex="-1"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="itemDetailModalLabel">상세 정보</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>
			<div class="modal-body">
				<div id="modal-item-title" class="fw-bold fs-4 mb-2"></div>
				<div id="modal-item-image" class="text-center mb-3"></div>
				<div id="modal-item-addr" class="mb-2">
					<i class="bi bi-geo-alt"></i> 주소: <span></span>
				</div>
				<div id="modal-item-tel" class="mb-2">
					<i class="bi bi-telephone"></i> 전화: <span></span>
				</div>
				<hr>
				<div id="modal-item-overview"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>

<!-- 축제 상세 모달 -->
<div class="modal fade" id="festivalDetailModal" tabindex="-1"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="festivalDetailModalLabel">축제 상세 정보</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div id="modal-festival-title" class="fw-bold fs-4 mb-2"></div>
				<div id="modal-festival-image" class="text-center mb-3"></div>
				<div id="modal-festival-period" class="mb-2">
					<i class="bi bi-calendar"></i> 기간: <span></span>
				</div>
				<div id="modal-festival-addr" class="mb-2">
					<i class="bi bi-geo-alt"></i> 주소: <span></span>
				</div>
				<div id="modal-festival-tel" class="mb-2">
					<i class="bi bi-telephone"></i> 전화: <span></span>
				</div>
				<div id="modal-festival-homepage" class="mb-2">
					<i class="bi bi-globe"></i> 홈페이지: <span></span>
				</div>
				<hr>
				<div id="modal-festival-overview"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>

<!-- 로딩중 오버레이 -->
<div class="overlay">
	<div class="spinner"></div>
</div>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

<!-- 카카오맵 SDK -->
<script
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=db96a1dbcdafea0a6919bc084465ec07&libraries=services"></script>

<script>
	const CONTEXT_PATH = "${pageContext.request.contextPath}";
	
	
</script>

<!-- JS 로직 -->
<script src="${pageContext.request.contextPath}/dist/js/specialty.js"></script>
<script src="${pageContext.request.contextPath}/dist/js/tour.min.js"></script>
<script src="${pageContext.request.contextPath}/dist/js/weather.js"></script>
<script
	src="${pageContext.request.contextPath}/dist/js/festival.min.js"></script>
<script src="${pageContext.request.contextPath}/dist/js/trafficMap.js"></script>

</body>

</html>