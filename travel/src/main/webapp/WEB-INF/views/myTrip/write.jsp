<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/layout/headerResources.jsp" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/dist/css/board.css"
	type="text/css">
<style type="text/css">
.preview-session {
	display: flex;
	justify-content: flex-start;
}

.preview-session .image-upload-list {
	display: flex;
	justify-content: flex-start;
	gap: 5px;
}

.preview-session .image-upload-btn, .preview-session .image-viewer,
	.preview-session .image-upload-list .image-item, .preview-session .image-upload-list .image-uploaded
	{
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	overflow: hidden;
	user-select: none;
	width: 60px;
	height: 60px;
	border: 1px solid rgb(195, 211, 219);
	border-radius: 8px;
	cursor: pointer;
	transition: ease-in-out 0.3s;
}

.preview-session .image-upload-btn:hover, .preview-session .image-upload-list .image-item:hover,
	.preview-session .image-upload-list .image-uploaded:hover {
	transform: scale(1.03);
}

.preview-session .image-viewer {
	font-size: 1.25rem;
	font-weight: 500;
	line-height: 1.2;
	background-color: #e4e4f4;
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
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
					<div class="body-title">
						<h3>
							<i class="bi bi-app"></i> 포토 갤러리
						</h3>
					</div>

					<div class="body-main">

						<form name="postForm" method="post" enctype="multipart/form-data">
							<table class="table mt-5 write-form">
								<tr>
									<td class="bg-light col-sm-2" scope="row">제 목</td>
									<td><input type="text" name="subject" maxlength="100"
										class="form-control" value="${dto.subject}"></td>
								</tr>

								<tr>
									<td class="bg-light col-sm-2" scope="row">작성자명</td>
									<td>
										<p class="form-control-plaintext">${sessionScope.member.userName}</p>
									</td>
								</tr>

								<tr>
									<td class="bg-light col-sm-2" scope="row">내 용</td>
									<td><textarea name="content" class="form-control">${dto.content}</textarea>
									</td>
								</tr>

								<tr>
									<td class="bg-light col-sm-2" scope="row">첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
									<td><input type="file" name="selectFile"
										class="form-control" multiple></td>
								</tr>
								<c:if test="${mode=='update'}">
									<c:forEach var="vo" items="${listFile}">
										<tr>
											<td class="bg-light col-sm-2" scope="row">첨부된파일</td>
											<td>
												<p class="form-control-plaintext">
													<a href="javascript:deleteFile('${vo.fileNum}')"><i
														class="bi bi-trash"></i></a> ${vo.originalFilename}
												</p>
											</td>
										</tr>
									</c:forEach>
								</c:if>
							</table>

							<table class="table table-borderless">
								<tr>
									<td class="text-center">
										<button type="button" class="btn btn-dark" onclick="sendOk();">${mode=="update"?"수정완료":"등록완료"}&nbsp;<i
												class="bi bi-check2"></i>
										</button>
										<button type="reset" class="btn btn-light">다시입력</button>
										<button type="button" class="btn btn-light"
											onclick="location.href='${pageContext.request.contextPath}/myTrip/list';">${mode=="update"?"수정취소":"등록취소"}&nbsp;<i
												class="bi bi-x"></i>
										</button> <c:if test="${mode=='update'}">
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
	const f = document.postForm;
	let str;
	
	str = f.subject.value.trim();
	if( ! str ) {
		alert('제목을 입력하세요. ');
		f.subject.focus();
		return;
	}

	str = f.content.value.trim();
	if( ! str ) {
		alert('내용을 입력하세요. ');
		f.content.focus();
		return;
	}

	let mode = '${mode}';
	if( (mode === 'write') && (!f.selectFile.value) ) {
		alert('이미지 파일을 추가 하세요. ');
		f.selectFile.focus();
		return;
	}
	
	f.action = '${pageContext.request.contextPath}/myTrip/${mode}';
	f.submit();
}

<c:if test="${mode=='update'}">
function deleteFile(fileNum) {
	if(! confirm('파일을 삭제 하시겠습니까 ? ')) {
		return;
	}

	let params = 'pageNo=${pageNo}&size=${size}&num=${dto.num}&fileNum=' + fileNum;
    let url = '${pageContext.request.contextPath}/myTrip/deleteFile?' + params;
	location.href = url;

}
</c:if>

</script>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
	</footer>

	<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>