<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header/header.jsp" />

<c:set var="ctxPath" value="${pageContext.request.contextPath}" />

<style>
:root{
  --bg:#f7f8fb;
  --card:#ffffff;
  --text:#111827;
  --muted:#6b7280;
  --line:#e5e7eb;
  --brand:#2563eb;
  --brand-2:#1d4ed8;
  --shadow: 0 8px 24px rgba(17, 24, 39, .06);
  --radius: 14px;
}
body{ background:var(--bg); }
.dashboard{
  max-width: 1280px;
  margin: 24px auto 64px;
  padding: 0 16px;
  font-family: 'Pretendard','맑은 고딕', system-ui, -apple-system, sans-serif;
  color: var(--text);
}
.grid-3{
  display: grid;
  grid-template-columns: 320px 1fr 420px;
  gap: 24px;
  align-items: start;
}

/* 카드 */
.card{
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
.card-bd{ padding: 18px; }

/* 왼쪽 카드(프로필/시계) */
.left-card {
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--card);
  padding: 16px 18px;
  margin-bottom: 18px;
  box-shadow: var(--shadow);
}

.profile-box {
  display: grid;
  grid-template-columns: 84px 1fr;
  column-gap: 14px;
  align-items: center;
}
.profile-avatar {
  width: 84px; height: 84px; border-radius: 50%;
  border: 3px solid #cfe0ff;
  display:flex; align-items:center; justify-content:center;
  font-size: 44px; color:#2b6be6; background: linear-gradient(180deg,#f7fbff,#eef5ff);
}
.profile-name-row {
  display:flex; align-items:baseline; gap: 8px;
  font-size: 22px; font-weight: 800;
}
.profile-name-row .suffix { font-size: 18px; font-weight: 600; color:#222; }

.inout-row {
  display:grid; grid-template-columns: 1fr 40px 1fr;
  align-items: start; margin-top: 14px;
}
.inout-col .label { font-size: 14px; color:#374151; margin-bottom: 2px; }
.inout-col .time  { font-size: 14px; color:#111827; }
.inout-arrow { display:flex; align-items:center; justify-content:center; font-size: 20px; color:#111; }

/* 시계 + 버튼 */
.clock-date {
  text-align:center; font-size: 20px; color:#111827; font-weight: 700; margin-bottom: 8px;
}
.clock-time {
  text-align:center; font-size: 48px; font-weight: 900; letter-spacing:.02em; margin-bottom: 6px;
}
.action-row { display:flex; justify-content:center; gap: 14px; margin-top: 12px; }
.btn-solid {
  min-width: 96px;
  background: linear-gradient(180deg,#3b82f6,#2563eb);
  color:#fff; border:0;
  padding: 10px 18px; border-radius: 10px; font-weight: 800; font-size: 18px;
  box-shadow: 0 6px 16px rgba(37,99,235,.25);
  cursor:pointer; transition: transform .06s ease, filter .2s ease;
}
.btn-solid.secondary { background: linear-gradient(180deg,#3358e6,#1d4ed8); }
.btn-solid:hover { filter: brightness(.97); }
.btn-solid:active { transform: translateY(1px); }

/* 공지/일정 */
.list{ list-style:none; padding:0; margin:0; }
.list li{ padding:12px 0; border-bottom:1px solid var(--line); }
.list li:last-child{ border-bottom:none; }
.item-title{ font-weight:700; }
.notice-area{
  min-height: 420px;
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  padding: 18px;
  box-shadow: var(--shadow);
}

/* 날씨 카드 */
.weather {
  display:flex; align-items:center; gap:14px;
}
.weather .emoji { font-size:36px; filter: drop-shadow(0 2px 2px rgba(0,0,0,.06)); }
.weather .temp { font-weight:800; font-size:18px; }

/* 반응형 */
@media (max-width: 1200px){ .grid-3{ grid-template-columns: 300px 1fr 360px; } }
@media (max-width: 980px){ .grid-3{ grid-template-columns: 1fr; } }

.title-link{
  color: var(--text);
  text-decoration: none;
  display: inline-block;
  cursor: pointer;
}
.title-link:visited,
.title-link:hover,
.title-link:active,
.title-link:focus{
  color: var(--text);
  text-decoration: none;
}

/* 내 결재 대기 문서 리스트 */
.doc-list{ list-style:none; margin:0; padding:0; }
.doc-list li{ padding:12px 0; border-bottom:1px solid var(--line); }
.doc-list li:last-child{ border-bottom:none; }

.doc-link{
  display:block;
  font-weight:700;
  color:var(--text);
  text-decoration:none;
  cursor:pointer;
}
.doc-link:link,
.doc-link:visited,
.doc-link:hover,
.doc-link:active,
.doc-link:focus{
  color:var(--text);
  text-decoration:none;
  outline:none;
}

.doc-meta{ font-size:12px; color:var(--muted); margin-top:2px; }
.doc-list li:hover{ background:#fafafa; }




/* 오늘 일정 리스트 */
.today-events {
  list-style:none;
  margin:0;
  padding:0;
  max-height: 220px; /* 일정 많으면 스크롤 */
  overflow-y:auto;
}
.today-events li {
  padding:10px 0;
  border-bottom:1px solid var(--line);
}
.today-events li:last-child {
  border-bottom:none;
}

.event-link {
  display:flex;
  flex-direction:column;
  text-decoration:none;
  color:var(--text);
  transition: background .15s ease;
  padding:6px 4px;
  border-radius:8px;
}
.event-link:hover {
  background:#f9fafb;
}

.event-time {
  font-size:12px;
  color:var(--muted);
  margin-bottom:2px;
}
.event-title {
  font-size:14px;
  font-weight:600;
}



</style>

<div class="dashboard">
  <div class="grid-3">

    <!-- 좌측: 프로필 / 시계·출퇴근 / 날씨 -->
    <aside>
      <!-- 프로필 카드 -->
      <div class="left-card">
        <div class="profile-box">
          <div class="profile-avatar">👤</div>
          <div>
            <div class="profile-name-row">
              <span><c:out value="${userName}"/></span>
              <c:out value="${gradeName}"/>님
            </div>
            <div class="inout-row">
              <div class="inout-col">
                <div class="label">출근 시간</div>
                <div class="time"><c:out value="${empty startTimeStr ? '-' : startTimeStr}"/></div>
              </div>
              <div class="inout-arrow">→</div>
              <div class="inout-col" style="text-align:right;">
                <div class="label">퇴근 시간</div>
                <div class="time"><c:out value="${empty endTimeStr ? '-' : endTimeStr}"/></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 시계 + 버튼 카드 -->
      <div class="left-card">
        <div class="clock-date" id="nowDate">--</div>
        <div class="clock-time" id="nowTime">--:--:--</div>

        <div class="action-row">
          <form action="${ctxPath}/member/startWork" method="post">
            <button type="submit" class="btn-solid">출근</button>
          </form>
          <form action="${ctxPath}/member/endWork" method="post">
            <button type="submit" class="btn-solid secondary">퇴근</button>
          </form>
        </div>
      </div>

      <div class="card mt-3">
        <div class="card-bd" style="display:flex; align-items:center; gap:14px;">
          <div id="weatherIcon" style="font-size:36px;">☀️</div>
          <div>
            <div class="item-title" id="weatherDesc">날씨 로딩중...</div>
            <div style="font-weight:800; font-size:18px;" id="weatherTemp">-- °C</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 가운데: 공지 (사내+부서 합산 최신 5개) -->
    <section>
      <div class="notice-area">
        <div class="item-title" style="margin-bottom:12px;">
          <a class="title-link"
             href="${ctxPath}/board/list?fkBoardCategorySeq=0">
            📢 공지사항
          </a>
        </div>

        <c:choose>
          <c:when test="${empty noticeList}">
            <div style="color:#6b7280">표시할 공지가 없습니다.</div>
          </c:when>
          <c:otherwise>
            <ul class="doc-list">
              <c:forEach var="n" items="${noticeList}">
                <li>
                  <a class="doc-link" href="${ctxPath}/board/view?boardSeq=${n.boardSeq}">
                    <c:choose>
                      <c:when test="${n.fkBoardTypeSeq == 0}">[사내게시판]</c:when>
                      <c:when test="${n.fkBoardTypeSeq == 1}">[부서게시판]</c:when>
                      <c:otherwise>[게시판]</c:otherwise>
                    </c:choose>
                    <c:out value="${n.boardTitle}"/>
                  </a>
                  <div class="doc-meta">
                    <c:out value="${n.memberName}"/> ·
                    <fmt:formatDate value="${n.boardRegdate}" pattern="yyyy-MM-dd"/>
                    <span style="margin-left:8px;">조회 <c:out value="${n.boardReadcount}"/></span>
                  </div>
                </li>
              </c:forEach>
            </ul>

            <div style="text-align:right; margin-top:10px;">
              <!-- 더보기: 공지 카테고리(0) 전체 보러가기 -->
              <a class="title-link" href="${ctxPath}/board/list?fkBoardCategorySeq=0">더보기 »</a>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </section>

    <!-- 우측: 오늘 일정 + 결재 대기 -->
	
	<aside>
		
		<div class="card">
		  <div class="card-bd">
		    <div class="item-title" style="margin-bottom:12px; font-size:16px; font-weight:700;">
		      📌 오늘 예정된 사내 일정
		    </div>

		    <c:choose>
		      <c:when test="${empty todayCompanyEvents}">
		        <div style="color:#6b7280; font-size:14px; padding:8px 0;">
		          오늘 등록된 사내 일정이 없습니다.
		        </div>
		      </c:when>
		      <c:otherwise>
		        <ul class="list today-events">
		          <c:forEach var="ev" items="${todayCompanyEvents}">
		            <li>
		              <a class="event-link" href="${ctxPath}/Calendar/detailCalendar?calendarSeq=${ev.calendarSeq}">
		                <span class="event-time">
		                  ⏰ <c:out value="${fn:substring(ev.calendarStart,11,16)}"/>~
		                      <c:out value="${fn:substring(ev.calendarEnd,11,16)}"/>
		                </span>
		                <span class="event-title">
		                  <c:out value="${ev.calendarName}"/>
		                </span>
		              </a>
		            </li>
		          </c:forEach>
		        </ul>
		      </c:otherwise>
		    </c:choose>
		  </div>
		</div>

      <div class="card" style="margin-top:18px;">
        <div class="card-bd">
          <a class="item-title title-link" href="${ctxPath}/sign/main" style="margin-bottom:10px;">
            📝 내 결재 대기 문서
          </a>
          <c:choose>
            <c:when test="${empty pendingLines}">
              <div style="color:#6b7280">결재 대기 중인 문서가 없습니다.</div>
            </c:when>
            <c:otherwise>
				<ul class="doc-list">
				  <c:forEach var="dl" items="${pendingLines}">
				    <li>
				      <a class="doc-link" href="${ctxPath}/sign/view/${dl.draft.draftSeq}">
				        <c:if test="${dl.draft.isEmergency == 1}">
				          🚨
				        </c:if>
				        [<c:out value="${dl.draft.draftType != null ? dl.draft.draftType.draftTypeName : '문서'}"/>]
				        <c:out value="${dl.draft.draftTitle}"/>
				      </a>
				      <div class="doc-meta">
				        <c:out value="${dl.draft.member.memberName}"/> ·
				        <c:out value="${fn:substring(dl.draft.draftRegdate,0,10)}"/>
				      </div>
				    </li>
				  </c:forEach>
				</ul>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </aside>
  </div>
</div>

<script>
(function clock(){
  var now = new Date();
  var pad = function(n){ return n.toString().padStart(2,'0'); };
  var yyyy = now.getFullYear();
  var mm   = pad(now.getMonth()+1);
  var dd   = pad(now.getDate());
  var days = ['일','월','화','수','목','금','토'];
  var HH   = pad(now.getHours());
  var MI   = pad(now.getMinutes());
  var SS   = pad(now.getSeconds());
  var dateEl = document.getElementById('nowDate');
  var timeEl = document.getElementById('nowTime');
  if (dateEl) dateEl.textContent = yyyy + '-' + mm + '-' + dd + '(' + days[now.getDay()] + ')';
  if (timeEl) timeEl.textContent = HH + ':' + MI + ':' + SS;
  setTimeout(clock, 1000);
})();

// ===== 날씨 (템플릿리터럴 금지) =====
(function () {
  var API_KEY = "0e815844f78602fe9f69a70317f59284"; // 본인 키
  var CITY    = "Seoul";

  var url = "https://api.openweathermap.org/data/2.5/weather"
          + "?q=" + encodeURIComponent(CITY)
          + "&appid=" + API_KEY
          + "&units=metric&lang=kr";

  fetch(url)
    .then(function(r){
      if(!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then(function(d){
      var desc = (d.weather && d.weather[0] && d.weather[0].description) || "정보 없음";
      var icon = (d.weather && d.weather[0] && d.weather[0].icon) || "01d";
      var temp = Math.round((d.main && d.main.temp) || 0);

      document.getElementById('weatherDesc').textContent = desc;
      document.getElementById('weatherTemp').textContent = temp + " °C";
      document.getElementById('weatherIcon').innerHTML =
        '<img src="https://openweathermap.org/img/wn/' + icon + '@2x.png" alt="" width="40" height="40">';
    })
    .catch(function(err){
      console.error('Weather fetch failed:', err);
      document.getElementById('weatherDesc').textContent = "날씨 불러오기 실패";
    });
})();
</script>

<jsp:include page="footer/footer.jsp" />