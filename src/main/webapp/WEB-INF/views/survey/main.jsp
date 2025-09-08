<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>설문관리</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="<%=ctx%>/js/jquery-3.7.1.min.js"></script>

  <style>
    :root{
      --sidebar-w: 240px; --header-h: 64px;
      --bg:#f7f8fb; --card:#ffffff; --line:#e5e7eb; --brand:#2563eb; --muted:#6b7280;
    }
    body{ background:var(--bg); }
    .app-shell{ display:flex; min-height:100vh; }

    /* 좌측 사이드바 */
    .sidebar{
      width:var(--sidebar-w);
      background:#0b4db4;
      color:#fff;
      padding:20px;
      position:sticky; top:0; height:100vh;
    }
    .avatar{
      width:56px; height:56px; border-radius:50%;
      background:#fff; color:#0b4db4; font-weight:700;
      display:inline-flex; align-items:center; justify-content:center;
    }

    /* 본문 */
    .content{ flex:1; padding:24px; }
    .card-rounded{ border-radius:18px; }
    .section-title{ font-weight:700; }

    /* 목록 타일 */
    .survey-item{
      border:1px solid var(--line); border-radius:12px; padding:14px; background:#fff;
      transition:.12s; cursor:pointer;
    }
    .survey-item:hover{ box-shadow:0 6px 16px rgba(17,24,39,.08); }
    .survey-item.active{ border-color:#0d6efd !important; box-shadow:0 0 0 .2rem rgba(13,110,253,.15); }
    .dim{ color:var(--muted); }

    /* 차트/표 자리 */
    #chart-placeholder{ height:320px; border:1px dashed var(--line); border-radius:12px;
      display:flex; align-items:center; justify-content:center; color:var(--muted); }
    #table-placeholder{ border:1px dashed var(--line); border-radius:12px; padding:16px; color:var(--muted); }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/header/header.jsp"/>

<div class="app-shell">

  <!-- 좌측 사이드바 -->
  <aside class="sidebar">
    <div class="d-flex align-items-center gap-3 mb-4">
      <div class="avatar">GW</div>
      <div>
        <div class="fw-bold">
          <c:choose>
            <c:when test="${not empty sessionScope.loginuser}">
              ${sessionScope.loginuser.memberName}
            </c:when>
            <c:otherwise>로그인 사용자</c:otherwise>
          </c:choose>
        </div>
        <div class="small text-white-50">${deptName} / ${gradeName}</div>
        <div class="small text-white-50">부서/직급</div>
      </div>
    </div>

    <button id="btnNew" class="btn btn-light text-primary fw-bold mb-3"
            data-bs-toggle="modal" data-bs-target="#modalNew">설문작성</button>
  </aside>

  <!-- 본문 -->
  <main class="content container-fluid">

    <!-- 상단 요약 카드 -->
    <div class="card shadow-sm card-rounded mb-3">
      <div class="card-body d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
          <h3 class="mb-1">설문관리</h3>
          <div class="text-muted">설문 생성·배포·결과 확인을 단계적으로 구현하세요.</div>
        </div>
        <div class="d-flex gap-3">
          <div class="text-nowrap"><span class="fw-bold">전체참여자</span> : <span class="text-muted">—</span></div>
          <div class="text-nowrap"><span class="fw-bold">참여완료</span> : <span class="text-muted">—</span></div>
          <div class="text-nowrap"><span class="fw-bold">미참여</span> : <span class="text-muted">—</span></div>
        </div>
      </div>
    </div>

    <!-- 목록 + 상세 2단 -->
    <div class="row g-3">
      <!-- 목록 -->
      <div class="col-12 col-xl-5">
        <div class="card shadow-sm card-rounded">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h5 class="mb-0 section-title">설문 목록</h5>
              <br><br>
            </div>

            <div class="vstack gap-2">
              <!-- SSR fallback -->
              <c:forEach var="s" items="${surveys}">
                <div class="survey-item" data-id="${s.surveyId}">
                  <div class="d-flex justify-content-between">
                    <div class="fw-bold"><c:out value="${s.title}"/></div>
                    <span class="badge bg-secondary"><c:out value="${s.statusCode}"/></span>
                  </div>
                  <div class="small dim mt-1">
                    <c:out value="${s.ownerMemberSeq}"/> ·
                    <c:out value="${s.startDate}"/> ~ <c:out value="${s.endDate}"/>
                  </div>
                  <div class="small mt-1">등록일 <c:out value="${s.regDate}"/></div>
                </div>
              </c:forEach>
            </div>
          </div>
        </div>
      </div>

      <!-- 상세/차트 -->
      <div class="col-12 col-xl-7">
        <div class="card shadow-sm card-rounded">
          <div class="card-body">
            <div class="d-flex align-items-center justify-content-between mb-2">
              <div class="d-flex align-items-center gap-2">
                <button class="btn btn-outline-secondary btn-sm" disabled>&larr;</button>
                <h5 class="mb-0 section-title">설문 상세</h5>
              </div>
              <div class="text-muted">등록일 —</div>
            </div>

            <!-- 차트 자리 -->
            <div id="chart-placeholder" class="mb-3">
              차트 영역 (Highcharts, Chart.js 등 추후 연결)
            </div>

            <!-- 표 자리 -->
            <div id="table-placeholder">
              응답 집계 표 영역 (후에 서버 데이터로 렌더링)
            </div>
          </div>
        </div>
      </div>
    </div>

  </main>
