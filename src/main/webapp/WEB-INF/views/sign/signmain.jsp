<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<style>
  .sign-main-wrap{
    margin-left: 220px;           /* sidebar width */
    padding: 90px 24px 80px;      /* header/ footer 간격 */
    min-height: calc(100vh - 60px);
    box-sizing: border-box;
    background:#fafbff;           /* 연한 블루톤 */
  }
  .section{
    background:#fff; border:1px solid #e5e7eb; border-radius:14px;
    padding:16px 18px; margin-bottom:18px;
  }
  .section-header{
    display:flex; align-items:center; justify-content:space-between; gap:10px;
    margin-bottom:10px;
  }
  .section-title{ font-size:18px; font-weight:700; }
  .more-btn{ font-size:14px; color:#2563eb; }
  .doc-list{ display:flex; flex-direction:column; gap:8px; }
  .doc-item{
    display:flex; align-items:center; justify-content:space-between; gap:10px;
    padding:10px 12px; border:1px solid #edf2f7; border-radius:10px; background:#fff;
  }
  .doc-left{ display:flex; align-items:center; gap:10px; min-width:0; }
  .badge{
    font-size:12px; padding:2px 8px; border-radius:999px; border:1px solid #e5e7eb; background:#f9fafb;
    white-space:nowrap;
  }
  .badge.danger{ border-color:#ef4444; color:#ef4444; background:#fff5f5; }
  .title{
    font-weight:600; color:#111827; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; max-width:42vw;
  }
  .meta{ color:#6b7280; font-size:12px; white-space:nowrap; }
  .status-0{ color:#1f2937; }   /* 진행중 */
  .status-1{ color:#10b981; }   /* 완료 */
  .status-9{ color:#ef4444; }   /* 반려 */

  /* === 링크 초기화 === */
  a, a:visited, a:hover, a:active {
    text-decoration: none;
    color: inherit;
  }
</style>

<div class="sign-main-wrap">

  <!-- 1) 결재 대기 문서 -->
  <div class="section">
    <div class="section-header">
      <div class="section-title">결재 대기 문서</div>
      <a class="more-btn" href="${pageContext.request.contextPath}/sign/inbox">더보기</a>
    </div>
    <div class="doc-list">
      <c:if test="${empty inboxPreview}">
        <div class="doc-item"><div class="title">대기 문서가 없습니다.</div></div>
      </c:if>
      <c:forEach var="row" items="${inboxPreview}">
        <a class="doc-item" href="${pageContext.request.contextPath}/sign/view/${row.draftSeq}">
          <div class="doc-left">
            <span class="badge"><c:out value="${row.docType}"/></span>
            <c:if test="${row.isEmergency == 1}">
              <span class="badge danger">긴급</span>
            </c:if>
            <span class="title"><c:out value="${row.title}"/></span>
          </div>
          <div class="meta">
            <c:out value="${row.drafterName}"/> ·
            <fmt:formatDate value="${row.regdate}" pattern="yyyy-MM-dd HH:mm"/>
          </div>
        </a>
      </c:forEach>
    </div>
  </div>

  <!-- 2) 결재 진행 문서 (내가 상신) -->
  <div class="section">
    <div class="section-header">
      <div class="section-title">결재 진행 문서</div>
      <a class="more-btn" href="${pageContext.request.contextPath}/sign/sent">더보기</a>
    </div>
    <div class="doc-list">
      <c:if test="${empty sentPreview}">
        <div class="doc-item"><div class="title">진행 중인 문서가 없습니다.</div></div>
      </c:if>
      <c:forEach var="row" items="${sentPreview}">
        <a class="doc-item" href="${pageContext.request.contextPath}/sign/view/${row.draftSeq}">
          <div class="doc-left">
            <span class="title"><c:out value="${row.title}"/></span>
          </div>
          <div class="meta">
            <span class="status-${row.status}">
              <c:choose>
                <c:when test="${row.status == 1}">완료</c:when>
                <c:when test="${row.status == 9}">반려</c:when>
                <c:otherwise>진행중</c:otherwise>
              </c:choose>
            </span>
            · <fmt:formatDate value="${row.regdate}" pattern="yyyy-MM-dd HH:mm"/>
          </div>
        </a>
      </c:forEach>
    </div>
  </div>

  <!-- 3) 결재 완료 문서 (내 이력) -->
  <div class="section">
    <div class="section-header">
      <div class="section-title">결재 완료 문서</div>
      <a class="more-btn" href="${pageContext.request.contextPath}/sign/sent">더보기</a>
    </div>
    <div class="doc-list">
      <c:if test="${empty historyPreview}">
        <div class="doc-item"><div class="title">처리한 이력이 없습니다.</div></div>
      </c:if>
      <c:forEach var="row" items="${historyPreview}">
        <a class="doc-item" href="${pageContext.request.contextPath}/sign/view/${row.draftSeq}">
          <div class="doc-left">
            <span class="badge"><c:out value="${row.docType}"/></span>
            <span class="title"><c:out value="${row.title}"/></span>
          </div>
          <div class="meta">
            <c:out value="${row.drafterName}"/> ·
            <fmt:formatDate value="${row.signDate}" pattern="yyyy-MM-dd HH:mm"/>
          </div>
        </a>
      </c:forEach>
    </div>
  </div>

</div>

<jsp:include page="../footer/footer.jsp" />
