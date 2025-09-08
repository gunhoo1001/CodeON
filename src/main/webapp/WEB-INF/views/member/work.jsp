<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../header/header.jsp" />
<style>
:root{
  --bg:#f7f8fb;
  --card:#ffffff;
  --text:#111827;
  --muted:#6b7280;
  --line:#e5e7eb;
  --brand:#2563eb;
}

.attendance-container {
  margin: 40px auto 56px;
  max-width: 1200px;
  font-family: 'Pretendard','맑은 고딕', system-ui, -apple-system, sans-serif;
  color: var(--text);
}

/* ── 월 이동 박스 ───────────────────────── */
.month-box-wrapper { text-align: center; }
.month-box {
  display: inline-flex; align-items: center; justify-content: center;
  gap: 14px;
  border: 1px solid var(--line);
  padding: 10px 18px;
  min-width: 220px;
  border-radius: 12px;
  background: var(--card);
  box-shadow: 0 2px 10px rgba(17,24,39,.06);
  margin-bottom: 28px;
}
.month-box > span{
  font-weight: 700; letter-spacing: .015em;
}
.month-btn{
  background: #fff;
  border: 1px solid var(--line);
  padding: 6px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: transform .08s ease, background .2s ease, box-shadow .2s ease;
}
.month-btn:hover{ background:#f3f4f6; box-shadow: 0 1px 5px rgba(0,0,0,.05); }
.month-btn:active{ transform: translateY(1px); }

/* ── 레이아웃(좌측 출퇴근 / 우측) ───────── */
.attendance-grid{
  display: grid;
  grid-template-columns: 320px 1fr;
  column-gap: 28px;
  align-items: start;
}

/* ── 좌측 출퇴근 박스 ──────────────────── */
.work-box{
  border: 1px solid var(--line);
  background: var(--card);
  padding: 18px 20px;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,.06);
  /* 추가: 내부 요소 중앙정렬 */
  text-align: center;
}

.work-box h3{
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--muted);
}

.work-label{
  font-size: 12px; 
  color: var(--muted);
  display: block;        /* 라벨도 블록화 */
  text-align: center;    /* 라벨 텍스트 중앙 */
}

.work-time{
  display:block; 
  margin: 6px 0 12px;
  font-size: 22px; 
  font-weight: 700; 
  letter-spacing:.01em;
}

.work-btn{
  background: var(--brand); 
  color:#fff; 
  border:0;
  padding: 8px 14px; 
  margin: 6px 6px 0 6px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(37,99,235,.25);
  transition: transform .08s ease, filter .2s ease;
}
.work-btn:hover{ filter: brightness(0.96); }
.work-btn:active{ transform: translateY(1px); }

