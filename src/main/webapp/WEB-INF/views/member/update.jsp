<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
   String ctxPath = request.getContextPath();
%>      
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"> 
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>직원 수정</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<style>
    /* 전체 레이아웃 설정 */
    body {
        background-color: #f0f2f5; /* 더 부드러운 배경색 */
        padding-top: 70px; /* 헤더 높이만큼 여백 */
        padding-bottom: 60px; /* 푸터 높이만큼 여백 */
    }
    
    .main-container {
        display: flex;
        justify-content: center;
        width: 100%;
        padding-left: 220px; /* 사이드바 너비만큼 여백 */
        box-sizing: border-box;
    }
    
    .update-card {
        margin: 2rem;
        max-width: 1400px;
        width: 100%;
        border: none; /* 카드 테두리 제거 */
        border-radius: 12px; /* 모서리 둥글게 */
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* 그림자 더 부드럽게 */
    }
    
    .card-header {
        background-color: #007bff; /* 헤더 색상을 버튼 색상과 통일 */
        border-radius: 12px 12px 0 0;
        padding: 1.5rem 1.25rem;
    }
    
    .required-symbol {
        color: #ff6b6b; /* 빨간색을 더 부드러운 색상으로 변경 */
        font-weight: bold;
    }

    .btn-primary {
        background-color: #007bff;
        border-color: #007bff;
        border-radius: 8px;
        transition: all 0.2s ease;
    }
    
    .btn-primary:hover {
        background-color: #0056b3;
        border-color: #0056b3;
    }

    .btn-outline-secondary {
        border-radius: 8px;
    }
    
    .form-control, .form-select {
        border-radius: 8px; /* 입력 필드 모서리 둥글게 */
    }

    @media (max-width: 768px) {
        .main-container {
            padding-left: 0;
            flex-direction: column;
            align-items: center;
        }
    }
</style>

<script type="text/javascript">
$(function() {
	
	$("#memberPwd").focus();
	
    $("#btnUpdate").click(function() {
        // 필수값 검사
        if ($.trim($("#memberUserid").val()) === "") {
            alert("아이디를 입력하세요.");
            $("#memberUserid").focus();
            return;
        }

        if ($.trim($("#memberEmail").val()) === "") {
            alert("이메일을 입력하세요.");
            $("#memberEmail").focus();
            return;
        }

        if ($("#fkDepartmentSeq").val() === null || $("#fkDepartmentSeq").val() === "") {
            alert("부서를 선택하세요.");
            $("#fkDepartmentSeq").focus();
            return;
        }

        if ($("#fkGradeSeq").val() === null || $("#fkGradeSeq").val() === "") {
            alert("직급을 선택하세요.");
            $("#fkGradeSeq").focus();
            return;
        }

        // 전화번호 형식 검사
        let phone = $.trim($("#memberMobile").val());
        if (phone !== "" && !/^(010-\d{4}-\d{4}|\d{10,11})$/.test(phone)) {
            alert("전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678 또는 01012345678");
            $("#memberMobile").focus();
            return;
        }

        // 생년월일 형식 검사 (입력된 경우만)
        let birthday = $.trim($("#memberBirthday").val());
        if (birthday !== "" && !/^\d{8}$/.test(birthday)) {
            alert("생년월일은 YYYYMMDD 형식으로 입력하세요.");
            $("#memberBirthday").focus();
            return;
        }

        let pwd = $.trim($("#memberPwd").val());
        if (pwd === "") {
            alert("비밀번호를 입력하세요.");
            $("#memberPwd").focus();
            return;
        }
        if (pwd.length < 4) {
            alert("비밀번호는 4자 이상 입력하세요.");
            $("#memberPwd").focus();
            return;
        }

        const formData = $("form[name='updateFrm']").serialize();

        $.ajax({
            url: "<%= ctxPath %>/memberInfo/update",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(json) {
                alert("수정이 완료되었습니다.");
                window.location.href = "<%= ctxPath %>/member/list"; 
            },
            error: function(request, status, error) {
                alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
            }
        });
    });
});
</script>


