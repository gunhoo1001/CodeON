<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<style>
    body { font-family: 'Arial', sans-serif; }
    .calendar-detail { width: 80%; margin: 50px auto; }
    .calendar-detail h2 { margin-bottom: 20px; }
    .calendar-detail a.back-link { text-decoration: none; color: #0071bd; font-weight: bold; }
    .calendar-table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    .calendar-table th, .calendar-table td { padding: 10px; text-align: left; border: 1px solid #ddd; }
    .calendar-table tr:nth-child(even) { background-color: #f9f9f9; }
    .calendar-table th { background-color: #f2f2f2; width: 150px; }
    .calendar-table textarea { width: 100%; border: 1px solid #ccc; padding: 5px; resize: none; font-family: inherit; font-size: 14px; }
    .btn-group { margin-top: 20px; text-align: right; }
    .btn-group button { padding: 8px 15px; border: none; border-radius: 5px; color: white; cursor: pointer; margin-left: 5px; font-size: 14px; }
    .btn-edit { background-color: #0071bd; }
    .btn-delete { background-color: #d9534f; }
    .btn-cancel { background-color: #999; }
</style>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script>
    
    // === 일정 삭제하기 ===
    function delCalendar(calendarSeq) {
        if (!calendarSeq) return;

        if (confirm("정말 삭제하시겠습니까?")) {
            $.ajax({
                url: "<%= ctxPath%>/Calendar/deleteCalendar",
                type: "post",
                data: { "calendarSeq": calendarSeq },
                dataType: "json",
                success: function(json) {
                    if (json.n == 1) {
                        alert("일정을 삭제하였습니다.");
                        location.href = "<%= ctxPath%>/Calendar/list";
                    } else {
                        alert("일정을 삭제하지 못했습니다.");
                    }
                },
                error: function(request, status, error) {
                    alert("code: " + request.status + "\n" +
                          "message: " + request.responseText + "\n" +
                          "error: " + error);
                }
            });
        }
    }

    
 	// 일정 수정하기
    function editCalendar(calendarSeq) {
        var frm = document.goEditFrm;
        frm.calendarSeq.value = calendarSeq;
        
        console.log("calendarSeq in form:", frm.elements['calendarSeq'].value);

        frm.action = "<%= ctxPath %>/Calendar/editCalendar";
        frm.method = "post";
        frm.submit();
    }
 
</script>

<c:set var="calendarSeq" value="${map.CALENDAR_SEQ}" />

<div class="calendar-detail">
    <h2>일정 상세보기</h2>
    <a href="<%= ctxPath%>/Calendar/list" class="back-link">◀ 캘린더로 돌아가기</a>

    <table class="calendar-table">
        <tr>
            <th>일자</th>
            <td>
                <span id="startdate"><c:out value="${map.calendarStart}" /></span> ~ 
                <span id="enddate"><c:out value="${map.calendarEnd}" /></span>
            </td>
        </tr>
        <tr>
            <th>제목</th>
            <td><c:out value="${map.calendarName}" /></td>
        </tr>
        <tr>
		    <th>캘린더 종류</th>
		    <td><c:out value="${map.calendarType}" /></td>
		</tr>
        <tr>
            <th>장소</th>
            <td><c:out value="${map.calendarLocation}" /></td>
        </tr>
        <tr>
            <th>내용</th>
            <td>
                <textarea rows="6" readonly><c:out value="${map.calendarContent}" /></textarea>
            </td>
        </tr>
        <tr>
            <th>작성자</th>
            <td><c:out value="${map.memberName}" /></td>
        </tr>
    </table>

	<form name="goEditFrm" style="display:none;">
	    <input type="hidden" name="calendarSeq"/>
	    <input type="hidden" name="gobackURL_detailCalendar" value="${requestScope.gobackURL_detailCalendar}"/>
	</form>

    <div class="btn-group">
    <c:if test="${sessionScope.loginuser.memberSeq eq map.fkMemberSeq}">
        <button type="button" class="btn-edit" onclick="editCalendar(${map.calendarSeq})">수정</button>
        <button type="button" class="btn-delete" onclick="delCalendar(${map.calendarSeq})">삭제</button>
    </c:if>
    <button type="button" class="btn-cancel" onclick="location.href='<%= ctxPath%>/Calendar/list'">취소</button>
</div>


<jsp:include page="../footer/footer.jsp" />
