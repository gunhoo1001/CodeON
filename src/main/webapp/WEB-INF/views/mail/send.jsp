<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Bootstrap & Icons -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>

	// row 색상 업데이트
	function updateRowReadStatus(row, newStatus) {
	    var colorClass = newStatus === '1' ? 'text-secondary' : 'text-dark';
	    row.find('td').not(':first').not(':nth-child(2)').removeClass('text-secondary text-dark').addClass(colorClass);
	}
	
	// 메일 row 클릭 → 상세보기
	$(document).on('click', 'tr.mail-row', function() {
	    var row = $(this);
	    var emailSeq = row.data('emailseq');
	    var icon = row.find('.read-icon'); 
	    var currentStatus = String(icon.data('readStatus'));
	
	    if (currentStatus === '0') {
	        $.ajax({
	            url: "<%= ctxPath %>/mail/updateReadStatus",
	            type: 'POST',
	            dataType: "json",
	            data: { emailSeq: emailSeq, readStatus: '1' },
	            async: false,
	            success: function(json) {
	                if (json.n === 1) {
	                    icon.removeClass('bi-envelope-fill text-primary').addClass('bi-envelope-open-fill text-secondary');
	                    icon.data('readStatus', '1');
	                    icon.attr('data-read-status', '1'); // HTML 속성 동기화
	                    updateRowReadStatus(row, '1');
	                }
	            }
	        });
	    }
	    window.location.href = '<%= ctxPath %>/mail/view?emailSeq=' + emailSeq;
	});
	
    // 중요 표시 토글
    $(document).on('click', '.important-icon', function(event) {
        event.stopPropagation();
        var icon = $(this);
        var emailSeq = icon.data('emailseq');
        var currentStatus = icon.data('importantStatus');
        var newStatus = currentStatus == 1 ? 0 : 1;

        $.ajax({
            url: "<%= ctxPath%>/mail/updateImportant",
            type: 'POST',
            dataType: "json",
            data: { emailSeq: emailSeq, importantStatus: newStatus },
            success: function(json) {
                if (json.n == 1) {
                    if (newStatus == 1) {
                        icon.removeClass('bi-star').addClass('bi-star-fill text-warning');
                    } else {
                        icon.removeClass('bi-star-fill text-warning').addClass('bi-star');
                    }
                    icon.data('importantStatus', newStatus);
                }
            }
        });
    });
    
    // 읽음 상태 아이콘 클릭
    $(document).on('click', '.read-icon', function(event) {
        event.stopPropagation();
        var icon = $(this);
        var row = icon.closest('tr');
        var emailSeq = icon.data('emailseq');
        var currentStatus = String(icon.data('readstatus'));
        var newStatus = currentStatus === '1' ? '0' : '1';

        $.ajax({
            url: "<%= ctxPath %>/mail/updateReadStatus",
            type: 'POST',
            dataType: "json",
            data: { emailSeq: emailSeq, readStatus: newStatus },
            success: function(json) {
                if (json.n === 1) {
                    icon.removeClass('bi-envelope-fill bi-envelope-open-fill text-primary text-secondary');
                    if (newStatus === '1') icon.addClass('bi-envelope-open-fill text-secondary');
                    else icon.addClass('bi-envelope-fill text-primary');
                    icon.data('readstatus', newStatus);
                    updateRowReadStatus(row, newStatus);
                    updateMailCount();
                }
            }
        });
    });

    // 행 클릭 시 상세보기
    $(document).on('click', 'tr.mail-row', function() {
        var emailSeq = $(this).data('emailseq');
        window.location.href = '<%= ctxPath%>/mail/view?emailSeq=' + emailSeq;
    });
    
 	// 체크박스 클릭 시 상세페이지로 이동 막기
    $(document).on("click", "input[name='chkMail']", function(event) {
        event.stopPropagation();
    });


    // 전체 선택/해제
    $(document).on("change", "#chkAll", function() {
        $("input[name='chkMail']").prop("checked", this.checked);
    });

    // 개별 선택 시 전체선택 체크박스 동기화
    $(document).on("change", "input[name='chkMail']", function() {
        $("#chkAll").prop("checked", $("input[name='chkMail']").length === $("input[name='chkMail']:checked").length);
    });

    // 선택 메일 삭제
    $(document).on("click", "#btnDelete", function() {
        let checkedMails = $("input[name='chkMail']:checked");
        if (checkedMails.length === 0) {
            alert("삭제할 메일을 선택하세요.");
            return;
        }
        if (!confirm("선택한 메일을 삭제하시겠습니까?")) return;

        let emailSeqArr = [];
        checkedMails.each(function() {
            emailSeqArr.push($(this).val());
        });

        $.ajax({
            url: "<%= ctxPath%>/mail/deleteMails",
            type: "POST",
            traditional: true,
            data: { emailSeqList: emailSeqArr },
            dataType: "json",
            success: function(json) {
                if (json.n > 0) {
                    alert("메일이 삭제되었습니다.");
                    location.reload();
                }
            }
        });
    });
