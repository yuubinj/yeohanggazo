<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Spring</title>
<jsp:include page="/WEB-INF/views/myPage/layout/headerResources.jsp"/>

<style>
  /* 헤더 fixed-top 예상 높이만큼 main 아래로 밀기 */
  main {
    margin-top: 55px; 
  }

  /* 사이드바 고정 위치 */
  nav.vertical_nav {
    position: fixed;
    top: 55px; /* 헤더 높이 */
    left: 0;
    width: 200px; /* 사이드바 너비 */
    height: calc(100vh - 55px);
    background-color: #f8f9fa;
    overflow-y: auto;
    z-index: 1030;
    border-right: 1px solid #dee2e6;
  }

  .wrapper {
    margin-left: 200px; /* 사이드바 너비 */
    padding: 20px;
  }

.body-title h2 {
  min-width: 200px;
  font-weight: bold;
}

.modal {
  z-index: 99999 !important;
}

.modal-backdrop {
  z-index: 99998 !important;
}
</style>
<script type="text/javascript">
    let currentPage = 1;
    let loading = false;
    let lastPageReached = false;

    function loadScrapList() {
        if (loading || lastPageReached) return;
        loading = true;

        $.ajax({
            url: "${pageContext.request.contextPath}/myPage/scrap/list",
            type: "GET",
            data: { pageNo: currentPage },
            success: function(result) {
                const trimmed = result.trim();
                if (trimmed.length === 0) {
                    if (currentPage === 1) {
                        // 첫 페이지인데 데이터 없음 ⇒ 기본 안내 출력
                        $("#scrapContent").html(`
                            <div class="text-center text-muted mt-4">
                              <i class="fa-regular fa-folder-open fa-2x"></i><br/>
                              등록된 스크랩이 없습니다.
                            </div>
                        `);
                    }
                    lastPageReached = true;
                    return;
                }

                $("#scrapContent").append(result);
                currentPage++;
            },
            error: function(xhr) {
                if (xhr.status === 403) {
                    location.href = "${pageContext.request.contextPath}/member/login";
                } else {
                    alert("스크랩 목록 불러오기 실패");
                }
            },
            complete: function() {
                loading = false;
            }
        });
    }

    $(document).ready(function() {
        loadScrapList(); // 최초 한 번은 무조건 호출됨

        $(window).on("scroll", function() {
            if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
                loadScrapList();
            }
        });
    });
    

    $(document).on('click', '.star-btn', function() {
        if (!confirm('스크랩을 삭제하시겠습니까?')) return;

        const btn = $(this);
        const apiTypeId = btn.data('num');

        $.ajax({
            url: '${pageContext.request.contextPath}/myPage/scrap/delete',
            type: 'POST',
            data: { apiTypeId: apiTypeId },
            dataType: 'json',
            success: function(res) {
                if (res.state === 'true') {
                    // 삭제 성공 시 해당 카드 제거
                    btn.closest('.scrap-card').remove();
                } else {
                    alert(res.message || '삭제에 실패했습니다.');
                }
            },
            error: function() {
                alert('서버와 통신 중 오류가 발생했습니다.');
            }
        });
    });
    </script>


</head>
<body>
<jsp:include page="/WEB-INF/views/myPage/layout/header.jsp"/>

<main>
  <!-- 고정 사이드바 -->
  <nav class="vertical_nav">
    <jsp:include page="/WEB-INF/views/myPage/layout/left.jsp"/>
  </nav>

  <!-- 본문 영역: 사이드바 너비만큼 margin-left 처리 -->
  <div class="wrapper">
      <div class="body-title row col-10">
  <div class="col">
    <h2><i class="fa-solid fa-star me-2"></i> 즐겨찾기 목록</h2>
  </div>
</div>
      <div id="scrapContent"></div>
  </div>
</main>

<footer>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>
<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>


<!-- 모달창 -->
<!-- 상세정보 모달 -->
<div class="modal fade" id="scrapDetailModal" tabindex="-1" aria-labelledby="scrapDetailModalLabel">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="scrapDetailModalLabel">상세정보</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>
      <div class="modal-body">
        <div class="row g-3">
          <div class="col-md-5">
            <img id="scrapDetailImg" src="" alt="관광지 이미지" class="img-fluid rounded" style="width: 100%; max-height: 300px; object-fit: cover;">
          </div>
          <div class="col-md-7">
            <h4 id="scrapDetailName" class="mb-3"></h4>
            <p><strong>주소:</strong> <span id="scrapDetailAddr"></span></p>
            <p><strong>전화번호:</strong> <span id="scrapDetailTel"></span></p>
            <p><strong>홈페이지:</strong> <a href="#" target="_blank" id="scrapDetailHomepage"></a></p>
            <hr>
            <p id="scrapDetailOverview" style="white-space: pre-wrap;"></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>


<script type="text/javascript">

function openScrapDetailModal(data) {
	$('#scrapDetailImg').attr('src', data.firstimage || '/images/default.jpg');
	$('#scrapDetailName').text(data.title || '정보 없음');
	$('#scrapDetailAddr').text((data.addr1 || '') + ' ' + (data.addr2 || ''));
	$('#scrapDetailTel').text(data.tel || '전화번호 정보 없음');
	console.log(data.homepage);
	if (data.homepage) {
	  let url = data.homepage;
	  $('#scrapDetailHomepage').attr('href', url).html(url);
	} else {
	  $('#scrapDetailHomepage').attr('href', '#').text('홈페이지 없음');
	}
	
	$('#scrapDetailOverview').text(data.overview || '개요 정보 없음');
	
	const modal = new bootstrap.Modal(document.getElementById('scrapDetailModal'));
	modal.show();
}

$(document).on('click', '.scrap-image, .scrap-name', function () {
	let apiId = $(this).closest('.scrap-card').find('.star-btn').data('apiid');
    if (!apiId) return;

    $.ajax({
        url: '${pageContext.request.contextPath}/myPage/scrap/details',
        method: 'GET',
        data: { contentId: apiId },
        dataType: 'json',
        success: function(data) {
            const item = data.response.body.items.item[0];
            openScrapDetailModal(item);
        },
        error: function(xhr) {
            console.error('에러:', xhr.responseText);
        }
    });
});
</script>


</body>
</html>
