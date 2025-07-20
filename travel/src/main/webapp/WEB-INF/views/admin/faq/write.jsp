<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<form name="faqForm" method="post">
	<table class="table mt-4 write-form">
		<tr> 
			<td class="bg-light col-sm-2">카테고리</td>
			<td>
				<div class="row">
					<div class="col-sm-5 pe-1">
						<select name="categoryNum" class="form-select">
							<c:forEach var="vo" items="${listCategory}">
								<option value="${vo.categoryNum}" ${dto.categoryNum==vo.categoryNum?"selected":""}>${vo.category}</option>
							</c:forEach>
						</select>
					</div>
				</div>					
			</td>
		</tr>
	
		<tr> 
			<td class="bg-light col-sm-2">제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
			<td> 
				<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
			</td>
		</tr>
	
		<tr>
			<td class="bg-light col-sm-2">작성자</td>
			<td> 
				<p class="form-control-plaintext">${sessionScope.member.userName}</p>
			</td>
		</tr>
	
		<tr> 
			<td class="bg-light col-sm-2">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
			<td> 
				<textarea name="content" class="form-control">${dto.content}</textarea>
			</td>
		</tr>
	</table>
	
	<table class="table table-borderless">
		<tr>
			<td class="text-center">
				<button type="button" class="btn btn-dark" onclick="sendOk('${mode}', '${pageNo}');">${mode=='update'?'수정완료':'등록하기'}</button>
				<button type="reset" class="btn btn-light">다시입력</button>
				<button type="button" class="btn btn-light" onclick="sendCancel();">${mode=='update'?'수정취소':'등록취소'}</button>
				<c:if test="${mode=='update'}">
					<input type="hidden" name="num" value="${dto.num}">
				</c:if>
			</td>
		</tr>
	</table>
</form>