</script>

<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
        <h3 class="mb-4">보낸 메일함</h3>

        <!-- 검색 -->
        <div class="card mb-4 shadow-sm">
            <div class="card-body">
                <form id="searchFrm" method="get" action="<%=ctxPath%>/mail/send" class="row g-2 align-items-center">
                    <div class="col-md-6 col-sm-12">
                        <input type="text" id="searchWord" name="searchWord" class="form-control"
                               placeholder="메일 검색..." value="${param.searchWord}">
                    </div>
                    <div class="col-md-6 col-sm-12 text-md-end mt-2 mt-md-0">
                        <button type="submit" class="btn btn-primary" id="btnSearch">검색</button>
                        <button type="button" class="btn btn-danger ms-2" id="btnDelete">삭제</button>
                    </div>
                </form>
            </div>
        </div>

<!-- 메일 테이블 -->
	<div class="card shadow-sm">
	    <div class="card-body p-0">
	        <table class="table table-hover mb-0">
	            <thead class="table-light">
	                <tr>
	                    <th style="width:40px;"><input type="checkbox" id="chkAll"></th>
	                    <th style="width:80px;"></th>
	                    <th>보낸 사람</th>
	                    <th>제목 / 받는 사람</th>
	                    <th>날짜</th>
	                </tr>
	            </thead>
	            <tbody>
	                <c:choose>
	                    <c:when test="${not empty mailList}">
	                        <c:forEach var="mail" items="${mailList}">
	                            <tr class="mail-row" style="cursor:pointer;" data-emailseq="${mail.emailSeq}">
	                                <td class="align-middle text-center">
	                                    <input type="checkbox" name="chkMail" value="${mail.emailSeq}">
	                                </td>
									<td class="text-center align-middle">
									    <div class="d-flex justify-content-center align-items-center gap-2" style="width:80px; font-size:1.25rem;">
									        <i class="bi ${mail.importantStatus == '1' ? 'bi-star-fill text-warning' : 'bi-star'} important-icon"
									           data-emailseq="${mail.emailSeq}" 
									           data-important-status="${mail.importantStatus}" 
									           title="중요"></i>
									        <i class="bi ${mail.readStatus == '1' ? 'bi-envelope-open-fill text-secondary' : 'bi-envelope-fill text-primary'} read-icon"
									           data-emailseq="${mail.emailSeq}" 
									           data-read-status="${mail.readStatus}" 
									           title="메일 상태"></i>
									        <i class="bi bi-paperclip attach-icon text-secondary" 
									           title="첨부파일"
									           style="${empty mail.emailFilename ? 'visibility:hidden;' : ''}"></i>
									    </div>
									</td>

                    				<td class="align-middle ${mail.readStatus == '1' ? 'text-secondary' : 'text-dark'}">
                                        ${mail.sendMemberEmail}
                                    </td>
                                    <td class="align-middle ${mail.readStatus == '1' ? 'text-secondary' : 'text-dark'}">
                                        <div>${mail.emailTitle}</div>
                                        <small class="text-muted">
                                            <c:set var="emails" value="${fn:split(mail.receiveMemberEmail, ',')}" />
                                            <c:choose>
                                                <c:when test="${fn:length(emails) == 1}">
                                                    ${emails[0]}
                                                </c:when>
                                                <c:otherwise>
                                                    ${emails[0]} 외 ${fn:length(emails) - 1}명
                                                </c:otherwise>
                                            </c:choose>
                                        </small>
                                    </td>
	                                <td class="align-middle ${mail.readStatus == '1' ? 'text-secondary' : 'text-dark'}">
	                                    ${mail.emailRegdate}
	                                </td>
	                            </tr>
	                        </c:forEach>
	                    </c:when>
	                    <c:otherwise>
	                        <tr>
	                            <td colspan="5" class="text-center text-muted py-4">받은 메일이 없습니다.</td>
	                        </tr>
	                    </c:otherwise>
	                </c:choose>
	            </tbody>
	        </table>
	
	        <c:if test="${not empty mailList}">
	            <div align="center" style="width:80%; margin:30px auto;">${requestScope.pageBar}</div>
	        </c:if>
	    </div>
	</div>

    </div>
</main>
<br><br>
<jsp:include page="../footer/footer.jsp" />

