<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>결재라인 추가</title>
  <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
  <style>
body {
	margin: 0;
	font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial;
}

.wrap {
	display: grid;
	grid-template-rows: auto 1fr auto;
	height: 100vh;
}

header, footer {
	padding: 10px 12px;
	border-bottom: 1px solid #e5e7eb;
	background: #f9fafb;
}

footer {
	border-top: 1px solid #e5e7eb;
	border-bottom: none;
}

main {
	display: grid;
	grid-template-columns: 1.2fr 1fr;
	gap: 0;
	min-height: 0;
}

.pane {
	min-height: 0;
}

.left {
	border-right: 1px solid #e5e7eb;
}

.search {
	padding: 8px;
	border-bottom: 1px solid #e5e7eb;
}

.input {
	height: 36px;
	width: 100%;
	box-sizing: border-box;
	padding: 0 10px;
	border: 1px solid #e5e7eb;
	border-radius: 8px;
}

.tree {
  padding: 16px 18px;
  overflow: auto;
  max-height: calc(100vh - 140px);
}

.group {
	border: 1px solid #e5e7eb;
	border-radius: 12px;
	margin-bottom: 10px;
	overflow: hidden;
}

.gh {
	background: #f9fafb;
	padding: 8px 10px;
	font-weight: 600;
	display: flex;
	justify-content: space-between;
	cursor: pointer;
}

.gl {
	padding: 8px 10px;
	display: grid;
	grid-template-columns: 1fr;
	gap: 6px;
	max-height: 200px;
	overflow: auto;
}

.right {
	padding: 10px;
	display: flex;
	flex-direction: column;
	gap: 12px;
	overflow: auto;
}

.cap {
	font-size: 12px;
	color: #6b7280;
}

.box {
	border: 1px dashed #e5e7eb;
	border-radius: 12px;
	padding: 10px;
	min-height: 120px;
}

.row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	border: 1px solid #e5e7eb;
	border-radius: 10px;
	padding: 8px 10px;
	margin-bottom: 8px;
}

.ord {
	width: 28px;
	text-align: center;
	font-weight: 700;
}

.btn {
	height: 34px;
	padding: 0 12px;
	border-radius: 8px;
	border: 1px solid #e5e7eb;
	background: #fff;
	cursor: pointer;
}

.btn.brand {
	border-color: #2563eb;
	color: #fff;
	background: #2563eb;
}

.btn.danger {
	border-color: #ef4444;
	color: #fff;
	background: #ef4444;
}

.actions .btn {
	height: 28px;
}

.row .meta {
	display: flex;
	gap: 10px;
}

.bar {
	display: flex;
	gap: 8px;
	align-items: center;
}

.title {
	font-size: 16px;
	font-weight: 700;
	margin: 0;
}

/* 왼쪽 영역 여백 조금 키우기 */
.tree { padding: 16px 18px; }

/* 직원 테이블 */
#deptTree table{
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 10px;        /* 행 간격 */
  position: relative;            /* stacking context */
}

/* 헤더 고정 및 간격 보정 */
#deptTree thead{
  position: sticky;
  top: -10px;   /* border-spacing 세로값(10px)만큼 끌어올리기 */
  z-index: 30;
}

#deptTree thead th {
  padding: 10px 12px;
  background: #f9fafb;           /* 불투명 배경 */
  position: sticky;
  top: 0;
  z-index: 31;
  text-align: center;
  vertical-align: middle;
  box-shadow: 0 1px 0 #e5e7eb;   /* 헤더 경계선 강조 */
}

/* 본문 */
#deptTree tbody{ 
  position: relative; 
  z-index: 1; 
}

#deptTree tbody td{
  position: relative;
  z-index: 1;
  padding: 10px 12px;
  background: #fff;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  border-left: 0; border-right: 0;
  text-align: center;
  vertical-align: middle;
}

/* 행을 카드처럼 둥글게 */
#deptTree tbody td:first-child{
  border-left: 1px solid #e5e7eb;
  border-radius: 10px 0 0 10px;
}
#deptTree tbody td:last-child{
  border-right: 1px solid #e5e7eb;
  border-radius: 0 10px 10px 0;
}

