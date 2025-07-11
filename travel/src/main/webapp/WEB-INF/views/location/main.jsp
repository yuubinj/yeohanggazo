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



</style>


</head>
<body>

	<header>
		<jsp:include page="/WEB-INF/views/layout/header.jsp" />
	</header>

	<main>
		<div class="container">
			<div class="body-container row justify-content-center">
				<div class="col-md-10 my-3 p-3">

					<h3>어디로 떠나볼까요?</h3>
					<h5>지역을 선택하세요</h5>
					
					
					
					<p style="font-size: 14px; color: #666;">📍 지역 선택</p>
					
					<!--  지역 버튼모양 
					<div class="region-button-group">
					  <button onclick="selectRegion(this, '전국')" class="active">전국</button>
					  <button onclick="selectRegion(this, '서울')">서울</button>
					  <button onclick="selectRegion(this, '인천')">인천</button>
					  <button onclick="selectRegion(this, '대전')">대전</button>
					  <button onclick="selectRegion(this, '대구')">대구</button>
					  <button onclick="selectRegion(this, '광주')">광주</button>
					  <button onclick="selectRegion(this, '부산')">부산</button>
					  <button onclick="selectRegion(this, '울산')">울산</button>
					  <button onclick="selectRegion(this, '경기')">경기</button>
					  <button onclick="selectRegion(this, '강원')">강원</button>
					  <button onclick="selectRegion(this, '충북')">충북</button>
					  <button onclick="selectRegion(this, '충남')">충남</button>
					  <button onclick="selectRegion(this, '경북')">경북</button>
					  <button onclick="selectRegion(this, '경남')">경남</button>
					  <button onclick="selectRegion(this, '전북')">전북</button>
					  <button onclick="selectRegion(this, '전남')">전남</button>
					  <button onclick="selectRegion(this, '제주')">제주</button>
					  <button onclick="selectRegion(this, '세종')">세종</button>
					</div>
					 -->
					 
					<div class="row row-cols-auto g-3">
					  <div class="col text-center">
					    <img src="${pageContext.request.contextPath}/dist/images/seoul.jpeg" alt="서울" width="60">
					    <div>서울</div>
					  </div>					  
					 
					
					
					<p style="margin-top: 10px; color: #444;">
					  선택한 지역의 날씨, 맛집, 축제 등 다양한 정보를 확인해보세요 😊
					</p>
											
					
					<!--  지역 버튼 기존 예시 
					<div id="region-select" class="region-button-group">
						<button onclick="selectRegion('전국')">전국</button>
						<button onclick="selectRegion('서울')">서울</button>
						<button onclick="selectRegion('부산')">부산</button>
						<button onclick="selectRegion('대구')">대구</button>
						<button onclick="selectRegion('대전')">대전</button>
						<button onclick="selectRegion('광주')">광주</button>
					</div>
					-->
					<br>
					
					<div class="body-main">

						<ul class="nav nav-tabs" id="myTab" role="tablist">
							<li class="nav-item" role="presentation">
								<button class="nav-link active" id="weather-tab"
									data-bs-toggle="tab" data-bs-target="#weather-panel"
									type="button" role="tab" aria-controls="weather-panel"
									aria-selected="true">날씨</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="restaurant-tab"
									data-bs-toggle="tab" data-bs-target="#restaurant-panel"
									type="button" role="tab" aria-controls="restaurant-panel"
									aria-selected="false">음식점</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="transport-tab" data-bs-toggle="tab"
									data-bs-target="#transport-panel" type="button" role="tab"
									aria-controls="transport-panel" aria-selected="false">교통</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="specialty-tab" data-bs-toggle="tab"
									data-bs-target="#specialty-panel" type="button" role="tab"
									aria-controls="specialty-panel" aria-selected="false">특산물</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="tourist-tab" data-bs-toggle="tab"
									data-bs-target="#tourist-panel" type="button" role="tab"
									aria-controls="tourist-panel" aria-selected="false">관광지</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="festival-tab" data-bs-toggle="tab"
									data-bs-target="#festival-panel" type="button" role="tab"
									aria-controls="festival-panel" aria-selected="false">축제</button>
							</li>
						</ul>

						<div class="tab-content pt-3" id="myTabContent">
							<div class="tab-pane fade show active" id="weather-panel"
								role="tabpanel" aria-labelledby="weather-tab">
								<p>날씨 정보가 여기에 표시됩니다.</p>
							</div>
							<div class="tab-pane fade" id="restaurant-panel" role="tabpanel"
								aria-labelledby="restaurant-tab">
								<p>음식점 정보가 여기에 표시됩니다.</p>
							</div>
							<div class="tab-pane fade" id="transport-panel" role="tabpanel"
								aria-labelledby="transport-tab">
								<p>교통 정보가 여기에 표시됩니다.</p>
							</div>
							<div class="tab-pane fade" id="specialty-panel" role="tabpanel"
								aria-labelledby="specialty-tab">
								<p>특산물 정보가 여기에 표시됩니다.</p>
							</div>
							<div class="tab-pane fade" id="tourist-panel" role="tabpanel"
								aria-labelledby="tourist-tab">
								<p>관광지 정보가 여기에 표시됩니다.</p>
							</div>
							<div class="tab-pane fade" id="festival-panel" role="tabpanel"
								aria-labelledby="festival-tab">
								<p>축제 정보가 여기에 표시됩니다.</p>
							</div>
						</div>

					</div>

				</div>
			</div>
		</div>
	</main>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
	</footer>

	<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>