</div>

<!-- 설문 작성 모달 -->
<div class="modal fade" id="modalNew" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <form id="surveyForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">새 설문 만들기</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>
      <div class="modal-body">

        <!-- 기본 정보 -->
        <div class="mb-3">
          <label class="form-label">설문 제목</label>
          <input id="svTitle" class="form-control" placeholder="예: 분기 워크숍 장소 선호도">
        </div>
        <div class="row g-3">
          <div class="col-sm-6">
            <label class="form-label">시작일</label>
            <input id="svStart" type="date" class="form-control">
          </div>
          <div class="col-sm-6">
            <label class="form-label">마감일</label>
            <input id="svEnd" type="date" class="form-control">
          </div>
        </div>

        <hr>

        <!-- 질문 영역 -->
        <div class="mb-3">
          <label class="form-label">설문 문항</label>
          <div id="questionList"></div>
          <button type="button" class="btn btn-sm btn-outline-primary mt-2" id="btnAddQ">+ 문항 추가</button>
        </div>

        <hr>

        <!-- 조사 대상 -->
        <div class="mb-3">
          <label class="form-label">조사 대상</label>
          <div>
            <label class="me-3"><input type="radio" name="targetType" value="ALL" checked> 전체</label>
            <label class="me-3"><input type="radio" name="targetType" value="DEPT"> 부서</label>
          </div>
          <select id="deptSelect" class="form-select mt-2 d-none">
            <option value="">부서 선택</option>
            <option value="10">인사팀</option>
            <option value="20">개발팀</option>
            <option value="30">기획팀</option>
            <option value="40">영업팀</option>
            <option value="50">고객지원팀</option>
          </select>
        </div>

      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal" type="button">닫기</button>
        <button class="btn btn-primary" id="btnSave" type="button">저장</button>
      </div>
    </form>
  </div>
</div>

<!-- 응답 모달 -->
<div class="modal fade" id="modalAnswer" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <form id="answerForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">설문 응답</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>
      <div class="modal-body">
        <div id="answerSurveyMeta" class="mb-2 small text-muted"></div>
        <div id="answerQuestionList"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" data-bs-dismiss="modal" type="button">취소</button>
        <button class="btn btn-primary" id="btnSubmitAnswer" type="button">제출</button>
      </div>
    </form>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>

