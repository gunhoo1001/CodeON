<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>결재라인 추가</title>
	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

	<!-- ====================== style ====================== -->
	<style>
		/* ---- 레이아웃/컴포넌트 ---- */
		body { margin:0; font-family:system-ui,-apple-system,Segoe UI,Roboto,Arial; }
		.wrap { display:grid; grid-template-rows:auto 1fr auto; height:100vh; background:#f6f7fb; }

		header,footer{ padding:10px 12px; border-bottom:1px solid #e5e7eb; background:#f9fafb; }
		footer{ border-top:1px solid #e5e7eb; border-bottom:none; }

		.content{
			/* 헤더~푸터 사이를 하나로 묶고 내부에서만 스크롤 */
			min-height:0;
			display:grid;
			grid-template-rows:auto 1fr;
			gap:10px;
			padding:10px 12px;
			overflow:hidden;
			background:#f6f7fb;
		}

		main{
			min-height:0;
			display:grid;
			grid-template-columns:1.1fr 1fr;
			gap:12px;
			overflow:hidden;   /* 각 pane이 스크롤 */
			background:#f6f7fb;
		}

		.pane{ min-height:0; overflow:auto; background:#fff; border:1px solid #e5e7eb; border-radius:12px; }

		.btn { height:32px; padding:0 10px; border:1px solid #e5e7eb; border-radius:8px; background:#fff; cursor:pointer; }
		.btn:hover{ filter:brightness(0.98); }
		.btn.brand{ border-color:#2563eb; background:#2563eb; color:#fff; }
		.btn.small{ height:26px; padding:0 8px; }
		.cap{ font-size:12px; color:#6b7280; }

		/* ---- 카드(저장된 결재라인) ---- */
		.card { background:#fff; border:1px solid #e5e7eb; border-radius:12px; overflow:hidden; }
		.card-h { padding:10px 12px; font-weight:700; border-bottom:1px solid #e5e7eb; display:flex; align-items:center; justify-content:space-between; background:#fff; }
		.card-b { padding:8px 10px; background:#fff; }

		.table-wrap{
			max-height: 220px;   /* ⬅️ 한 화면에 들어오도록 제한 */
			overflow:auto;
			-webkit-overflow-scrolling:touch;
		}
		table { width:100%; border-collapse:separate; border-spacing:0; }
		thead th{
			position:sticky; top:0; z-index:50; background:#fff;
			border-bottom:1px solid #e5e7eb; padding:10px 12px; text-align:left; font-size:13px; color:#374151;
			box-shadow:0 1px 0 #e5e7eb;
		}
		tbody td{ border-bottom:1px solid #e5e7eb; padding:10px 12px; vertical-align:middle; background:#fff; }
		.empty { text-align:center; color:#6b7280; padding:16px 8px; }
		.pagination { display:flex; gap:6px; align-items:center; }

		/* ---- 저장된 결재라인 표 컬럼 ---- */
		.tpl-table th.sel-col   { width:80px;  text-align:center; }
		.tpl-table th.name-col  { width:260px; }
		.tpl-table th.count-col { width:120px; text-align:center; }
		.tpl-table td.sel-cell  { text-align:center; }
		.tpl-table td.count-cell{ text-align:center; }

		.chips{ display:flex; gap:6px; flex-wrap:wrap; }
		.chip{ display:inline-flex; align-items:center; gap:6px; border:1px solid #e5e7eb; background:#fff; border-radius:999px; padding:4px 10px; font-size:12px; }
		.avatar{ width:20px; height:20px; border-radius:50%; background:#e8eefc; display:inline-flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; color:#2b3a63; }

		/* ---- 직원 리스트 테이블(좌측 pane 안) ---- */
		.tree{ padding:12px; }  /* pane 자체가 스크롤을 가짐 */
		#deptTree table{ width:100%; border-collapse:separate; border-spacing:0 8px; } /* 간격 조정 */
		#deptTree thead th{
			position:sticky; top:0; z-index:100;
			background:#fff; box-shadow:0 1px 0 #e5e7eb;
			padding:8px 10px; text-align:center; vertical-align:middle;
		}
		#deptTree tbody, #deptTree tbody tr, #deptTree tbody td{ position:relative; z-index:1; }
		#deptTree tbody td{
			padding:10px 12px; background:#fff; text-align:center; vertical-align:middle;
			border-top:1px solid #e5e7eb; border-bottom:1px solid #e5e7eb;
		}
		#deptTree tbody td:first-child{ border-left:1px solid #e5e7eb; border-radius:10px 0 0 10px; }
		#deptTree tbody td:last-child{ border-right:1px solid #e5e7eb; border-radius:0 10px 10px 0; }
		#deptTree .btn{ height:26px; padding:0 8px; }

		/* ---- 우측 선택 박스 ---- */
		.box{ border:1px dashed #e5e7eb; border-radius:12px; padding:10px; min-height:160px; background:#fff; }
		.row{ display:flex; align-items:center; justify-content:space-between; border:1px solid #e5e7eb; border-radius:10px; padding:6px 8px; margin-bottom:8px; background:#fff; }
		.ord{ width:26px; text-align:center; font-weight:700; }
		.actions .btn{ height:26px; }
	</style>

	<!-- CSRF 메타 -->
	<c:if test="${_csrf != null}">
		<meta name="_csrf" content="${_csrf.token}">
		<meta name="_csrf_header" content="${_csrf.headerName}">
		<meta name="_csrf_parameter" content="${_csrf.parameterName}">
	</c:if>

	<!-- ====================== script ====================== -->
	<script>
		const deptMap  = {10:"인사팀", 20:"개발팀", 30:"기획팀", 40:"영업팀", 50:"고객지원팀"};
		const gradeMap = {1:"사원", 2:"대리", 3:"과장", 4:"부장", 5:"사장"};
		const ctx = "<%= ctxPath %>";

		let page = 1, size = 10, totalPages = 1;

		$(function(){
			bindEvents();
			loadTemplates();   // 저장된 결재라인
			loadMembers();     // 직원 목록
		});

		function bindEvents(){
			$("#tplPrev").on("click", function(){ if(page>1){ page--; loadTemplates(); }});
			$("#tplNext").on("click", function(){ if(page<totalPages){ page++; loadTemplates(); }});
			$("#tplApply").on("click", useSelectedTemplate);
		}

		/* ====== 템플릿 목록 ====== */
		function loadTemplates(){
			const $tbody = $("#tplTbody").empty().append(
				$("<tr/>").append($("<td/>", {colspan:4, class:"empty", text:"불러오는 중…"}))
			);

			const pageParam = Math.max(0, (page|0) - 1);

			$.ajax({
				url: ctx + "/sign/lines",
				type: "GET",
				data: { page: pageParam, size: size },
				dataType: "json",
				cache: false,
				headers: { "Accept": "application/json" }
			})
			.done(function(res){
				const pageObj =
					Array.isArray(res) ? { content: res, totalPages: 1 } :
					(res && Array.isArray(res.content)) ? res :
					(res && res.data && Array.isArray(res.data.content)) ? res.data :
					(res && res.result && Array.isArray(res.result.content)) ? res.result :
					{ content: [], totalPages: 1 };

				const rows = Array.isArray(pageObj.content) ? pageObj.content : [];
				totalPages = Math.max(1, Number(pageObj.totalPages) || 1);
				$("#pageInfo").text(page + "/" + totalPages);

				if(rows.length === 0){
					$tbody.empty().append(
						$("<tr/>").append($("<td/>", {colspan:4, class:"empty", text:"저장된 결재라인이 없습니다."}))
					);
					return;
				}

				$tbody.empty();
				rows.forEach(function(t){
					const $tr = $("<tr/>");
					const id   = (t.signlineSeq != null ? t.signlineSeq : t.id);
					const name = (t.signlineName || t.name || "");

					const members = Array.isArray(t.members) ? t.members
						: Array.isArray(t.preview) ? t.preview
						: Array.isArray(t.memberList) ? t.memberList
						: [];

					const count = (t.count != null ? t.count : (t.memberCount != null ? t.memberCount : members.length || 0));

					const $sel = $('<input/>', { type:'radio', name:'tpl', value: String(id || '') });

					// 미리보기 멤버를 라디오에 data로 저장 → 상세 조회 없이 부모로 전송 가능
					if (members && members.length){
						var appr = [];
						for (var i=0; i<members.length; i++){
							var m = members[i];
							var o = m.member || m;
							var seq = (o && (o.memberSeq != null ? o.memberSeq : (o.seq != null ? o.seq : o.id)));
							appr.push({
								memberSeq: (seq != null ? Number(seq) : NaN),
								name:      (o && (o.memberName || o.name)) || "",
								dept:      (o && (o.deptName
									|| o.departmentName
									|| (o.department && (o.department.departmentName || o.department.name)))) || "",
								grade:     (o && (o.title
									|| o.position
									|| (o.position && (o.position.positionName || o.position.name))
									|| o.grade)) || "",
								lineOrder: (m && m.lineOrder != null ? m.lineOrder
									: (o && o.lineOrder != null ? o.lineOrder : (i+1)))
							});
						}
						appr = appr.filter(function(a){ return Number.isFinite(a.memberSeq); })
							.sort(function(a,b){ return (a.lineOrder||0) - (b.lineOrder||0); });
						if (appr.length){ $sel.data('approvers', appr); }
					}

					$tr.append($("<td/>", {class:"sel-cell"}).append($sel));
					$tr.append($("<td/>").text(name || "(이름없음)"));

					const $chips = $("<div/>", {class:"chips"});
					if(members.length){
						members.forEach(function(m, i){
							const o    = m.member || m;
							const dept = (o && (o.deptName || o.departmentName
								|| (o.department && (o.department.departmentName || o.department.name)))) || "";
							const grade= (o && (o.title || o.position
								|| (o.position && (o.position.positionName || o.position.name))
								|| o.grade)) || "";
							const nm   = (o && (o.memberName || o.name)) || "";
							const $chip = $("<span/>", {class:"chip"});
							$chip.append($("<span/>", {class:"avatar", text:String(i+1)}));
							$chip.append(document.createTextNode((dept || " ") + " / " + (grade || " ") + " / " + (nm || " ")));
							$chips.append($chip);
						});
					} else {
						$chips.append($("<span/>", {class:"cap", text:"미리보기 없음"}));
					}
					$tr.append($("<td/>").append($chips));

					$tbody.append($tr);
				});
			})
			.fail(function(){
				$tbody.empty().append(
					$("<tr/>").append($("<td/>", {colspan:4, class:"empty", text:"불러오기 실패"}))
				);
			});
		}

		/* ====== 템플릿 적용(부모창에 즉시 전달) ====== */
		function useSelectedTemplate(){
			var $picked = $('input[name="tpl"]:checked');
			if($picked.length === 0){ alert("템플릿을 선택하세요."); return; }

			var id = $picked.val();
			if(!id){ alert("템플릿 ID가 비어 있습니다."); return; }

			// 1) 목록 data에 approvers가 있으면 바로 적용
			var stored = $picked.data('approvers');
			if (Array.isArray(stored) && stored.length){
				var approvers1 = stored.map(function(a){
					return { memberSeq:a.memberSeq, name:a.name||'', dept:a.dept||'', grade:a.grade||'' };
				});
				if(window.opener && !window.opener.closed && typeof window.opener.receiveSignline === 'function'){
					window.opener.receiveSignline(approvers1);
					if (window.close) window.close();
					return;
				} else {
					alert("부모창에 receiveSignline 함수가 없습니다.");
					return;
				}
			}

			// 2) 없으면 상세 조회
			$.getJSON(ctx + "/sign/lines/" + encodeURIComponent(id))
				.done(function(dto){
					var members = (dto && dto.members ? dto.members.slice() : []).sort(function(a,b){
						return (a.lineOrder||0) - (b.lineOrder||0);
					});
					var approvers2 = members.map(function(m){
						return {
							memberSeq: m.memberSeq,
							name:      m.memberName || '',
							dept:      m.deptName   || '',
							grade:     m.title      || ''
						};
					});

					if(window.opener && !window.opener.closed && typeof window.opener.receiveSignline === 'function'){
						window.opener.receiveSignline(approvers2);
						if (window.close) window.close();
					} else {
						alert("부모창에 receiveSignline 함수가 없습니다.");
					}
				})
				.fail(function(jqXHR){
					alert("템플릿 상세 조회 실패\nstatus=" + jqXHR.status + "\n" + (jqXHR.responseText||"").slice(0,200));
				});
		}

		/* ====== 직원 목록 ====== */
		function loadMembers(){
			const $holder = $("#deptTree").empty().append($("<div/>", {class:"cap", text:"불러오는 중…"}));

			$.getJSON(ctx + "/sign/members")
				.done(function(json){
					const $table = $("<table/>");
					const $thead = $("<thead/>").append(
						$("<tr/>")
							.append($("<th/>", {text:"부서"}))
							.append($("<th/>", {text:"이름"}))
							.append($("<th/>", {text:"직급"}))
							.append($("<th/>", {text:"추가"}))
					);
					const $tbody = $("<tbody/>");

					if(!json || json.length===0){
						$tbody.append($("<tr/>").append($("<td/>", {colspan:4, style:"text-align:center", text:"직원이 없습니다."})));
					} else {
						json.forEach(function(m){
							const dept = (m.department && m.department.departmentName) || m.deptName || deptMap[m.fkDepartmentSeq] || "";
							const position = m.position || m.title || (gradeMap[m.fkGradeSeq] || "");
							const name     = m.name || m.memberName || "";
							const seq      = m.memberSeq || m.seq || m.id || 0;

							const $tr = $("<tr/>");
							$tr.append($("<td/>", {text:dept}));
							$tr.append($("<td/>", {text:name}));
							$tr.append($("<td/>", {text:position}));

							const $btn = $("<button/>", {type:"button", class:"btn small"}).text("추가");
							$btn.on("click", function(){
								const $r  = $(this).closest("tr");
								const deptTxt = $r.children().eq(0).text().trim();
								const nameTxt = $r.children().eq(1).text().trim();
								const posTxt  = $r.children().eq(2).text().trim();
								addApprover(seq, nameTxt, deptTxt, posTxt);
							});

							$tr.append($("<td/>").append($btn));
							$tbody.append($tr);
						});
					}

					$table.append($thead, $tbody);
					$holder.empty().append($table);
				})
				.fail(function(){
					$holder.empty().append($("<div/>", {class:"cap", text:"불러오기 실패"}));
				});
		}

		/* ====== 우측 선택 박스 ====== */
		function addApprover(seq, name, dept, pos){
			const $box = $("#selectedBox");
			if($box.find(".row").length >= 3){ alert("최대 3명까지 선택 가능합니다."); return; }
			if($box.find('.row[data-seq="' + String(seq) + '"]').length){ alert("이미 선택된 결재자입니다."); return; }

			const nextNo = $box.find(".row").length + 1;
			const $row = $("<div/>", {class:"row"}).attr("data-seq", String(seq));
			$row.append($("<div/>", {class:"ord", text:String(nextNo)}));

			const $meta = $("<div/>", {class:"meta", style:"flex:1;"});
			$meta.append($("<strong/>").text(name));
			$meta.append($("<span/>", {class:"cap", text: (pos || '')}));
			$meta.append($("<span/>", {class:"cap", text: ' / ' + (dept || '')}));
			$row.append($meta);

			const $actions = $("<div/>", {class:"actions"});
			const $up   = $("<button/>", {type:"button", class:"btn small", text:"▲"}).on("click", function(){ moveUp(this); });
			const $down = $("<button/>", {type:"button", class:"btn small", text:"▼"}).on("click", function(){ moveDown(this); });
			const $del  = $("<button/>", {type:"button", class:"btn small", text:"삭제"})
				.css({background:"#ef4444", color:"#fff", borderColor:"#ef4444"})
				.on("click", function(){ removeRow(this); });
			$actions.append($up, $down, $del);
			$row.append($actions);

			$box.append($row);
			reorder();
		}
		function fillSelectedBox(approvers){
			const $box = $("#selectedBox").empty();
			(approvers||[]).forEach(function(a){ addApprover(a.memberSeq, a.name, a.dept, a.grade); });
			reorder();
		}
		function moveUp(btn){
			const $row = $(btn).closest('.row');
			const $prev = $row.prev('.row');
			if($prev.length){ $prev.before($row); reorder(); }
		}
		function moveDown(btn){
			const $row = $(btn).closest('.row');
			const $next = $row.next('.row');
			if($next.length){ $next.after($row); reorder(); }
		}
		function removeRow(btn){
			$(btn).closest('.row').remove();
			reorder();
		}
		function reorder(){
			$("#selectedBox .row").each(function(i, el){ $(el).find(".ord").text(i+1); });
			$("#selCount").text($("#selectedBox .row").length + "/3");
		}

		/* ====== 현재 선택을 부모창에 적용 ====== */
		function applyToParentFromCurrentSelection(){
			const rows = $("#selectedBox .row");
			if(rows.length===0){ alert("결재자를 선택하세요."); return; }

			const approvers = [];
			rows.each(function(i, el){
				const $el = $(el);
				const seq = Number($el.data("seq"));
				const name = $el.find("strong").text().trim();
				const caps = $el.find(".cap");
				const grade = (caps.eq(0).text()||"").trim();
				const dept  = (caps.eq(1).text()||"").replace('/', '').trim();
				approvers.push({ memberSeq:seq, name:name, dept:dept, grade:grade });
			});

			if(window.opener && !window.opener.closed && typeof window.opener.receiveSignline === 'function'){
				window.opener.receiveSignline(approvers);
				if (window.close) window.close();
			}else{
				alert("부모창에 receiveSignline(approvers) 함수가 없습니다.");
			}
		}
	</script>
</head>

<!-- ====================== body ====================== -->
<body>
	<div class="wrap">
		<header>
			<div style="display:flex; gap:8px; align-items:center;">
				<h1 style="font-size:16px; font-weight:700; margin:0;">결재라인 추가</h1>
				<span class="cap">(템플릿에서 선택하거나, 아래에서 직접 구성하세요)</span>
			</div>
		</header>

		<!-- 가운데 전체(스크롤 영역) -->
		<div class="content">
			<!-- 템플릿 목록 카드 -->
			<section class="card" aria-label="저장된 결재라인">
				<div class="card-h">
					<span>저장된 결재라인</span>
					<div class="controls" style="display:flex; gap:8px; align-items:center;">
						<div class="pagination">
							<button class="btn small" id="tplPrev">&laquo;</button>
							<span id="pageInfo" class="cap">1/1</span>
							<button class="btn small" id="tplNext">&raquo;</button>
						</div>
						<button class="btn brand" id="tplApply">선택 템플릿 적용</button>
					</div>
				</div>
				<div class="card-b">
					<div class="table-wrap">
						<table class="tpl-table">
							<thead>
								<tr>
									<th class="sel-col">선택</th>
									<th class="name-col">템플릿명</th>
									<th>결재선 미리보기</th>
								</tr>
							</thead>
							<tbody id="tplTbody">
								<tr><td colspan="4" class="empty">불러오는 중…</td></tr>
							</tbody>
						</table>
					</div>
				</div>
			</section>

			<!-- 메인 2컬럼: 좌 직원/우 선택 박스 -->
			<main>
				<!-- 좌: 직원 목록 -->
				<section class="pane left">
					<div id="deptTree" class="tree"></div>
				</section>

				<!-- 우: 선택/정렬 -->
				<section class="pane right">
					<div style="display:flex; align-items:center; justify-content:space-between; padding:10px;">
						<strong>선택한 결재자</strong>
						<span class="cap" id="selCount">0/3</span>
					</div>
					<div id="selectedBox" class="box" style="margin:10px;"></div>
				</section>
			</main>
		</div>

		<footer>
			<div style="display:flex; gap:8px; justify-content:flex-end;">
				<button class="btn" onclick="if(window.close){window.close()}">취소</button>
				<button class="btn brand" onclick="applyToParentFromCurrentSelection()">확인</button>
			</div>
		</footer>
	</div>
</body>
</html>
