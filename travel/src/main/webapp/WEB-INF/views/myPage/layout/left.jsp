<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<nav id="verticalNav" class="vertical_nav">
	<ul id="js-menu" class="menu">
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/"
			class="menu--link"> <i class="menu--icon bi bi-houses"></i> <span
				class="menu--label">홈으로</span></a>
		</li>
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/myPage/myPage"
			class="menu--link"> <i class="menu--icon bi bi-house"></i> <span
				class="menu--label">마이페이지 홈</span></a>
		</li>

		<li class="menu--item">
			<a href="${pageContext.request.contextPath}/myPage/schedule/main"class="menu--link toggle-menu"> 
				<i class="menu--icon bi bi-box-seam"></i> <span class="menu--label">일정관리</span>
			</a>
			
			<ul class="submenu d-none">
				<li><a
					href="${pageContext.request.contextPath}/myPage/schedule/main"
					class="menu--link2" aria-controls="1">월별일정</a></li>
				<li><a
					href="${pageContext.request.contextPath}/myPage/schedule/day"
					class="menu--link2" aria-controls="2">일정관리</a></li>
				<li><a
					href="${pageContext.request.contextPath}/myPage/schedule/year"
					class="menu--link2" aria-controls="3">년도</a></li>
			</ul>
		</li>
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/myPage/scrap/main"

			class="menu--link"> <i class="menu--icon bi bi-person"></i> <span
				class="menu--label">즐겨찾기 목록</span>
		</a></li>
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/member/pwd"
			class="menu--link"> <i class="menu--icon bi bi-person"></i> <span
				class="menu--label">내 정보수정</span>
		</a></li>
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/member/logout"
			class="menu--link"> <i class="menu--icon bi bi-door-closed"></i>
				<span class="menu--label">로그아웃</span>
		</a></li>
		<li class="menu--item"><a
			href="${pageContext.request.contextPath}/member/pwd?mode=delete"
			class="menu--link"> <i class="menu--icon bi bi-person-x"></i> <span
				class="menu--label">회원탈퇴</span>
		</a></li>
	</ul>
</nav>

<script type="text/javascript">
document.addEventListener('DOMContentLoaded', function() {
	  const toggles = document.querySelectorAll('.toggle-menu');
	  
	  const currentPath = window.location.pathname;

	  toggles.forEach(function(toggle) {
	    const submenu = toggle.nextElementSibling;

	    if (submenu && submenu.classList.contains('submenu')) {
	      const links = submenu.querySelectorAll('a');

	      for(let link of links) {
	        if(link.pathname === currentPath) {
	          submenu.classList.remove('d-none');
	          break;
	        }
	      }
	    }

	    toggle.addEventListener('click', function(e) {

	      toggles.forEach(t => {
	        if(t !== toggle) {
	          const otherSubmenu = t.nextElementSibling;
	          if(otherSubmenu && otherSubmenu.classList.contains('submenu')) {
	            otherSubmenu.classList.add('d-none');
	          }
	        }
	      });

	      if (submenu && submenu.classList.contains('submenu')) {
	        submenu.classList.toggle('d-none');
	      }
	    });
	  });

	  // 다른 메뉴 클릭 시 서브메뉴 모두 닫기
	  const menuLinks = document.querySelectorAll('.menu--item > a:not(.toggle-menu)');
	  menuLinks.forEach(link => {
	    link.addEventListener('click', function() {
	      toggles.forEach(toggle => {
	        const submenu = toggle.nextElementSibling;
	        if(submenu && submenu.classList.contains('submenu')) {
	          submenu.classList.add('d-none');
	        }
	      });
	    });
	  });
	});
</script>
