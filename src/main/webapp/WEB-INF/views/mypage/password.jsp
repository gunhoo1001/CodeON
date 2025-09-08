<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../header/header.jsp" />

<style>
:root{
  --bg:#f7f8fb; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --card:#fff; --brand:#2563eb; --danger:#ef4444;
}
.wrap{ max-width:720px; margin:40px auto 60px; color:var(--text); font-family:'Pretendard','맑은 고딕',sans-serif; }
.card{ background:var(--card); border:1px solid var(--line); border-radius:12px; box-shadow:0 2px 12px rgba(0,0,0,.06); padding:24px; }
.h1{ font-size:20px; font-weight:800; margin-bottom:14px; }
.form-row{ display:grid; grid-template-columns: 160px 1fr; gap:12px; align-items:center; margin-bottom:14px; }
.input{ width:100%; border:1px solid var(--line); border-radius:10px; padding:10px 12px; font-size:14px; background:#fff; }
.actions{ margin-top:8px; }
.btn{ display:inline-block; background:var(--brand); color:#fff; border:0; padding:10px 16px; border-radius:10px; font-weight:700; cursor:pointer; box-shadow:0 2px 8px rgba(37,99,235,.25); }
.btn-outline{ background:#fff; color:#1f2937; border:1px solid var(--line); margin-left:8px; }
.help{ margin-top:8px; font-size:13px; color:var(--muted); }
.error{ color:var(--danger); }
</style>

<div class="wrap">
  <div class="card">
    <div class="h1">비밀번호 변경</div>

    <form id="pwdForm" action="${ctxPath}/mypage/password" method="post" novalidate>
      <!-- Spring Security 사용 중이면 CSRF 토큰 추가
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      -->

      <div class="form-row">
        <label>현재 비밀번호</label>
        <input type="password" name="currentPwd" class="input" required minlength="4" maxlength="30" />
      </div>

      <div class="form-row">
        <label>새 비밀번호</label>
        <input type="password" name="newPwd" class="input" required minlength="8" maxlength="30"
               placeholder="8~30자, 영문/숫자 조합 권장" />
      </div>

      <div class="form-row">
        <label>새 비밀번호 확인</label>
        <input type="password" name="newPwdConfirm" class="input" required minlength="8" maxlength="30" />
      </div>

      <div class="actions">
        <button type="submit" class="btn">변경</button>
        <a href="${ctxPath}/mypage" class="btn btn-outline">취소</a>
      </div>

      <c:if test="${not empty message}">
        <div class="help ${error ? 'error' : ''}">
          ${message}
        </div>
      </c:if>
    </form>

    <div class="help">안전한 비밀번호를 위해 영문 대소문자, 숫자 조합을 권장합니다.</div>
  </div>
</div>

<jsp:include page="../footer/footer.jsp" />

<script>
(function(){
  const f = document.getElementById('pwdForm');
  f.addEventListener('submit', function(e){
    const cur = f.currentPwd.value.trim();
    const n1  = f.newPwd.value.trim();
    const n2  = f.newPwdConfirm.value.trim();
    const errs = [];

    if (!cur) errs.push('현재 비밀번호를 입력하세요.');
    if (n1.length < 8 || n1.length > 30) errs.push('새 비밀번호는 8~30자여야 합니다.');
    if (n1 !== n2) errs.push('새 비밀번호와 확인이 일치하지 않습니다.');
    if (cur && n1 && cur === n1) errs.push('현재 비밀번호와 다른 비밀번호를 사용하세요.');
    if (!/[A-Za-z]/.test(n1) || !/[0-9]/.test(n1)) errs.push('영문과 숫자를 조합하세요.');

    if (errs.length){
      e.preventDefault();
      alert(errs.join('\\n'));
    }
  });
})();
</script>
