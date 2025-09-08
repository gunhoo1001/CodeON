<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<%
    String ctxPath = request.getContextPath();
%>

<style>
    body { padding-top: 70px; }
    .board-content { white-space: pre-line; }
    .reply-row div {
        border-left: 2px solid #dee2e6;
        padding-left: 10px;
        margin-bottom: 5px;
    }
</style>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<div class="container mt-5">
    <!-- 게시글 상세 -->
    <div class="card mb-4 shadow-sm">
        <div class="card-header bg-light">
            <h4 class="mb-1">${board.boardTitle}</h4>
            <small class="text-muted">
                작성자: ${board.memberName} | 
                <fmt:formatDate value="${board.boardRegdate}" pattern="yyyy-MM-dd HH:mm"/> | 
                조회수: ${board.boardReadcount}
            </small>
             <% if(session.getAttribute("loginuser") != null) { %>
             <button type="button" class="btn btn-primary btn-sm" onclick="goEdit(${board.boardSeq})">수정</button>
            <button type="button" class="btn btn-danger btn-sm" onclick="goDelete(${board.boardSeq})">삭제</button>
        <% } %>
        </div>
        
        <div class="card-body">
            <p class="board-content">${board.boardContent}</p>

            <!-- 첨부파일 -->
            <c:if test="${not empty board.boardFileSaveName}">
                <c:choose>
                    <c:when test="${fn:endsWith(board.boardFileSaveName, '.jpg') 
                                or fn:endsWith(board.boardFileSaveName, '.png') 
                                or fn:endsWith(board.boardFileSaveName, '.gif')}">
                        <div class="mt-3">
                         <img src="${ctxPath}/resources/upload/${board.boardFileSaveName}" 
    						 alt="${board.boardFileOriName}" class="img-fluid rounded">
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="mt-3">
                            <i class="bi bi-paperclip"></i>
                            <a href="${ctxPath}/board/download?fileName=${board.boardFileSaveName}">
                                ${board.boardFileOriName}
                            </a>
                        </p>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>
    
<div class="mt-4 d-flex justify-content-between align-items-center">
    <div class="d-flex align-items-center mt-2">
    <button type="button" id="btnRecommend" class="btn btn-outline-success d-flex align-items-center" style="gap:6px;">
    👍 <span id="recommendCount">${board.recommendCount}</span>
</button>
</div>
</div>
<input type="hidden" id="boardSeq" value="${board.boardSeq}">

<br>



<div class="modal fade" id="recommendModal" tabindex="-1" aria-labelledby="recommendModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content border-0 shadow-lg" style="border-radius: 12px; overflow: hidden;">
      
      <!-- 헤더 -->
      <div class="modal-header bg-primary text-white" style="border-bottom: none;">
        <h5 class="modal-title" id="recommendModalLabel">
          <i class="bi bi-hand-thumbs-up-fill me-2"></i>현재 추천인
        </h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      
      <!-- 본문 -->
      <div class="modal-body p-3" style="background-color: #f8f9fa;">
        <ul id="recommendMemberList" class="list-group list-group-flush">
          <!-- AJAX로 추천한 사람 이름이 들어갑니다 -->
          <!-- 예: <li class="list-group-item d-flex justify-content-between align-items-center">
                  홍길동 <span class="badge bg-primary rounded-pill">👍</span>
               </li> -->
        </ul>
      </div>

      <!-- 푸터 (선택 사항) -->
      <div class="modal-footer border-0 justify-content-center" style="background-color: #f8f9fa;">
        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">닫기</button>
      </div>

    </div>
  </div>
</div>

    <!-- 댓글 입력 폼 -->
    <form id="frmComment" class="mb-3">
        <input type="hidden" id="boardSeq" value="${board.boardSeq}" />
        <textarea id="commentContent" class="form-control mb-2" rows="3" placeholder="댓글을 입력하세요"></textarea>
        <% if(session.getAttribute("loginuser") != null) { %>
            <button type="button" class="btn btn-primary" onclick="goWriteComment()">등록</button>
        <% } %>
    </form>

    <!-- 댓글 목록 -->
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>No</th>
                <th>내용</th>
                <th>작성자</th>
                <th>작성일</th>
                <th>reply/수정/삭제</th>
            </tr>
        </thead>
        <tbody id="commentDisplay">
           
        </tbody>
    </table>
</div>


