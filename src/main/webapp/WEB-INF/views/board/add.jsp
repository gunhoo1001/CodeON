<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="../header/header.jsp" />

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- SmartEditor 2 -->
<script type="text/javascript" src="<%= ctxPath %>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript">
$(function(){
    var obj = [];

    nhn.husky.EZCreator.createInIFrame({
        oAppRef: obj,
        elPlaceHolder: "boardContent",
        sSkinURI: "<%= ctxPath%>/smarteditor/SmartEditor2Skin.html",
        htParams : {
            bUseToolbar : true,
            bUseVerticalResizer : true,
            bUseModeChanger : true
        }
    });

    $("#btnWrite").click(function(){
        obj.getById["boardContent"].exec("UPDATE_CONTENTS_FIELD", []);

        const title = $('input[name="boardTitle"]').val().trim();
        if(title === "") { alert("제목을 입력하세요."); return; }

        let contentVal = $('textarea[name="boardContent"]').val().trim();
        contentVal = contentVal.replace(/&nbsp;/gi, "");
        contentVal = contentVal.substring(contentVal.indexOf("<p>")+3);
        contentVal = contentVal.substring(0, contentVal.indexOf("</p>"));
        if(contentVal.trim().length === 0) { alert("내용을 입력하세요."); return; }

        const pw = $('input[name="boardPassword"]').val();
        if(pw === "") { alert("글 암호를 입력하세요."); return; }

        const frm = document.addFrm;
        frm.method = "post";
        frm.action = "<%= ctxPath%>/board/add";
        frm.submit();
    });
});
</script>
<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border-radius: 1rem;
        }
        #boardContent {
            min-height: 400px;
        }
    </style>

<div class="container my-5">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h3 class="mb-0">게시글 작성</h3>
        </div>
        <div class="card-body">
            <form name="addFrm" enctype="multipart/form-data" method="post" action="${pageContext.request.contextPath}/board/add">
                <input type="hidden" name="fkMemberSeq" value="${sessionScope.loginuser.memberSeq}" />
                <input type="hidden" name="fkBoardTypeSeq" value="${param.fkBoardTypeSeq}" />

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">작성자</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="memberName" value="${sessionScope.loginuser.memberName}" readonly />
                    </div>
                </div>

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">카테고리</label>
                    <div class="col-sm-10">
                        <select name="fkBoardCategorySeq" class="form-select">
                            <c:forEach var="cate" items="${boardCategoryList}">
                                <c:choose>
                                    <c:when test="${cate.BOARDCATEGORYSEQ != 0}">
                                        <option value="${cate.BOARDCATEGORYSEQ}">${cate.BOARDCATEGORYNAME}</option>
                                    </c:when>
                                    <c:when test="${cate.BOARDCATEGORYSEQ == 0 and ((param.fkBoardTypeSeq == 0 and sessionScope.loginuser.fkDepartmentSeq == 10) or (param.fkBoardTypeSeq == 1 and sessionScope.loginuser.fkGradeSeq == 4))}">
                                        <option value="${cate.BOARDCATEGORYSEQ}">${cate.BOARDCATEGORYNAME}</option>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">제목</label>
                    <div class="col-sm-10">
                        <input type="text" class="form-control" name="boardTitle" maxlength="200" placeholder="제목을 입력하세요." />
                    </div>
                </div>

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">내용</label>
                    <div class="col-sm-10">
                        <textarea class="form-control" name="boardContent" id="boardContent" style="height: 400px;" placeholder="내용을 입력하세요."></textarea>
                    </div>
                </div>

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">파일첨부</label>
                    <div class="col-sm-10">
                        <input type="file" class="form-control" name="attach" />
                    </div>
                </div>

                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label fw-bold">글암호</label>
                    <div class="col-sm-10">
                        <input type="password" class="form-control" name="boardPassword" maxlength="100" placeholder="글 암호를 입력하세요." />
                    </div>
                </div>

                <div class="text-end">
                    <button type="button" class="btn btn-primary me-2" id="btnWrite">글쓰기</button>
                    <button type="button" class="btn btn-secondary" onclick="history.back()">취소</button>
                </div>
            </form>
        </div>
    </div>
</div>