<script>
  const CTX = '<%=ctx%>';

  // ---------- 유틸 ----------
  function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, function (m) {
      return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;', "'":'&#39;'}[m]);
    });
  }
  function escapeAttr(str){
    return String(str).replace(/"/g,'&quot;').replace(/'/g,'&#39;');
  }
  function safeParseQuestions(jsonStr) {
    if (!jsonStr) return [];
    try {
      var parsed = JSON.parse(jsonStr);
      if (Array.isArray(parsed)) return parsed;
      if (parsed && typeof parsed === 'object') {
        return Object.keys(parsed).sort().map(function (k) { return parsed[k]; });
      }
    } catch (e) { console.warn('questionsJson parse error', e); }
    return [];
  }
  function summarizeTargets(list) {
    const hasAll = list.some(x => x.targetType === 'ALL');
    if (hasAll) return '전체';
    const depts = Array.from(new Set(list.filter(x => x.targetType === 'DEPT').map(x => x.deptSeq)));
    return depts.length ? ('부서 ' + depts.join(', ')) : '—';
  }

  // ---------- 목록 ----------
  function renderList(items) {
    var $wrap = $(".vstack.gap-2").empty();
    if (!items.length) {
      $wrap.append('<div class="text-muted">등록된 설문이 없습니다.</div>');
      return;
    }
    items.forEach(function (s) {
      var start = s.startDate || '';
      var end   = s.endDate   || '';
      var reg   = (s.regDate || '').toString().replace('T', ' ');
      var html  =
        '<div class="survey-item" data-id="' + s.surveyId + '">' +
          '<div class="d-flex justify-content-between">' +
            '<div class="fw-bold">' + escapeHtml(s.title || '') + '</div>' +
            '<span class="badge bg-secondary">' + escapeHtml(s.statusCode || '') + '</span>' +
          '</div>' +
          '<div class="small dim mt-1">' +
            escapeHtml(String(s.ownerMemberSeq || '')) + ' · ' + start + ' ~ ' + end +
          '</div>' +
          '<div class="small mt-1">등록일 ' + reg + '</div>' +
        '</div>';
      $wrap.append(html);
    });
  }
  function loadList(after) {
    $.getJSON(CTX + '/api/surveys', function (items) {
      items = items || [];
      renderList(items);
      if (typeof after === 'function') after(items);
    }).fail(function (xhr) {
      console.error('list fail', xhr);
      if (xhr.status === 401) alert('로그인이 필요합니다.');
    });
  }

  // ---------- 상세 ----------
  function renderAnswerActionBar(s) {
    $("#answerActions").remove();
    let actions = '';
    if (s.answered) {
      actions = '<span class="badge bg-success">이미 응답했습니다</span>';
    } else if (s.canAnswer) {
      actions = '<button class="btn btn-primary btn-sm" id="btnAnswer" data-id="'+s.surveyId+'">응답하기</button>';
    } else {
      actions = '<span class="badge bg-secondary">' + escapeHtml(s.notAllowedReason || '응답 불가') + '</span>';
    }
    $("#chart-placeholder").before('<div id="answerActions" class="d-flex justify-content-end mb-2">' + actions + '</div>');
    $("#btnAnswer").off('click').on('click', function(){
      openAnswerModal(s);
    });
  }

  function openDetail(surveyId) {
    if (!surveyId) return;

    // 메타
    $.getJSON(CTX + '/api/surveys/' + surveyId, function (s) {
      $(".col-12.col-xl-7 .section-title").text('설문 상세 — ' + (s.title || ''));
      var reg = (s.regDate || '').toString().replace('T', ' ');
      $(".col-12.col-xl-7 .text-muted").text('등록일 ' + reg);

      var html =
        '<div class="mb-2">' +
          '<span class="badge bg-light text-dark">기간</span>' +
          '<span class="ms-1">' + (s.startDate || '') + ' ~ ' + (s.endDate || '') + '</span>' +
          '<span class="badge bg-light text-dark ms-3">상태</span>' +
          '<span class="ms-1">' + (s.statusCode || '') + '</span>' +
        '</div>';

      var qs = safeParseQuestions(s.questionsJson);
      if (qs.length) {
        html += '<ul class="list-group mb-3">';
        qs.forEach(function (q, i) {
          var opts = Array.isArray(q.options) ? q.options.map(escapeHtml).join(', ') : '';
          html +=
            '<li class="list-group-item">' +
              '<div class="fw-bold">Q' + (i + 1) + '. ' + escapeHtml(q.title || '') + '</div>' +
              '<div class="small text-muted">타입: ' + escapeHtml(q.type || '') + (opts ? ' / 보기: ' + opts : '') + '</div>' +
            '</li>';
        });
        html += '</ul>';
      } else {
        html += '<div class="text-muted">문항 정의가 없습니다.</div>';
      }
      $('#table-placeholder').html(html);

      $('#chart-placeholder').text('응답 통계 차트 영역 (추후 연동)');

      renderAnswerActionBar(s);                 // 응답하기 버튼/배지
      loadStatsAndRenderChart(surveyId, s);     // 통계 차트
    }).fail(function (xhr) {
      alert('상세 조회 실패: ' + (xhr.responseText || xhr.status));
    });

    // 대상 요약(옵션)
    $.getJSON(CTX + '/api/surveys/' + surveyId + '/targets', function (list) {
      if (!Array.isArray(list) || !list.length) return;
      var summary = summarizeTargets(list);
      $('#table-placeholder').append(
        '<div class="mt-3">' +
          '<span class="badge bg-light text-dark">조사 대상</span>' +
          '<span class="ms-1">' + escapeHtml(summary) + '</span>' +
        '</div>'
      );
    }).fail(function () { /* 대상 정보 없으면 무시 */ });
  }

  // ---------- 생성 모달 ----------
  function bindCreateHandlers() {
    var qIndex = 0;

    $('#btnAddQ').off('click').on('click', function () {
      qIndex++;
      var qHtml =
        '<div class="card p-3 mb-2" data-qindex="' + qIndex + '">' +
          '<div class="d-flex justify-content-between align-items-center mb-2">' +
            '<label class="fw-bold mb-0">질문 ' + qIndex + '</label>' +
            '<button type="button" class="btn btn-sm btn-outline-danger btnRemoveQ">삭제</button>' +
          '</div>' +
          '<input class="form-control mb-2 q-title" placeholder="질문 내용을 입력하세요">' +
          '<select class="form-select mb-2 q-type">' +
            '<option value="SINGLE">단일 선택</option>' +
            '<option value="MULTI">복수 선택</option>' +
          '</select>' +
          '<textarea class="form-control q-options" rows="2" placeholder="보기 항목을 줄바꿈으로 입력"></textarea>' +
        '</div>';
      $('#questionList').append(qHtml);
    });

    $(document).off('click', '.btnRemoveQ').on('click', '.btnRemoveQ', function () {
      $(this).closest('.card').remove();
    });

    $('input[name=targetType]').on('change', function () {
      $('#deptSelect').addClass('d-none');
      if (this.value === 'DEPT') $('#deptSelect').removeClass('d-none');
    });

    $('#btnSave').off('click').on('click', function () {
      var title = $('#svTitle').val().trim();
      var start = $('#svStart').val();
      var end   = $('#svEnd').val();
      if (!title || !start || !end) { alert('제목/시작일/마감일은 필수입니다.'); return; }
      if (end < start) { alert('마감일은 시작일 이후여야 합니다.'); return; }

      var questions = [];
      $('#questionList .card').each(function () {
        var qTitle = $(this).find('.q-title').val().trim();
        var qType  = $(this).find('.q-type').val();
        var opts   = $(this).find('.q-options').val().split('\n').map(function (x) { return x.trim(); }).filter(Boolean);
        questions.push({ title: qTitle, type: qType, options: opts });
      });

      var targetType = $('input[name=targetType]:checked').val();
      var targetDept = $('#deptSelect').val() || null;

      var payload = {
        title: title,
        startDate: start,
        endDate: end,
        statusCode: 'OPEN',
        questionsJson: JSON.stringify(questions),
        targetType: targetType,
        targetDept: targetType === 'DEPT' ? Number(targetDept) : null
      };

      $.ajax({
        url: CTX + '/api/surveys',
        method: 'POST',
        contentType: 'application/json; charset=UTF-8',
        data: JSON.stringify(payload)
      }).done(function (newId) {
        const modal = bootstrap.Modal.getInstance(document.getElementById('modalNew'));
        if (modal) modal.hide();
        $('#surveyForm')[0].reset();
        $('#questionList').empty();
        $('#deptSelect').addClass('d-none');

        loadList(function (items) {
          $('.survey-item').each(function () {
            if (Number($(this).data('id')) === Number(newId)) {
              $(this).addClass('active');
              openDetail(newId);
            }
          });
        });
        alert('설문이 생성되었습니다. ID=' + newId);
      }).fail(function (xhr) {
        alert('생성 실패: ' + (xhr.responseText || xhr.status));
      });
    });
  }

  // ---------- 응답 모달 ----------
  function openAnswerModal(survey) {
    const modalEl = document.getElementById('modalAnswer');
    const modal   = bootstrap.Modal.getOrCreateInstance(modalEl);

    $("#answerSurveyMeta").text((survey.startDate||'') + " ~ " + (survey.endDate||'') + " / 상태 " + (survey.statusCode||''));
    const $list = $("#answerQuestionList").empty();

    const qs = safeParseQuestions(survey.questionsJson);
    if (!qs.length) {
      $list.html('<div class="text-muted">응답할 문항이 없습니다.</div>');
    } else {
      qs.forEach(function(q, i){
        const key = "q" + (i+1);
        let block = '<div class="mb-3"><div class="fw-bold mb-1">Q' + (i+1) + '. ' + escapeHtml(q.title||'') + '</div>';
        const type = (q.type || 'TEXT').toUpperCase();

        if (type === 'SINGLE') {
          (q.options || []).forEach(function(opt, idx){
            const id = key + '_opt_' + idx;
            block +=
              '<div class="form-check">' +
                '<input class="form-check-input" type="radio" name="'+key+'" id="'+id+'" value="'+escapeAttr(opt)+'">' +
                '<label class="form-check-label" for="'+id+'">'+escapeHtml(opt)+'</label>' +
              '</div>';
          });
        } else if (type === 'MULTI') {
          (q.options || []).forEach(function(opt, idx){
            const id = key + '_opt_' + idx;
            block +=
              '<div class="form-check">' +
                '<input class="form-check-input" type="checkbox" name="'+key+'[]'+'" id="'+id+'" value="'+escapeAttr(opt)+'">' +
                '<label class="form-check-label" for="'+id+'">'+escapeHtml(opt)+'</label>' +
              '</div>';
          });
        } else {
          block += '<textarea class="form-control" id="'+key+'_text" rows="3" placeholder="답변을 입력하세요"></textarea>';
        }
        block += '</div>';
        $list.append(block);
      });
    }

    modal.show();

    $("#btnSubmitAnswer").off('click').on('click', function(){
      const answers = {};
      qs.forEach(function(q, i){
        const key = "q" + (i+1);
        const type = (q.type || 'TEXT').toUpperCase();
        if (type === 'SINGLE') {
          answers[key] = $('input[name="'+key+'"]:checked').val() || null;
        } else if (type === 'MULTI') {
          answers[key] = $('input[name="'+key+'[]'+'"]:checked').map((i,e)=>e.value).get();
        } else {
          answers[key] = ($('#'+key+'_text').val() || '').trim();
        }
      });

      $.ajax({
        url: CTX + "/api/surveys/" + survey.surveyId + "/responses",
        method: "POST",
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify({ answersJson: JSON.stringify(answers) })
      }).done(function(){
        bootstrap.Modal.getInstance(document.getElementById('modalAnswer')).hide();
        openDetail(survey.surveyId); // 재조회 → 배지 변경
      }).fail(function(xhr){
        alert("제출 실패: " + (xhr.responseText || xhr.status));
      });
    });
  }
	
  let _chart;
  function loadStatsAndRenderChart(surveyId, surveyMeta){
    $("#chart-placeholder").html('<canvas id="statsChart" height="140"></canvas>');
    $.getJSON(CTX + '/api/surveys/' + surveyId + '/stats', function(stats){
    	// ▼ [추가] 요약카드 갱신
        renderSummaryFromStats(stats);
      if (!stats || !stats.questions || !stats.questions.length) {
        $("#chart-placeholder").text('통계 데이터가 없습니다.');
        return;
      }
      // 첫 번째 SINGLE/MULTI 문항만 간단 바차트 (필요하면 반복 렌더)
      const q = stats.questions.find(q => q.type === 'SINGLE' || q.type === 'MULTI');
      if (!q || !q.counts) {
        $("#chart-placeholder").text('선택형 문항 통계가 없습니다.');
        return;
      }
      const labels = Object.keys(q.counts);
      const data   = Object.values(q.counts);
      const ctx = document.getElementById('statsChart');
      if (_chart) { _chart.destroy(); }
      _chart = new Chart(ctx, {
        type: 'bar',
        data: { labels, datasets: [{ label: '응답 수', data }] },
        options: {
          maintainAspectRatio: false,
          scales: { y: { beginAtZero: true, ticks: { precision:0 } } },
          plugins: { title: { display:true, text: 'Q' + q.index + '. ' + q.title } }
        }
      });
    }).fail(function(xhr){
      console.error('stats fail', xhr);
      $("#chart-placeholder").text('통계 조회 실패');
    });
  }

  // ---------- 초기화 ----------
  $(function () {
    // 델리게이션
    $(document).on('click', '.survey-item', function () {
      $('.survey-item').removeClass('active');
      $(this).addClass('active');
      const id = $(this).data('id');
      if (id) openDetail(id);
    });

    // 초기 목록 로딩 후 첫 항목 자동 상세
    loadList(function (items) {
      if (items && items.length) {
        const firstId = items[0].surveyId;
        $('.survey-item[data-id="' + firstId + '"]').addClass('active');
        openDetail(firstId);
      }
    });

    // 생성 모달 핸들러
    bindCreateHandlers();
  });
  
	//▼ [추가] 요약카드 렌더
  function renderSummaryFromStats(stats){
    const $card = $('.card.shadow-sm.card-rounded .card-body').first();
    // 숫자 채우기
    $card.find('.text-nowrap').eq(0).find('span.text-muted').text(stats.eligible ?? '—');
    $card.find('.text-nowrap').eq(1).find('span.text-muted').text(stats.totalResponses ?? '—');
    $card.find('.text-nowrap').eq(2).find('span.text-muted').text(
      (typeof stats.notAnswered === 'number') ? stats.notAnswered : 
      ((typeof stats.eligible === 'number' && typeof stats.totalResponses === 'number') ? Math.max(0, stats.eligible - stats.totalResponses) : '—')
    );
 	// 버튼 활성화 정책: 응답 1건 이상이면 다운로드 활성
    const $btnShare = $card.find('.btn-group .btn-warning');
    const $btnDown  = $card.find('.btn-group .btn-info');
    $btnShare.prop('disabled', false).off('click').on('click', function(){ /* 공유 UI 붙이면 됨 */ });
    $btnDown.prop('disabled', !(stats.totalResponses > 0)).off('click').on('click', function(){
      // TODO: 서버에 export 엔드포인트가 생기면 교체
      alert('다운로드는 추후 연동합니다.');
    });
  }
</script>

</body>
</html>