<!-- 이전글 / 다음글 영역 -->
<div class="card mt-3 p-3" 
     style="border:1px solid #dee2e6; border-radius:10px; background:#ffffff; box-shadow:0 2px 6px rgba(0,0,0,0.05);">

    <ul class="list-unstyled mb-0 text-center" style="font-size:14px; line-height:1.6;">

        <!-- 다음글 (위쪽) -->
        <c:if test="${not empty nextBoard}">
            <li class="mb-2" style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                <span class="fw-bold text-secondary">다음글</span> :
                <a href="${ctxPath}/board/view?boardSeq=${nextBoard.boardSeq}" 
                   class="text-primary text-decoration-none" 
                   style="display:inline-block; max-width:70%; vertical-align:middle; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                    ${nextBoard.boardTitle}
                </a>
            </li>
        </c:if>

        <!-- 이전글 (아래쪽) -->
        <c:if test="${not empty prevBoard}">
            <li style="white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                <span class="fw-bold text-secondary">이전글</span> :
                <a href="${ctxPath}/board/view?boardSeq=${prevBoard.boardSeq}" 
                   class="text-primary text-decoration-none" 
                   style="display:inline-block; max-width:70%; vertical-align:middle; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                    ${prevBoard.boardTitle}
                </a>
            </li>
        </c:if>

    </ul>
</div>

<script>
const isLogin = <%= (session.getAttribute("loginuser") != null) %>;



$("#commentContent").on("keypress", function(e) {
    if(e.key === "Enter" && !e.shiftKey) { // Shift+Enter는 줄바꿈
        e.preventDefault(); // 줄바꿈 방지
        goWriteComment();   // 댓글 등록
    }
});


$(document).on("keypress", "[id^=replyContent-]", function(e) {
    if(e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        const parentCommentSeq = $(this).attr("id").split("-")[1];
        goWriteReply(parentCommentSeq); // 대댓글 등록
    }
});


// 추천 시작
$(document).ready(function() {

    const fkBoardSeq = $("#boardSeq").val(); // hidden input 필요

    // 초기 추천 상태 체크 및 추천수 표시
    function initRecommend() {
        $.ajax({
            url: "${ctxPath}/comment/checkRecommend",
            type: "GET",
            data: { fkBoardSeq: fkBoardSeq },
            success: function(response) {
                // 버튼 색상 / 텍스트 토글
                if(response.exists) {
                    $("#btnRecommend").removeClass("btn-outline-success")
                                      .addClass("btn-success")
                                      .text("추천 ❌");
                } else {
                    $("#btnRecommend").removeClass("btn-success")
                                      .addClass("btn-outline-success")
                                      .text("추천 👍");
                }
                $("#recommendCount").text(response.count);
            },
            error: function() {
                console.log("추천 상태 확인 실패");
            }
        });
    }

    // 추천자 목록 모달 띄우기
    function loadRecommendMembers() {
        $.ajax({
            url: "${ctxPath}/comment/recommendMembers",
            type: "GET",
            data: { fkBoardSeq: fkBoardSeq },
            dataType: "json",
            success: function(memberList) {
                const $list = $("#recommendMemberList");
                $list.empty();

                if(memberList.length === 0){
                    $list.append('<li class="list-group-item">추천한 사람이 없습니다.</li>');
                } else {
                    memberList.forEach(function(name) {
                        $list.append('<li class="list-group-item">' + name + '</li>');
                    });
                }

                // 모달 띄우기
                const recommendModal = new bootstrap.Modal(document.getElementById('recommendModal'));
                recommendModal.show();
            },
            error: function() {
                console.log("추천자 목록 불러오기 실패");
            }
        });
    }

    // 추천 버튼 클릭
    $("#btnRecommend").click(function() {
        $.ajax({
        	 url: "${ctxPath}/comment/toggleRecommend",
             type: "POST",
             data: { fkBoardSeq: fkBoardSeq },
             success: function(response) {
                 if(response.status === "added") {
                     $("#btnRecommend").removeClass("btn-outline-success")
                                       .addClass("btn-success")
                                       .text("❌ " + response.newCount); // 버튼 안에 추천수 표시
                 } else if(response.status === "removed") {
                     $("#btnRecommend").removeClass("btn-success")
                                       .addClass("btn-outline-success")
                                       .text("👍 " + response.newCount); // 버튼 안에 추천수 표시
                 } else if(response.status === "fail") {
                     alert("로그인이 필요합니다.");
                     return;
                 }

                // 추천수 업데이트
                $("#recommendCount").text(response.newCount);

                // 추천자 목록 갱신 + 모달 표시
                loadRecommendMembers();
            },
            error: function() {
                alert("추천 처리 중 오류가 발생했습니다.");
            }
        });
    });

    // 페이지 로딩 시 초기화
    initRecommend();
});





