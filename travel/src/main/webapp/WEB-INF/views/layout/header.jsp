<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<style type="text/css">
.btn-primary {
	background-color: #91c4e8;
	color: #fff;
	border: none;
	height: 50px;
	font-size: 16px;
}

.btn-primary:hover {
	background-color: #56a5da;
}
</style>

<nav class="navbar navbar-expand-lg navbar-light" style="height: 80px">
	<div
		class="header container d-flex align-items-center justify-content-between">
		<a class="navbar-brand" href="${pageContext.request.contextPath}/" style="color: #fff; font-size: 30px; font-weight: bold"><i class="fa-solid fa-car-side"></i>&nbsp;모여모여</a>

		<div class="navmenu collapse navbar-collapse"
			id="navbarSupportedContent">
			<ul class="navbar-nav mx-auto flex-nowrap">
				<!-- mx-auto : 우측으로 정렬 -->
				<li class="nav-item"><a class="nav-link" aria-current="page"
					href="${pageContext.request.contextPath}/">홈</a></li>

				<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/theme/main">테마</a></li>

				<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/location/main">지역</a></li>

				<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/festival/festivalMain">축제</a></li>

				<li class="nav-item dropdown"><a
					class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
					role="button" data-bs-toggle="dropdown" aria-expanded="false">
						커뮤니티 </a>
					<ul class="dropdown-menu dropdown-content" aria-labelledby="navbarDropdown">
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/bbs/list">자유게시판</a></li>
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/myTrip/list">내 여행
								자랑하기</a></li>
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/inquiry/main">문의</a></li>
						<li><a class="dropdown-item"
							href="${pageContext.request.contextPath}/faq/main">FAQ</a></li>
					</ul></li>

				<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/roulette/main">어디 갈까?</a></li>
			</ul>
		</div>

		<div class="d-flex justify-content-end align-items-center"
			style="color: #fff; font-size: 16px;">
			<c:choose>
				<c:when test="${empty sessionScope.member}">
					<div class="p-2">
						<a href="javascript:dialogLogin();" title="로그인"
							style="color: #fff"><i class="bi bi-person"
							style="color: #fff"></i> <span>Log In</span> </a>
					</div>
					<span> | </span>
					<div class="p-2">
						<a href="${pageContext.request.contextPath}/member/account"
							title="회원가입"> <span
							style="margin-bottom: 0; color: #fff;">Sign
								up</span>
						</a>
					</div>
				</c:when>
				<c:otherwise>
					<div class="p-2">
						<a href="${pageContext.request.contextPath}/member/logout"
							title="로그아웃" style="color: white"><i class="bi bi-unlock"></i></a>
					</div>
					<c:if test="${sessionScope.member.userLevel>50}">
						<div class="p-2">
							<a href="${pageContext.request.contextPath}/admin" title="관리자"><i
								class="bi bi-gear" style="color: white"></i></a>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="header-right d-flex align-items-center">
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="header-avatar">
					<c:if test="${not empty sessionScope.member}">
						<c:choose>
							<c:when test="${not empty sessionScope.member.avatar}">
								<img src="${pageContext.request.contextPath}/uploads/member/${sessionScope.member.avatar}" class="avatar-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
							</c:when>
							<c:otherwise>
								<img src="${pageContext.request.contextPath}/dist/images/avatar.png" class="avatar-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
							</c:otherwise>
						</c:choose>
						<ul class="dropdown-menu">
							
							<li><a href="${pageContext.request.contextPath}/myPage/myPage" class="dropdown-item">마이페이지</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a href="${pageContext.request.contextPath}/myPage/schedule/main" class="dropdown-item">일정관리</a></li>
							<li><a href="${pageContext.request.contextPath}/myPage/scrap/main" class="dropdown-item">즐겨찾기</a></li>
								   	
							<li><hr class="dropdown-divider"></li>
							<li><a href="${pageContext.request.contextPath}/member/pwd" class="dropdown-item">정보수정</a></li>
							<c:if test="${sessionScope.member.userLevel < 51}">
								<li><a href="${pageContext.request.contextPath}/member/pwd?mode=delete" class="dropdown-item">회원탈퇴</a></li>
							</c:if>
						</ul>
					</c:if>
				</div>
			
		</div>
	</div>

</nav>

<div class="modal fade" id="loginModal" tabindex="-1"
	data-bs-backdrop="static" data-bs-keyboard="false"
	aria-labelledby="loginModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="loginViewerModalLabel">Login</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div class="p-3">
					<form name="modalLoginForm" action="" method="post" class="row g-3">
						<div class="mt-0">
							<p class="form-control-plaintext">계정으로 로그인 하세요</p>
						</div>
						<div class="mt-0">
							<input type="text" name="userId" class="form-control"
								placeholder="아이디">
						</div>
						<div>
							<input type="password" name="userPwd" class="form-control"
								autocomplete="off" placeholder="패스워드">
						</div>
						<div>
							<div class="form-check">
								<input class="form-check-input" type="checkbox"
									id="rememberMeModal"> <label class="form-check-label"
									for="rememberMeModal"> 아이디 저장</label>
							</div>
						</div>
						<div>
							<button type="button" class="btn btn-primary w-100"
								onclick="sendModalLogin();">Login</button>
						</div>
					</form>
					<hr class="mt-3">
					<div>
						<p class="form-control-plaintext mb-0">
							아직 회원이 아니세요 ? <a
								href="${pageContext.request.contextPath}/member/account"
								class="text-decoration-none">회원가입</a>
						</p>
					</div>
				</div>

			</div>
		</div>
	</div>
</div>

<!-- Login Modal -->
<script type="text/javascript">
	console.log('${sessionScope.member.avatar}');
	console.log('${sessionScope.member}');
	function dialogLogin() {
		$('form[name=modalLoginForm] input[name=userId]').val('');
		$('form[name=modalLoginForm] input[name=userPwd]').val('');

		$('#loginModal').modal('show');

		$('form[name=modalLoginForm] input[name=userId]').focus();
	}

	function sendModalLogin() {
		const f = document.modalLoginForm;
		let str;

		str = f.userId.value;
		if (!str) {
			f.userId.focus();
			return;
		}

		str = f.userPwd.value;
		if (!str) {
			f.userPwd.focus();
			return;
		}

		f.action = '${pageContext.request.contextPath}/member/login';
		f.submit();
	}
</script>
