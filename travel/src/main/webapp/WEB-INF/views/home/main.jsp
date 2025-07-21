<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp"/>
<style type="text/css">

.item-box {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 70%;
    cursor: pointer;
    border-radius: 5px;
    
    box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);
    transition: all 0.3s cubic-bezier(.25,.8,.25,1);
}

.item-box:hover {
    box-shadow: 0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
}

.nav-link{
    color: silver;
}

.nav {
--bs-nav-link-color: silver;
}

#rouletteImg {
    margin: auto;
    display: block;
}
</style>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<!-- 최근 사진 5개 출력 -->
		<div class="container-fluid mt-3">

			<div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="carousel">
				<div class="carousel-indicators">
					<c:forEach var="dto" items="${listAd}" varStatus="status">
						<button type="button" data-bs-target="#carouselExampleIndicators"
							data-bs-slide-to="${status.index}" class="${status.index==0?'active':''}" aria-current="true"
							aria-label="${dto.subject}"></button>
					</c:forEach>
				</div>
				<div class="carousel-inner">
					<c:forEach var="dto" items="${listAd}" varStatus="status">
						<div class="carousel-item ${status.index==0?'active':''}">
							<a href="${pageContext.request.contextPath}/photo/article?page=1&num=${dto.num}">
								<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}" class="d-block w-100" style="height: 450px;">
							</a>
						</div>
					</c:forEach>
					<c:if test="${listPhoto.size()==0}">
						<img src="${pageContext.request.contextPath}/dist/images/bg.png" class="d-block w-100" style="max-height: 450px;">
					</c:if>
				</div>
				<button class="carousel-control-prev" type="button"
					data-bs-target="#carouselExampleIndicators" data-bs-slide="prev">
					<span class="carousel-control-prev-icon" aria-hidden="true"></span>
					<span class="visually-hidden">Previous</span>
				</button>
				<button class="carousel-control-next" type="button"
					data-bs-target="#carouselExampleIndicators" data-bs-slide="next">
					<span class="carousel-control-next-icon" aria-hidden="true"></span>
					<span class="visually-hidden">Next</span>
				</button>
			</div>

		</div>
		
		<div style="margin: 30px;">
			<img id="rouletteImg" src="${pageContext.request.contextPath}/dist/images/whereToGo.png" 
				onclick="location.href='${pageContext.request.contextPath}/roulette/main'"
				style="width: 400px;">
		</div>
		
		<!-- 커뮤니티 -->
		<div style="margin: 50px">
			<ul class="nav nav-tabs" id="myTab" role="tablist" style="--bs-nav-tabs-border-width: none; font-weight: bold; font-size: 16px;">
				<li class="nav-inquiry-item" role="presentation">
					<button class="nav-link ${selectedCateCommu=='bbs' || selectedCateCommu == null ?'active':''}" id="tab-0" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="bbs">자유게시판</button>
				</li>
				<li class="nav-inquiry-item" role="presentation">
					<button class="nav-link ${selectedCateCommu=='myTrip' ?'active':''}" id="tab-1" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="myTrip">내 여행 자랑하기</button>
				</li>
				<li class="nav-inquiry-item" role="presentation">
					<button class="nav-link ${selectedCateCommu=='inquiry' ?'active':''}" id="tab-2" data-bs-toggle="tab" data-bs-target="#nav-content" type="button" role="tab" aria-selected="true" data-tab="inquiry">문의하기</button>
				</li>
			</ul>

			<div class="tab-content pt-2" id="nav-tabContent">
				
				<div class="tab-pane fade show active" id="nav-content" role="tabpanel" aria-labelledby="nav-tab-content"></div>
			
			</div>

		</div>
	</div>
</main>

<script type="text/javascript">
function login() {
	location.href = '${pageContext.request.contextPath}/member/login';
}

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
					login();
					return false;
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

$(function(){
    listPage(1);
	
	// 탭이 변경될 때 실행
    $('button[role="tab"]').on('click', function(e){
    	
    	listPage(1);
    });
});

function listPage(page) {
	const $tab = $('button[role="tab"].active');
	let categoryName = $tab.attr('data-tab');

	let url = '${pageContext.request.contextPath}/home/communityList';
	let query = 'categoryName=' + categoryName;

	let selector = '#nav-content';
	
	const fn = function(data) {
		$(selector).html(data);
	};
	
	sendAjaxRequest(url, 'get', query, 'text', fn);
}


</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>

</body>
</html>