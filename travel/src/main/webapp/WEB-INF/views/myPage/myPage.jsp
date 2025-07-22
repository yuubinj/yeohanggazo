<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Spring</title>
<jsp:include page="/WEB-INF/views/myPage/layout/headerResources.jsp"/>
<style type="text/css">
.img-person {
	display: inline-block;
	width: 100px;
	height: 100px;
	background-size: cover;
	background-position: center;
	border-radius: 50%;
}
.body-title h3 {
    min-width: 200px;
}
  .carousel-control-prev-icon,
  .carousel-control-next-icon {
    background-color: rgba(0, 0, 0, 0.1); /* 연한 회색 */
    border-radius: 50%;
    padding: 20px;
    transition: background-color 0.3s;
  }

  .carousel-control-prev-icon:hover,
  .carousel-control-next-icon:hover {
    background-color: rgba(0, 0, 0, 0.3);
  }

  .card {
    border: none;
    border-radius: 0.75rem;
    transition: box-shadow 0.3s;
  }
.carousel-schedule-row {
  margin-top: 25px;
}
  .card:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }

  .card-body {
    padding: 1rem;
  }

  .card-title a {
    font-weight: bold;
  }

  .carousel-inner {
    padding-bottom: 1rem;
  }
</style>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>

<jsp:include page="/WEB-INF/views/myPage/layout/header.jsp"/>

<main>
	<jsp:include page="/WEB-INF/views/myPage/layout/left.jsp"/>
		<div class="wrapper">
			<div class="body-container">

				<div class="body-title row col-10">
					<div class="body-title col">
						<h3>
							<i class="fa-solid fa-house"></i> 마이페이지
						</h3>
					</div>
					<div class="row">
						<div class="col-2 align-self-center ps-4 ">
							<c:choose>
								<c:when test="${not empty sessionScope.member.avatar}">
									<span class="img-person"
										style="background-image: url('${pageContext.request.contextPath}/uploads/member/${sessionScope.member.avatar}');"></span>
								</c:when>
								<c:otherwise>
									<span class="img-person"
										style="background-image: url('${pageContext.request.contextPath}/dist/images/avatar.png');"></span>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="col-auto align-self-top ps-4">
							<div class="card border-0">
								<div class="card-body p-3">
									<h5 class="card-title mb-3">
										<i class="fa-solid fa-address-card me-2"
											style="color: rgb(13, 110, 253);"></i>기본 정보
									</h5>
									<div class="small mb-2 d-flex ">이름:
										${sessionScope.member.userName}</div>
									<div class="small mb-2 d-flex ">이메일:
										${sessionScope.member.email}</div>
									<div class="small mb-2 d-flex ">전화번호:
										${sessionScope.member.tel}</div>
									<div class="small mb-2 d-flex ">가입일:
										${sessionScope.member.register_date}</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="body-main">
					<div class="body-title row col-10">
						<div class="col">
							<div class="mb-4">
								<h3>
									<i class="fa-solid fa-calendar-days	"></i> 나의 일정
								</h3>
							</div>
							<div>
								<div class="mt-5">
									<!-- 여기로 공간 확보 -->
									<div id="scheduleCarousel" class="carousel slide"
										data-bs-ride="carousel" data-bs-interval="7000">
										<div class="carousel-inner">
											<c:choose>
												<c:when test="${empty listSchedule}">
													<div class="text-center my-4 text-muted"
														style="font-size: 1.2rem;">오늘 이후로 일정이 없습니다.</div>
												</c:when>
												<c:otherwise>
													<c:forEach var="i" begin="0"
														end="${listSchedule.size() - 1}" step="5"
														varStatus="groupStatus">
														<div
															class="carousel-item ${groupStatus.index == 0 ? 'active' : ''}">
															<div class="row g-4 px-3 justify-content-center">
																<c:forEach var="j" begin="0" end="4">
																	<c:if test="${i + j < listSchedule.size()}">
																		<div class="card h-100 shadow-sm mx-2"
																			style="max-width: 200px;">
																			<div class="card-body">
																				<h6 class="card-title text-truncate">
																					제목: <a
																						href="${pageContext.request.contextPath}/myPage/schedule/main"
																						class="text-decoration-none text-dark">
																						${listSchedule[i + j].subject} </a>
																				</h6>
																				<p class="card-text small mb-1 text-secondary">
																					<strong>시작일:</strong> ${listSchedule[i + j].do_date}
																				</p>
																				<p class="card-text small mb-1 text-secondary">
																					<strong>종료일:</strong> ${listSchedule[i + j].end_date}
																				</p>
																				<p class="card-text small text-muted">
																					<strong>내용:</strong> ${listSchedule[i + j].memo}
																				</p>
																			</div>
																		</div>
																	</c:if>
																</c:forEach>
															</div>
														</div>
													</c:forEach>
												</c:otherwise>
											</c:choose>
										</div>

										<!-- 캐러셀 버튼 -->
										<button class="carousel-control-prev" type="button"
											data-bs-target="#scheduleCarousel" data-bs-slide="prev">
											<span class="carousel-control-prev-icon" aria-hidden="true"></span>
											<span class="visually-hidden">이전</span>
										</button>
										<button class="carousel-control-next" type="button"
											data-bs-target="#scheduleCarousel" data-bs-slide="next">
											<span class="carousel-control-next-icon" aria-hidden="true"></span>
											<span class="visually-hidden">다음</span>
										</button>
									</div>
								</div>
								<div class="pt-2 text-end">
									<a
										href="${pageContext.request.contextPath}/myPage/schedule/main"
										class="text-reset">더보기</a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="body-main">
					<div class="body-title row col-10">
						<div class="col">
							<div>
								<h3>
									<i class="fa-solid fa-star"></i> 즐겨찾기 목록
								</h3>
							</div>
							<div>
								<div class="row px-2 mt-3">
									<c:choose>
										<c:when test="${empty listScrap}">
											<div class="text-center text-muted py-3">등록된 즐겨찾기가
												없습니다.</div>
										</c:when>
										<c:otherwise>
											<c:forEach var="dto" items="${listScrap}">
												<div class="col-6 col-md-4 col-lg-3 mb-3">
													<div class="card h-100 shadow-sm">
														<div class="ratio ratio-4x3">
															<img src="${dto.scrapImg}" class="card-img-top"
																alt="이미지 없음!">
														</div>
														<div class="card-body p-2">
															<h6 class="card-title text-truncate mb-0">
																${dto.scrapName}</h6>
														</div>
													</div>
												</div>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</div>
								<div class="pt-2 text-end">
									<a href="${pageContext.request.contextPath}/myPage/scrap/main"
										class="text-reset">더보기</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</main>
<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp"/>
</body>
</html>