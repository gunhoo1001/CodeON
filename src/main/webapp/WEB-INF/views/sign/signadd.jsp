<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>기안문서 작성</title>
<style>
:root { --header-h:70px; --sidebar-w:220px; --bg:#f6f7fb; --card:#fff; --text:#111; --muted:#6b7280; --line:#e5e7eb; --brand:#2563eb; --brand-100:#e8eefc; --danger:#ef4444; --radius:16px; }
body { margin:0; background:var(--bg); font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial; }
.main-content { margin-left:var(--sidebar-w); padding:20px 28px 64px; min-height:100vh; box-sizing:border-box; }
.page-wrap { max-width:1200px; margin:24px auto; padding:0 16px; box-sizing:border-box }
.topbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px }
.h1 { margin:0; font-size:22px; font-weight:800 }
.btn { height:36px; padding:0 14px; border-radius:10px; border:1px solid var(--line); background:#fff; cursor:pointer }
.btn.brand { border-color:var(--brand); background:var(--brand); color:#fff }
.btn.ghost { background:#fff }
.badge-red { display:inline-flex; gap:8px; align-items:center; font-size:13px; color:var(--danger) }
.grid { display:grid; grid-template-columns:1fr 1.1fr; gap:14px }
.card { background:var(--card); border:1px solid var(--line); border-radius:var(--radius); overflow:hidden }
.card-h { padding:12px 14px; border-bottom:1px solid var(--line); font-weight:700 }
.card-b { padding:14px }
.row2 { display:grid; grid-template-columns:120px 1fr; gap:10px; align-items:center }
.input,.select,.date,.number,.textarea { height:40px; border:1px solid var(--line); border-radius:10px; padding:0 12px; box-sizing:border-box; width:100%; }
.textarea { height:180px; padding:10px 12px; resize:vertical }
.approval-table { width:100%; border-collapse:collapse }
.approval-table th,.approval-table td { border:1px solid var(--line); padding:8px }
.approval-table th { background:#f9fafb }
.empty { color:var(--muted); text-align:center; padding:24px 0 }
/* 탭 */
.doc-tabs { margin-top:14px }
.doc-tabs input[type=radio]{ display:none }
.tabbar { display:flex; gap:8px; flex-wrap:wrap }
.tabbar label { cursor:pointer; border:1px solid var(--line); background:#fff; border-radius:999px; padding:8px 14px; font-size:14px; }
#t-proposal:checked ~ .tabbar label[for=t-proposal],
#t-vacation:checked ~ .tabbar label[for=t-vacation],
#t-expense:checked  ~ .tabbar label[for=t-expense],
#t-trip:checked     ~ .tabbar label[for=t-trip] { border-color:var(--brand); background:rgba(37,99,235,.1); color:#1e40af; font-weight:700; }
.forms .form { display:none; margin-top:12px }
#t-proposal:checked ~ .forms .f-proposal,
#t-vacation:checked ~ .forms .f-vacation,
#t-expense:checked  ~ .forms .f-expense,
#t-trip:checked     ~ .forms .f-trip { display:block }
/* 지출내역 */
.exp-table { width:100%; border-collapse:collapse; margin-top:8px }
.exp-table th,.exp-table td { border:1px solid var(--line); padding:8px; text-align:left }
.exp-table th { background:#f9fafb }
.exp-actions { display:flex; gap:8px; margin-top:8px }
.sum { margin-top:8px; color:#111; font-weight:700; text-align:right }
.small { height:32px; padding:0 10px; border-radius:8px }
</style>
</head>

<body>
<div class="main-content">
  <div class="page-wrap">
    <!-- 상단 -->
    <div class="topbar">
      <h1 class="h1">기안문서 작성</h1>
      <div style="display:flex;gap:10px;align-items:center">
        <label class="badge-red">
          <input id="urgent" type="checkbox" style="accent-color:#ef4444" />
          긴급 문서
        </label>
        <button type="button" class="btn ghost" id="btnCancel">취소</button>
        <button type="button" class="btn brand" id="btnSubmit">상신</button>
      </div>
    </div>

    <!-- 문서정보 / 결재라인 -->
    <div class="grid">
      <!-- 문서정보 -->
      <section class="card">
        <div class="card-h">문서정보</div>
        <div class="card-b" style="display:grid;gap:10px">
          <div class="row2">
            <div>기안자</div>
            <input class="input" id="drafter" value="${sessionScope.loginuser.memberName}" readonly>
          </div>
          <div class="row2">
            <div>소속</div>
           	<input class="input" id="dept" value="${loginDeptName}" readonly>
          </div>
          <div class="row2">
            <div>기안일</div>
            <input class="input" id="draftDate" value="<%= java.time.LocalDate.now().toString() %>" readonly>
          </div>
          <div class="row2">
            <div>문서번호</div>
            <input class="input" id="docNo" value="${requestScope.previewNo}" readonly>
          </div>
        </div>
      </section>

      <!-- 결재라인 -->
      <section class="card">
        <div class="card-h" style="display:flex;justify-content:space-between;align-items:center">
          <span>결재라인</span>
          <div style="display:flex;gap:8px">
            <button type="button" class="btn small" id="btnEditLine">선택하기</button>
          </div>
        </div>
        <div class="card-b">
          <table class="approval-table">
            <thead>
              <tr><th style="width:70px">순서</th><th>소속</th><th style="width:120px">직급</th><th style="width:140px">성명</th></tr>
            </thead>
            <tbody id="apprTbody">
              <tr><td colspan="4" class="empty">결재자를 선택하세요.</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>

    <!-- 카테고리 & 폼 -->
    <section class="card doc-tabs" style="margin-top:14px">
      <div class="card-h">결재 양식</div>
      <div class="card-b">
        <!-- 탭 라디오 -->
        <input type="radio" id="t-proposal" name="doctype" checked>
        <input type="radio" id="t-vacation" name="doctype">
        <input type="radio" id="t-expense"  name="doctype">
        <input type="radio" id="t-trip"     name="doctype">

        <!-- 탭바 -->
        <div class="tabbar" role="tablist">
          <label for="t-proposal" role="tab" aria-controls="pane-proposal">업무품의서</label>
          <label for="t-vacation" role="tab" aria-controls="pane-vacation">휴가 신청서</label>
          <label for="t-expense"  role="tab" aria-controls="pane-expense">지출 결의서</label>
          <label for="t-trip"     role="tab" aria-controls="pane-trip">출장 보고서</label>
        </div>

        <!-- 폼들 -->
        <div class="forms">
          <!-- 업무품의서 -->
          <section id="pane-proposal" class="form f-proposal">
            <div class="row2"><div>제목</div><input class="input" id="p-title" placeholder="제목을 입력하세요."></div>
            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">내용</div>
              <textarea class="textarea" id="p-body" placeholder="내용을 입력하세요."></textarea>
            </div>
            <div style="margin-top:10px;display:flex;gap:8px;align-items:center">
              <input type="file" id="p-file" multiple>
            </div>
          </section>

          <!-- 휴가 신청서 -->
          <section id="pane-vacation" class="form f-vacation">
            <div class="row2">
              <div>제목</div>
              <input class="input" id="v-title" placeholder="예) 연차 신청">
            </div>

            <div class="row2" style="margin-top:8px">
              <div>휴가 종류</div>
              <select class="select" id="v-type">
                <option value="ANNUAL">연차</option>
                <option value="HALF">반차</option>
              </select>
            </div>

            <div class="row2" style="margin-top:8px">
              <div>기간</div>
              <div style="display:flex;gap:8px">
                <input type="date" class="date" id="v-from">
                <span style="align-self:center">~</span>
                <input type="date" class="date" id="v-to">
              </div>
            </div>

            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">사유</div>
              <textarea class="textarea" id="v-reason" placeholder="사유를 입력하세요."></textarea>
            </div>

            <div style="margin-top:10px;display:flex;gap:8px;align-items:center">
              <input type="file" id="v-file" multiple>
            </div>
          </section>

          <!-- 지출 결의서 -->
          <section id="pane-expense" class="form f-expense">
            <div class="row2"><div>제목</div><input class="input" id="e-title"></div>
            <div class="row2" style="margin-top:8px"><div>지출 사유</div><input class="input" id="e-reason"></div>

            <div style="margin-top:12px">
              <div style="display:flex;justify-content:space-between;align-items:center">
                <strong>지출 내역</strong>
                <div class="exp-actions">
                  <button type="button" class="btn small" id="btnAddRow">행 추가</button>
                  <button type="button" class="btn small" id="btnDelRow">선택 삭제</button>
                </div>
              </div>
              <table class="exp-table" id="expTable">
                <thead>
                  <tr>
                    <th style="width:140px">지출일자</th>
                    <th>사용처</th>
                    <th style="width:160px">금액</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><input type="date" class="date"></td>
                    <td><input class="input" placeholder="사용처 입력"></td>
                    <td><input type="number" class="number money" min="0" step="100" placeholder="0"></td>
                  </tr>
                </tbody>
              </table>
              <div class="sum">합계: <span id="sumMoney">0</span> 원</div>
            </div>

            <div style="margin-top:10px;display:flex;gap:8px;align-items:center">
              <input type="file" id="e-file" multiple>
            </div>
          </section>

          <!-- 출장 보고서 -->
          <section id="pane-trip" class="form f-trip">
            <div class="row2"><div>제목</div><input class="input" id="t-title"></div>
            <div class="row2" style="margin-top:8px"><div>출장 목적</div><input class="input" id="t-purpose" placeholder="목적 입력"></div>
            <div class="row2" style="margin-top:8px"><div>출장 기간</div>
              <div style="display:flex;gap:8px">
                <input type="date" class="date" id="t-from"><span style="align-self:center">~</span>
                <input type="date" class="date" id="t-to">
              </div>
            </div>
            <div class="row2" style="margin-top:8px"><div>출장 지역</div><input class="input" id="t-area" ></div>
            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">출장 결과</div>
              <textarea class="textarea" id="t-result" placeholder="결과를 입력하세요."></textarea>
            </div>

            <div style="margin-top:10px;display:flex;gap:8px;align-items:center">
              <input type="file" id="t-file" multiple>
            </div>
          </section>
        </div>
      </div>
    </section>
  </div>

  <!-- 팝업이 히든 넣는 전용 폼(그대로 사용) -->
  <form id="draftForm" method="post" enctype="multipart/form-data" style="display:none"></form>
</div>

<script type="text/javascript">
  const ctx = "${pageContext.request.contextPath}";

  function openSignlineLoadPopup(){
    const w=1000,h=700;
    const dualLeft=(window.screenLeft??window.screenX??0);
    const dualTop =(window.screenTop ??window.screenY??0);
    const vw=window.outerWidth||document.documentElement.clientWidth||screen.width;
    const vh=window.outerHeight||document.documentElement.clientHeight||screen.height;
    const left=Math.max(0, Math.round(dualLeft + (vw - w)/2));
    const top =Math.max(0, Math.round(dualTop  + (vh - h)/2));
    const features=[`width=${w}`,`height=${h}`,`left=${left}`,`top=${top}`,'resizable=yes','scrollbars=yes','toolbar=no','location=no','status=no','menubar=no'].join(',');
    const win=window.open(ctx + "/sign/line/load", "signlineLoadPopup", features);
    if (win) { try { win.focus(); win.resizeTo(w,h); win.moveTo(left,top); } catch(_){} }
  }

  // 팝업 콜백(원래 로직 유지)
  window.receiveSignline = function(approvers){
    const $tb = $("#apprTbody").empty();
    if (!approvers || approvers.length === 0) {
      $tb.html('<tr><td colspan="4" class="empty">결재자를 선택하세요.</td></tr>');
    } else {
      approvers.forEach((p, idx) => {
        const dept  = p.dept  ?? p.departmentName ?? (p.department && p.department.departmentName) ?? '';
        const grade = p.grade ?? p.gradeName      ?? (p.grade && p.grade.gradeName)               ?? '';
        const name  = p.name ?? p.memberName ?? '';
        const $tr = $("<tr/>");
        $tr.append($("<td/>", { class:"center", text: idx + 1 }));
        $tr.append($("<td/>", { text: dept }));
        $tr.append($("<td/>", { class:"center", text: grade }));
        $tr.append($("<td/>", { class:"center", text: name }));
        $tb.append($tr);
      });
    }
    // hidden inputs(원래 방식)
    const $form = $("#draftForm").empty();
    (approvers || []).forEach((p, i) => {
      $("<input>").attr({ type:"hidden", name:"approverSeq" }).val(p.memberSeq).appendTo($form);
      $("<input>").attr({ type:"hidden", name:"lineOrder"   }).val(i + 1).appendTo($form);
    });
  };

  // 합계
  function recalcSum(){
    let sum=0;
    $("#expTable tbody .money").each(function(){
      const v = Number(String($(this).val()).replace(/,/g,'')) || 0;
      sum += v;
    });
    $("#sumMoney").text(sum.toLocaleString());
    return sum;
  }

  // ★ 반차 선택 시 종료일 고정/비활성화
  function syncHalfDay(){
    const type = $("#v-type").val(); // "ANNUAL" | "HALF"
    const from = $("#v-from").val();
    if (type === "HALF") {
      if (from) $("#v-to").val(from);
      $("#v-to").prop("disabled", true);
    } else {
      $("#v-to").prop("disabled", false);
    }
  }

  // 상신 클릭 → 선택 탭만 모아서 각 타입 전용 컨트롤러로 전송
  $("#btnSubmit").on("click", async function(){
    // 결재자 필수
    if ($('#draftForm input[name="approverSeq"]').length === 0) {
      alert("결재자를 선택하세요.");
      return;
    }

    const memberSeq = "${sessionScope.loginuser.memberSeq}";
    const isEmergency = $("#urgent").is(":checked") ? 1 : 0;

    // 타입 판단(휴가1/출장2/품의3/지출4 기준)
    const isVac   = $("#t-vacation").is(":checked");
    const isTrip  = $("#t-trip").is(":checked");
    const isProp  = $("#t-proposal").is(":checked");
    const isExp   = $("#t-expense").is(":checked");

    let url = "";
    const fd = new FormData();

    // 공통
    fd.append("fk_member_seq", memberSeq);
    fd.append("is_emergency", isEmergency);

    // 결재라인
    $("#draftForm input[name='approverSeq']").each(function(){ fd.append("approverSeq", this.value); });
    $("#draftForm input[name='lineOrder']").each(function(){ fd.append("lineOrder", this.value); });

    // 타입별 분기 + 요약 필드 포함
    if (isProp) {
      url = ctx + "/sign/draft/proposal";
      fd.append("fk_draft_type_seq", 3);
      const title = $("#p-title").val() || "";
      const body  = $("#p-body").val()  || "";
      fd.append("conform_title",   title);
      fd.append("conform_content", body);
      fd.append("draft_title",     title);
      fd.append("draft_content",   body);
      const files = ($("#p-file")[0] && $("#p-file")[0].files) || [];
      for (let i=0;i<files.length;i++) fd.append("files", files[i], files[i].name);

    } else if (isVac) {
      url = ctx + "/sign/draft/vacation";
      fd.append("fk_draft_type_seq", 1);

      const title = $("#v-title").val()   || "";
      const start = $("#v-from").val()    || "";
      const type  = $("#v-type").val();                 // "ANNUAL" | "HALF"
      const end   = (type === "HALF") ? start : ($("#v-to").val() || "");
      const reason= $("#v-reason").val()  || "";

      if (!start) { alert("휴가 시작일을 입력하세요."); return; }
      if (type !== "HALF" && !end) { alert("휴가 종료일을 입력하세요."); return; }
      if (type !== "HALF" && start > end) { alert("종료일이 시작일보다 빠릅니다."); return; }

      fd.append("vacation_title",   title);
      fd.append("vacation_start",   start);
      fd.append("vacation_end",     end);
      fd.append("vacation_content", reason);
      fd.append("vacation_type",    type);
      fd.append("draft_title",      title);
      fd.append("draft_content",    reason);

      const vfiles = ($("#v-file")[0] && $("#v-file")[0].files) || [];
      for (const f of vfiles) fd.append("files", f, f.name);

    } else if (isExp) {
      url = ctx + "/sign/draft/expense";
      fd.append("fk_draft_type_seq", 4);
      const title = $("#e-title").val()  || "";
      const why   = $("#e-reason").val() || "";
      fd.append("payment_title",   title);
      fd.append("payment_content", why);

      // 리스트
      $("#expTable tbody tr").each(function(){
        const reg = $(this).find("input[type='date']").val() || "";
        const use = $(this).find("input.input").val()         || "";
        const amt = $(this).find(".money").val()              || "0";
        fd.append("payment_list_regdate",  reg);
        fd.append("payment_list_content",  use);
        fd.append("payment_list_price[]",  amt);
      });

      // 합계
      const total = recalcSum();
      fd.append("total_amount", total);
      fd.append("draft_title",  title);
      fd.append("draft_content",why);

      const efiles = ($("#e-file")[0] && $("#e-file")[0].files) || [];
      for (const f of efiles) fd.append("files", f, f.name);

    } else if (isTrip) {
      url = ctx + "/sign/draft/trip";
      fd.append("fk_draft_type_seq", 2);
      const title   = $("#t-title").val()   || "";
      const purpose = $("#t-purpose").val() || "";
      const start   = $("#t-from").val()    || "";
      const end     = $("#t-to").val()      || "";
      const loc     = $("#t-area").val()    || "";
      const result  = $("#t-result").val()  || "";
      fd.append("business_title",    title);
      fd.append("business_content",  purpose);
      fd.append("business_start",    start);
      fd.append("business_end",      end);
      fd.append("business_location", loc);
      fd.append("business_result",   result);
      fd.append("draft_title",       title);
      fd.append("draft_content",     result);

      const tfiles = ($("#t-file")[0] && $("#t-file")[0].files) || [];
      for (const f of tfiles) fd.append("files", f, f.name);
    }

    if (!url) { alert("문서 유형을 선택하세요."); return; }

    try {
      const res = await fetch(url, { method:"POST", body: fd });
      if (!res.ok) throw new Error((await res.text()) || ("HTTP " + res.status));
      location.href = ctx + "/sign/main";
    } catch (e) {
      alert("상신 실패: " + e.message);
    }
  });

  // 부가: 지출행 추가/삭제 + 휴가 반차 동기화
  $(function(){
    $("#btnEditLine").on("click", openSignlineLoadPopup);

    $(document).on("input", "#expTable tbody .money", recalcSum);
    $("#btnAddRow").on("click", function(){
      $("#expTable tbody").append(
        '<tr>'
        + '<td><input type="date" class="date"></td>'
        + '<td><input class="input" placeholder="사용처 입력"></td>'
        + '<td><input type="number" class="number money" min="0" step="100" placeholder="0"></td>'
        + '</tr>'
      );
    });
    $("#btnDelRow").on("click", function(){
      const $rows = $("#expTable tbody tr");
      if($rows.length > 1) $rows.last().remove();
      recalcSum();
    });

    $("#btnCancel").on("click", function(){ history.back(); });

    // ★ 휴가 폼 이벤트 바인딩
    $("#v-type, #v-from").on("change input", syncHalfDay);
    syncHalfDay(); // 초기 호출
  });
</script>

</body>
</html>

<jsp:include page="../footer/footer.jsp" />
