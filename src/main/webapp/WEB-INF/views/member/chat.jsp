<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String ctxPath = request.getContextPath();
%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<meta charset="UTF-8">
<title>인사 도우미</title>

<script>
$(document).ready(function() {
    $('#btnSend').click(function() {
        const question = $('#question').val().trim();
        if (!question) {
            alert('질문을 입력해주세요.');
            return;
        }

        $('#response').text('응답을 기다리는 중...');

        $.ajax({
            url: '<%= ctxPath %>/ai/hrChat',
            method: 'GET',
            data: { question: question },
            success: function(data) {
                $('#response').text(data);
            },
            error: function(xhr, status, error) {
                $('#response').text('오류 발생: ' + error);
            }
        });
    });
});
</script>

<main style="margin-left: 240px; padding-top: 30px;">
    <div class="container-fluid mt-4">
        <div class="card border-light shadow-sm">
            <div class="card-header bg-white border-bottom">
                <h3 class="mb-0">인사 도우미</h3>
            </div>
            <div class="card-body">
                
                <!-- 질문 입력 + 버튼 한 줄 -->
                <div class="input-group mb-3">
                    <input type="text" class="form-control" id="question" placeholder="질문을 입력하세요">
                    <button class="btn btn-outline-secondary" id="btnSend"><i class="bi bi-send"></i> 전송</button>
                </div>

                <!-- 답변 영역 -->
                <h5>답변</h5>
                <pre id="response" class="border rounded p-3 bg-light" style="white-space: pre-wrap; min-height: 150px;"></pre>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
