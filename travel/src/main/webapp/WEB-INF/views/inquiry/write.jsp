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
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-whatsapp"></i> 문의하기 </h3>
				</div>
				
				<div class="body-main">

					<form name="questionForm" method="post">
						<table class="table mt-5 write-form">
							<tr>
								<td class="bg-light col-sm-2" scope="row">제 목</td>
								<td>
									<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
								</td>
							</tr>
		        
							<tr>
								<td class="bg-light col-sm-2" scope="row">작성자명</td>
		 						<td>
									<p class="form-control-plaintext">${sessionScope.member.userName}</p>
								</td>
							</tr>
		
							<tr>
								<td class="bg-light col-sm-2" scope="row">공개여부</td>
								<td class="py-3"> 
									<input type="radio" name="secret" id="secret1" class="form-check-input" 
										value="0" ${empty dto || dto.secret==0?"checked='checked'":"" }>
									<label class="form-check-label" for="secret1">공개</label>
									<input type="radio" name="secret" id="secret2" class="form-check-input"
										value="1" ${dto.secret==1?"checked='checked'":"" }>
									<label class="form-check-label" for="secret2">비공개</label>
								</td>
							</tr>
		
							<tr>
								<td class="bg-light col-sm-2" scope="row">카테고리</td>
								<td class="py-3"> 
									<input type="radio" name="categoryNum" id="categoryNum1" class="form-check-input" 
										value="1" ${dto.categoryNum==1?"checked='checked'":"" }>
									<label class="form-check-label" for="categoryNum1">회원정보</label>
									<input type="radio" name="categoryNum" id="categoryNum2" class="form-check-input" 
										value="2" ${dto.categoryNum==2?"checked='checked'":"" }>
									<label class="form-check-label" for="categoryNum2">지역정보</label>
									<input type="radio" name="categoryNum" id="categoryNum3" class="form-check-input" 
										value="3" ${dto.categoryNum==3?"checked='checked'":"" }>
									<label class="form-check-label" for="categoryNum3">이용안내</label>
									<input type="radio" name="categoryNum" id="categoryNum4" class="form-check-input"
										value="4" ${dto.categoryNum==4?"checked='checked'":"" }>
									<label class="form-check-label" for="categoryNum4">기타</label>
								</td>
							</tr>
						
							<tr>
								<td class="bg-light col-sm-2" scope="row">내 용</td>
								<td>
									<textarea name="question" class="form-control">${dto.question}</textarea>
								</td>
							</tr>
						</table>
						
						<table class="table table-borderless">
		 					<tr>
								<td class="text-center">
									<button type="button" class="btn btn-dark" onclick="sendOk();">${mode=='update'?'수정완료':'등록완료'}&nbsp;<i class="bi bi-check2"></i></button>
									<button type="reset" class="btn btn-light">다시입력</button>
									<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/inquiry/main';">${mode=='update'?'수정취소':'등록취소'}&nbsp;<i class="bi bi-x"></i></button>
									<c:if test="${mode=='update'}">
										<input type="hidden" name="num" value="${dto.num}">
										<input type="hidden" name="pageNo" value="${pageNo}">
									</c:if>
								</td>
							</tr>
						</table>
					</form>

				</div>
			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
function sendOk() {
	const f = document.questionForm;
	let str;
	
	str = f.subject.value.trim();
	if( ! str ) {
		alert('제목을 입력하세요. ');
		f.subject.focus();
		return;
	}

	str = f.question.value.trim();
	if( ! str ) {
		alert('내용을 입력하세요. ');
		f.question.focus();
		return;
	}

	f.action = '${pageContext.request.contextPath}/inquiry/${mode}';
	f.submit();
}
</script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>

</body>
</html>