function goWriteComment() {
    const content = $("#commentContent").val().trim();
    if(!content) { alert("댓글 내용을 입력하세요."); return; }

    $.ajax({
        url: "<%= ctxPath %>/comment/add",
        type: "POST",
        data: { fkBoardSeq: $("#boardSeq").val(), commentContent: content },
        success: function(json) {
            if(json === "success") {
                $("#commentContent").val("");
                goReadComment(1);
            } else {
                alert("댓글 등록 실패");
            }
        }
    });
}

function goReadComment(currentShowPageNo) {
    $.ajax({
        url: "<%= ctxPath %>/comment/list",
        type: "GET",
        data: { fkBoardSeq: $("#boardSeq").val() },
        dataType: "json",
        success: function(json) {
            let v_html = "";
            if(json.length > 0) {
                $.each(json, function(index, item) {
                    v_html += `
                        <tr id="comment-\${item.commentSeq}">
                            <td>\${index + 1}</td>
                            <td>
                                <div id="commentContentDisplay-\${item.commentSeq}">\${item.commentContent}</div>
                                <div id="commentEditArea-\${item.commentSeq}" style="display:none;">
                                    <textarea id="commentContent-\${item.commentSeq}" class="form-control mb-2" rows="2"></textarea>
                                    <button class="btn btn-primary btn-sm" onclick="submitEditComment(\${item.commentSeq})">저장</button>
                                    <button class="btn btn-secondary btn-sm" onclick="cancelEditComment(\${item.commentSeq})">취소</button>
                                </div>
                            </td>
                            <td>\${item.memberName}</td>
                            <td>\${item.commentRegdate}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-secondary" onclick="showReplyForm(\${item.commentSeq})">reply</button>`;
                    if(item.mine) {
                        v_html += `
                                <button class="btn btn-sm btn-outline-primary" onclick="startEditComment(\${item.commentSeq})">수정</button>
                                <button class="btn btn-sm btn-outline-danger" onclick="goDeleteComment(\${item.commentSeq})">삭제</button>`;
                    }
                    v_html += `
                            </td>
                        </tr>
                        <tr id="reply-area-\${item.commentSeq}" class="reply-row" style="display:none;">
                            <td colspan="5">
                                <textarea id="replyContent-\${item.commentSeq}" class="form-control mb-2" rows="2" placeholder="대댓글 입력"></textarea>
                                <button class="btn btn-primary btn-sm" onclick="goWriteReply(\${item.commentSeq})">등록</button>
                                <div id="reply-list-\${item.commentSeq}" class="mt-2"></div>
                            </td>
                        </tr>
                    `;
                });
            } else {
                v_html = `<tr><td colspan='5'>댓글이 없습니다</td></tr>`;
            }
            $("#commentDisplay").html(v_html);

            // 대댓글도 로딩
            $.each(json, function(_, item){
                loadReplyList(item.commentSeq);
            });
        }
    });
}

//댓글 수정 관련
function startEditComment(commentSeq) {
    $("#commentContent-" + commentSeq).val(""); // 빈 textarea
    $("#commentContentDisplay-" + commentSeq).hide();
    $("#commentEditArea-" + commentSeq).show();
}

function cancelEditComment(commentSeq) {
    $("#commentEditArea-" + commentSeq).hide();
    $("#commentContentDisplay-" + commentSeq).show();
}

function submitEditComment(commentSeq) {
    const content = $("#commentContent-" + commentSeq).val().trim();
    if(!content) { 
        alert("댓글 내용을 입력하세요."); 
        return; 
    }

    $.ajax({
        url: "<%= ctxPath %>/comment/edit",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            commentSeq: commentSeq,
            commentContent: content
        }),
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
               
                goReadComment(1);
            } else {
                alert(json.message || "댓글 수정 실패");
            }
        },
        error: function() {
            alert("댓글 수정 중 오류가 발생했습니다.");
        }
    });
}


function showReplyForm(commentSeq) {
    $("#reply-area-" + commentSeq).toggle();
}

function goWriteReply(parentCommentSeq) {
    if(!isLogin) { alert("로그인 후 사용 가능합니다."); return; }
    const content = $("#replyContent-" + parentCommentSeq).val().trim();
    if(!content) { alert("내용을 입력하세요"); return; }

    $.ajax({
        url: "<%= ctxPath %>/comment/addReply",
        type: "POST",
        data: {
            parentCommentSeq: parentCommentSeq,
            fkBoardSeq: $("#boardSeq").val(),
            commentContent: content
        },
        success: function(json) {
            if(json === "success") {
                $("#replyContent-" + parentCommentSeq).val("");
                loadReplyList(parentCommentSeq);
            } else {
                alert("대댓글 등록 실패");
            }
        }
    });
}

