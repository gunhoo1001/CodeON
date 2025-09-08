<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../header/header.jsp" />

<style>
:root{
  --bg:#f7f8fb; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --card:#fff; --brand:#2563eb;
}
.address-container{ max-width: 1100px; margin: 40px auto 60px; color: var(--text); font-family: 'Pretendard','맑은 고딕',sans-serif; }
.toolbar{
  display:flex; gap:10px; align-items:center; padding:12px; background:var(--card);
  border:1px solid var(--line); border-radius:12px; box-shadow:0 2px 10px rgba(0,0,0,.05);
}
.select, .input, .btn{
  border:1px solid var(--line); border-radius:10px; padding:8px 12px; font-size:14px; background:#fff;
}
.select{ min-width:130px; }
.input{ flex:1; }
.btn{ background:var(--brand); color:#fff; font-weight:700; cursor:pointer; }
.btn:hover{ filter:brightness(.96); }

.card{
  margin-top:14px; background:var(--card); border:1px solid var(--line); border-radius:12px;
  box-shadow:0 2px 12px rgba(0,0,0,.05); overflow:hidden;
}

.table{ width:100%; border-collapse:collapse; }
.table th, .table td{ padding:12px 14px; border-bottom:1px solid var(--line); text-align:center; font-size:14px; }
.table th{ background:#f9fafb; font-weight:800; color:#374151; }
.table tr:hover td{ background:#fafafa; }

.empty{
  padding:48px 20px; text-align:center; color:var(--muted);
}

.pager{
  display:flex; justify-content:center; gap:6px; margin-top:14px;
}
.pager a, .pager span{
  display:inline-block; min-width:36px; padding:8px 10px; border:1px solid var(--line); border-radius:10px; text-align:center; font-size:14px; background:#fff;
}
.pager .on{ background:#eef2ff; border-color:#c7d2fe; font-weight:800; }
.pager a:hover{ background:#f3f4f6; }
@media (max-width: 640px){
  .toolbar{ flex-wrap:wrap; }
  .table th:nth-child(4), .table td:nth-child(4){ display:none; } /* 모바일에서 전화번호 숨김 예시 */
}
</style>

<div class="address-container">

  <!-- 검색 툴바 -->
  <form class="toolbar" action="${ctxPath}/address" method="get">
    <select name="dept" class="select" aria-label="부서 선택">
	  <option value="">부서 선택</option>
	  <c:forEach var="d" items="${departments}">
	    <option value="${d.departmentSeq}"
	            <c:if test="${selectedDept != null and selectedDept == d.departmentSeq}">selected</c:if>>
	      ${d.departmentName}
	    </option>
	  </c:forEach>
	</select>

    <input type="text" name="q" value="${keyword}" class="input" placeholder="이름/이메일/전화 검색" />
    <button type="submit" class="btn">검색</button>
  </form>

  <!-- 목록 카드 -->
  <div class="card">
    <table class="table">
      <thead>
        <tr>
          <th style="width:18%;">부서</th>
          <th style="width:18%;">이름</th>
          <th style="width:34%;">이메일</th>
          <th style="width:20%;">전화</th>
          <th style="width:10%;">직급</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${not empty items}">
            <c:forEach var="it" items="${items}">
              <tr>
                <td>${it.deptName}</td>
                <td>${it.name}</td>
                <td><a href="mailto:${it.email}">${it.email}</a></td>
                <td>
                  <c:choose>
                    <c:when test="${not empty it.mobile}">${it.mobile}</c:when>
                    <c:otherwise>-</c:otherwise>
                  </c:choose>
                </td>
                <td>
			      <c:choose>
			        <c:when test="${not empty it.gradeName}">${it.gradeName}</c:when>
			        <c:otherwise>-</c:otherwise>
			      </c:choose>
			    </td> 
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="5" class="empty">검색 결과가 없습니다.</td></tr>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

  <!-- 페이지네이션 -->
  <c:if test="${totalPages > 1}">
    <div class="pager">
      <c:set var="prev" value="${page - 1}" />
      <c:set var="next" value="${page + 1}" />

      <c:if test="${page > 1}">
        <a href="${ctxPath}/address?dept=${selectedDept}&q=${keyword}&page=1">«</a>
        <a href="${ctxPath}/address?dept=${selectedDept}&q=${keyword}&page=${prev}">‹</a>
      </c:if>

      <!-- 가운데 5페이지 창 -->
      <c:set var="start" value="${page - 2 < 1 ? 1 : page - 2}" />
      <c:set var="end"   value="${start + 4 > totalPages ? totalPages : start + 4}" />
      <c:forEach var="p" begin="${start}" end="${end}">
        <c:choose>
          <c:when test="${p == page}">
            <span class="on">${p}</span>
          </c:when>
          <c:otherwise>
            <a href="${ctxPath}/address?dept=${selectedDept}&q=${keyword}&page=${p}">${p}</a>
          </c:otherwise>
        </c:choose>
      </c:forEach>

      <c:if test="${page < totalPages}">
        <a href="${ctxPath}/address?dept=${selectedDept}&q=${keyword}&page=${next}">›</a>
        <a href="${ctxPath}/address?dept=${selectedDept}&q=${keyword}&page=${totalPages}">»</a>
      </c:if>
    </div>
  </c:if>

</div>

<jsp:include page="../footer/footer.jsp" />
