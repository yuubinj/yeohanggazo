<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

	<div class="container-fluid bg-light header-top">
		<div class="container">
			<div class="row">
				<div class="col">
					<div class="p-2">
						<i class="bi bi-telephone-inbound-fill"></i> +82-1234-1234
					</div>
				</div>
				<div class="col">
					<div class="d-flex justify-content-end">
						<c:choose>
							<c:when test="${empty sessionScope.member}">
								<div class="p-2">
									<a href="javascript:dialogLogin();" title="로그인"><i class="bi bi-lock"></i></a>
								</div>
								<div class="p-2">
									<a href="${pageContext.request.contextPath}/member/account" title="회원가입"><i class="bi bi-person-plus"></i></a>
								</div>	
							</c:when>
							<c:otherwise>
								<div class="p-2">
									<a href="#" title="알림"><i class="bi bi-bell"></i></a>
								</div>
								<div class="p-2">
									<a href="${pageContext.request.contextPath}/member/logout" title="로그아웃"><i class="bi bi-unlock"></i></a>
								</div>
								<c:if test="${sessionScope.member.userLevel>50}">
									<div class="p-2">
										<a href="${pageContext.request.contextPath}/admin" title="관리자"><i class="bi bi-gear"></i></a>
									</div>					
								</c:if>
							</c:otherwise>
						</c:choose>
					</div>
					
				</div>
			</div>
		</div>
	</div>
	
	<nav class="navbar navbar-expand-lg navbar-light">
		<div class="header container d-flex align-items-center justify-content-between">
			<a class="navbar-brand" href="${pageContext.request.contextPath}/"><i class="bi bi-app-indicator"></i></a>
				
			<div class="navmenu collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav mx-auto flex-nowrap"> <!-- mx-auto : 우측으로 정렬 -->
					<li class="nav-item">
						<a class="nav-link" aria-current="page" href="${pageContext.request.contextPath}/home/mein">홈</a>
					</li>
					
					<li class="nav-item">
						<a class="nav-link" href="#">소개</a>
					</li>
					
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
							강좌
						</a>
						<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
							<li><a class="dropdown-item" href="#">프로그래밍</a></li>
							<li><a class="dropdown-item" href="#">데이터베이스</a></li>
							<li><a class="dropdown-item" href="#">웹프로그래밍</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="#">강좌 질문과 답변</a></li>
						</ul>
					</li>
										
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
							서비스
						</a>
						<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">맛집정보</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">레시피</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">관광정보</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">날씨</a></li>
						</ul>
					</li>

					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
							커뮤니티
						</a>
						<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">자유게시판</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/inquiry/main">문의</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">FAQ</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="#">자료실</a></li>
						</ul>
					</li>
					
					<li class="nav-item">
						<a class="nav-link" href="#">쇼핑</a>
					</li>
	
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
							고객센터
						</a>
						<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">자주하는질문</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">공지사항</a></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">문의하기</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="${pageContext.request.contextPath}/">이벤트</a></li>
						</ul>
					</li>
					
				</ul>
			</div>
			
			<div class="header-right d-flex align-items-center">
				<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>
			
				<div class="header-avatar">
					<div class="p-2">
						<a href="#" title="알림"><i class="bi bi-bell"></i></a>
					</div>
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
                            <li><a href="${pageContext.request.contextPath}/member/myPage" class="dropdown-item">마이페이지</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a href="${pageContext.request.contextPath}/" class="dropdown-item">사진첩</a></li>
                            <li><a href="${pageContext.request.contextPath}/" class="dropdown-item">일정관리</a></li>
                            <li><a href="#" class="dropdown-item">쪽지함</a></li>
                            <li><a href="${pageContext.request.contextPath}/" class="dropdown-item">메일</a></li>
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
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
	                <div class="p-3">
	                    <form name="modalLoginForm" action="" method="post" class="row g-3">
	                    	<div class="mt-0">
	                    		 <p class="form-control-plaintext">계정으로 로그인 하세요</p>
	                    	</div>
	                        <div class="mt-0">
	                            <input type="text" name="userId" class="form-control" placeholder="아이디">
	                        </div>
	                        <div>
	                            <input type="password" name="userPwd" class="form-control" autocomplete="off" placeholder="패스워드">
	                        </div>
	                        <div>
	                            <div class="form-check">
	                                <input class="form-check-input" type="checkbox" id="rememberMeModal">
	                                <label class="form-check-label" for="rememberMeModal"> 아이디 저장</label>
	                            </div>
	                        </div>
	                        <div>
	                            <button type="button" class="btn btn-primary w-100" onclick="sendModalLogin();">Login</button>
	                        </div>
	                        <div>
	                    		 <p class="form-control-plaintext text-center">
	                    		 	<a href="#" class="text-decoration-none me-2">패스워드를 잊으셨나요 ?</a>
	                    		 </p>
	                    	</div>
	                    </form>
	                    <hr class="mt-3">
	                    <div>
	                        <p class="form-control-plaintext mb-0">
	                        	아직 회원이 아니세요 ?
	                        	<a href="${pageContext.request.contextPath}/member/account" class="text-decoration-none">회원가입</a>
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
		    if(!str) {
		        f.userId.focus();
		        return;
		    }
		
		    str = f.userPwd.value;
		    if(!str) {
		        f.userPwd.focus();
		        return;
		    }
		
		    f.action = '${pageContext.request.contextPath}/member/login';
		    f.submit();
		}
	</script>