// 대댓글 리스트 로딩
function loadReplyList(parentCommentSeq) {
    $.ajax({
        url: "<%= ctxPath %>/comment/listReply",
        type: "GET",
        data: { parentCommentSeq: parentCommentSeq },
        dataType: "json",
        success: function(json) {
            let html = "";

            if(json.length > 0) {
                $.each(json, function(i, reply) {
                    html += '<div class="border-start ps-3 mb-1">';
                    html += '<strong>' + reply.memberName + '</strong> : ' + reply.replyContent;
                    html += ' <small class="text-muted">(' + reply.replyRegdate + ')</small>';

                    // 수정/삭제 버튼 영역
                    if(reply.mine) {
                        html += ` <button class="btn btn-sm btn-outline-primary" onclick="startEditReply(\${reply.replySeq})">수정</button>`;
                        html += ` <button class="btn btn-sm btn-outline-danger" onclick="goDeleteReply(\${reply.replySeq}, \${parentCommentSeq})">삭제</button>`;
                    }

                    // 수정용 textarea + 버튼 (초기 숨김)
                    html += `<div id="replyEditArea-\${reply.replySeq}" style="display:none; margin-top:5px;">
                                <textarea id="replyEditContent-\${reply.replySeq}" class="form-control mb-1" rows="2"></textarea>
                                <button class="btn btn-primary btn-sm" onclick="submitEditReply(\${reply.replySeq}, \${parentCommentSeq})">저장</button>
                                <button class="btn btn-secondary btn-sm" onclick="cancelEditReply(\${reply.replySeq})">취소</button>
                            </div>`;

                    html += '</div>';
                });
            } else {
                html = "<small class='text-muted'>대댓글이 없습니다.</small>";
            }

            $("#reply-list-" + parentCommentSeq).html(html);
        }
    });
}

//글 수정
function goEdit(boardSeq) {
    location.href = "<%= ctxPath %>/board/edit?boardSeq=" + boardSeq;
}

//대댓글 수정 관련
function startEditReply(replySeq) {
    $("#replyEditContent-" + replySeq).val(""); // 빈 textarea
    $("#replyContentDisplay-" + replySeq).hide();
    $("#replyEditArea-" + replySeq).show();
}

function cancelEditReply(replySeq) {
    $("#replyEditArea-" + replySeq).hide();
    $("#replyContentDisplay-" + replySeq).show();
}

function submitEditReply(replySeq, parentCommentSeq) {
    const content = $("#replyEditContent-" + replySeq).val().trim();
    if(!content) { 
        alert("대댓글 내용을 입력하세요."); 
        return; 
    }

    $.ajax({
        url: "<%= ctxPath %>/comment/editReply", //  대댓글 수정
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            replySeq: replySeq,
            replyContent: content
        }),
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                
                loadReplyList(parentCommentSeq);
            } else {
                alert(json.message || "대댓글 수정 실패");
            }
        },
        error: function() {
            alert("대댓글 수정 중 오류가 발생했습니다.");
        }
    });
}


// 글 삭제
function goDelete(boardSeq) {
    if(!confirm("정말로 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/board/delete",
        type: "POST",
        data: { boardSeq: boardSeq },
        dataType: "json", // 서버에서 JSON으로 응답 받음
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                location.href = "<%= ctxPath %>/board/list";
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("오류 발생");
        }
    });
}

function goDeleteComment(commentSeq) {
    if(!confirm("정말로 이 댓글을 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/comment/delete",
        type: "POST",
        data: { commentSeq: commentSeq },
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                $("#comment-" + commentSeq).remove(); // 댓글 행 삭제
                $("#reply-area-" + commentSeq).remove(); // 대댓글 영역 삭제, 전부 삭제 
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("댓글 삭제 중 오류가 발생했습니다.");
        }
    });
}

function goDeleteReply(replySeq, parentCommentSeq) {
    if(!confirm("정말로 이 대댓글을 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/comment/deleteReply",
        type: "POST",
        data: { replySeq: parseInt(replySeq) },
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                // 삭제 후 부모 댓글의 대댓글 목록 다시 로딩
                loadReplyList(parentCommentSeq);
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("대댓글 삭제 중 오류가 발생했습니다.");
        }
    });
}
$(document).ready(function() {
    goReadComment(1);
});
</script>
