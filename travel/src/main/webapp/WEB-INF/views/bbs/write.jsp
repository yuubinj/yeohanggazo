<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 작성</title>
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
				<h3><i class="bi bi-book-half"></i> 게시글 작성</h3>

				<form name="postForm" method="post" enctype="multipart/form-data">
					<table class="table mt-4 write-form">
						<tr>
						    <td class="bg-light col-sm-2" scope="row">제 목</td>
						    <td>
						        <select name="categoryNum" class="form-select w-auto d-inline">
									<c:choose>
										<c:when test="${sessionScope.member.userLevel == 99}">
											<option value=""> 선택 </option>
											<option value="1" ${categoryNum == 1 ? "selected" : ""}>공지</option>
											<option value="2" ${categoryNum == 2 ? "selected" : ""}>현지인 추천</option>
											<option value="3" ${categoryNum == 3 ? "selected" : ""}>리뷰</option>
											<option value="4" ${categoryNum == 4 ? "selected" : ""}>기타</option>
										</c:when>
										<c:otherwise>
											<option value=""> 선택 </option>
											<option value="2" ${categoryNum == 2 ? "selected" : ""}>현지인 추천</option>
											<option value="3" ${categoryNum == 3 ? "selected" : ""}>리뷰</option>
											<option value="4" ${categoryNum == 4 ? "selected" : ""}>기타</option>
										</c:otherwise>
									</c:choose>
								</select>
						        <input type="text" name="subject" maxlength="100" class="form-control d-inline w-75" value="${dto.subject}">
						    </td>
						</tr>
						<tr>
							<td class="bg-light" scope="row">작성자명</td>
							<td><p class="form-control-plaintext">${sessionScope.member.userName}</p></td>
						</tr>
						<tr>
							<td class="bg-light" scope="row">내 용</td>
							<td>
								<textarea name="content" id="ir1" class="form-control" style="width: 99%; height: 300px;">${dto.content}</textarea>
							</td>
						</tr>
						<tr>
							<td class="bg-light" scope="row">첨부파일</td>
							<td><input type="file" name="selectFile" class="form-control"></td>
						</tr>
						<c:if test="${mode=='update'}">
							<tr>
								<td class="bg-light" scope="row">첨부된파일</td>
								<td>
									<c:if test="${not empty dto.saveFilename}">
										<a href="javascript:deleteFile('${dto.num}');"><i class="bi bi-trash"></i></a>
										${dto.originalFilename}
									</c:if>
								</td>
							</tr>
						</c:if>
					</table>

					<div class="text-center mt-3">
						<button type="button" class="btn btn-dark" onclick="submitContents(this.form);">${mode=='update'?'수정완료':'등록완료'} <i class="bi bi-check2"></i></button>
						<button type="reset" class="btn btn-light">다시입력</button>
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/bbs/list?category=${category}';">${mode=='update'?'수정취소':'등록취소'} <i class="bi bi-x"></i></button>
						<c:if test="${mode=='update'}">
							<input type="hidden" name="num" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
							<input type="hidden" name="saveFilename" value="${dto.saveFilename}">
							<input type="hidden" name="originalFilename" value="${dto.originalFilename}">
						</c:if>
					</div>
				</form>

			</div>
		</div>
	</div>
</main>

<script type="text/javascript">
function check() {
	const f = document.postForm;

	// 스마트에디터 내용을 textarea(content)에 반영 - 'ir1'이 정확한 ID야
	oEditors.getById["ir1"].exec("UPDATE_CONTENTS_FIELD", []);

	// 이후 검증 시작
	if (f.subject.value.trim() === "") {
		alert("제목을 입력하세요.");
		f.subject.focus();
		return false;
	}

	// 텍스트 내용만 파싱해서 검사
	let contentText = f.content.value.replace(/<[^>]*>/g, '').trim();
	if (contentText === "") {
		alert("내용을 입력하세요.");
		oEditors.getById["ir1"].exec("FOCUS");
		return false;
	}

	if (!f.categoryNum.value) {
		alert("글 분류를 선택해주세요.");
		f.categoryNum.focus();
		return false;
	}

	f.action = '${pageContext.request.contextPath}/bbs/${mode}';
	return true;
}

<c:if test="${mode=='update'}">
function deleteFile(num) {
	if (!confirm("파일을 삭제하시겠습니까?")) return;
	location.href = '${pageContext.request.contextPath}/bbs/deleteFile?num=' + num + '&category=${category}&page=${page}';
}
</c:if>
</script>


<script type="text/javascript" src="${pageContext.request.contextPath}/dist/vendor/se2/js/service/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript">
var oEditors = [];
nhn.husky.EZCreator.createInIFrame({
	oAppRef: oEditors,
	elPlaceHolder: 'ir1',
	sSkinURI: '${pageContext.request.contextPath}/dist/vendor/se2/SmartEditor2Skin.html',
	fCreator: 'createSEditor2',
	fOnAppLoad: function(){
		// 로딩 완료 후
		oEditors.getById['ir1'].setDefaultFont('돋움', 12);
	},
});

function submitContents(elClickedObj) {
	 oEditors.getById['ir1'].exec('UPDATE_CONTENTS_FIELD', []);
	 try {
		if(! check()) {
			return;
		}
		
		elClickedObj.submit();
		
	} catch(e) {
	}
}
</script>
<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>
</body>
</html>
