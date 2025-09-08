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

    $("#btnEdit").click(function(){
        obj.getById["boardContent"].exec("UPDATE_CONTENTS_FIELD", []);

        const title = $('input[name="boardTitle"]').val().trim();
        if(title === "") {
            alert("제목을 입력하세요.");
            return;
        }

        let contentVal = $('textarea[name="boardContent"]').val().trim();
        contentVal = contentVal.replace(/&nbsp;/gi, "");
        contentVal = contentVal.substring(contentVal.indexOf("<p>")+3);
        contentVal = contentVal.substring(0, contentVal.indexOf("</p>"));
        if(contentVal.trim().length === 0) {
            alert("내용을 입력하세요.");
            return;
        }

        const frm = document.editFrm;
        frm.method = "post";
        frm.action = "<%= ctxPath%>/board/edit";
        frm.submit();
    });
});
</script>

<div style="display: flex;">
    <div style="margin: auto; padding-left: 3%;">
        <h2 style="margin-bottom: 30px;">글 수정</h2>
        
        <form name="editFrm" enctype="multipart/form-data" method="post" action="${pageContext.request.contextPath}/board/edit">
            <input type="hidden" name="boardSeq" value="${board.boardSeq}" />
            <table style="width: 1024px" class="table table-bordered">
                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">작성자</th>
                    <td>
                        <input type="hidden" name="fkMemberSeq" value="${sessionScope.loginuser.memberSeq}" />
                        <input type="text" name="memberName" value="${sessionScope.loginuser.memberName}" readonly />
                    </td>
                </tr>
                <tr>
				    <th style="background-color: #DDDDDD;">게시판 타입</th>
				    <td>
				        <select name="fkBoardTypeSeq" class="form-control">
				            <c:forEach var="type" items="${boardTypeList}">
				                <option value="${type.BOARDTYPESEQ}" 
                                    <c:if test="${type.BOARDTYPESEQ == board.fkBoardTypeSeq}">selected</c:if>>
				                    ${type.BOARDTYPENAME}
				                </option>
				            </c:forEach>
				        </select>
				    </td>
				</tr>
				<tr>
				    <th style="background-color: #DDDDDD;">카테고리</th>
				    <td>
				        <select name="fkBoardCategorySeq" class="form-control">
				            <c:forEach var="cate" items="${boardCategoryList}">
				                <option value="${cate.BOARDCATEGORYSEQ}" 
                                    <c:if test="${cate.BOARDCATEGORYSEQ == board.fkBoardCategorySeq}">selected</c:if>>
				                    ${cate.BOARDCATEGORYNAME}
				                </option>
				            </c:forEach>
				        </select>
				    </td>
				</tr>
                <tr>
                    <th style="background-color: #DDDDDD;">제목</th>
                    <td><input type="text" name="boardTitle" size="100" maxlength="200" value="${board.boardTitle}" /></td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">내용</th>
                    <td><textarea style="width: 100%; height: 500px;" name="boardContent" id="boardContent">${board.boardContent}</textarea></td>
                </tr>
               <tr>
				    <th style="background-color: #DDDDDD;">파일첨부</th>
				    <td>
				        <input type="file" name="attach" />
				        <c:if test="${not empty board.boardFileOriName}">
				            <p>현재 첨부파일: ${board.boardFileOriName}</p>
				        </c:if>
				    </td>
				</tr>
            </table>
            <div style="margin: 20px;">
                <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnEdit">수정</button>
                <button type="button" class="btn btn-secondary btn-sm" onclick="history.back()">취소</button>
            </div>
        </form>
    </div>
</div>
