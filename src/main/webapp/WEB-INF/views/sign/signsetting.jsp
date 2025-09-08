<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
String ctxPath = request.getContextPath();
%>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<style>
:root {
	--header-h: 70px;
	--sidebar-w: 220px;
	--bg: #f6f7fb;
	--card: #fff;
	--text: #111;
	--muted: #6b7280;
	--line: #e5e7eb;
	--brand: #2563eb;
	--brand-100: #e8eefc;
	--danger: #ef4444;
	--radius: 16px;
}

.main-content {
	margin-left: var(--sidebar-w);
	padding: 20px 28px 64px;
	min-height: 100vh;
	box-sizing: border-box;
}

.grid-1 {
	display: grid;
	grid-template-columns: 1fr;
	gap: 18px;
}

.card {
	background: var(--card);
	border: 1px solid var(--line);
	border-radius: var(--radius);
	box-shadow: 0 1px 3px rgba(0, 0, 0, .04);
}

.card-head, .card-foot {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px;
	border-bottom: 1px solid var(--line);
}

.card-foot {
	border-top: 1px solid var(--line);
	border-bottom: none;
}

.card h2 {
	font-size: 16px;
	margin: 0;
}

/* ===== 도장 업로더 ===== */
.stamp-wrap {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 12px;
}

.stamp-head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px;
	border-bottom: 1px solid var(--line);
}

.stamp-body {
	display: flex;
	gap: 24px;
	padding: 16px;
	align-items: flex-start;
	flex-wrap: wrap;
}

.stamp-foot {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 16px;
	border-top: 1px solid var(--line);
}

#stampDrop {
	flex: 1;
	min-width: 320px;
	height: 220px;
	border: 2px dashed var(--line);
	border-radius: 10px;
	background: #fafafa;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	cursor: pointer;
	position: relative;
	padding: 10px;
	transition: background-color .2s, border-color .2s;
}

#stampDrop.dragover {
	background: var(--brand-100);
	border-color: var(--brand);
}

#stampPreview {
	max-width: 100%;
	max-height: 100%;
	object-fit: contain;
	display: none;
	margin-top: 8px;
}

.stamp-hint {
	color: #6b7280;
	font-size: 14px;
}

.stamp-right {
	min-width: 260px;
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.stamp-right .btn {
	height: 38px;
}

.btn-primary {
	background: #2563eb;
	color: #fff;
	border: 1px solid #2563eb;
	border-radius: 8px;
	cursor: pointer;
	width: 150px;
	height: 30px;
}

.btn-outline {
	background: #fff;
	color: #2563eb;
	border: 1px solid #2563eb;
	border-radius: 8px;
	cursor: pointer;
	width: 120px;
	height: 30px;
}

.stamp-input {
	height: 38px;
	padding: 0 10px;
	border: 1px solid var(--line);
	border-radius: 8px;
}

/* ===== 결재라인: 카드/목록 ===== */
.line-actions {
	display: flex;
	gap: 8px;
	align-items: center;
}

.saved-lines {
	padding: 16px;
}

.line-card {
	border: 1px solid var(--line);
	border-radius: 12px;
	background: #fff;
	margin-bottom: 10px;
	overflow: hidden;
}

.line-card .h {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 10px 12px;
	background: #f9fafb;
	border-bottom: 1px solid var(--line);
}

.line-card .b {
	padding: 10px 12px;
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	color: var(--muted);
}

.badge {
	border: 1px solid var(--line);
	border-radius: 999px;
	padding: 4px 10px;
	font-size: 12px;
	background: #fff;
}

.btn {
	height: 38px;
	padding: 0 14px;
	border-radius: 10px;
	border: 1px solid var(--line);
	background: #fff;
	cursor: pointer;
}

.btn.small {
	height: 30px;
	padding: 0 10px;
	font-size: 12px;
}

.btn.icon {
	width: 30px;
	height: 30px;
	padding: 0;
}

.btn.danger {
	border-color: var(--danger);
	color: #fff;
	background: var(--danger);
}

/* 결재자 칩 리스트 */
.approver-list{display:flex;flex-wrap:wrap;gap:8px;margin:10px 0 2px}
.approver-chip{
  display:flex;align-items:center;gap:8px;
  padding:6px 10px;border:1px solid var(--line);
  border-radius:999px;background:#f9fafb;font-size:13px
}
.ord-badge{
  width:22px;height:22px;border-radius:50%;
  display:inline-flex;align-items:center;justify-content:center;
  border:1px solid var(--line);font-weight:700;
  background:var(--brand-100);color:var(--brand)
}
.sub-tag{
  padding:2px 6px;border:1px solid var(--line);
  border-radius:999px;background:#fff;color:var(--muted);font-size:12px
}

</style>

<script>

window.esc = window.esc || function(s){
	  return String(s ?? '')
	    .replace(/&/g,'&amp;').replace(/</g,'&lt;')
	    .replace(/>/g,'&gt;').replace(/"/g,'&quot;')
	    .replace(/'/g,'&#39;');
	};
	
/* =========================
   전역: CSRF 헤더 자동 세팅(있으면)
   ========================= */
$(function(){
  const token = $('meta[name="_csrf"]').attr('content');
  const header= $('meta[name="_csrf_header"]').attr('content');
  if(token && header){
    $(document).ajaxSend(function(e, xhr){ xhr.setRequestHeader(header, token); });
  }
  
  initStampPreviewFromServer();
});

/* ====== 도장 업로더 ====== */
let total_fileSize = 0;
let file_arr_copy = [];

function stampUpdatePreviewAndFileName(file){
  if(!(file.type === "image/jpeg" || file.type === "image/png")){
    alert("jpg 또는 png 파일만 가능합니다."); return;
  }
  if(file.size >= 10 * 1024 * 1024){
    alert("10MB 이상인 이미지는 업로드 불가합니다."); return;
  }

  file_arr_copy = [file];
  total_fileSize = file.size;

  const fr = new FileReader();
  fr.readAsDataURL(file);
  fr.onload = function(){
    $("#stampPreview").attr("src", fr.result).show().css("display","block");
    $("#stampDrop .stamp-hint").hide();
  };
  $("#filename").val(file.name);

  const dt = new DataTransfer();
  dt.items.add(file);
  $("#stampFile")[0].files = dt.files;
}

// 문서 전체 기본 드래그 동작 차단
$(document).on("dragover drop", function(e){ e.preventDefault(); e.stopPropagation(); });

$(function(){
  $("#stampFile").on("change", function(e){
    const f = e.target.files && e.target.files[0];
    if(f) stampUpdatePreviewAndFileName(f);
  });

  $("#stampDrop")
    .on("dragenter dragover", function(e){
      e.preventDefault(); e.stopPropagation();
      $(this).addClass("dragover");
    })
    .on("dragleave", function(e){
      e.preventDefault(); e.stopPropagation();
      $(this).removeClass("dragover");
    })
    .on("drop", function(e){
      e.preventDefault(); e.stopPropagation();
      $(this).removeClass("dragover");
      const oe = e.originalEvent || e;
      const dt = oe.dataTransfer || e.dataTransfer;
      const files = dt && dt.files ? dt.files : null;
      if(files && files.length > 0){
        stampUpdatePreviewAndFileName(files[0]);
      }
    })
    .on("click", function(e){
      if (!$(e.target).is("#stampFile")){
        $("#stampFile").trigger("click");
      }
    });

  $("#btnStampPick").on("click", function(){
    $("#stampFile").trigger("click");
  });

  // 초기화(서버 삭제 + 화면 리셋)
  $("#btnStampReset").on("click", function(){
    if(!confirm("정말 초기화하시겠습니까? 기존 도장 이미지가 삭제됩니다.")) return;

    $.ajax({
      url: "<%=ctxPath%>/sign/stampImageDelete",
      type: "POST",
      success: function(res){
        const ok = res && (res.result === "success" || res.success === true);
        if(ok){
          file_arr_copy = [];
          total_fileSize = 0;
          $("#stampPreview").attr("src","").hide();
          $("#stampDrop .stamp-hint").show();
          $("#filename").val("");
          $("#stampFile").val("");

          $("#stampDrop")
            .removeData("stamp-url").removeAttr("data-stamp-url")
            .removeData("stamp-fn").removeAttr("data-stamp-fn");

          alert("도장 이미지가 삭제되었습니다.");
        }else{
          alert("삭제에 실패했습니다.");
        }
      },
      error: function(xhr, status, error){
        console.error(error);
        alert("삭제 중 오류가 발생했습니다: " + (xhr.responseText || status));
      }
    });
  });
});

// 업로드 저장
function stampImageSave(){
  const input = $("#stampFile")[0];
  if(!input.files || input.files.length === 0){
    alert("업로드할 도장 이미지를 선택하세요.");
    return;
  }
  const file = input.files[0];

  $.ajax({
    url: "<%=ctxPath%>/sign/stampImageSave",
    type: "POST",
    data: file,
    processData: false,
    contentType: "application/octet-stream",
    headers: { "file-name": encodeURIComponent(file.name) },
    success: function(res){
      const ok = res && (res.result === "success" || res.success === true);
      if(ok){
        alert("저장이 완료되었습니다.");
        if(res.url){
          $("#stampPreview").attr("src", res.url).show().css("display","block");
          $("#stampDrop .stamp-hint").hide();
        }
        $("#filename").val(file.name);
      }else{
        alert("저장에 실패하였습니다.");
      }
    },
    error: function(xhr, status, error){
      console.error(error);
      alert("업로드 중 오류가 발생했습니다: " + (xhr.responseText || status));
    }
  });
}

// 초기 미리보기 로드
function initStampPreviewFromServer(){
  const $drop = $("#stampDrop");
  if(!$drop.length) return; // 가드

  // camelCase + attr() 폴백
  let url = $drop.data("stampUrl") || $drop.attr("data-stamp-url") || "";
  const fn  = $drop.data("stampFn")  || $drop.attr("data-stamp-fn")  || "";
  const ctx = "<%=ctxPath%>";

  // ctxPath 보정
  if (url && url.startsWith("/resources/") && ctx) url = ctx + url;
  if (!url && fn) url = ctx + "/resources/stamp_upload/" + encodeURIComponent(fn);
  if (!url) return;

  // 디버그 로그
  console.log("stamp preview url:", url);

  // 캐시버스터 + 로드/에러 핸들러
  url += (url.includes("?") ? "&" : "?") + "v=" + Date.now();

  $("<img/>")
    .on("load", function(){
      $("#stampPreview").attr("src", url).show();
      $("#stampDrop .stamp-hint").hide();
    })
    .on("error", function(){
      console.warn("stamp preview load failed:", url);
    })
    .attr("src", url);
}

/* =========================
   결재라인: 목록 + 팝업 열기
   ========================= */

// 팝업 열기 유틸
function openLinePopup(id){
  const w = 1000, h = 700;

  // 현재 브라우저 창의 화면 좌표(멀티모니터 대응)
  const dualLeft = (window.screenLeft ?? window.screenX ?? 0);
  const dualTop  = (window.screenTop  ?? window.screenY  ?? 0);

  // 브라우저 바깥 크기(툴바 포함) 우선 사용
  const viewportW = window.outerWidth  || document.documentElement.clientWidth  || screen.width;
  const viewportH = window.outerHeight || document.documentElement.clientHeight || screen.height;

  const left = Math.max(0, Math.round(dualLeft + (viewportW - w) / 2));
  const top  = Math.max(0, Math.round(dualTop  + (viewportH - h) / 2));

  const url = id
    ? "<%=ctxPath%>/sign/setting/line?id=" + encodeURIComponent(id)
    : "<%=ctxPath%>/sign/setting/line";

  // features 문자열은 공백 없이!
  const features = [
    `width=${w}`, `height=${h}`,
    `left=${left}`, `top=${top}`,
    'resizable=yes','scrollbars=yes',
    'toolbar=no','location=no','status=no','menubar=no'
  ].join(',');

  const win = window.open(url, 'linePopup', features);

  // 같은 이름의 창 재사용 시 크기/위치 보정
  if (win) {
    try {
      win.focus();
      win.resizeTo(w, h);
      win.moveTo(left, top);
    } catch (_) { /* same-origin이 아닐 때 접근 제한될 수 있음 */ }
  }
}


// 이벤트 바인딩
$(function(){
  $("#btnOpenLinePopup").on("click", function(){ openLinePopup(null); });
  $("#btnReloadLines").on("click", loadSavedLines);
  loadSavedLines(); // 초기 목록
});

// 저장된 라인 목록 로드/렌더
function loadSavedLines(){
  $("#savedLines").html('<div class="sel-cap" style="padding:8px 12px;">불러오는 중...</div>');

  $.ajax({
    url: "<%=ctxPath%>/sign/lines",   // ★ JSON 반환하는 엔드포인트
    type: "GET",
    dataType: "json",                  // JSON만 기대
    cache: false,                      // 캐시 방지 (_=타임스탬프 자동 추가)
    headers: { "Accept": "application/json" },
    success: function(list){
      if (!Array.isArray(list) || list.length === 0) {
        $("#savedLines").html('<div class="sel-cap" style="padding:8px 12px;">저장된 결재라인이 없습니다.</div>');
        return;
      }
      renderSavedLines(list);
    },
    error: function(xhr, status, err){
      console.error("loadSavedLines error:", status, err, xhr);
      $("#savedLines").html('<div class="sel-cap" style="padding:8px 12px;">불러오기 실패</div>');
    }
  });
}

function renderSavedLines(list){
	  if (!Array.isArray(list) || list.length === 0){
	    $("#savedLines").html('<div class="sel-cap" style="padding:8px 12px;">저장된 결재라인이 없습니다.</div>');
	    return;
	  }

	  let html = '';
	  list.forEach(item => {
	    const id   = item.signlineSeq;
	    const name = esc(item.signlineName || '이름 없음');
	    const members = Array.isArray(item.members) ? item.members : [];

	    const chips = members.map(m =>
	      '<li class="approver-chip">'
	      +  '<span class="ord-badge">' + (m.lineOrder ?? '') + '</span>'
	      +  '<strong>' + esc(m.memberName || '') + '</strong>'
	      +  (m.deptName ? '<span class="sub-tag">' + esc(m.deptName) + '</span>' : '')
	      + '</li>'
	    ).join('');

	    html +=
	      '<div class="line-card">'
	      +  '<div class="h">'
	      +    '<strong>' + name + '</strong>'
	      +    '<div class="line-actions">'
	      +      '<button type="button" class="btn small" onclick="openLinePopup(' + id + ')">편집</button>'
	      +      '<button type="button" class="btn small danger" onclick="deleteLine(' + id + ')">삭제</button>'
	      +    '</div>'
	      +  '</div>'
	      +  '<div class="b"><span class="badge">결재자 ' + members.length + '명</span></div>'
	      +  '<ul class="approver-list">' + (chips || '<li class="cap">결재자 없음</li>') + '</ul>'
	      + '</div>';
	  });

	  $("#savedLines").html(html);
	}

function deleteLine(id){
	  if (!confirm('해당 결재라인을 삭제할까요?')) return;

	  $.ajax({
	    url: "<%=ctxPath%>/sign/lines/" + encodeURIComponent(id) + "/delete",
	    type: "POST",
	    dataType: "json",
	    success: function(res){
	      if(res && res.ok){
	        loadSavedLines(); // 목록 갱신
	      }else{
	        alert(res && res.msg ? res.msg : '삭제에 실패했습니다.');
	      }
	    },
	    error: function(xhr, status, err){
	      console.error(err);
	      alert('삭제 중 오류가 발생했습니다: ' + (xhr.responseText || status));
	    }
	  });
	}


</script>

<div class="header-spacer"></div>

<main class="main-content">
	<section class="grid-1">
		<!-- ===== 1) 도장 업로드 ===== -->
		<article class="stamp-wrap">
			<div class="stamp-head">
				<h2 style="margin: 0; font-size: 16px;">승인 도장 이미지 업로드</h2>
				<button type="button" id="btnStampSave" class="btn-primary"
					onclick="stampImageSave()">도장 저장</button>
			</div>

			<div class="stamp-body">
				<!-- 드래그&드롭 박스 -->
				<div id="stampDrop" title="도장 이미지를 드래그하거나 클릭하여 선택"
					data-stamp-fn="${fn:escapeXml(stampFilename)}"
					data-stamp-url="${fn:escapeXml(stampUrl)}">
					<div class="stamp-hint">
						여기로 드래그해서 업로드<br>(또는 클릭)
					</div>
					<img id="stampPreview" alt="도장 미리보기" /> <input type="file"
						name="stamp_image" id="stampFile" accept="image/jpeg,image/png"
						style="display: none;">
				</div>

				<!-- 우측 정보/버튼 -->
				<div class="stamp-right">
					<input type="text" id="filename" class="stamp-input"
						placeholder="선택된 파일 없음" readonly>
					<div style="display: flex; gap: 8px;">
						<button type="button" id="btnStampPick" class="btn-outline"
							style="flex: 1;">파일첨부</button>
						<button type="button" id="btnStampReset" class="btn-outline"
							style="flex: 1;">초기화</button>
					</div>
					<div class="stamp-hint">PNG/JPG · 10MB 이하 권장 · 투명 배경 추천</div>
				</div>
			</div>

			<div class="stamp-foot">
				<span class="stamp-hint">업로드 후 <b>도장 저장</b> 버튼을 누르면 적용됩니다.
				</span>
			</div>
		</article>

		<!-- ===== 2) 결재라인 설정 ===== -->
		<article class="card">
			<header class="card-head">
				<h2>결재라인 설정</h2>
				<div class="line-actions">
					<button type="button" class="btn small" id="btnOpenLinePopup">결재라인
						추가</button>
					<button type="button" class="btn small" id="btnReloadLines">새로고침</button>
				</div>
			</header>

			<!-- 저장된 라인 목록 -->
			<div id="savedLines" class="saved-lines">
				<!-- JS 렌더링 -->
			</div>
		</article>
	</section>
</main>

<jsp:include page="../footer/footer.jsp" />


