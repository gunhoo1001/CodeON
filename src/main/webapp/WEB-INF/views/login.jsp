<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>로그인</title>
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

<link rel="stylesheet" href="<%= ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css">
<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script src="<%= ctxPath %>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>

<style>
  :root{
    --surface:#ffffff;
    --line:#d9dce3;
    --text:#111827;
    --muted:#6b7280;
    --brand:#2563eb;
    --brand-2:#1d4ed8;
    --shadow:0 16px 40px rgba(17,24,39,.08);
    --radius:16px;
  }
  html,body{ height:100%; }
  body{
    background:
      radial-gradient(1000px 500px at 10% -10%, #e8edff 0%, transparent 50%),
      radial-gradient(1000px 500px at 110% 0%, #f2fbf7 0%, transparent 50%),
      #f6f8fb;
    font-family: 'Pretendard','맑은 고딕', system-ui, -apple-system, Segoe UI, Roboto, 'Apple SD Gothic Neo', sans-serif;
    color:var(--text);
    margin:0;
  }

  /* 중앙 정렬 래퍼 */
  .login-wrap{
    min-height:100vh;
    display:flex;
    align-items:center;
    justify-content:center;
    padding:40px 16px;
  }

  /* 로그인 카드 */
  .login-card{
    width: 560px;
    max-width: 92vw;
    background:var(--surface);
    border:1px solid var(--line);
    border-radius:var(--radius);
    box-shadow: var(--shadow);
  }
  .login-bd{ padding: 36px 40px 32px; }

  /* 상단 로고 */
  .brand{
    text-align:center; margin-bottom: 26px;
  }
  .brand img{
    height:56px; display:block; margin:0 auto 8px;
  }
  .brand-name{
    font-weight: 900; letter-spacing:.2px; color:#1f4ed8;
  }

  /* 라벨-인풋 2열 정렬 */
  .field-row{
    display:grid; grid-template-columns: 110px 1fr; grid-column-gap: 14px;
    align-items:center;
    margin-bottom: 16px;
  }
  .field-row label{
    margin:0; color:#111827; font-weight:700;
  }
  .field-row input.form-control{
    height: 44px;
  }

  /* 버튼 */
  .btn-login{
    display:inline-flex; align-items:center; justify-content:center;
    width: 260px; height: 44px;
    background: linear-gradient(180deg, var(--brand), var(--brand-2));
    color:#fff; font-weight: 800; border:0; border-radius:10px;
    box-shadow: 0 12px 24px rgba(37,99,235,.25);
    transition: transform .06s ease, filter .2s ease;
  }
  .btn-login:hover{ filter: brightness(.97); }
  .btn-login:active{ transform: translateY(1px); }

  .helper{
    text-align:center; margin-top: 14px; font-size:14px;
  }
  .helper a{ color:#111; text-decoration:none; }
  .helper a:hover{ text-decoration:underline; }

  /* 작은 화면 대응 */
  @media (max-width: 480px){
    .field-row{ grid-template-columns: 1fr; }
    .field-row label{ margin-bottom:6px; }
    .btn-login{ width:100%; }
    .login-bd{ padding: 28px 20px 24px; }
  }
</style>

<script>
  $(function(){
    $("#btnLOGIN").on("click", submitLogin);
    $("#memberPwd").on("keydown", function(e){
      if(e.keyCode === 13) submitLogin();
    });
  });

  function submitLogin(){
    var uid = $("#memberUserId").val() || "";
    var pwd = $("#memberPwd").val() || "";
    if(uid.trim()===""){
      alert("아이디를 입력하세요.");
      $("#memberUserId").focus(); return;
    }
    if(pwd.trim()===""){
      alert("비밀번호를 입력하세요.");
      $("#memberPwd").focus(); return;
    }
    var frm = document.loginFrm;
    frm.action = "<%= ctxPath %>/login/loginEnd";
    frm.method = "POST";
    frm.submit();
  }
</script>
</head>
<body>

<div class="login-wrap">
  <div class="login-card">
    <div class="login-bd">

      <!-- 로고 -->
      <div class="brand">
        <img src="<%= ctxPath %>/image/logo.png" alt="CODEON 로고">
      </div>

      <!-- 로그인 폼 -->
      <form name="loginFrm" autocomplete="on">
        <div class="field-row">
          <label for="memberUserId">아이디</label>
          <input type="text" class="form-control" id="memberUserId" name="memberUserId" autocomplete="username">
        </div>

        <div class="field-row">
          <label for="memberPwd">비밀번호</label>
          <input type="password" class="form-control" id="memberPwd" name="memberPwd" autocomplete="current-password">
        </div>

        <div class="text-center mt-4">
          <button type="button" id="btnLOGIN" class="btn-login">로그인</button>
        </div>

        <div class="helper">
          <a href="<%= ctxPath %>/login/findPassword">비밀번호 찾기</a>
        </div>
      </form>

    </div>
  </div>
</div>

</body>
</html>
