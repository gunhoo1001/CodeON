<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctxPath = request.getContextPath();
%>
<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />
<meta charset="UTF-8">
<title>일정 등록</title>

<!-- jQuery UI (자동완성용) -->
<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>

<style>
:root{ --bg:#f4f6f8; --card:#ffffff; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --brand:#22c55e; --shadow:0 8px 20px rgba(0,0,0,.08); }
*{box-sizing:border-box}
body{margin:0; background:var(--bg); color:var(--text); font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR",Arial,Helvetica,sans-serif;}
.container{max-width:760px; margin:46px auto; background:var(--card); border:1px solid var(--line); border-radius:16px; box-shadow:var(--shadow); padding:28px 34px;}
h2{margin:0 0 18px; text-align:center}
label{font-weight:600; color:#374151; margin-top:14px; display:block}
input, textarea, select{width:100%; padding:10px 12px; margin-top:6px; border:1px solid #cfd4dc; border-radius:10px; font-size:14px; background:#fafafa; transition:all .2s}
input:focus, textarea:focus, select:focus{outline:none; border-color:#8b5cf6; background:#fff; box-shadow:0 0 0 3px rgba(139,92,246,.15)}
textarea{resize:vertical; min-height:110px}
.row{display:grid; grid-template-columns:1fr 1fr; gap:14px}
.helper{font-size:12px; color:var(--muted); margin-top:6px}
.btn-submit{margin-top:22px; width:100%; padding:12px; background:linear-gradient(90deg,#22c55e,#16a34a); border:none; border-radius:10px; color:#fff; font-size:16px; font-weight:700; cursor:pointer}
.alert{margin-top:12px; padding:12px; border:1px solid #fecaca; background:#fff1f2; color:#991b1b; border-radius:10px; font-size:13px}
.hidden{display:none}

/* 공유자 태그 스타일 */
span.plusUser{
    float:left;
    background-color:#737373;
    color:white;
    border-radius: 6px;
    padding: 6px 10px;
    margin: 3px;
    font-size:13px;
}
span.plusUser > i {
    cursor: pointer;
    margin-left:6px;
}
.displayUserList{margin-top:8px; min-height:24px;}
</style>
</head>

<body>
<div class="container">
  <h2>일정 등록</h2>

  <form action="<%= ctxPath %>/Calendar/addCalendarForm" method="post" onsubmit="return validateForm();">

    <!-- 대분류 -->
   <label>캘린더 선택</label>
   <select name="bigCategorySeq" id="bigCategorySeq" required>
     <option value="">-- 선택하세요 --</option>
     <c:forEach var="bigCat" items="${bigCategoryList}">
       <c:choose>
        
         <c:when test="${bigCat.bigCategoryName eq '부서 캘린더' && sessionScope.loginuser.fkGradeSeq lt 3}">
         
         </c:when>
         <c:otherwise>
           <option value="${bigCat.bigCategorySeq}">${bigCat.bigCategoryName}</option>
         </c:otherwise>
       </c:choose>
     </c:forEach>
   </select>
   <div class="helper">예: 사내 / 부서 / 내 캘린더 등</div>

    <!-- 소분류 -->
    <label>일정 선택</label>
    <select name="smallCategorySeq" id="smallCategorySeq" required>
      <option value="">-- 캘린더를 먼저 선택하세요 --</option>
      <c:forEach var="smallCat" items="${smallCategoryList}">
        <option value="${smallCat.smallCategorySeq}" data-fk="${smallCat.fkBigCategorySeq}">
          ${smallCat.smallCategoryName}
        </option>
      </c:forEach>
    </select>
    <div id="noSmallAlert" class="alert hidden">
      선택한 대분류에 연결된 소분류가 없습니다.
    </div>


    <!-- 제목/내용 -->
    <label>일정 제목</label>
    <input type="text" id="title" name="title" placeholder="일정 제목을 입력하세요" required>

    <label>일정 내용</label>
    <textarea name="content" placeholder="세부 내용을 입력하세요"></textarea>

    <!-- 시작/종료 -->
    <div class="row">
      <div>
        <label>시작 날짜</label>
        <input type="datetime-local" id="startDate" name="startDate" value="${param.date}T09:00" required>
      </div>
      <div>
        <label>종료 날짜</label>
        <input type="datetime-local" id="endDate" name="endDate" value="${param.date}T10:00" required>
      </div>
    </div>

    <!-- 장소 / 색상 -->
    <div class="row">
      <div>
        <label>장소</label>
        <input type="text" id="calendarLocation" name="calendarLocation" placeholder="회의실 A, 온라인 등">
      </div>
      <div>
        <label>색상</label>
        <input type="color" id="calendarColor" name="calendarColor" value="">
      </div>
    </div>

    <!-- 반복 -->
    <label>반복</label>
    <select name="repeatType">
      <option value="NONE">반복 없음</option>
      <option value="DAILY">매일</option>
      <option value="WEEKLY">매주</option>
      <option value="MONTHLY">매월</option>
    </select>

    <button type="submit" id="btnSubmit" class="btn-submit">등록하기</button>
  </form>
</div>

<script>

   function validateForm() {
        // 제목 필수
        if (!$("#title").val().trim()) {
          alert("일정 제목을 입력하세요.");
          $("#title").focus();
          return false;
        }

        // 대분류 필수
        if (!$("#bigCategorySeq").val()) {
          alert("캘린더(대분류)를 선택하세요.");
          $("#bigCategorySeq").focus();
          return false;
        }

        // 소분류 필수
        if (!$("#smallCategorySeq").val()) {
          alert("일정을 선택하세요.");
          $("#smallCategorySeq").focus();
          return false;
        }

        // 시작 / 종료 날짜 검사
        const start = $("#startDate").val();
        const end = $("#endDate").val();
        if (!start || !end) {
          alert("시작일과 종료일을 입력하세요.");
          return false;
        }
        if (start > end) {
          alert("종료일은 시작일 이후여야 합니다.");
          $("#endDate").focus();
          return false;
        }

        // 장소 (선택이지만 길이 제한)
        if ($("#calendarLocation").val().length > 50) {
          alert("장소는 50자 이내로 입력하세요.");
          $("#calendarLocation").focus();
          return false;
        }

        // 내용 (선택이지만 길이 제한)
        if ($("textarea[name=content]").val().length > 500) {
          alert("내용은 500자 이내로 입력하세요.");
          $("textarea[name=content]").focus();
          return false;
        }

        return true; // 통과 시 submit 진행
      }



</script>

<jsp:include page="../footer/footer.jsp" />
</body>
</html>
