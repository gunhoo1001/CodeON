<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<style>
    body { font-family: 'Arial', sans-serif; }
    .calendar-edit { width: 70%; margin: 50px auto; }
    .calendar-edit h2 { margin-bottom: 30px; }
    .calendar-table { width: 100%; border-collapse: collapse; }
    .calendar-table th, .calendar-table td { padding: 10px; text-align: left; border: 1px solid #ddd; }
    .calendar-table th { width: 150px; background-color: #f2f2f2; }
    .calendar-table tr:nth-child(even) { background-color: #f9f9f9; }
    .calendar-table input, .calendar-table select, .calendar-table textarea { width: 100%; padding: 6px; font-size: 14px; }
    .btn-group { margin-top: 20px; text-align: right; }
    .btn-group button { padding: 8px 15px; border: none; border-radius: 5px; color: white; cursor: pointer; margin-left: 5px; font-size: 14px; }
    .btn-save { background-color: #0071bd; }
    .btn-cancel { background-color: #999; }
</style>

<div class="calendar-edit">
    <h2>일정 수정하기</h2>

    <form name="editFrm" method="post" action="<%= ctxPath %>/Calendar/editCalendar_end">
        <input type="hidden" name="calendarSeq" value="${map.calendarSeq}" />

        <table class="calendar-table">
		    <tr>
		        <th>제목</th>
		        <td><input type="text" name="calendarName" value="${map.calendarName}" required /></td>
		    </tr>
		    <tr>
		        <th>일시</th>
		        <td>
		            <input type="datetime-local" name="calendarStart" value="${map.calendarStart}" required />
		            ~
		            <input type="datetime-local" name="calendarEnd" value="${map.calendarEnd}" required />
		        </td>
		    </tr>
		    <tr>
		        <th>캘린더 종류</th>
		        <td>
		        	<input type="text" name="calednarType" value="${map.calendarType}" readonly />
		        </td>
		    </tr>
		    <tr>
		        <th>장소</th>
		        <td><input type="text" name="calendarLocation" value="${map.calendarLocation}" /></td>
		    </tr>
		    <tr>
		        <th>내용</th>
		        <td><textarea name="calendarContent" rows="6">${map.calendarContent}</textarea></td>
		    </tr>
		    <tr>
		        <th>작성자</th>
		        <td>
		            <input type="text" name="memberName" value="${map.memberName}" readonly />
		            <!-- 필요하다면 hidden 으로 사용자ID도 넘길 수 있음 -->
		            <input type="hidden" name="fk_userid" value="${map.fkMemberSeq}" />
		        </td>
		    </tr>
		</table>

        <div class="btn-group">
            <button type="submit" class="btn-save">저장</button>
            <button type="button" class="btn-cancel" onclick="location.href='<%= ctxPath %>/Calendar/list'">취소</button>
        </div>
    </form>
</div>


<!-- 유효성검사 기능 추가 -->
<script>
document.addEventListener("DOMContentLoaded", function() {
    const form = document.forms["editFrm"];

    form.addEventListener("submit", function(event) {
        const title = form.calendarName.value.trim();
        const start = new Date(form.calendarStart.value);
        const end = new Date(form.calendarEnd.value);
        const content = form.calendarContent.value.trim();

        // 제목 필수, 2~50자 제한
        if (title.length < 2 || title.length > 50) {
            alert("제목은 2~50자 사이로 입력해주세요.");
            form.calendarName.focus();
            event.preventDefault();
            return false;
        }

        // 시작일 < 종료일 검증
        if (start >= end) {
            alert("종료일시는 시작일시보다 뒤여야 합니다.");
            form.calendarEnd.focus();
            event.preventDefault();
            return false;
        }

        // 내용 필수, 5~500자 제한
        if (content.length < 5 || content.length > 500) {
            alert("내용은 5~500자 사이로 입력해주세요.");
            form.calendarContent.focus();
            event.preventDefault();
            return false;
        }

        // 장소(선택 입력이지만 길이 제한)
        const location = form.calendarLocation.value.trim();
        if (location.length > 100) {
            alert("장소는 최대 100자까지 입력 가능합니다.");
            form.calendarLocation.focus();
            event.preventDefault();
            return false;
        }
    });
});
</script>


<jsp:include page="/WEB-INF/views/footer/footer.jsp" />