<body>
<div class="main-container">
    <div class="card shadow-sm update-card">
        <div class="card-header text-white text-center d-flex align-items-center justify-content-center py-3">
            <h4 class="mb-0">직원 정보 수정</h4>
        </div>
        <div class="card-body p-4">
            <form name="updateFrm" novalidate>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="memberName" class="form-label">이름</label>
                        <input type="hidden" name="memberSeq" id="memberSeq" value="${mbrDto.memberSeq}">
                        <input type="text" name="memberName" id="memberName" class="form-control" value="${mbrDto.memberName}" readonly>
                    </div>
                    <div class="col-md-6">
                        <label for="memberUserid" class="form-label">아이디 <span class="required-symbol">*</span></label>
                        <input type="text" name="memberUserid" id="memberUserid" class="form-control" value="${mbrDto.memberUserid}">
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-md-6">
                        <label for="memberPwd" class="form-label">비밀번호 <span class="required-symbol">*</span></label>
                        <input type="password" name="memberPwd" id="memberPwd" class="form-control" placeholder="비밀번호를 입력하세요">
                    </div>
                    <div class="col-md-6">
                        <label for="memberEmail" class="form-label">이메일</label>
                        <input type="email" name="memberEmail" id="memberEmail" class="form-control" value="${mbrDto.memberEmail}" readonly>
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-12">
                        <label for="memberMobile" class="form-label">전화번호 <span class="required-symbol">*</span></label>
                        <input type="tel" name="memberMobile" id="memberMobile" class="form-control" value="${mbrDto.memberMobile}" placeholder="010-1234-5678" >
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-md-6">
                        <label for="fkDepartmentSeq" class="form-label">부서 <span class="required-symbol">*</span></label>
                        <select name="fkDepartmentSeq" id="fkDepartmentSeq" class="form-select" required>
                            <option value="" disabled>선택하세요</option>
                            <option value="10" ${mbrDto.fkDepartmentSeq == 10 ? "selected" : ""}>인사팀</option>
                            <option value="20" ${mbrDto.fkDepartmentSeq == 20 ? "selected" : ""}>개발팀</option>
                            <option value="30" ${mbrDto.fkDepartmentSeq == 30 ? "selected" : ""}>기획팀</option>
                            <option value="40" ${mbrDto.fkDepartmentSeq == 40 ? "selected" : ""}>영업팀</option>
                            <option value="50" ${mbrDto.fkDepartmentSeq == 50 ? "selected" : ""}>고객지원팀</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="fkGradeSeq" class="form-label">직급 <span class="required-symbol">*</span></label>
                        <select name="fkGradeSeq" id="fkGradeSeq" class="form-select" required>
                            <option value="" disabled>선택하세요</option>
                            <option value="1" ${mbrDto.fkGradeSeq == 1 ? "selected" : ""}>사원</option>
                            <option value="2" ${mbrDto.fkGradeSeq == 2 ? "selected" : ""}>대리</option>
                            <option value="3" ${mbrDto.fkGradeSeq == 3 ? "selected" : ""}>과장</option>
                            <option value="4" ${mbrDto.fkGradeSeq == 4 ? "selected" : ""}>부장</option>
                            <option value="5" ${mbrDto.fkGradeSeq == 5 ? "selected" : ""}>사장</option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mt-3 align-items-end">
                    <div class="col-md-4">
                        <label for="memberBirthday" class="form-label">생년월일 <span class="required-symbol">*</span></label>
                        <input type="text" name="memberBirthday" id="memberBirthday" class="form-control" value="${fn:substring(mbrDto.memberBirthday, 0, 8)}" placeholder="YYYYMMDD">
                    </div>
                    <div class="col-md-4">
                        <label for="memberHiredate" class="form-label">입사일자 <span class="required-symbol">*</span></label>
                        <input type="date" name="memberHiredate" id="memberHiredate" class="form-control" value="${fn:substring(mbrDto.memberHiredate, 0, 10)}">
                    </div>
                    <div class="col-md-4 d-flex flex-column justify-content-center">
                        <label class="form-label">성별 <span class="required-symbol">*</span></label>
                        <div class="d-flex align-items-center">
                            <div class="form-check me-3">
                                <input class="form-check-input" type="radio" name="memberGender" id="genderMale" value="0" ${mbrDto.memberGender == 0 ? "checked" : ""} required>
                                <label class="form-check-label" for="genderMale">남성</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="memberGender" id="genderFemale" value="1" ${mbrDto.memberGender == 1 ? "checked" : ""} required>
                                <label class="form-check-label" for="genderFemale">여성</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-center mt-4">
                    <button type="button" id="btnUpdate" class="btn btn-primary px-5 me-2">
                        수정
                    </button>
                    <button type="button" class="btn btn-outline-secondary px-5" onclick="window.location.href='<%= ctxPath%>/member/list'">
                        취소
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />