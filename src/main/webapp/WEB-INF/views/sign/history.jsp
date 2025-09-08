<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="../header/header.jsp"/>
<jsp:include page="signsidebar.jsp"/>

<style>
  .doc-wrap{ margin-left:220px;padding:20px 28px 64px;background:#f6f7fb;min-height:100vh }
  .doc-page{ max-width:1200px;margin:24px auto }
  .doc-card{ background:#fff;border:1px solid #e5e7eb;border-radius:16px }
  .doc-body{ padding:14px }

  .doc-table{ width:100%; border-collapse:collapse; }
  .doc-th, .doc-td{ border:1px solid #e5e7eb; padding:10px; }
  .doc-th{ background:#f9fafb; font-weight:700; }
  .rowlink{ cursor:pointer; }
  .rowlink:hover{ background:#fbfdff; }
  .rowlink:focus{ outline:2px solid #93c5fd; outline-offset:-2px; }
</style>

<div class="main-content doc-wrap">
  <div class="doc-page">
    <h2 style="margin:0 0 14px;font-weight:800">결재함</h2>

    <div class="doc-card">
      <div class="doc-body">
        <table class="doc-table">
          <thead>
            <tr>
              <th class="doc-th" style="width:90px">문서번호</th>
              <th class="doc-th" style="width:120px">유형</th>
              <th class="doc-th">제목</th>
              <th class="doc-th" style="width:120px">기안자</th>
              <th class="doc-th" style="width:120px">처리일시</th>
              <th class="doc-th" style="width:100px">내 결재</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty rows}">
                <tr><td class="doc-td" colspan="6" style="text-align:center;color:#6b7280">처리한 문서가 없습니다.</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="r" items="${rows}">
                  <tr class="rowlink" tabindex="0"
                      data-href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}">
                    <td class="doc-td" style="text-align:center">${r.draftSeq}</td>
                    <td class="doc-td" style="text-align:center">${r.docType}</td>
                    <td class="doc-td">
                      <a href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}" style="color:#111;font-weight:600;text-decoration:none">
                        <c:out value="${r.title}"/>
                      </a>
                    </td>
                    <td class="doc-td" style="text-align:center">${r.drafterName}</td>
                    <td class="doc-td" style="text-align:center"><fmt:formatDate value="${r.signDate}" pattern="yyyy-MM-dd"/></td>
                    <td class="doc-td" style="text-align:center">
                      <c:choose>
                        <c:when test="${r.myStatus==1}"><span style="color:#16a34a">승인</span></c:when>
                        <c:when test="${r.myStatus==9}"><span style="color:#ef4444">반려</span></c:when>
                        <c:otherwise>-</c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script>
document.addEventListener('click',(e)=>{
  const tr=e.target.closest('tr.rowlink'); if(!tr) return;
  if(e.target.closest('a,button')) return;
  location.href=tr.dataset.href;
});
document.addEventListener('keydown',(e)=>{
  if(e.key!=='Enter') return;
  const tr=e.target.closest('tr.rowlink'); if(!tr) return;
  location.href=tr.dataset.href;
});
</script>

<jsp:include page="../footer/footer.jsp"/>
