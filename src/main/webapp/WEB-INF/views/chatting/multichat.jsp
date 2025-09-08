<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.7.1.min.js"></script>

<script type="text/javascript">
$(function(){

  var $mc = $("#mycontent");
  if ($mc.length) { $mc.css({"background-color":"#cce0ff"}); }

  // ws / wss 자동 선택 + 컨텍스트 경로 포함
  var wsScheme = (location.protocol === 'https:') ? 'wss://' : 'ws://';
  var wsUrl    = wsScheme + location.host + '<%=ctxPath%>/chatting/multichatstart';
  var websocket = new WebSocket(wsUrl);

  // ---- WebSocket 이벤트 ----
  let messageObj = {};

  websocket.onopen = function() {

  };

  websocket.onmessage = function(event) {
    // 접속자 명단
    if(event.data.substr(0,1)=="「" && event.data.substr(event.data.length-1)=="」") {
      $("#connectingUserList").html(event.data);
    }
    // 테이블(방 리스트 등) 갱신
    else if(event.data.substr(0,1)=="⊆") {
      $("#tbl > tbody").html(event.data);
    }
    // 일반 채팅 메시지
    else {
      $("#chatMessage").append(event.data).append("<br>").scrollTop(99999999);
    }
  };

  websocket.onclose = function(){
    // 필요 시 닫힘 처리
  };

  // ---- 전송 로직 ----
  let isOnlyOneDialog = false; // 귓속말 여부

  // Enter 로 전송
  $("#message").on("keyup", function(e){
    if (e.keyCode === 13) $("#btnSendMessage").click();
  });

  $("#btnSendMessage").on("click", function(){
    const $msg = $("#message");
    if ($msg.val().trim() === "") return;

    // XSS 최소 방어
    let messageVal = $msg.val().replace(/<script/gi, "&lt;script");

    messageObj = {
      message: messageVal,
      type   : "all",
      to     : "all"
    };

    const to = $("#to").val();
    if (to !== "") {
      messageObj.type = "one"; // 귓속말
      messageObj.to   = to;
    }

    websocket.send(JSON.stringify(messageObj));

    // 내가 보낸 말풍선 즉시 표시
    const now = new Date();
    let ampm = "오전 ";
    let hours = now.getHours();
    if (hours > 12) { hours -= 12; ampm = "오후 "; }
    if (hours === 0) hours = 12;
    if (hours === 12) ampm = "오후 ";
    let minutes = now.getMinutes();
    if (minutes < 10) minutes = "0" + minutes;
    const currentTime = ampm + hours + ":" + minutes;

    if (!isOnlyOneDialog) {
      $("#chatMessage").append(
        "<div style='background-color:#ffff80;display:inline-block;max-width:60%;float:right;padding:7px;border-radius:15%;word-break:break-all;'>" +
        messageVal +
        "</div><div style='display:inline-block;float:right;padding:20px 5px 0 0;font-size:7pt;'>"+
        currentTime+
        "</div><div style='clear:both;'>&nbsp;</div>"
      );
    } else {
      $("#chatMessage").append(
        "<div style='background-color:#ffff80;display:inline-block;max-width:60%;float:right;padding:7px;border-radius:15%;word-break:break-all;color:red;'>" +
        messageVal +
        "</div><div style='display:inline-block;float:right;padding:20px 5px 0 0;font-size:7pt;'>"+
        currentTime+
        "</div><div style='clear:both;'>&nbsp;</div>"
      );
    }

    $("#chatMessage").scrollTop(99999999);
    $msg.val("").focus();
  });

  // 처음엔 끄기 버튼 숨김
  $("#btnAllDialog").hide();

  // 접속자 이름 클릭 → 귓속말 대상 지정 (서버가 span.loginuserName 으로 내려보냄)
  $(document).on("click", "span.loginuserName", function(){
    const ws_id = $(this).prev().text(); // 직전 요소에 id를 넣어 내려보낸다고 가정
    $("#to").val(ws_id);
    $("#privateWho").text($(this).text());
    $("#btnAllDialog").show();
    $("#message").css({backgroundColor:"black", color:"white"}).attr("placeholder","귀속말 메시지 내용");
    isOnlyOneDialog = true;
  });

  // 귓속말 끊기
  $("#btnAllDialog").on("click", function(){
    $("#to").val("");
    $("#privateWho").text("");
    $("#message").css({backgroundColor:"", color:""}).attr("placeholder","메시지 내용");
    $(this).hide();
    isOnlyOneDialog = false;
  });

});
</script>

<style>
:root{
  --header-h: 100px;      /* 페이지 헤더 높이(네가 쓰는 값) */
  --footer-h: 50px;
  --gap: 20px;
  --radius: 14px;
  --line: #e5e7eb;
  --bg: #f7f8fb;
  --card: #fff;
  --text:#111827;
  --muted:#6b7280;
  --brand:#2563eb;
}

