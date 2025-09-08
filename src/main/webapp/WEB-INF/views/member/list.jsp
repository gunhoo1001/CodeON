<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<meta charset="UTF-8">
<title>직원 목록</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<style>
    /* 기본적인 레이아웃 설정 */
    body {
        background-color: #f8f9fa; /* 부드러운 배경색 */
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

    /* 카드 스타일 */
    .employee-card {
        margin: 2rem;
        max-width: 1400px;
        width: 100%;
    }

    /* 테이블 스타일 */
    .table-hover tbody tr:hover {
        background-color: #e9ecef;
        cursor: pointer;
    }
    table th, table td {
        vertical-align: middle;
        text-align: center;
    }
    
    /* 검색 폼 및 버튼 그룹 스타일 */
    .search-form-group {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }
    .action-buttons {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }
    
    @media (max-width: 768px) {
        .main-container {
            padding-left: 0;
            flex-direction: column;
            align-items: center;
        }
        .sidebar {
            position: relative;
            width: 100%;
            height: auto;
            border-right: none;
            border-bottom: 1px solid #ccc;
            top: 0;
        }
    }
</style>

<script>
$(function() {
    // 엑셀 업로드 버튼 클릭 시 파일 선택
    $("#btn_upload_excel").click(function() {
        $("#upload_excel_file").click();
    });

    // 파일 선택 시 폼 제출
    $("#upload_excel_file").change(function() {
        if (!$(this).val()) {
            alert("업로드할 엑셀파일을 선택하세요!!");
            return;
        }
        let formData = new FormData($("form[name='excel_upload_frm']")[0]);
        $.ajax({
            url: "<%=ctxPath%>/memberInfo/uploadExcelFile",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            dataType: "json",
            success: function(json) {
                console.log(JSON.stringify(json));
                alert(json.result == 1 ? "엑셀파일 업로드 성공했습니다.^^" : "엑셀파일 업로드 실패했습니다.ㅜㅜ");
                location.reload();
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
            }
        });
    });

    $("#searchWord").keyup(function(e){
        if(e.keyCode === 13) goSearch();
    });

    <c:if test="${not empty requestScope.searchType}">
        $("#searchType").val("${requestScope.searchType}");
        $("#searchWord").val("${requestScope.searchWord}");
    </c:if>

    <c:if test="${not empty requestScope.gender}">
        $("#gender").val("${requestScope.gender}");
    </c:if>

    $("#btnExcel").click(function() {
        const frm = document.searchFrm;
        frm.method = "POST";
        frm.action = "<%=ctxPath%>/member/downloadExcelFile";
        frm.submit();
    });
    
    // ai 분석 버튼
    $("#btnAnalyze").click(function() {
        // 1. 로딩 표시
        $("#aiLoading").show();
        $("#aiResult").text(""); // 이전 결과 초기화

        $.ajax({
            url: "<%= ctxPath %>/ai/memberChat", // GET 요청
            type: "GET",
            success: function(data) {
                // 2. 결과 출력 (줄바꿈 처리)
                $("#aiResult").html(data.replace(/\n/g, "<br>"));
            },
            error: function(xhr, status, error) {
                $("#aiResult").text("AI 분석 중 오류 발생!");
            },
            complete: function() {
                // 3. 로딩 숨기기
                $("#aiLoading").hide();
            }
        });
    });

});

// 직원 검색
function goSearch() {
    const frm = document.searchFrm;
    frm.method = "GET";
    frm.action = "<%= ctxPath%>/member/list";
    frm.submit();
}

// 직원 삭제
function goDelete(memberSeq) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        $.ajax({
            url: "<%= ctxPath%>/memberInfo/delete",
            type: "DELETE",
            data: { "memberSeq": memberSeq },
            dataType: "json",
            success: function(json) {
            	console.log(JSON.stringify(json));
                if (json.n == 1) {
                    alert("삭제가 완료되었습니다.");
                    location.reload();
                }
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
            }
        });
    }
}

