<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<nav class="vertical_nav">
	<ul id="js-menu" class="menu">
		<li class="menu--item">
			<a href="<c:url value='/admin' />" class="menu--link" title="Home">
				<i class="menu--icon bi bi-h-square"></i>
				<span class="menu--label">Home</span>
			</a>
		</li>
	
		<li class="menu--item">
	        <a href="<c:url value='/admin' />" class="menu--link" title="회원관리">
				<i class="menu--icon bi bi-person-square"></i>
				<span class="menu--label">회원관리</span>
			</a>
		</li>
	
		<li class="menu--item menu--item__has_sub_menu">
			<label class="menu--link" title="강좌관리">
				<i class="menu--icon bi bi-book-half"></i>
				<span class="menu--label">강좌관리</span>
			</label>
		
			<ul class="sub_menu">
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">카테고리</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">강좌</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">강사</a>
				</li>
			</ul>
		</li>
			
		<li class="menu--item menu--item__has_sub_menu">
			<label class="menu--link" title="고객센터관리">
				<i class="menu--icon bi bi-question-square"></i>
				<span class="menu--label">고객센터관리</span>
			</label>
	
			<ul class="sub_menu">
				<li class="sub_menu--item">
					<a href="<c:url value='/admin' />" class="sub_menu--link">자주하는 질문</a>
				</li>
				<li class="sub_menu--item">
					<a href="<c:url value='/admin/notice/list' />" class="sub_menu--link">공지사항</a>
				</li>
				<li class="sub_menu--item">
					<a href="<c:url value='/admin' />" class="sub_menu--link">문의</a>
				</li>
				<li class="sub_menu--item">
					<a href="<c:url value='/admin' />" class="sub_menu--link">이벤트</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">신고</a>
				</li>
			</ul>
		</li>
	
		<li class="menu--item menu--item__has_sub_menu">
			<label class="menu--link" title="서비스관리">
				<i class="menu--icon bi bi-x-diamond-fill"></i>
				<span class="menu--label">서비스관리</span>
			</label>

			<ul class="sub_menu">
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">맛집정보</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">레시피</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">관광정보</a>
				</li>
				<li class="sub_menu--item">
					<a href="#" class="sub_menu--link">날씨</a>
				</li>
			</ul>
		</li>
	
		<li class="menu--item">
	        <a href="#" class="menu--link" title="쇼핑관리">
				<i class="menu--icon bi bi-bag"></i>
				<span class="menu--label">쇼핑관리</span>
			</a>
		</li>
	
		<li class="menu--item">
	        <a href="#" class="menu--link" title="메인화면 설정">
				<i class="menu--icon bi bi-gear-fill"></i>
				<span class="menu--label">메인화면 설정</span>
			</a>
		</li>
	
		<li class="menu--item">
	        <a href="#" class="menu--link" title="GroupWare">
				<i class="menu--icon bi bi-c-square"></i>
				<span class="menu--label">GroupWare</span>
			</a>
		</li>
	
		<li class="menu--item">
	        <a href="<c:url value='/'/>" class="menu--link" title="Logout">
				<i class="menu--icon bi bi-unlock"></i>
				<span class="menu--label">Logout</span>
			</a>
		</li>
	</ul>
	
	<button id="collapse_menu" class="collapse_menu">
		<i class="collapse_menu--icon bi bi-chevron-left"></i>
		<span class="menu--label">Menu</span>
	</button>	
</nav>

<script type="text/javascript">
// 메뉴 활성화
$(function(){
    var url = window.location.pathname;
    var urlRegExp = new RegExp(url.replace(/\/$/, '') + "$");
    
    try {
        $('nav ul>li>a').each(function() {
            if (urlRegExp.test(this.href.replace(/\/$/, ''))) {
                $(this).addClass('active_menu');
                return false;
            }
        });
        if ($('nav ul>li>a').hasClass('active_menu')) return false;

        var parent = url.replace(/\/$/, '').substring(0, url.replace(/\/$/, '').lastIndexOf('/'));
        if (!parent) parent = '/';
        var urlParentRegExp = new RegExp(parent);
        
        $('nav ul>li>a').each(function() {
            if ($(this).attr('href') === '#' || ! $(this).attr('href').trim()) return true;

            var phref = this.href.replace(/\/$/, '').substring(0, this.href.replace(/\/$/, '').lastIndexOf('/'));

            if (urlParentRegExp.test(phref)) {
                $(this).addClass('active_menu');
                return false;
            }
        });
        if($('nav ul>li>a').hasClass('active_menu')) return false;

    	$('nav ul>.menu--item__has_sub_menu ul>li>a').each(function() {
    		if ($(this).attr('href') === '#' || ! $(this).attr('href').trim()) return true;
    		
    		let preUrl = url.substring(0, url.lastIndexOf('/'));
    		if (this.href.endsWith(preUrl)) {
    			$(this).addClass('active_menu');
    			return false;
    		}
    	});
        if($('nav ul>li>a').hasClass('active_menu')) return false;
    	
    	$('nav ul>.menu--item__has_sub_menu ul>li>a').each(function() {
    		if ($(this).attr('href') === '#' || ! $(this).attr('href').trim()) return true;
    		
    		let preUrl = url.substring(0, url.lastIndexOf('/'));
    		if(preUrl.lastIndexOf('/') === -1) return false;
    		preUrl = preUrl.substring(0, preUrl.lastIndexOf('/'));
    		
    		if ($(this).attr('href').startsWith(preUrl)) {
    			$(this).addClass('active_menu');
    			return false;
    		}
    	});
        if($('nav ul>li>a').hasClass('active_menu')) return false;
        
    }catch(e) {
    }
});

$(function(){
	$('nav ul>.menu--item__has_sub_menu ul>li>a').each(function() {
		if($(this).hasClass('active_menu')) {
			$(this).closest(".menu--item__has_sub_menu").addClass('menu--subitens__opened');
			return false;
		}
	});
});
</script>