/* ── 우측 요약 박스(4개 한 줄 고정) ─────── */
.summary-boxes{
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* ✅ 한 줄 고정 */
  gap: 16px;
  margin: 2px 0 18px;
}
.summary-item{
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.summary-label{
  font-size: 12px; line-height:1.1; color: var(--muted); letter-spacing:.02em;
}
.summary-value{
  margin-top: 8px;
  font-size: 24px; line-height:1.25; font-weight: 800; color: var(--text);
  word-break: keep-all;
}

/* ── 테이블 ────────────────────────────── */
h3 { margin: 14px 0 10px; font-size: 16px; }
table{
  width:100%; border-collapse: collapse; background: var(--card);
  border: 1px solid var(--line);
  box-shadow: 0 2px 12px rgba(0,0,0,.06);
  border-radius: 12px; overflow: hidden;
}
th, td{
  border-bottom: 1px solid var(--line);
  padding: 12px 14px; text-align: center; font-size: 14px;
}
th{ background: #f9fafb; font-weight: 700; color:#374151; }
tbody tr:last-child td{ border-bottom: none; }

/* ── 반응형(태블릿 이하에서는 2열 → 1열) ─── */
@media (max-width: 1024px){
  .attendance-grid{ grid-template-columns: 1fr; row-gap: 18px; }
  .summary-boxes{ grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 560px){
  .summary-boxes{ grid-template-columns: 1fr; }
  .work-time{ font-size: 20px; }
  .summary-value{ font-size: 22px; }
}
</style>

<div class="attendance-container">

  <!-- 상단: 월 이동 박스(페이지 가로 기준 가운데 정렬용 래퍼) -->
  <div class="month-box-wrapper">
    <div class="month-box">
      <!-- 이전 달 / 다음 달: EL 3.0 이상이라면 minusMonths/plusMonths 호출 가능 -->
      <a class="month-btn" href="${ctxPath}/member/work?month=${currentMonth.minusMonths(1)}">&lt;-</a>
      <span>${currentMonth}</span>
      <a class="month-btn" href="${ctxPath}/member/work?month=${currentMonth.plusMonths(1)}">-&gt;</a>
    </div>
  </div>

  <!-- 본문 2열: 좌측 출퇴근 박스 / 우측 요약+테이블 (시작 높이 일치) -->
  <div class="attendance-grid">

    <!-- 좌측: 출퇴근 박스 -->
    <div class="work-box">
      <h3>${todayStr}</h3>
      <div>
	    <span class="work-label">출근시간</span>
	    <span class="work-time">
	        <c:set var="startTime" value="-" />
			<c:forEach var="att" items="${attendanceList}">
			  <c:if test="${att.workDateStr eq todayStr}">
			    <c:set var="startTime" value="${att.startTimeStr != null ? att.startTimeStr : '-'}" />
			  </c:if>
			</c:forEach>
			${startTime}
	    </span>
	</div>
	
	<div>
	    <span class="work-label">퇴근시간</span>
	    <span class="work-time">
	        <c:set var="endTime" value="-" />
	        <c:forEach var="att" items="${attendanceList}">
	            <c:if test="${att.workDate eq todayStr}">
	                <c:set var="endTime" value="${att.endTimeStr != null ? att.endTimeStr : '-'}" />
	            </c:if>
	        </c:forEach>
	        ${endTime}
	    </span>
	</div>


      <!-- 출근/퇴근 버튼: POST 사용 -->
      <form action="${ctxPath}/member/startWork" method="post" style="display:inline;">
        <button type="submit" class="work-btn">출근하기</button>
      </form>
      <form action="${ctxPath}/member/endWork" method="post" style="display:inline;">
        <button type="submit" class="work-btn">퇴근하기</button>
      </form>
    </div>

    <!-- 우측: 요약 + 테이블 -->
    <div class="right-panel">

      <!-- 요약 박스 -->
	 <div class="summary-boxes">
	   <div class="summary-item">
	     <div class="summary-label">한달누적근무시간</div>
	     <div class="summary-value">${summary.totalMinutesStr}</div>
	   </div>
	
	   <div class="summary-item">
	     <div class="summary-label">근무일수</div>
	     <div class="summary-value">${summary.workDays}일</div>
	   </div>
	
	   <div class="summary-item">
		  <div class="summary-label">사용연차 / 잔여연차</div>
		  <div class="summary-value">
		    <c:choose>
		      <c:when test="${not empty leave}">
		        <c:out value="${leave.usedLeave}" /> /
		        <c:out value="${leave.remainingLeave}" />
		      </c:when>
		      <c:otherwise>- / -</c:otherwise>
		    </c:choose>
		  </div>
		</div>

  <div class="summary-item">
    <div class="summary-label">연장근무</div>
    <div class="summary-value">${summary.totalOvertime}분</div>
  </div>
</div>


      <!-- 근무 시간 확인 테이블 -->
      <div>
        <h3>근무 시간 확인</h3>
        <table>
          <thead>
            <tr>
              <th>근무일</th>
              <th>사원명</th>
              <th>사번</th>
              <th>출근 시각</th>
              <th>퇴근 시각</th>
              <th>근무 시간</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="att" items="${attendanceList}">
              <tr>
                <td>${att.workDateStr}</td>
                <td>${userName}</td>
                <td>${att.memberSeq}</td>
                <td>
                  <c:choose>
                    <c:when test="${att.startTimeStr != null}">${att.startTimeStr}</c:when>
                    <c:otherwise>-</c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <c:choose>
                    <c:when test="${att.endTimeStr != null}">${att.endTimeStr}</c:when>
                    <c:otherwise>-</c:otherwise>
                  </c:choose>
                </td>
                <td>
				  <c:choose>
				    <c:when test="${att.workedTimeStr != null}">
				      ${att.workedTimeStr}
				    </c:when>
				    <c:otherwise>-</c:otherwise>
				  </c:choose>
				</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

    </div><!-- /.right-panel -->

  </div><!-- /.attendance-grid -->

</div>

<jsp:include page="../footer/footer.jsp" />