// 직원 상세 정보 보기
function goDetail(memberSeq) {
    $.ajax({
        url: "<%= ctxPath%>/memberInfo/detail",
        type: "GET",
        data: { "memberSeq": memberSeq },
        dataType: "json",
        success: function(member) {
            $("#detailMemberName").text(member.memberName);
            $("#detailMemberSeq").text(member.memberSeq);
            $("#detailMemberHiredate").text(member.memberHiredate);
            $("#detailMemberEmail").text(member.memberEmail);
            $("#detailMemberMobile").text(member.memberMobile);
            
            let genderText = member.memberGender == 0 ? "남" : "여";
            $("#detailMemberGender").text(genderText);

            let departmentText = "";
            switch(member.fkDepartmentSeq) {
                case 10: departmentText = "인사팀"; break;
                case 20: departmentText = "개발팀"; break;
                case 30: departmentText = "기획팀"; break;
                case 40: departmentText = "영업팀"; break;
                case 50: departmentText = "고객지원팀"; break;
            }
            $("#detailDepartment").text(departmentText);

            let gradeText = "";
            switch(member.fkGradeSeq) {
                case 1: gradeText = "사원"; break;
                case 2: gradeText = "대리"; break;
                case 3: gradeText = "과장"; break;
                case 4: gradeText = "부장"; break;
                case 5: gradeText = "사장"; break;
            }
            $("#detailGrade").text(gradeText);

            $("#memberDetailModal").modal('show');
        },
        error: function(request, status, error){
            alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
        }
    });
}
</script>

