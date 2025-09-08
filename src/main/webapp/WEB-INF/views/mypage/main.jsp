<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../header/header.jsp" />

<style>
:root{
  --bg:#f7f8fb; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --card:#fff; --brand:#2563eb;
}
.mypage-wrap{ max-width:1100px; margin:40px auto 60px; color:var(--text); font-family:'Pretendard','맑은 고딕',sans-serif; }
.mypage-grid{ display:grid; grid-template-columns: 220px 1fr; gap:22px; }

.aside{
  background:var(--card); border:1px solid var(--line); border-radius:12px; padding:14px;
  box-shadow:0 2px 10px rgba(0,0,0,.04);
}
.aside a{ display:block; padding:10px 12px; border-radius:8px; text-decoration:none; color:#1f2937; }
.aside a:hover{ background:#f3f4f6; }

.card{
  background:var(--card); border:1px solid var(--line); border-radius:12px; box-shadow:0 2px 12px rgba(0,0,0,.06);
  padding:24px;
}
.profile{
  display:grid; grid-template-columns: 260px 1fr; gap:28px; align-items:start;
}
.avatar{
  border:2px solid #9cb7ff; border-radius:12px; padding:18px; text-align:center;
}
.avatar .circle{
  width:120px; height:120px; border:3px solid #3b82f6; border-radius:50%; margin:8px auto 14px;
  display:flex; align-items:center; justify-content:center; font-size:46px; color:#3b82f6;
}
.avatar .name{ font-size:20px; font-weight:800; margin-top:8px; }
.avatar .meta{ margin-top:8px; color:var(--muted); line-height:1.5; font-size:13px; }

.form-row{ display:grid; grid-template-columns: 120px 1fr; gap:10px; align-items:center; margin-bottom:12px; }
.input, .select{
  width:100%; border:1px solid var(--line); border-radius:10px; padding:8px 10px; font-size:14px; background:#fff;
}
.note{ color:var(--muted); font-size:13px; margin-left:8px; }
.actions{ margin-top:16px; }
.btn{
  display:inline-block; background:var(--brand); color:#fff; border:0; padding:9px 16px; border-radius:10px;
  font-weight:700; cursor:pointer; box-shadow:0 2px 8px rgba(37,99,235,.25);
}
.btn:hover{ filter:brightness(.96); }
.btn-outline{ background:#fff; color:#1f2937; border:1px solid var(--line); margin-left:8px; }
.help{ margin-top:8px; color:var(--muted); font-size:13px; }

@media (max-width: 900px){
  .profile{ grid-template-columns: 1fr; }
}
</style>

<div class="mypage-wrap">

  <div class="mypage-grid">

    <!-- 좌측 사이드 -->
    <aside class="aside">
      <a href="${ctxPath}/mypage">내 정보 수정하기</a>
      <a href="${ctxPath}/mypage/password">비밀번호 바꾸기</a>
    </aside>

    <!-- 우측 컨텐츠 -->
    <section class="card">

      <!-- 상단: 프로필 박스 + 수정 폼 -->
      <div class="profile">

        <!-- 프로필 카드 -->
        <div class="avatar">
          <div class="circle">👤</div>
          <div class="name">
            <c:out value="${profile.name}"/>
            <span style="font-weight:600; color:#4b5563;">
              <c:out value="${empty profile.gradeName ? '사원' : profile.gradeName}"/>
            </span>
          </div>
          <div class="meta">
            입사일 :
            <c:choose>
              <c:when test="${not empty profile.hiredate}">
                <c:out value="${profile.hiredate}"/>
              </c:when>
              <c:otherwise>-</c:otherwise>
            </c:choose><br/>
            부서 :
            <c:out value="${empty profile.deptName ? '-' : profile.deptName}"/><br/>
            사번 :
            <c:out value="${profile.memberSeq}"/>
          </div>
        </div>

        <!-- 정보 수정 폼 -->
        <div>
          <form action="${ctxPath}/mypage/update" method="post">
            <input type="hidden" name="memberSeq" value="${profile.memberSeq}"/>

            <div class="form-row">
              <label>이름</label>
              <input type="text" name="name" class="input" value="${profile.name}" required maxlength="30"/>
            </div>

            <div class="form-row">
              <label>번호</label>
              <input type="text" name="mobile" class="input" value="${profile.mobile}"
                     placeholder="010-1234-5678" required
                     pattern="^\d{2,3}-\d{3,4}-\d{4}$"/>
            </div>
            <div class="form-row">
			  <label>이메일</label>
			  <!-- readonly: 수정은 막고, 제출은 되게 -->
			  <input type="email" name="email" class="input"
			         value="${profile.email}" readonly maxlength="50"
			         title="이메일은 보안 정책상 변경할 수 없습니다."/>
			</div>
            <div class="form-row">
              <label>부서</label>
              <div style="display:flex; align-items:center; gap:10px;">
                <select name="deptSeq" class="select" disabled>
                  <c:forEach var="d" items="${departments}">
                    <option value="${d.departmentSeq}"
                      <c:if test="${profile.deptSeq == d.departmentSeq}">selected</c:if>>
                      ${d.departmentName}
                    </option>
                  </c:forEach>
                </select>
                <span class="note">인사팀 문의</span>
              </div>
            </div>

            <div class="form-row">
              <label>직급</label>
              <div style="display:flex; align-items:center; gap:10px;">
                <input type="text" class="input" value="${empty profile.gradeName ? '직급' : profile.gradeName}" disabled/>
              </div>
            </div>

            <div class="actions">
              <button type="submit" class="btn">수정</button>
              <a href="${ctxPath}/mypage" class="btn btn-outline">새로고침</a>
            </div>

            <c:if test="${not empty message}">
              <div class="help">${message}</div>
            </c:if>
          </form>
        </div>

      </div><!-- /.profile -->
    </section>

  </div><!-- /.mypage-grid -->

</div>

<jsp:include page="../footer/footer.jsp" />
