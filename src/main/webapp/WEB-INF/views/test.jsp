<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
   String ctxPath = request.getContextPath();
   // ctxPath => 
%>      

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>CODEON 메인</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body { background-color: #f9f9f9; }
        .navbar-brand { font-weight: bold; }
        .profile-box, .time-box, .notice-box, .schedule-box, .weather-box {
            border: 1px solid #ccc; padding: 15px; background: #fff; margin-bottom: 15px;
        }
        .time-display { font-size: 1.5rem; font-weight: bold; }
        .btn-work { width: 80px; }
        .content-row { margin-top: 20px; }
        .icon-weather { font-size: 2rem; }
    </style>
</head>
<!-- Bootstrap CSS -->
<link rel="stylesheet" href="<%= ctxPath%>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css">

<%-- Font Awesome 6 Icons --%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

<%-- 직접 만든 CSS 1 --%>
<link rel="stylesheet" type="text/css" href="<%=ctxPath%>/css/style1.css" />

<%-- Optional JavaScript --%>
<script type="text/javascript" src="<%=ctxPath%>/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="<%=ctxPath%>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js" ></script>
<script type="text/javascript" src="<%=ctxPath%>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script> 

<%-- 스피너 및 datepicker 를 사용하기 위해 jQueryUI CSS 및 JS --%>
<link rel="stylesheet" type="text/css" href="<%=ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.min.css" />
<script type="text/javascript" src="<%=ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.min.js"></script>
    
<body>
<<<<<<< HEAD
faf
=======
<!-- 상단 네비게이션 -->
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><img src="logo.png" alt="CODEON" style="height:30px;"> CODEON</a>
        <div>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="#">게시판</a></li>
                <li class="nav-item"><a class="nav-link" href="#">메일</a></li>
                <li class="nav-item"><a class="nav-link" href="#">일정</a></li>
                <li class="nav-item"><a class="nav-link" href="#">근태관리</a></li>
                <li class="nav-item"><a class="nav-link" href="#">전자결재</a></li>
                <li class="nav-item"><a class="nav-link" href="#">주소록</a></li>
                <li class="nav-item"><a class="nav-link" href="#">마이페이지</a></li>
                <li class="nav-item"><a class="nav-link" href="#">인사(인사팀만)</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row content-row">
        <!-- 좌측 -->
        <div class="col-md-2">
            <div class="profile-box text-center">
                <img src="profile.png" alt="프로필" class="rounded-circle mb-2" width="60">
                <h6>${member.name} <small>사원님</small></h6>
                <p class="mb-1">출근 시간 ${attendance.startTime}</p>
                <p>퇴근 시간 ${attendance.endTime}</p>
            </div>

            <div class="time-box text-center">
                <p>${today}</p>
                <div class="time-display" id="clock"></div>
                <div class="mt-2">
                    <button class="btn btn-primary btn-work">출근</button>
                    <button class="btn btn-secondary btn-work">퇴근</button>
                </div>
            </div>

            <div class="weather-box text-center">
                <p>날씨 API</p>
                <div class="icon-weather">☀</div>
                <p>${weather.desc} ${weather.temp}도</p>
            </div>
        </div>

        <!-- 중앙 -->
        <div class="col-md-6">
            <div class="notice-box">
                <h6>게시판 중 공지사항 불러오기</h6>
                <ul>
                    <c:forEach var="notice" items="${noticeList}">
                        <li><a href="noticeDetail.do?id=${notice.id}">${notice.title}</a></li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <!-- 우측 -->
        <div class="col-md-4">
            <div class="schedule-box">
                <h6>📌 오늘 예정된 일정</h6>
                <ul class="list-unstyled mt-2">
                    <c:forEach var="schedule" items="${todaySchedule}">
                        <li>- ${schedule.time} ${schedule.title}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>

<script>
    // 시계
    function updateClock() {
        const now = new Date();
        document.getElementById("clock").textContent =
            now.toLocaleTimeString('ko-KR', { hour12: false });
    }
    setInterval(updateClock, 1000);
    updateClock();
</script>
>>>>>>> refs/heads/main
</body>
</html>
