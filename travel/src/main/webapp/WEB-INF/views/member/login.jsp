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
.btn-primary {
	background-color: #5f0080;
	border: none;
	height: 50px;
	font-size: 16px;
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
			<div class="col-md-5 my-3 p-3">

				<div class="border mt-5 p-4">
                    <form name="loginForm" action="" method="post" class="row g-3">
                        <h3 class="text-center"><i class="bi bi-lock"></i> 회원 로그인</h3>
                        <div class="col-12">
                            <label class="mb-1">아이디</label>
                            <input type="text" name="userId" class="form-control" placeholder="아이디">
                        </div>
                        <div class="col-12">
                            <label class="mb-1">패스워드</label>
                            <input type="password" name="userPwd" class="form-control" autocomplete="off" 
                            	placeholder="패스워드">
                        </div>
                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="rememberMe">
                                <label class="form-check-label" for="rememberMe"> 아이디 저장</label>
                            </div>
                        </div>
                        <div class="col-12">
                            <button type="button" class="btn btn-primary float-end" onclick="sendLogin();">&nbsp;Login&nbsp;<i class="bi bi-check2"></i></button>
                        </div>
                    </form>
                    <hr class="mt-4">
                    <div class="col-12">
                        <p class="text-center mb-0">
                        	<a href="#" class="text-decoration-none me-2">아이디 찾기</a>
                        	<a href="#" class="text-decoration-none me-2">패스워드 찾기</a>
                        	<a href="${pageContext.request.contextPath}/member/account" class="text-decoration-none">회원가입</a>
                        </p>
                    </div>
                </div>

                <div class="d-grid">
                    <p class="form-control-plaintext text-center text-primary">${message}</p>
				</div>

			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
function sendLogin() {
    const f = document.loginForm;
	
    if( ! f.userId.value.trim() ) {
        f.userId.focus();
        return;
    }

    if( ! f.userPwd.value.trim() ) {
        f.userPwd.focus();
        return;
    }
    console.log('${pageContext.request.contextPath}');
    f.action = '${pageContext.request.contextPath}/member/login';
    console.log(f.action);
    f.submit();
}
</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>

</body>
</html>