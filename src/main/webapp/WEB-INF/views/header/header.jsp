<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String ctxPath = request.getContextPath();
%>

<style>
  :root {
    --header-height: 70px;
  }
  /* 전체 기본 여백 초기화 */
  * { margin: 0; padding: 0; box-sizing: border-box; }

  body {
    font-family: '맑은 고딕', system-ui, -apple-system, Segoe UI, Roboto, 'Apple SD Gothic Neo', sans-serif;
    padding-top: var(--header-height); /* 헤더 높이만큼 아래로 */
    background-color: #f8f9fb;
  }

  /* ===== 헤더(기존 디자인 유지, 정렬만 보강) ===== */
  .site-header {
    display: flex; align-items: center; justify-content: space-between;
    padding: 0 25px; border-bottom: 1px solid #ccc; background-color: #fff;
    height: var(--header-height); position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  }
  .left-section {
    display: flex; align-items: center; gap: 22px; min-width: 0;
    height: 100%;
  }
  .logo {
    display: inline-flex; align-items: center; height: 100%;
    font-weight: 700; font-size: 26px; color: #0055a5; text-decoration: none;
  }
  .logo img {
    height: 48px; /* 55px → 48px: 내비 텍스트와 수평 맞춤 */
    margin-right: 10px; display:block;
  }

  /* 내비게이션: 카테고리와 로고/버튼 수직정렬 맞춤 */
  nav {
    display: flex; align-items: center; gap: 18px; font-weight: 600; font-size: 17px;
    height: 100%; /* 헤더 높이에 맞춰 수직 중앙정렬 */
  }
  nav a {
    display: inline-flex; align-items: center; height: 40px; /* 40px 라인 높이 고정 */
    padding: 0 2px;
    text-decoration: none; color: #000;
    border-bottom: 2px solid transparent; transition: border-color .2s ease, color .2s ease;
  }
  nav a:hover {
    border-bottom: 2px solid #000; font-weight: 700;
  }

  .logout-btn {
    background-color: #1E90FF; color: #fff;
    padding: 8px 14px; border: none; border-radius: 8px;
    font-size: 15px; font-weight: 700; cursor: pointer;
    transition: background-color .2s ease, transform .06s ease;
  }
  .logout-btn:hover { background-color: #1C86EE; }
  .logout-btn:active { transform: translateY(1px); }

  /* 본문 공통 */
  main { padding: 24px; min-height: calc(100vh - var(--header-height)); }

  /* ===== 알림 배너(새 디자인) ===== */
  .toast-wrap{
    position: fixed; top: 16px; right: 16px; z-index: 2000;
    display: flex; flex-direction: column; gap: 10px;
  }
  .toast-card{
    min-width: 300px; max-width: 380px; background:#111827; color:#fff;
    border-radius: 14px; box-shadow: 0 12px 26px rgba(15,23,42,.28);
    border: 1px solid rgba(255,255,255,.08); overflow: hidden;
  }
  .toast-inner{
    display:flex; gap:10px; padding: 12px 14px 10px 12px; align-items:flex-start;
  }
  .toast-icon{
    width: 28px; height: 28px; border-radius: 8px;
    background: linear-gradient(180deg,#3b82f6,#2563eb);
    display:flex; align-items:center; justify-content:center; font-size:16px;
    box-shadow: 0 6px 18px rgba(37,99,235,.35);
    flex: 0 0 28px;
  }
  .toast-body{ flex:1; min-width: 0; }
  .toast-title{ font-weight: 800; letter-spacing:.2px; }
  .toast-msg{ font-size: 13px; opacity:.92; margin-top:2px; line-height:1.45; word-break: keep-all; }
  .toast-close{
    appearance:none; border:0; background:transparent; color:#cbd5e1;
    font-size:18px; line-height:1; cursor:pointer; padding:0 2px;
  }
  .toast-close:hover{ color:#fff; }
  .toast-bar{
    height: 3px; width: 100%; background: linear-gradient(90deg,#60a5fa,#2563eb);
    opacity:.95; transform-origin: left center;
    transition: transform linear;
  }
</style>

<header class="site-header">
  <div class="left-section">
    <a href="<%= ctxPath %>/index" class="logo">
      <img src="<%= ctxPath %>/image/logo.png" alt="CODEON 로고" />
    </a>

    <nav>
      <a href="<%= ctxPath %>/board/list">게시판</a>
      <a href="<%= ctxPath %>/mail/list">메일</a>
      <a href="<%= ctxPath %>/Calendar/list">일정</a>
      <a href="<%= ctxPath %>/member/work">근태관리</a>
      <a href="<%= ctxPath %>/sign/main">전자결재</a>
      <a href="<%= ctxPath %>/address">주소록</a>
      <a href="<%= ctxPath %>/mypage">마이페이지</a>
      <a href="<%= ctxPath %>/chatting/multichat">웹채팅</a>
      <a href="<%= ctxPath %>/survey/main">설문</a>
      <a href="<%= ctxPath %>/member/register">인사</a>
      <a href="<%= ctxPath %>/company/organization">사내 조직도</a>
    </nav>
  </div>

  <form action="<%= ctxPath %>/login/logout" method="get">
    <button type="submit" class="logout-btn">로그아웃</button>
  </form>
</header>

<script>
/* ===== WebSocket 연결 & 알림 배너 표시 ===== */
(function(){
  if (window.__APP_WS__) return; // 중복 연결 방지

  var ctx = '<%= ctxPath %>';
  var wsUrl = (location.protocol === 'https:' ? 'wss://' : 'ws://') + location.host + ctx + '/chatting/multichatstart';

  try {
    var ws = new WebSocket(wsUrl);
    window.__APP_WS__ = ws;
  } catch (e) {
    console.error('WebSocket open failed:', e);
    return;
  }

  ws.onmessage = function(ev){
    var txt = ev.data || '';
    if (txt.trim().startsWith('{')) {
      try {
        var d = JSON.parse(txt);
        if (d.kind === 'notify') {
          // 세션(탭) 기준 10초 중복 차단
          if (d.notiId) {
            var key = 'seen_noti_' + d.notiId;
            var now = Date.now();
            var rec = sessionStorage.getItem(key);
            if (rec && (now - parseInt(rec, 10) < 10000)) return;
            sessionStorage.setItem(key, String(now));
          }
          showToast(d.title || '알림', d.body || '', d.link || '#');
          return;
        }
      } catch(e){ /* ignore parse errors for non-json */ }
    }
  };

  function showToast(title, body, link){
    var wrap = document.querySelector('.toast-wrap');
    if (!wrap) {
      wrap = document.createElement('div');
      wrap.className = 'toast-wrap';
      document.body.appendChild(wrap);
    }

    var card = document.createElement('div');
    card.className = 'toast-card';

    var inner = document.createElement('div');
    inner.className = 'toast-inner';

    var icon = document.createElement('div');
    icon.className = 'toast-icon';
    icon.textContent = '🔔';

    var bodyBox = document.createElement('div');
    bodyBox.className = 'toast-body';
    var t = document.createElement('div'); t.className = 'toast-title'; t.textContent = title || '알림';
    var m = document.createElement('div'); m.className = 'toast-msg'; m.textContent = body || '';
    bodyBox.appendChild(t); bodyBox.appendChild(m);

    var close = document.createElement('button');
    close.className = 'toast-close';
    close.setAttribute('aria-label', '알림 닫기');
    close.innerHTML = '&times;';
    close.onclick = function(e){ e.stopPropagation(); removeCard(); };

    inner.appendChild(icon);
    inner.appendChild(bodyBox);
    inner.appendChild(close);

    var bar = document.createElement('div');
    bar.className = 'toast-bar';

    card.appendChild(inner);
    card.appendChild(bar);

    if (link && link !== '#') {
      card.style.cursor = 'pointer';
      card.addEventListener('click', function(){ location.href = link; });
    }

    wrap.appendChild(card);

    // 진행바 애니메이션 & 자동 제거
    var life = 4500; // ms
    requestAnimationFrame(function(){
      bar.style.transform = 'scaleX(1)';
      bar.style.transitionDuration = life + 'ms';
      bar.style.transform = 'scaleX(0)';
    });

    var timer = setTimeout(removeCard, life);

    function removeCard(){
      clearTimeout(timer);
      card.style.transition = 'opacity .35s, transform .35s';
      card.style.opacity = '0';
      card.style.transform = 'translateY(-6px)';
      setTimeout(function(){ card.remove(); }, 360);
    }
  }
})();
</script>