/* 페이지 배경 */
body{ background: var(--bg); }

/* 전체 레이아웃: 헤더 아래부터 뷰포트 꽉 채움 */
.chat-wrap{
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 16px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: var(--gap);
  /* 푸터에 가려지지 않도록 카드 자체가 뷰포트 높이를 계산해 차지 */
  min-height: calc(100vh - var(--header-h) - var(--footer-h) - 32px);
}

/* 카드 공통 */
.card{
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: var(--radius);
  box-shadow: 0 8px 24px rgba(17,24,39,.06);
}

/* ===== 좌측 사이드바 ===== */
.sidebar{
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.side-section h4{
  margin: 0 0 8px;
  font-size: 14px;
  color: var(--muted);
  font-weight: 800;
}

/* 접속자 리스트 영역 */
#connectingUserList{
  border: 1px dashed var(--line);
  border-radius: 10px;
  padding: 10px;
  max-height: 280px;
  overflow: auto;
  background: #fafbff;
}

/* 귓속말 표시줄 */
.private-row{
  display:flex; align-items:center; gap:8px; flex-wrap:wrap;
  font-size: 14px;
}
#btnAllDialog{ margin-left: auto; }

/* ===== 우측 채팅 카드 ===== */
.chat-card{
  display: grid;
  grid-template-rows: auto 1fr auto;   /* 헤더 / 스크롤 메시지 / 입력창 */
  height: calc(100vh - var(--header-h) - var(--footer-h) - 48px);
}

/* 채팅 헤더(타이틀 줄) */
.chat-hd{
  padding: 14px 16px;
  border-bottom: 1px solid var(--line);
  display:flex; align-items:center; justify-content:space-between;
  font-weight: 800;
}

/* 스크롤 영역 */
#chatMessage{
  padding: 16px;
  overflow: auto;
  background: #e6f3ff;
}

/* 입력창(하단 고정) */
.chat-composer{
  border-top: 1px solid var(--line);
  padding: 12px;
  display:flex; align-items:center; gap: 10px;
  background: var(--card);
}
.chat-composer input[type="text"]{
  flex: 1;
  height: 44px;
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 0 12px;
  font-size: 15px;
}
.btn{
  background: var(--brand);
  color:#fff;
  border:0;
  height: 44px;
  padding: 0 16px;
  border-radius: 10px;
  font-weight: 800;
  box-shadow: 0 4px 12px rgba(37,99,235,.22);
  cursor: pointer;
}
.btn-secondary{
  background: #ef4444; /* 나가기 버튼 */
  box-shadow: 0 4px 12px rgba(239,68,68,.18);
}

/* 말풍선(네가 JS로 append하는 인라인 스타일 그대로 둬도 되고, 아래 클래스를 써도 됨) */
.msg{
  display: inline-block; max-width: 70%; padding: 10px 12px;
  border-radius: 14px; word-break: break-all;
  margin: 4px 0;
}
.msg.me{ background:#fff7a1; float:right; }
.msg.other{ background:#fff; border:1px solid var(--line); float:left; }

/* 반응형 */
@media (max-width: 980px){
  .chat-wrap{ grid-template-columns: 1fr; }
}
</style>

<!-- ===== 새 레이아웃 ===== -->
<div class="chat-wrap">

  <!-- 좌측: 사이드바 -->
  <aside class="card sidebar">
    <div class="side-section">
      <h4>현재 접속자</h4>
      <div id="connectingUserList"></div>
    </div>

    <div class="side-section">
      <h4>귓속말</h4>
      <div class="private-row">
        <span>♡ 대상:</span>
        <span id="privateWho" style="font-weight:700;color:#ef4444;"></span>
        <button type="button" id="btnAllDialog" class="btn" style="height:34px;padding:0 10px;">귓속말 끊기</button>
      </div>
      <input type="hidden" id="to" />
    </div>

    <div class="side-section">
      <h4>도움말</h4>
      <div style="font-size:13px;color:var(--muted);line-height:1.6;">
        · 검정색: 모두에게 공개<br>
        · <span style="color:#dc2626;">붉은색</span>: 나에게만 보이는 1:1 귓속말<br>
        · 귓속말은 사용자명을 클릭
      </div>
    </div>
  </aside>

  <!-- 우측: 채팅 카드 -->
  <section class="card chat-card">

    <div class="chat-hd">
      <div>💬 팀 채팅방</div>
      <!-- 필요하면 방 이름·인원 등 -->
    </div>

    <!-- 메시지 스크롤 영역 (ID 유지) -->
    <div id="chatMessage"></div>

    <!-- 하단 입력창(고정) -->
    <div class="chat-composer">
      <input type="text" id="message" placeholder="메시지를 입력하세요" />
      <button id="btnSendMessage" class="btn">메시지보내기</button>
      <button class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/index'">나가기</button>
    </div>

  </section>
</div>

<jsp:include page="/WEB-INF/views/footer/footer.jsp" />