/* 버튼 크기 */
#deptTree .btn{
  height: 28px;
  padding: 0 10px;
}

</style>

<!-- CSRF 메타 추가 -->
  <c:if test="${_csrf != null}">
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
    <meta name="_csrf_parameter" content="${_csrf.parameterName}">
  </c:if>
</head>

<script>
var deptMap  = {10:"인사팀", 20:"개발팀", 30:"기획팀", 40:"영업팀", 50:"고객지원팀"};
var gradeMap = {1:"사원",   2:"대리",   3:"과장",   4:"부장",   5:"사장"};

function esc(s){ s=s||''; return String(s)
  .replace(/&/g,'&amp;').replace(/</g,'&lt;')
  .replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#39;'); }

$(function(){
	loadMembers();
	
  $("#btnCancel").on("click", function(){
	    window.close();
	  });
});   // 페이지 로드시 호출
	
function loadMembers() {
	  $.ajax({
	    url: "<%= ctxPath %>/sign/members",
	    dataType: "json",
	    success: function(json) {
	      var v_html = "<table class='table table-striped'>"
	                 + "  <thead><tr>"
	                 + "    <th>부서</th><th>이름</th><th>직급</th><th>추가</th>"
	                 + "  </tr></thead><tbody>";

	      if(!json || json.length===0){
	        v_html += "<tr><td colspan='5' align='center'>직원이 없습니다.</td></tr>";
	      } else {
	        // 디버그: 첫 아이템 키 확인하고 싶으면 주석 해제
	        // console.log('members sample:', json[0]);

	        $.each(json, function(i, m){
	          // 백엔드가 어떤 키로 주든 대응 (둘 중 있는 것 사용)
	          var deptName = (m.deptName!=null) ? m.deptName
	                        : (deptMap[m.fkDepartmentSeq] || "");
	          var position = (m.position!=null) ? m.position
	                        : (gradeMap[m.fkGradeSeq] || "");
	          var userId   = (m.userId!=null) ? m.userId : (m.memberUserid||"");
	          var name     = (m.name!=null)   ? m.name   : (m.memberName||"");
	          var seq      = (m.memberSeq!=null) ? m.memberSeq : (m.seq||m.id||0);

	          v_html += "<tr>"
	                  + "  <td>"+ esc(deptName) +"</td>"
	                  + "  <td>"+ esc(name) +"</td>"
	                  + "  <td>"+ esc(position) +"</td>"
	                  + "  <td>"
	                  + "    <button type='button' class='btn btn-sm btn-primary'"
	                  + "      onclick='addApprover(" + seq + ",\"" + esc(name) + "\",\"" + esc(deptName) + "\",\"" + esc(position) + "\")'>추가</button>"
	                  + "  </td>"
	                  + "</tr>";
	        });
	      }

	      v_html += "</tbody></table>";
	      $("#deptTree").html(v_html);
	    },
	    error: function(req, status, err){
	      alert("code:"+req.status+"\nmessage:"+req.responseText+"\nerror:"+err);
	    }
	  });
	}


function esc(s){
	  s = s || '';
	  return String(s)
	    .replace(/&/g,'&amp;').replace(/</g,'&lt;')
	    .replace(/>/g,'&gt;').replace(/"/g,'&quot;')
	    .replace(/'/g,'&#39;');
	}

function addApprover(seq, name, dept, pos){
	  if($("#selectedBox .row").length >= 3){
	    alert("최대 3명까지 선택 가능합니다."); return;
	  }
	  if($('#selectedBox .row[data-seq="'+seq+'"]').length){
	    alert("이미 선택된 결재자입니다."); return;
	  }

	  var nextNo = $("#selectedBox .row").length + 1;

	  var html = ""
	    + "<div class='row' data-seq='"+ seq +"'>"
	    + "  <div class='ord'>" + nextNo + "</div>"
	    + "  <div class='meta' style='flex:1;'>"
	    + "    <strong>" + esc(name) + "</strong>"
	    + "    <span class='cap'>" + esc(pos||'') + "</span>"
	    + "    <span class='cap'>/ " + esc(dept||'') + "</span>"
	    + "  </div>"
	    + "  <div class='actions'>"
	    + "    <button type='button' class='btn' onclick='moveUp(this)'>▲</button>"
	    + "    <button type='button' class='btn' onclick='moveDown(this)'>▼</button>"
	    + "    <button type='button' class='btn danger' onclick='removeRow(this)'>삭제</button>"
	    + "  </div>"
	    + "</div>";

	  $("#selectedBox").append(html);
	  reorder();
	}

function moveUp(btn){
	  var $row = $(btn).closest('.row');
	  var $prev = $row.prev('.row');
	  if($prev.length){ $prev.before($row); reorder(); }
	}

	function moveDown(btn){
	  var $row = $(btn).closest('.row');
	  var $next = $row.next('.row');
	  if($next.length){ $next.after($row); reorder(); }
	}

	function removeRow(btn){
	  $(btn).closest('.row').remove();
	  reorder();
	}

	function reorder(){
	  $("#selectedBox .row").each(function(i, el){
	    $(el).find(".ord").text(i+1);
	  });
	  $("#selCount").text($("#selectedBox .row").length + "/3");
	}


	
function signlineSave() {
	  var lineName = $.trim($("#lineName").val());
	  if(!lineName){ alert("결재라인 이름을 입력하세요."); $("#lineName").focus(); return; }

	  var $rows = $("#selectedBox .row");
	  if($rows.length === 0){ alert("결재자를 최소 1명 선택하세요."); return; }

	  var $f = $("#saveLineForm");
	  $f.empty(); // 기존 값 제거

	  // 라인명
	  $f.append($('<input type="hidden" name="lineName">').val(lineName));

	  // 선택된 결재자(화면 순서 = 결재 순서). order는 서버에서 i+1로 계산
	  $rows.each(function(i, el){
	    var seq = $(el).data("seq");
	    $f.append($('<input type="hidden" name="approverSeq">').val(seq));
	  });

	  // (선택) 수정 모드라면 ?id= 값 같이 전송
	  var id = new URLSearchParams(location.search).get("id");
	  if(id){ $f.append($('<input type="hidden" name="id">').val(id)); }

	  // (선택) Spring Security 쓰면 CSRF hidden 추가
	  var token  = $('meta[name="_csrf"]').attr('content');
	  var pname  = $('meta[name="_csrf_parameter"]').attr('content') || '_csrf';
	  if(token){ $f.append($('<input type="hidden">').attr('name', pname).val(token)); }

	  $f.trigger("submit");
	}


</script>
<body>
<div class="wrap">
  <header>
    <div class="bar">
      <h1 class="title">결재라인 추가</h1>
      <span class="cap">(최대 3명, ▲/▼로 순서 조정)</span>
    </div>
  </header>

  <main>
    <!-- 좌측: 부서/직원 -->
    <section class="pane left">
      <div id="deptTree" class="tree"></div>
    </section>

    <!-- 우측: 선택/정렬/이름 -->
    <section class="pane right">
      <div style="display:flex; align-items:center; justify-content:space-between;">
        <strong>선택한 결재자</strong>
        <span class="cap" id="selCount">0/3</span>
      </div>
      <div id="selectedBox" class="box"></div>

      <div>
        <label for="lineName">결재라인 이름</label>
        <input id="lineName" class="input" placeholder="예: 기본 결재라인">
      </div>
    </section>
  </main>

  <footer>
    <div style="display:flex; gap:8px; justify-content:flex-end;">
      <button class="btn" id="btnCancel">취소</button>
      <button class="btn brand" id="btnSave" onclick="signlineSave()" >저장</button>
    </div>
  </footer>
  
  <form id="saveLineForm" method="post" action="<%= ctxPath %>/sign/lines/write" style="display:none;"></form>
  
</div>


</body>
</html>
