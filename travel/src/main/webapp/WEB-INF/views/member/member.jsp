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
<style type="text/css">
.w-px-100 {
	width: 100px;
}

.h-px-100 {
	height: 100px;
}

/* 마켓컬리 스타일 반영 */
.form-label {
	font-weight: 600;
	font-size: 15px;
	margin-bottom: 8px;
}

.form-control {
	height: 48px;
	font-size: 15px;
	border-radius: 0;
	border: 1px solid #ccc;
}

.help-block {
	font-size: 13px;
	color: #999;
	margin-top: 5px;
}

.btn-primary {
	background-color: #5f0080;
	border: none;
	height: 50px;
	font-size: 16px;
}

.btn-light {
	height: 50px;
	font-size: 16px;
	background-color: #f9f9f9;
	border: 1px solid #ccc;
}

.input-group-text {
	background-color: #f5f5f5;
	font-size: 14px;
}

.text-section {
	margin-bottom: 28px;
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
				<div class="col-md-7 my-3 p-3">

					<div class="body-title">
						<h3>
							<i class="bi bi-person-square"></i> ${mode=="update" ? "정보수정":"회원가입"}
						</h3>
					</div>

					<div class="body-main">

						<form name="memberForm" method="post"
							enctype="multipart/form-data">

							<div class="d-flex align-items-center g-3 pb-4 border-bottom">
								<img
									src="${pageContext.request.contextPath}/dist/images/user.png"
									class="img-avatar d-block w-px-100 h-px-100 rounded"
									alt="프로필 이미지">

								<div class="ms-3 d-flex flex-column">
									<!-- 버튼들 가로 정렬 -->
									<div class="d-flex gap-2 mb-2">
										<label for="selectFile"
											class="btn btn-primary d-flex flex-column justify-content-center align-items-center text-center"
											tabindex="0" title="사진 업로드"
											style="height: 50px; width: 120px;"> <span
											class="d-none d-sm-block">사진 업로드</span> <i
											class="bi bi-upload d-block d-sm-none"></i> <input
											type="file" name="selectFile" id="selectFile" hidden=""
											accept="image/png, image/jpeg">
										</label>

										<button type="button"
											class="btn-photo-init btn btn-light d-flex flex-column justify-content-center align-items-center text-center"
											title="초기화" style="height: 50px; width: 120px;">
											<span class="d-none d-sm-block">초기화</span> <i
												class="bi bi-arrow-counterclockwise d-block d-sm-none"></i>
										</button>
									</div>

									<!-- 안내 문구 -->
									<div>Allowed JPG, GIF or PNG. Max size of 800K</div>
								</div>
							</div>


							<!-- 아이디 -->
							<div class="text-section">
								<label class="form-label" for="userId">아이디</label>
								<div class="d-flex gap-2">
									<input type="text" class="form-control wrap-userId" id="userId"
										name="userId" value="${dto.userId}"
										${mode=="update" ? "readonly" : ""} autofocus>
									<c:if test="${mode=='account'}">
										<button type="button" class="btn btn-light col-auto"
											onclick="userIdCheck();">중복확인</button>
									</c:if>
								</div>
								<c:if test="${mode=='account'}">
									<div class="help-block">아이디는 5~10자, 영문자로 시작해야 합니다.</div>
								</c:if>
							</div>

							<!-- 패스워드 -->
							<div class="text-section">
								<label class="form-label" for="userPwd">비밀번호</label> <input
									type="password" class="form-control" id="userPwd"
									name="userPwd" autocomplete="off">
								<div class="help-block">영문자 포함, 숫자/특수문자 포함 5~10자</div>
							</div>

							<!-- 비밀번호 확인 -->
							<div class="text-section">
								<label class="form-label" for="userPwd2">비밀번호 확인</label> <input
									type="password" class="form-control" id="userPwd2"
									name="userPwd2" autocomplete="off">
								<div class="help-block">비밀번호를 다시 입력하세요.</div>
							</div>

							<!-- 이름 -->
							<div class="text-section">
								<label class="form-label" for="userName">이름</label> <input
									type="text" class="form-control" id="userName" name="userName"
									value="${dto.userName}" ${mode=="update" ? "readonly" : ""}>
							</div>

							<!-- 생년월일 -->
							<div class="text-section">
								<label class="form-label" for="birth">생년월일</label> <input
									type="date" class="form-control" id="birth" name="birth"
									value="${dto.birth}" ${mode=="update" ? "readonly" : ""}>
							</div>

							<!-- 이메일 -->
							<div class="text-section">
								<label class="form-label">이메일</label>
								<div class="row g-2 align-items-center">
									<!-- 이메일 아이디 입력 -->
									<div class="col-4">
										<input type="text" class="form-control" name="email1"
											placeholder="이메일 아이디" value="${dto.email1}">
									</div>

									<!-- @ 표시 -->
									<div class="col-auto">
										<span class="input-group-text">@</span>
									</div>

									<!-- 이메일 도메인 입력 -->
									<div class="col-4">
										<input type="text" class="form-control" name="email2"
											id="email2" placeholder="도메인" value="${dto.email2}" readonly>
									</div>

									<!-- 셀렉트박스 -->
									<div class="col-auto">
										<select name="selectEmail" id="selectEmail"
											class="form-select" onchange="changeEmail();">
											<option value="">이메일 선택</option>
											<option value="naver.com"
												${dto.email2 == 'naver.com'  ? "selected" : ""}>naver.com</option>
											<option value="gmail.com"
												${dto.email2 == 'gmail.com'  ? "selected" : ""}>gmail.com</option>
											<option value="hanmail.net"
												${dto.email2 == 'hanmail.net'? "selected" : ""}>hanmail.net</option>
											<option value="outlook.com"
												${dto.email2 == 'outlook.com'? "selected" : ""}>outlook.com</option>
											<option value="icloud.com"
												${dto.email2 == 'icloud.com' ? "selected" : ""}>icloud.com</option>
											<option value="direct">직접입력</option>
										</select>
									</div>
								</div>
							</div>

							<!-- 전화번호 -->
							<div class="text-section">
								<label class="form-label" for="tel">전화번호</label> <input
									type="text" class="form-control" id="tel" name="tel"
									value="${dto.tel}">
							</div>

							<!-- 우편번호 -->
							<div class="text-section">
								<label class="form-label" for="zip">우편번호</label>
								<div class="d-flex gap-2">
									<input type="text" class="form-control" id="zip" name="zip"
										value="${dto.zip}" readonly tabindex="-1">
									<button type="button" class="btn btn-light col-auto"
										onclick="daumPostcode();">우편번호 찾기</button>
								</div>
							</div>

							<!-- 주소 -->
							<div class="text-section">
								<label class="form-label" for="addr1">주소</label> <input
									type="text" class="form-control mb-2" id="addr1" name="addr1"
									value="${dto.addr1}" readonly placeholder="기본 주소" tabindex="-1">
								<input type="text" class="form-control" id="addr2" name="addr2"
									value="${dto.addr2}" placeholder="상세 주소">
							</div>

							<!-- 약관 동의 -->
							<div class="text-section">
								<div class="form-check">
									<input class="form-check-input" type="checkbox" name="agree"
										id="agree" checked
										onchange="form.sendButton.disabled = !checked"> <label
										class="form-check-label" for="agree"> <a href="#"
										class="text-primary text-decoration-underline">이용약관</a>에
										동의합니다.
									</label>
								</div>
							</div>

							<!-- 버튼 -->
							<div class="text-center mt-4 row">
								<button type="button" name="sendButton"
									class="btn btn-primary mb-2 col-4" onclick="memberOk();">
									${mode=="update"?"정보수정":"회원가입"}</button>
								<button type="reset"class="btn btn-light col-4">초기화</button>	
								<button type="button" class="btn btn-light col-4"
									onclick="location.href='${pageContext.request.contextPath}/';">
									${mode=="update"?"수정취소":"가입취소"}</button>
								<input type="hidden" name="userIdValid" id="userIdValid"
									value="false">
								<c:if test="${mode=='update'}">
									<input type="hidden" name="profile_photo"
										value="${dto.profile_photo}">
								</c:if>
							</div>


						</form>

					</div>

				</div>
			</div>
		</div>
	</main>

	<script type="text/javascript">
window.addEventListener('DOMContentLoaded', ev => {
	let img = '${dto.profile_photo}';

	const avatarEL = document.querySelector('.img-avatar');
	const inputEL = document.querySelector('form[name=memberForm] input[name=selectFile]');
	const btnEL = document.querySelector('form[name=memberForm] .btn-photo-init');
	
	let avatar;
	if( img ) {
		avatar = '${pageContext.request.contextPath}/uploads/member/' + img;
		avatarEL.src = avatar;
	}
	
	const maxSize = 800 * 1024;
	inputEL.addEventListener('change', ev => {
		let file = ev.target.files[0];
		if(! file) {
			if( img ) {
				avatar = '${pageContext.request.contextPath}/uploads/member/' + img;
			} else {
				avatar = '${pageContext.request.contextPath}/dist/images/user.png';
			}
			avatarEL.src = avatar;
			
			return;
		}
		
		if(file.size > maxSize || ! file.type.match('image.*')) {
			inputEL.focus();
			return;
		}
		
		var reader = new FileReader();
		reader.onload = function(e) {
			avatarEL.src = e.target.result;
		}
		reader.readAsDataURL(file);			
	});
	
	btnEL.addEventListener('click', ev => {
		if( img ) {
			if(! confirm('등록된 이미지를 삭제하시겠습니까 ? ')){
				return false; 
			}
			
			avatar = '${pageContext.request.contextPath}/uploads/member/' + img;
			
			// 등록된 이미지 삭제
			let url = '${pageContext.request.contextPath}/member/deleteProfile';
			$.post(url, {profile_photo: img}, function(data){
				let state = data.state;
				
				if(state === 'true'){
					img = '${pageContext.request.contextPath}/dist/images/user.png';
					
					$('form input[name=profile_photo]').val('');
				}
				inputEL.value = '';
				avatarEL.src = avatar;
			}, 'json');
			
		} else {
			avatar = '${pageContext.request.contextPath}/dist/images/user.png';
			inputEL.value = '';
			avatarEL.src = avatar;
		}
		
	});
});

function isValidDateString(dateString) {
	try {
		const date = new Date(dateString);
		const [year, month, day] = dateString.split("-").map(Number);
		
		return date instanceof Date && !isNaN(date) && date.getDate() === day;
	} catch(e) {
		return false;
	}
}

function memberOk() {
	const f = document.memberForm;
	let str, p;

	p = /^[a-z][a-z0-9_]{4,9}$/i;
	str = f.userId.value;
	if( ! p.test(str) ) { 
		alert('아이디를 다시 입력 하세요. ');
		f.userId.focus();
		return;
	}


	let mode = '${mode}';
	if( mode === 'account' && f.userIdValid.value === 'false' ) {
		str = '아이디 중복 검사가 실행되지 않았습니다.';
		$('.wrap-userId').find('.help-block').html(str);
		f.userId.focus();
		return;
	}


	p =/^(?=.*[a-z])(?=.*[!@#$%^*+=-]|.*[0-9]).{5,10}$/i;
	str = f.userPwd.value;
	if( ! p.test(str) ) { 
		alert('패스워드를 다시 입력 하세요. ');
		f.userPwd.focus();
		return;
	}

	if( str !== f.userPwd2.value ) {
        if( f.userPwd2.value.length == '0'){
	        alert('패스워드확인을 입력하여 주십시오.');
	        f.userPwd2.focus();
        } else {
    	    alert('패스워드가 일치하지 않습니다. ');
	        f.userPwd2.focus();
        }
        return;
	}
	
	p = /^[가-힣]{2,5}$/;
    str = f.userName.value;
    if( ! p.test(str) ) {
        alert('이름을 다시 입력하세요. ');
        f.userName.focus();
        return;
    }

    str = f.birth.value;
    if( ! isValidDateString(str) ) {
        alert('생년월일를 입력하세요. ');
        f.birth.focus();
        return;
    }
    p = /^[a-z][a-z0-9_]{4,20}$/i;
    str = f.email1.value.trim();
    if( ! p.test(str) ) {
        alert('이메일을 입력하세요. ');
        f.email1.focus();
        return;
    }

    str = f.email2.value.trim();
    if( ! str ) {
        alert('이메일을 입력하세요. ');
        f.email2.focus();
        return;
    }
    
    p = /^(010)-?\d{4}-?\d{4}$/;    
    str = f.tel.value;
    if( ! p.test(str) ) {
        alert('전화번호를 입력하세요. ');
        f.tel.focus();
        return;
    }

    f.action = '${pageContext.request.contextPath}/member/${mode}';
    f.submit();
}

function userIdCheck() {
	// 아이디 중복 검사
	let p;
	let userId = $('#userId').val();
	
	p = /^[a-z][a-z0-9_]{4,9}$/i;
	if(! p.test(userId)){
		let s = '아이디는 5~10자 이내이며, 첫글자는 영문자로 시작해야 합니다.';
		$('#userId').focus();
		$('#userId').closest('.wrap-userId').find('.help-block').html(s);
		return;
	}
	
	let url = '${pageContext.request.contextPath}/member/userIdCheck';
	let params = 'userId=' + userId;
	
	$.ajax({
		type:'post',
		url: url,
		data: params,
		dataType: 'json',
		success: function(data){
			let passed = data.passed;
			let s;
			if(passed === 'true'){
				s = '<span style="color:blue; font-weight: bold;">' + userId + '</span> 아이디는 사용가능 합니다.';
				
				$('#userIdValid').val('true');
				$('#userId').closest('.wrap-userId').find('.help-block').html(s);
			} else{
				s = '';
				s = '<span style="color:red; font-weight: bold;">' + userId + '</span> 아이디는 사용할수 없습니다.';
				
				$('#userId').closest('.wrap-userId').find('.help-block').html(s);
				$('#userIdValid').val('false');
				$('#userId').val('');
				$('#userId').focus();
			}
		}
	})
}

function changeEmail() {
    const f = document.memberForm;
	    
    let str = f.selectEmail.value;
    if( str !== 'direct' ) {
        f.email2.value = str; 
        f.email2.readOnly = true;
        f.email1.focus(); 
    }
    else {
        f.email2.value = '';
        f.email2.readOnly = false;
        f.email1.focus();
    }
}

/*
window.addEventListener('DOMContentLoaded', () => {
	const dateELS = document.querySelectorAll('form input[type=date]');
	dateELS.forEach( inputEL => inputEL.addEventListener('keydown', e => e.preventDefault()) );
});
*/
</script>

	<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
	<script>
    function daumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var fullAddr = ''; // 최종 주소 변수
                var extraAddr = ''; // 조합형 주소 변수

                // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    fullAddr = data.roadAddress;

                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    fullAddr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 조합한다.
                if(data.userSelectedType === 'R'){
                    //법정동명이 있을 경우 추가한다.
                    if(data.bname !== ''){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있을 경우 추가한다.
                    if(data.buildingName !== ''){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
                    fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                document.getElementById('zip').value = data.zonecode; //5자리 새우편번호 사용
                document.getElementById('addr1').value = fullAddr;

                // 커서를 상세주소 필드로 이동한다.
                document.getElementById('addr2').focus();
            }
        }).open();
    }
</script>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
	</footer>

	<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>