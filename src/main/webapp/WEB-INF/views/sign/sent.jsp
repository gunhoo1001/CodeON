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

  .badge{ padding:2px 8px; border-radius:999px; font-size:12px; white-space:nowrap; }
  .b-type{ background:#f3f4f6; color:#374151; }
  .b-state-progress { background:#eef2ff; color:#3730a3; }
  .b-state-approved { background:#ecfdf5; color:#065f46; }
  .b-state-rejected { background:#fef2f2; color:#991b1b; }
  .b-emg { background:#fff5f5; color:#b91c1c; border:1px solid #fecaca; }
</style>

<div class="main-content doc-wrap">
  <div class="doc-page">
    <h2 style="margin:0 0 14px;font-weight:800">문서함</h2>

    <div class="doc-card">
      <div class="doc-body">
        <table class="doc-table">
          <thead>
            <tr>
              <th class="doc-th" style="width:90px">문서번호</th>
              <th class="doc-th" style="width:120px">유형</th>
              <th class="doc-th">제목</th>
              <th class="doc-th" style="width:120px">기안일</th>
              <th class="doc-th" style="width:80px">긴급</th>
              <th class="doc-th" style="width:120px">상태</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty rows}">
                <tr>
                  <td class="doc-td" colspan="6" style="text-align:center;color:#6b7280">상신한 문서가 없습니다.</td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="r" items="${rows}">
                  <tr class="rowlink" tabindex="0"
                      data-href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}">
                    <td class="doc-td" style="text-align:center">${r.draftSeq}</td>
                    <td class="doc-td" style="text-align:center"><span class="badge b-type"><c:out value="${r.docType}"/></span></td>
                    <td class="doc-td">
                      <a href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}" style="color:#111;font-weight:600;text-decoration:none">
                        <c:out value="${r.title}"/>
                      </a>
                    </td>
                    <td class="doc-td" style="text-align:center"><fmt:formatDate value="${r.regdate}" pattern="yyyy-MM-dd"/></td>
                    <td class="doc-td" style="text-align:center"><c:if test="${r.isEmergency == 1}"><span class="badge b-emg">긴급</span></c:if></td>
                    <td class="doc-td" style="text-align:center">
                      <c:choose>
                        <c:when test="${r.status == 1}"><span class="badge b-state-approved">승인</span></c:when>
                        <c:when test="${r.status == 9}"><span class="badge b-state-rejected">반려</span></c:when>
                        <c:otherwise><span class="badge b-state-progress">진행중</span></c:otherwise>
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
