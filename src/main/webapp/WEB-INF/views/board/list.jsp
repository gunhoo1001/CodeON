<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시판 목록</title>

    <!-- Bootstrap CSS + Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f4f6f9;
            padding-top: 70px;
            padding-left: 20px;
            padding-right: 20px;
            font-family: "Noto Sans KR", sans-serif;
        }

        /* Sidebar */
        .sidebar {
            position: sticky;
            top: 80px;
        }
        .sidebar h5 {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
        }
        .list-group-item:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        /* Popular badge */
        .popular-badge {
            font-size: 0.85rem;
        }

        /* 게시글 카드 */
        .board-card {
            transition: all 0.2s;
        }
        .board-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(0,0,0,0.1);
        }
        .board-title {
            font-size: 1.05rem;
            font-weight: 600;
        }
        .board-meta {
            font-size: 0.85rem;
            color: #6c757d;
        }

        /* 검색창 */
        .search-bar .form-select,
        .search-bar .form-control {
            height: 38px;
        }

        /* 페이지바 */
        .page-bar ul { list-style: none; padding: 0; margin: 0; display: inline-block; }
        .page-bar li { display: inline-block; margin: 0 3px; font-size: 12pt; }
        .page-bar li.active { border: 1px solid #6c757d; color: #dc3545; padding: 2px 6px; border-radius: 4px; }

        /* 버튼 그룹 */
        .board-type-btns a {
            margin-right: 5px;
        }
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row">

        <!-- 사이드바: 이번 주 인기글 -->
        <div class="col-md-3 sidebar mt-5">
            <h5 class="mb-3">이번 주 인기글 TOP 5</h5>
            <div class="list-group">
                <c:forEach var="board" items="${weeklyPopular}">
                    <c:if test="${board.recommendCount > 0}">
                        <a href="${ctxPath}/board/view?boardSeq=${board.boardSeq}" 
                           class="list-group-item list-group-item-action mb-2 shadow-sm rounded d-flex justify-content-between align-items-center">
                            <span class="fw-semibold text-truncate" style="max-width: 180px;">${board.boardTitle}</span>
                            <span class="badge bg-primary rounded-pill ms-2 popular-badge">👍 ${board.recommendCount}</span>
                        </a>
                    </c:if>
                </c:forEach>
            </div>
        </div>

        <!-- 메인 콘텐츠 -->
        <div class="col-md-9">

            <!-- 게시판 유형 + 글쓰기 -->
            <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                <div class="board-type-btns">
                    <a href="${ctxPath}/board/list?fkBoardTypeSeq=0" 
                       class="btn ${param.fkBoardTypeSeq=='0'?'btn-primary':'btn-outline-primary'}">사내게시판</a>
                    <a href="${ctxPath}/board/list?fkBoardTypeSeq=1" 
                       class="btn ${param.fkBoardTypeSeq=='1'?'btn-primary':'btn-outline-primary'}">부서게시판</a>
                </div>
                <button type="button" class="btn btn-success"
                    onclick="location.href='${ctxPath}/board/add?fkBoardTypeSeq=${param.fkBoardTypeSeq != null ? param.fkBoardTypeSeq : 0}'">글쓰기</button>
            </div>

            <!-- 부서 표시 -->
            <c:if test="${param.fkBoardTypeSeq == '1'}">
                <div class="mb-3 text-center">
                    <span style="font-size:1.2rem; font-weight:bold;">
                        현재 로그인 계정 부서: ${loginUserDeptName}
                    </span>
                </div>
            </c:if>

            <!-- 검색/카테고리 -->
<form class="d-flex justify-content-center align-items-center mb-3 search-bar" method="get" action="${ctxPath}/board/list">
    <input type="hidden" name="fkBoardTypeSeq" value="${param.fkBoardTypeSeq}" />

    <!-- 카테고리 select -->
    <select class="form-select me-2" name="fkBoardCategorySeq" style="width:130px; height:38px;">
        <option value="">전체</option>
        <option value="0" ${param.fkBoardCategorySeq=='0'?'selected':''}>공지사항</option>
        <option value="1" ${param.fkBoardCategorySeq=='1'?'selected':''}>일반</option>
        <option value="2" ${param.fkBoardCategorySeq=='2'?'selected':''}>경조사</option>
    </select>

    <!-- 검색 타입 select -->
    <select class="form-select me-2" name="searchType" style="width:130px; height:38px;">
        <option value="boardTitle" ${param.searchType=='boardTitle'?'selected':''}>제목</option>
        <option value="boardContent" ${param.searchType=='boardContent'?'selected':''}>내용</option>
        <option value="titleContent" ${param.searchType=='titleContent'?'selected':''}>제목+내용</option>
        <option value="memberName" ${param.searchType=='memberName'?'selected':''}>글쓴이</option>
    </select>

    <!-- 검색어 입력 -->
    <input type="text" class="form-control me-2" name="searchword" 
           value="${param.searchword}" placeholder="검색어 입력" style="width:250px; height:38px;" />

    <!-- 검색 버튼 -->
    <button type="submit" class="btn btn-primary" style="height:38px;">검색</button>
</form>
            <!-- 게시글 카드 리스트 -->
            <div class="row row-cols-1 g-3">
                <c:forEach var="board" items="${boardList}">
                    <div class="col">
                        <div class="card board-card shadow-sm">
                            <div class="card-body d-flex justify-content-between align-items-center">
                                <div>
                                    <a href="${ctxPath}/board/view?boardSeq=${board.boardSeq}" class="board-title text-decoration-none text-dark">
                                        ${board.boardTitle}
                                    </a>
                                    <div class="board-meta mt-1">
                                        ${board.boardCategoryName} | ${board.memberName} | 
                                        <fmt:formatDate value="${board.boardRegdate}" pattern="yyyy-MM-dd" /> | 조회: ${board.boardReadcount}
                                        <c:if test="${not empty board.boardFileSaveName}">
                                            <i class="bi bi-paperclip ms-1"></i>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="text-end">
                                    <span class="badge bg-success">👍 ${board.recommendCount}</span>
                                    <span class="badge bg-secondary">${board.commentCount}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- 페이지네이션 -->
            <div class="text-center mt-4 page-bar">
                <c:out value="${pageBar}" escapeXml="false"/>
            </div>

        </div>
    </div>
</div>

</body>
</html>
