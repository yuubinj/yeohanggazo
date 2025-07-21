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
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/dist/css/paginate.css"
	type="text/css">
<style type="text/css">

canvas {
  transition: 2s;
  pointer-events: none;
}

#rouletteBtn {
  background: #c4dff3;
  margin-top: 1rem;
  padding: .8rem 1.8rem;
  border: none;
  font-size: 1.5rem;
  font-weight: bold;
  border-radius: 5px;
  transition: .2s;
  cursor: pointer;
    color: #fff;
}

#rouletteBtn:hover {
  background: #91c4e8;
}

#rouletteBtn:active {
  background: #444;
  color: #fff;
}
 
#roulett-div {
  width: 380px;
  display: flex;
  align-items: center;
  flex-direction: column;
  position: relative;
}

#roulett-div::before {
  content: "";
  position: absolute;
  width: 10px;
  height: 50px;
  border-radius: 5px;
  background: #000;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 22;
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
						<h3 class="section-title">
							<i class="bi bi-app"></i> 룰렛 돌리기
						</h3>
					</div>

					<div class="body-main">
						<div id="roulett-div">
						  <canvas width="380" height='380' style="font-size:10px;"></canvas>  
						  <button id="rouletteBtn" onclick="rotate()">룰렛 돌리기</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</main>
	
<script type="text/javascript">
const $c = document.querySelector("canvas");
const ctx = $c.getContext(`2d`);

const product = [
  "서울",
  "인천",
  "대전",
  "광주",
  "대구",
  "부산",
  "울산",
  "경기도",
  "강원도",
  "충청북도",
  "충청남도",
  "경상북도",
  "경상남도",
  "전라남도",
  "전라북도",
  "제주도",
  "세종",
];

const colors = [
	"#f2f8fd",
	"#e5eff9",
	"#c4dff3",
	"#91c4e8",
	"#56a5da",
	"#308ac7",
	"#216da8",
	"#1c5888",
	"#1b4b71",
	"#1b405f",
	"#12293f"
];

const newMake = () => {
  const [cw, ch] = [$c.width / 2, $c.height / 2];
  const arc = (2 * Math.PI) / product.length;

  for (let i = 0; i < product.length; i++) {
    ctx.beginPath();
    ctx.fillStyle = colors[i % colors.length];
    ctx.moveTo(cw, ch);
    ctx.arc(cw, ch, cw - 2, arc * i - Math.PI / 2, arc * (i + 1) - Math.PI / 2);
    ctx.fill();
    ctx.closePath();
  }

  ctx.strokeStyle = "#000";
  ctx.lineWidth = 1;

  ctx.fillStyle = "#000";
  ctx.font = "12px Pretendard";
  ctx.textAlign = "center";

  for (let i = 0; i < product.length; i++) {
    const angle = arc * i + arc / 2 - Math.PI / 2;

    ctx.save();

    ctx.translate(
      cw + Math.cos(angle) * (cw - 50),
      ch + Math.sin(angle) * (ch - 50)
    );

    ctx.rotate(angle + Math.PI / 2);

    product[i].split(" ").forEach((text, j) => {
      ctx.fillText(text, 0, 30 * j);
    });

    ctx.restore();
  }

  ctx.fillStyle = "#000";
  ctx.beginPath();
  ctx.moveTo(cw, ch);
  ctx.arc(cw, ch, 3, 0, Math.PI * 2);
  ctx.fill();
  ctx.closePath();
};

const rotate = () => {
  $c.style.transform = `initial`;
  $c.style.transition = `initial`;

  setTimeout(() => {
    const ran = Math.floor(Math.random() * product.length);

    const arc = 360 / product.length;
    const rotate = (360 - arc * (ran + 1) + 3600) + (arc / 3);

    $c.style.transform = `rotate(\${rotate}deg)`;
    $c.style.transition = `2s`;

    setTimeout(
      () => alert(`이번 여행지로 '\${product[ran]}' 어떠신가요?`),
      2000
    );
  }, 1);
};

newMake();
</script>	

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</footer>

<jsp:include page="/WEB-INF/views/layout/footerResources.jsp" />

</body>
</html>