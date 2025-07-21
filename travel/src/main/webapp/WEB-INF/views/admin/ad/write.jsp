<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/admin/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">
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

.preview-session .image-upload-btn,
.preview-session .image-viewer,
.preview-session .image-upload-list .image-item,
.preview-session .image-upload-list .image-uploaded {
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

.preview-session .image-upload-btn:hover,
.preview-session .image-upload-list .image-item:hover,
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
	<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>
</header>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<div class="body-title">
					<h3><i class="bi bi-app"></i> 광고글 관리 </h3>
				</div>
				
				<div class="body-main">

					<form name="postForm" method="post" enctype="multipart/form-data">
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
							
							<c:if test="${mode=='update'}">
								<tr>
									<td class="bg-light col-sm-2" scope="row">숨김 여부</td>
									<td class="py-3"> 
										<input type="radio" name="hide" id="hideTrue" class="form-check-input" 
											value="0" ${dto.hide==0?"checked='checked'":"" }>
										<label class="form-check-label" for="categoryNum1">공개</label>
										<input type="radio" name="hide" id="hideFalse" class="form-check-input" 
											value="1" ${dto.hide==1?"checked='checked'":"" }>
										<label class="form-check-label" for="categoryNum2">숨김</label>
									</td>
								</tr>
							</c:if>
							
							<tr>
								<td class="bg-light col-sm-2" scope="row">이미지</td>
								<td>
									<div class="preview-session">
										<label for="selectFile" class="me-2" tabindex="0" title="사진 업로드">
											<span class="image-viewer"></span>
											<input type="file" name="selectFile" id="selectFile" hidden="" accept="image/png, image/jpeg, image/jpg">
											<c:if test="${mode=='update'}">
												<input type="hidden" name="imageFilename" value="${dto.imageFilename}">
											</c:if>
										</label>
									</div>
								</td>
							</tr>
						</table>
						
						<table class="table table-borderless">
		 					<tr>
								<td class="text-center">
									<button type="button" class="btn btn-dark" onclick="sendOk();">${mode=="update"?"수정완료":"등록완료"}&nbsp;<i class="bi bi-check2"></i></button>
									<button type="reset" class="btn btn-light">다시입력</button>
									<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/ad/list';">${mode=="update"?"수정취소":"등록취소"}&nbsp;<i class="bi bi-x"></i></button>
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
// 단일 이미지 추가
window.addEventListener('DOMContentLoaded', evt => {
	const imageViewer = document.querySelector('form .image-viewer');
	const inputEL = document.querySelector('form input[name=selectFile]');
	
	let uploadImage = '${dto.imageFilename}';
	let img;
	if( uploadImage ) { // 수정인 경우
		img = '${pageContext.request.contextPath}/uploads/photo/' + uploadImage;
	} else {
		img = '${pageContext.request.contextPath}/dist/images/add_photo.png';
	}
	imageViewer.textContent = '';
	imageViewer.style.backgroundImage = 'url(' + img + ')';
	
	inputEL.addEventListener('change', ev => {
		let file = ev.target.files[0];
		if(! file) {
			let img;
			if( uploadImage ) { // 수정인 경우
				img = '${pageContext.request.contextPath}/uploads/photo/' + uploadImage;
			} else {
				img = '${pageContext.request.contextPath}/dist/images/add_photo.png';
			}
			imageViewer.textContent = '';
			imageViewer.style.backgroundImage = 'url(' + img + ')';
			
			return;
		}
		
		if(! file.type.match('image.*')) {
			inputEL.focus();
			return;
		}
		
		const reader = new FileReader();
		reader.onload = e => {
			imageViewer.textContent = '';
			imageViewer.style.backgroundImage = 'url(' + e.target.result + ')';
		};
		reader.readAsDataURL(file);
	});
});

function sendOk() {
	const f = document.postForm;
	let str;
	
	str = f.subject.value.trim();
	if( ! str ) {
		alert('제목을 입력하세요. ');
		f.subject.focus();
		return;
	}

	let mode = '${mode}';
	if( (mode === 'write') && (!f.selectFile.value) ) {
		alert('이미지 파일을 추가 하세요. ');
		f.selectFile.focus();
		return;
	}
	
	f.action = '${pageContext.request.contextPath}/admin/ad/${mode}';
	f.submit();
}
</script>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>

</body>
</html>