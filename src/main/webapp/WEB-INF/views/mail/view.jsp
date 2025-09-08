<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Bootstrap & Icons -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    // 메일 삭제
    function deleteMail(emailSeq) {
        if (!confirm("정말로 이 메일을 삭제하시겠습니까?")) return;

        $.ajax({
            url: "<%= ctxPath%>/mail/deleteMail",
            type: "POST",
            data: { emailSeq: emailSeq },
            success: function(response) {
                if (response.n == 1) {
                    alert("메일이 삭제되었습니다.");
                    location.href = "<%= ctxPath%>/mail/list"; // 삭제 후 목록으로 이동
                } else {
                    alert("메일 삭제 실패");
                }
            },
            error: function() {
                alert("서버 오류 발생");
            }
        });
    }
</script>

<!-- 메일 상세 페이지 -->
<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
		<div class="mb-3">
			<span class="text-primary fw-bold" style="cursor:pointer;"
			      onclick="location.href='${prevPage}'">
			   < 목록보기
			</span>
		</div>
    
        <div class="card shadow-sm">
			<!-- 헤더 -->
			<div class="card-header d-flex justify-content-between align-items-center">
			    <div>
			        <span class="ms-2 fw-bold">${mail.emailTitle}</span>
			    </div>
			    <div>
			        <button class="btn btn-sm btn-primary"
			                onclick="location.href='${ctxPath}/mail/resend?sendMemberEmail=${mail.sendMemberEmail}&emailSeq=${mail.emailSeq}'">
			            답장
			        </button>
			        <button class="btn btn-sm btn-danger" onclick="deleteMail(${mail.emailSeq})">
			            삭제
			        </button>
			    </div>
			</div>


            <!-- 본문 -->
            <div class="card-body">
                <p class="mb-1"><strong>보낸 사람:</strong> ${mail.sendMemberEmail}</p>
                <p class="mb-1"><strong>받는 사람:</strong> ${mail.receiveMemberEmail}</p>	
                <p class="text-muted"><small>${mail.emailRegdate}</small></p>
                <hr>
                <div>
                    <p>${mail.emailContent}</p>
                </div>
            </div>

            <!-- 첨부파일 -->
            <c:if test="${not empty mail.emailFilename}">
                <div class="card-footer bg-light">
                    <h6 class="fw-bold mb-2"><i class="bi bi-paperclip"></i> 첨부파일</h6>
                    <p class="mb-0">
                        <a href="${ctxPath}/mail/download?emailSeq=${mail.emailSeq}" class="text-decoration-none">
                            <i class="bi bi-file-earmark-text"></i> ${mail.emailOrgFilename}
                        </a>
                        <c:if test="${not empty mail.emailFilesize}">
                            <small class="text-muted">(${mail.emailFilesize} KB)</small>
                        </c:if>
                    </p>
                </div>
            </c:if>
        </div>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
