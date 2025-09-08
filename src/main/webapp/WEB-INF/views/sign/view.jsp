<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../header/header.jsp"/>
<jsp:include page="signsidebar.jsp"/>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style>
  :root{--ink:#111;--muted:#6b7280;--line:#d1d5db;--card:#fff;--bg:#f6f7fb;--brand:#2563eb;--danger:#ef4444}
  body{background:var(--bg)}
  .page{margin-left:220px;padding:24px 20px 160px}
  .doc{max-width:1000px;margin:0 auto;background:var(--card);border:1px solid #e5e7eb;border-radius:16px;box-shadow:0 4px 16px rgba(0,0,0,.04);padding:22px}
  .title{font-size:20px;font-weight:800;margin:0 0 14px}

  .head{display:flex;gap:20px;align-items:flex-start}
  .meta{width:470px}
  .m-table{border:2px solid #111;border-collapse:collapse;width:100%}
  .m-table th,.m-table td{border:2px solid #111;padding:10px 12px;text-align:left;font-size:14px}
  .m-table th{width:110px;background:#fafafa}
  .subject{margin-top:10px;font-size:16px}

  .stamp-box{margin-left:auto}
  .stamps{
    display:flex;
    gap:12px;
    flex-wrap:wrap;
    justify-content:flex-end;
  }
  .stamp{width:140px;border:2px solid #111;border-radius:2px;background:#fff}
  .stamp .h{border-bottom:2px solid #111;text-align:center;padding:6px 4px;font-weight:700;font-size:15px}
  .stamp .b{min-height:120px;display:flex;align-items:center;justify-content:center;cursor:pointer}
  .stamp .f{border-top:2px solid #111;text-align:center;padding:6px 4px;font-size:12px;color:#444}
  .stamp-img{max-width:90px;max-height:90px;object-fit:contain;opacity:.95}
  .hint{font-size:12px;color:#9ca3af}

  .section{margin-top:18px;border:2px solid #111;border-radius:2px;background:#fff}
  .section-h{border-bottom:2px solid #111;background:#fafafa;padding:10px 12px;font-weight:700}
  .section-b{padding:12px}
  .note{width:100%;min-height:120px;border:1px solid #e5e7eb;border-radius:10px;padding:10px;box-sizing:border-box;font-size:14px}

  .opin-item{display:flex;align-items:flex-start;justify-content:space-between;gap:12px;border:1px solid #e5e7eb;border-radius:10px;padding:10px;margin-bottom:8px;background:#fff}
  .opin-author{font-weight:700}
  .opin-meta{flex:0 0 auto;min-width:190px;white-space:nowrap;color:#6b7280;text-align:right}
  .opin-body{margin-top:4px;white-space:pre-wrap}

  .actions{position:fixed;right:28px;bottom:92px;z-index:1000;display:flex;gap:10px}
  .btn{height:40px;padding:0 18px;border-radius:10px;border:1px solid #e5e7eb;background:#fff;cursor:pointer;font-weight:600}
  .btn.primary{background:var(--brand);border-color:var(--brand);color:#fff}
  .btn.danger{color:var(--danger);border-color:var(--danger);background:#fff}
  .btn[disabled]{opacity:.5;cursor:not-allowed}

  /* 첨부파일 */
  .attach-list{list-style:none;margin:0;padding:0}
  .attach-list li{display:flex;align-items:center;gap:10px;padding:8px 0;border-bottom:1px solid #f0f0f0}
  .attach-list li:last-child{border-bottom:0}
  .attach-name{flex:1 1 auto;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
  .attach-down{flex:0 0 auto}
  .pill{display:inline-block;border:1px solid var(--line);border-radius:999px;padding:2px 8px;font-size:12px;color:#555}

  .topbar{display:flex;align-items:center;gap:12px;margin-bottom:14px}
  .topbar .title{margin:0}
  .topbar form{margin-left:auto}
  
  .stamp-reject{
  width:92px;height:92px;
  border:3px solid #ef4444;
  border-radius:50%;
  display:flex;align-items:center;justify-content:center;
  font-weight:800; font-size:20px; color:#ef4444;
  transform:rotate(-10deg);
  position:relative;
  letter-spacing:0.2em;
  box-shadow: inset 0 0 0 3px rgba(239,68,68,.15);
}
.stamp-reject::after{
  content:""; position:absolute; inset:8px;
  border:2px solid rgba(239,68,68,.5); border-radius:50%;
}
</style>

<c:set var="regStr" value="${draft.draftRegdate}"/>
<c:set var="regDay" value="${empty regStr ? '' : fn:substring(regStr,0,10)}"/>

<c:set var="myComment" value=""/>
<c:set var="myStatus" value=""/>
<c:forEach var="l" items="${lines}">
  <c:if test="${l.approver.memberSeq == loginMemberSeq}">
    <c:set var="myComment" value="${l.signComment}"/>
    <c:set var="myStatus" value="${l.signStatus}"/>
  </c:if>
</c:forEach>

<div class="page">
  <div class="doc">
    <div class="topbar">
      <h2 class="title"><c:out value="${docTypeName}"/></h2>
      <form id="excelForm" action="<c:url value='/sign/downloadExcelFile'/>" method="post">
        <input type="hidden" name="draftSeq" value="${draft.draftSeq}"/>
        <c:if test="${_csrf != null}">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </c:if>
        <button type="submit" class="btn primary">엑셀 다운로드</button>
      </form>
    </div>

    <div class="head">
      <!-- 메타 -->
      <div class="meta">
        <table class="m-table">
          <tr><th>기안자</th><td><c:out value="${draft.member.memberName}"/></td></tr>
          <tr><th>소속</th><td><c:out value="${draft.member.department != null ? draft.member.department.departmentName : ''}"/></td></tr>
          <tr><th>기안일</th><td><c:out value="${regDay}"/></td></tr>
          <tr><th>문서번호</th><td><c:out value="${draft.draftSeq}"/></td></tr>
        </table>
        <div class="subject"><strong>제목</strong> : <c:out value="${draft.draftTitle}"/></div>
      </div>

      <!-- 도장 -->
      <div class="stamp-box">
        <div class="stamps">
          <c:forEach var="l" items="${lines}">
            <c:set var="sd" value="${l.signDate}"/>
            <c:set var="sday" value="${empty sd ? '' : fn:substring(sd,0,10)}"/>
            <div class="stamp">
              <div class="h">
                <c:out value="${l.approver.memberName}"/>
                &nbsp;<c:out value="${l.approver.grade != null ? l.approver.grade.gradeName : ''}"/>
              </div>
              <div class="b" onclick="onStampBoxClick(${l.approver.memberSeq})">
                <c:choose>
                  <c:when test="${l.signStatus == 1}">
                    <c:choose>
                      <c:when test="${not empty l.approver.stampImage}">
                        <c:url var="stampSrc" value="/resources/stamp_upload/${l.approver.stampImage}"/>
                        <img class="stamp-img" src="${stampSrc}" alt="stamp"/>
                      </c:when>
                      <c:otherwise><span class="hint">도장 미등록</span></c:otherwise>
                    </c:choose>
                  </c:when>
                    <c:when test="${l.signStatus == 9}">
				      <div class="stamp-reject">반려</div>
				    </c:when>
                  <c:otherwise><span class="hint">대기</span></c:otherwise>
                </c:choose>
              </div>
              <div class="f"><c:out value="${sday}"/></div>
            </div>
          </c:forEach>
        </div>
      </div>
    </div>

    <!-- 본문 -->
    <div class="section">
      <div class="section-h">본문</div>
      <div class="section-b">
        <c:choose>
        
          <c:when test="${not empty vacation}">
            <c:set var="vacTypeKo" value="${vacation.vacationType == 'HALF' ? '반차' : '연차'}"/>
            <div style="margin-bottom:6px">
              <strong>기간</strong> :
              <c:out value="${vacation.vacationStart}"/> ~ <c:out value="${vacation.vacationEnd}"/>
              <span style="color:#6b7280;margin-left:6px">(<c:out value="${vacTypeKo}"/>)</span>
            </div>
            <div style="white-space:pre-line"><c:out value="${vacation.vacationContent}"/></div>
          </c:when>

          <c:when test="${not empty payment}">
            <div style="margin-bottom:10px">
              <div><strong>지출 제목</strong> : <c:out value="${payment.paymentTitle}"/></div>
              <div style="margin-top:6px;white-space:pre-line">
                <strong>지출 사유</strong><br/>
                <c:out value="${payment.paymentContent}"/>
              </div>
            </div>

            <table class="m-table" style="margin-top:8px">
              <thead>
                <tr>
                  <th style="width:140px">지출일자</th>
                  <th>사용처</th>
                  <th style="width:160px">금액</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="row" items="${paymentLists}">
                  <tr>
                    <td><c:out value="${row.regdate}"/></td>
                    <td><c:out value="${row.content}"/></td>
                    <td style="text-align:right"><fmt:formatNumber value="${row.price}" type="number"/> 원</td>
                  </tr>
                </c:forEach>
              </tbody>
              <tfoot>
                <tr>
                  <th colspan="2" style="text-align:right">합계</th>
                  <th style="text-align:right"><fmt:formatNumber value="${payment.totalAmount}" type="number"/> 원</th>
                </tr>
              </tfoot>
            </table>
          </c:when>

          <c:when test="${not empty business}">
            <div style="display:grid;row-gap:8px">
              <div><strong>출장 목적</strong> : <c:out value="${business.businessContent}"/></div>
              <div><strong>출장 기간</strong> :
                <c:out value="${business.businessStart}"/> ~ <c:out value="${business.businessEnd}"/>
              </div>
              <div><strong>출장 지역</strong> : <c:out value="${business.businessLocation}"/></div>
              <div style="white-space:pre-line">
                <strong>출장 결과</strong><br/>
                <c:out value="${business.businessResult}"/>
              </div>
            </div>
          </c:when>

          <c:otherwise>
            <div style="white-space:pre-line"><c:out value="${draft.draftContent}"/></div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- 첨부파일 -->
    <c:if test="${not empty attachments}">
      <div class="section">
        <div class="section-h">첨부파일</div>
        <div class="section-b">
          <ul class="attach-list">
            <c:forEach var="f" items="${attachments}">
              <li>
                <span class="pill">FILE</span>
                <span class="attach-name" title="${f.fileName}"><c:out value="${f.fileName}"/></span>
                <a class="attach-down" href="${ctx}/sign/files/${f.draftFileSeq}/download">다운로드</a>
              </li>
            </c:forEach>
          </ul>
        </div>
      </div>
    </c:if>

    <!-- 의견 기록 -->
    <div class="section">
      <div class="section-h">의견 기록</div>
      <div class="section-b">
        <c:set var="hasAny" value="false"/>
        <c:forEach var="l" items="${lines}">
          <c:if test="${not empty l.signComment}">
            <c:set var="hasAny" value="true"/>
            <c:set var="sd2" value="${l.signDate}"/>
            <c:set var="sday2" value="${empty sd2 ? '' : fn:replace(fn:substring(sd2,0,19),'T',' ')}"/>
            <div class="opin-item">
              <div style="min-width:0">
                <div class="opin-author">
                  <c:out value="${l.approver.memberName}"/>
                  <c:if test="${not empty l.approver.grade}"> <c:out value="${l.approver.grade.gradeName}"/></c:if>
                  <span style="margin-left:8px;font-size:12px;color:#6b7280">
                    <c:choose>
                      <c:when test="${l.signStatus == 1}">승인</c:when>
                      <c:when test="${l.signStatus == 9}">반려</c:when>
                      <c:otherwise>대기</c:otherwise>
                    </c:choose>
                  </span>
                </div>
                <div class="opin-body"><c:out value="${l.signComment}"/></div>
              </div>
              <div class="opin-meta"><c:out value="${sday2}"/></div>
            </div>
          </c:if>
        </c:forEach>

        <c:if test="${!hasAny}">
          <div style="color:#6b7280">남겨진 의견이 없습니다.</div>
        </c:if>
      </div>
    </div>

    <!-- 의견 입력 -->
    <c:if test="${canAct || canEdit}">
      <div class="section">
        <div class="section-h">의견 <span style="color:#6b7280;font-weight:400">(반려 시 필수)</span></div>
        <div class="section-b">
          <textarea id="opinion" class="note" placeholder="의견을 입력하세요."><c:out value="${myComment}"/></textarea>
          <c:if test="${!canAct && canEdit}">
            <div style="margin-top:6px;font-size:12px;color:#6b7280">※ 내 차례가 아니지만, 이전 결재 결과를 수정할 수 있습니다.</div>
          </c:if>
        </div>
      </div>
    </c:if>
  </div>
</div>

<!-- 우하단 고정 버튼 -->
<div class="actions">
  <c:choose>
    <c:when test="${canAct}">
      <button class="btn primary" onclick="approve(false)">승인</button>
      <button class="btn danger"  onclick="reject(false)">반려</button>
    </c:when>
    <c:when test="${!canAct && canEdit}">
      <button class="btn primary" onclick="approve(true)">승인(수정)</button>
      <button class="btn danger"  onclick="reject(true)">반려(수정)</button>
    </c:when>
    <c:otherwise>
      <button class="btn" disabled>처리 권한 없음</button>
    </c:otherwise>
  </c:choose>
</div>

<script>
(function () {
  const ctx = '${pageContext.request.contextPath}';
  const myLineSeq = ${myDraftLineSeq != null ? myDraftLineSeq : 'null'};
  const myStampImage = '${fn:escapeXml(myStampImage)}';
  const loginMemberSeq = '${loginMemberSeq}';

  window.onStampBoxClick = function (memberSeq) {
    if (String(memberSeq) === String(loginMemberSeq)) {
      if (confirm('내 도장 이미지를 등록/변경하시겠어요?')) {
        location.href = ctx + '/sign/setting';
      }
    }
  };

  async function post(url, body) {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams(body || {})
    });
    return res.json();
  }

  window.approve = async function (editMode) {
    if (myLineSeq === null) { alert('처리 권한이 없습니다.'); return; }
    if (!myStampImage) {
      if (confirm('도장 이미지가 등록되어 있지 않습니다. 설정에서 등록하시겠어요?')) {
        location.href = ctx + '/sign/setting';
      }
      return;
    }
    const comment = (document.getElementById('opinion')?.value || '');
    const url = ctx + '/sign/lines/' + encodeURIComponent(myLineSeq) + '/approve';
    const payload = { comment };
    if (editMode) payload.editing = '1';
    try {
      const j = await post(url, payload);
      if (!j.ok) { alert(j.msg || '승인 실패'); return; }
      location.reload();
    } catch (e) {
      console.error(e); alert('승인 실패');
    }
  };

  window.reject = async function (editMode) {
    if (myLineSeq === null) { alert('처리 권한이 없습니다.'); return; }
    const comment = (document.getElementById('opinion')?.value || '').trim();
    if (!comment) { alert('반려 사유를 입력하세요.'); return; }
    const url = ctx + '/sign/lines/' + encodeURIComponent(myLineSeq) + '/reject';
    const payload = { comment };
    if (editMode) payload.editing = '1';
    try {
      const j = await post(url, payload);
      if (!j.ok) { alert(j.msg || '반려 실패'); return; }
      location.reload();
    } catch (e) {
      console.error(e); alert('반려 실패');
    }
  };
})();
</script>

<jsp:include page="../footer/footer.jsp"/>
