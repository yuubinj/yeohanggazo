<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>관리자 - 공지 작성/수정</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="/WEB-INF/views/admin/layout/headerResources.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/board.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/dist/css/paginate.css" type="text/css">


<style type="text/css">
/* 사이즈바 */
.nav-tabs .nav-link {
	min-width: 130px;
	background: #f3f5f7;
	border-radius: 0;
	border-top: 1px solid #dbdddf;
	border-right: 1px solid #dbdddf;
	color: #333;
	font-weight: 600;
}
.nav-tabs .nav-item:first-child .nav-link {
	border-left: 1px solid #dbdddf;
}

.nav-tabs .nav-link.active {
	background: #3d3d4f;
	color: #fff;
}
.tab-pane { min-height: 70px; }

.category-item { color: #0d6efd; font-weight: 500; }

/* 폼 전체 배경 */
body {
    background-color: #f8f9fc;
}

/* 카드 스타일 */
.rounded-box {
    background-color: #fff;
    border-radius: 16px;
    border: 1px solid #e2e2e2;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
    padding: 28px;
}

/* 테이블 정리 */
.table.write-form {
    border-collapse: separate !important;
    border-spacing: 0;
    border: none;
    width: 100%;
}

.table.write-form td {
    border: none !important;
    background: transparent !important;
    padding: 14px 12px;
    vertical-align: middle;
    font-weight: normal;
    color: #333;
}

/* 제목, 작성자, 내용 등의 라벨 셀 */
.table.write-form td:first-child {
    width: 100px;
    color: #666;
    font-size: 16px;
    font-weight: 500;
    color: #444;
}

/* 오른쪽 칸 자동 확장 */
.table.write-form td:last-child {
    width: auto;
}

/* 줄 구분선 */
.table.write-form tr + tr td {
    border-top: 1px solid #eee !important;
}

/* 공지칸 디자인 */
.disabled-select-style {
    pointer-events: none;
    background-color: #f1f3f5;
    color: #495057;
    border: 1px solid #ced4da;
    border-radius: 10px; 
    padding: 8px 12px;  
    font-size: 16px;
    height: 44px;       
    line-height: 1.5;
    display: flex;
    align-items: center; 
  	font-weight: 600;
}

/* 제목 입력 */
.input-subject-style {
    background-color: #f8f9fa;
    border-radius: 10px;
    border: 1px solid #ccc;
    height: 44px;
    padding: 8px 12px;
    font-size: 16px;
    transition: border 0.2s ease;
    flex: 1;
    margin-left: 0;
}

.input-subject-style:focus {
    outline: none;
    border-color: #7da4ff;
}

/* 기본 input, select, textarea */
.form-control{
    border-radius: 10px !important;
    border: 1px solid #ccc !important;
    box-shadow: none !important;
    transition: border 0.2s ease;
}

.form-control:focus{
    border-color: #7da4ff !important;
    outline: none;
}

/* 실제 작성자명 출력 텍스트 스타일 */
.writer-name {
    font-size: 16px;
    color: #222;
}

/* 버튼 통일감 있게 */
.btn {
    padding: 8px 18px;
    font-size: 15px;
    border-radius: 8px;
    transition: all 0.2s ease-in-out;
}

.btn-dark:hover {
    background-color: #1c1c1c;
}

.btn-light:hover {
    background-color: #f0f0f0;
}

</style>

</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	<div class="container">
		<div class="body-container row justify-content-center">
			<div class="col-md-10 my-3 p-3">
				<h3><i class="bi bi-pencil-square"></i> ${mode=='update'?'공지수정':'공지작성'}</h3>

				<form name="postForm" method="post" enctype="multipart/form-data">
				
					<!-- 제목 + 작성자 박스 -->
					<div class="rounded-box mb-4"><!-- 새로 추가됨: 둥근 흰 박스 -->
					
						<table class="table write-form mb-0"><!-- 수정됨: class 추가(write-form), mb-0로 여백 제거 -->
							<tr>
								<td class="col-sm-2" scope="row">제 목</td> <!-- 수정됨: class="bg-light" 삭제 -->
								<td>
									<!-- 수정됨: 제목과 카테고리를 나란히 정렬하는 flex 구조 -->
									 <div class="d-flex gap-2 align-items-center">
	    								<div class="category-wrapper">
										    <div class="disabled-select-style" tabindex="-1">공지</div>
       										<input type="hidden" name="categoryNum" id="categoryNum" value="1">
										</div>
										
										<!-- 수정됨: 제목 input에 배경색 주기 위해 input-subject-style 클래스 추가 -->
										<input type="text" name="subject" maxlength="100"
										       class="form-control border-0 input-subject-style"
										       style="flex:1;"
										       placeholder="제목을 입력해 주세요."
										       value="${dto.subject}">
									</div>
								</td>
							</tr>
					
							<tr>
								<td scope="row">작성자명</td> <!-- 수정됨: class="bg-light" 삭제 -->
								<td>
									<p class="form-control-plaintext mb-0 writer-name">${sessionScope.member.userName}</p> <!-- 수정됨: mb-0로 아래 여백 제거 -->
								</td>
							</tr>
						</table>
					</div>
					
					<!-- 내용 + 첨부파일 박스 -->
					<div class="rounded-box mb-4"><!-- 새로 추가됨: 둥근 흰 박스 -->
					
						<table class="table write-form mb-0"><!-- 수정됨: class 추가(write-form), mb-0로 여백 제거 -->
							<tr>
								<td class="col-sm-2" scope="row">내 용</td> <!-- 수정됨: class="bg-light" 삭제 -->
								<td>
									<textarea name="content" id="ir1" class="form-control"
									          style="width: 99%; height: 300px;">${dto.content}</textarea>
								</td>
							</tr>
					
							<tr>
								<td scope="row">첨부파일</td> <!-- 수정됨: class="bg-light" 삭제 -->
								<td><input type="file" name="selectFile" class="form-control"></td>
							</tr>
					
							<c:if test="${mode=='update' && not empty dto.saveFilename}">
								<tr>
									<td scope="row">첨부된파일</td> <!-- 수정됨: class="bg-light" 삭제 -->
									<td>
										<div id="attachedFileArea">
											<a href="javascript:markFileForDelete();"><i class="bi bi-trash"></i></a>
											${dto.originalFilename}
										</div>
									</td>
								</tr>
							</c:if>
						</table>
					</div>
					
					<div class="text-center mt-3">
						<button type="button" class="btn btn-dark" onclick="submitContents(this.form);">${mode=='update'?'수정완료':'등록완료'} <i class="bi bi-check2"></i></button>
						<button type="reset" class="btn btn-light">다시입력</button>
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/admin/bbs/list?category=1';">${mode=='update'?'수정취소':'등록취소'} <i class="bi bi-x"></i></button>
						
						<!-- 수정모드일 경우 hidden 값들 세팅 -->
						<c:if test="${mode=='update'}">
							<input type="hidden" name="num" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
							<input type="hidden" name="saveFilename" value="${dto.saveFilename}">
							<input type="hidden" name="originalFilename" value="${dto.originalFilename}">
						</c:if>
						
						<!-- 삭제 여부 전달용 히든필드 (항상 포함) -->
						<input type="hidden" name="deleteFlag" id="deleteFlag" value="false">
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

	f.action = '${pageContext.request.contextPath}/admin/bbs/${mode}';
	return true;
}

// 기존 파일 삭제 의도만 표시 (실제 삭제는 Controller에서 수행)
function markFileForDelete() {
	if (!confirm("파일을 삭제하시겠습니까?")) return;

	// 화면에서 파일 숨김
	const fileArea = document.getElementById("attachedFileArea");
	if (fileArea) fileArea.style.display = "none";

	// 삭제 의사 표시
	document.getElementById("deleteFlag").value = "true";
}
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
	<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/admin/layout/footerResources.jsp"/>
</body>
</html>