<body class="bg-light">
    <div class="main-container">
        <div class="card shadow-sm employee-card">
            <div class="card-header bg-white d-flex flex-wrap justify-content-between align-items-center py-3">
                <h5 class="card-title fw-bold mb-0">
                    &nbsp;직원 목록
                </h5>
                <div class="action-buttons">
                	<button id="btnAnalyze" class="btn btn-info btn-sm">AI 요약 보기</button>
                    <button type="button" class="btn btn-success btn-sm" id="btnExcel">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-excel-fill" viewBox="0 0 16 16">
                            <path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.707 0H9.293zM5.884 6.68L8 9.882l2.116-3.202a.5.5 0 1 1 .768.64L8.651 10l2.233 3.442a.5.5 0 1 1-.768.64L8 11.318l-2.116 3.202a.5.5 0 1 1-.768-.64L7.349 10 5.116 6.68a.5.5 0 1 1 .768-.64z"/>
                        </svg>
                        Excel 다운로드
                    </button>
                    <button type="button" class="btn btn-info btn-sm" id="btn_upload_excel">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-upload" viewBox="0 0 16 16">
                            <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 .5-.5V5a.5.5 0 0 1-.5-.5H14a.5.5 0 0 1-.5.5v2H2v-2a.5.5 0 0 1-.5-.5H.5v4.9z"/>
                            <path d="M7.646 1.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 2.707V11.5a.5.5 0 0 1-1 0V2.707L4.354 4.854a.5.5 0 1 1-.708-.708l3-3z"/>
                        </svg>
                        Excel 업로드
                    </button>
                    <a href="<%=ctxPath%>/resources/excel/sample.xlsx" class="btn btn-warning btn-sm" download>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-arrow-down-fill" viewBox="0 0 16 16">
                            <path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.707 0H9.293zm-3.9 6.7a.5.5 0 0 0-.708.708l2 2a.5.5 0 0 0 .708 0l2-2a.5.5 0 0 0-.708-.708L8.5 8.293V5.5a.5.5 0 0 0-1 0v2.793L6.293 6.7z"/>
                        </svg>
                        양식 다운로드
                    </a>
                </div>
            </div>
            
            <form name="excel_upload_frm" style="display: none;">
                <input type="file" id="upload_excel_file" name="excel_file"
                       accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" />
            </form>
            
            <div class="card-body p-4">
				<form name="searchFrm" class="d-flex align-items-center mb-4">
				    <select class="form-select me-2" id="searchType" name="searchType" style="flex-shrink: 0; width: 140px;">
				        <option value="">검색 기준</option>
				        <option value="fkDepartmentSeq">부서</option>
				        <option value="fkGradeSeq">직급</option>
				        <option value="memberName">이름</option>
				    </select>
				    <input type="text" class="form-control me-2" id="searchWord" name="searchWord" placeholder="검색어 입력" style="flex-grow: 1;">
				    <select class="form-select me-2" id="gender" name="gender" style="flex-shrink: 0; width: 90px;">
				        <option value="">성별</option>
				        <option value="0">남</option>
				        <option value="1">여</option>
				    </select>
				    <button type="button" class="btn btn-primary" onclick="goSearch()">
				        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
				            <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
				        </svg>
				    </button>
				</form>
				
				<!-- AI 분석 결과 영역 -->
				<div id="aiLoading" class="text-center my-2" style="display:none;">
				    <div class="spinner-border text-primary" role="status">
				        <span class="visually-hidden">Loading...</span>
				    </div>
				</div>
				
				<div id="aiResult" class="border p-3 mt-2"></div>
				
                <div class="table-responsive">
                    <table class="table table-hover table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>입사일</th>
                                <th>사원번호</th>
                                <th>부서</th>
                                <th>직급</th>
                                <th>이름</th>
                                <th>이메일</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty MemberDtoList}">
                                    <tr>
                                        <td colspan="7">가입된 회원이 없습니다.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${MemberDtoList}">
                                        <tr onclick="goDetail(${item.memberSeq})">
                                            <td>${item.memberHiredate}</td>
                                            <td>${item.memberSeq}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.fkDepartmentSeq == 10}">인사팀</c:when>
                                                    <c:when test="${item.fkDepartmentSeq == 20}">개발팀</c:when>
                                                    <c:when test="${item.fkDepartmentSeq == 30}">기획팀</c:when>
                                                    <c:when test="${item.fkDepartmentSeq == 40}">영업팀</c:when>
                                                    <c:when test="${item.fkDepartmentSeq == 50}">고객지원팀</c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.fkGradeSeq == 1}">사원</c:when>
                                                    <c:when test="${item.fkGradeSeq == 2}">대리</c:when>
                                                    <c:when test="${item.fkGradeSeq == 3}">과장</c:when>
                                                    <c:when test="${item.fkGradeSeq == 4}">부장</c:when>
                                                    <c:when test="${item.fkGradeSeq == 5}">사장</c:when>
                                                </c:choose>
                                            </td>
                                            <td>${item.memberName}</td>
                                            <td>${item.memberEmail}</td>
                                            <td onclick="event.stopPropagation();">
                                                <div class="d-flex justify-content-center">
                                                    <button class="btn btn-sm btn-outline-primary me-1"
                                                            onclick="window.location.href='<%= ctxPath%>/member/update?memberSeq=${item.memberSeq}'">
                                                        수정
                                                    </button>
                                                    <button class="btn btn-sm btn-outline-danger" onclick="goDelete('${item.memberSeq}')">
                                                        삭제
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="text-center mt-4">
                    ${requestScope.pageBar}
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="memberDetailModal" tabindex="-1" aria-labelledby="memberDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="memberDetailModalLabel">직원 상세 정보</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">이름</div>
                        <div class="col-8" id="detailMemberName"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">사원번호</div>
                        <div class="col-8" id="detailMemberSeq"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">부서</div>
                        <div class="col-8" id="detailDepartment"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">직급</div>
                        <div class="col-8" id="detailGrade"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">입사일</div>
                        <div class="col-8" id="detailMemberHiredate"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">성별</div>
                        <div class="col-8" id="detailMemberGender"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">이메일</div>
                        <div class="col-8" id="detailMemberEmail"></div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-4 fw-bold">연락처</div>
                        <div class="col-8" id="detailMemberMobile"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>

<jsp:include page="../footer/footer.jsp" />