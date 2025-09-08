<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<div id="admin-sidebar">
  <jsp:include page="../admin/adminsidebar.jsp" />
</div>
        
<meta charset="UTF-8">
<title>통계 차트</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<style type="text/css">
  :root{
    --header-h: 70px;      /* header 높이에 맞게 조정 */
    --sidebar-w: 220px;    /* adminsidebar 실제 폭에 맞게 조정 */
    --bg:#f7f8fb;
    --card:#ffffff;
    --text:#111827;
    --muted:#6b7280;
    --line:#e5e7eb;
    --brand:#2563eb;
  }

  /* === 사이드바: 항상 위에 떠 있고 클릭 가능 === */
  #admin-sidebar{
    position: fixed;
    top: var(--header-h);
    left: 0;
    width: var(--sidebar-w);
    height: calc(100vh - var(--header-h));
    z-index: 1040;            /* 메인 컨텐츠보다 높은 z-index */
    overflow: auto;
    background: #fff;         /* 필요 시 배경 */
    border-right: 1px solid var(--line);
  }

  /* === 메인 컨텐츠: 사이드바 폭만큼 마진 확보 === */
  .main-content{
    margin-left: var(--sidebar-w);
    padding-top: 1rem;
    min-height: calc(100vh - var(--header-h));
    position: relative;     /* stacking context 분리 */
    z-index: 1;
  }

  .highcharts-figure,
  .highcharts-data-table table {
      min-width: 320px;
      max-width: 800px;
      margin: 1em auto;
  }
  
  #chart_container { height: 400px; }
  
  .highcharts-data-table table {
      font-family: Verdana, sans-serif;
      border-collapse: collapse;
      border: 1px solid #ebebeb;
      margin: 10px auto;
      text-align: center;
      width: 100%;
      max-width: 500px;
  }
  
  .highcharts-data-table caption {
      padding: 1em 0;
      font-size: 1.2em;
      color: #555;
  }
  
  .highcharts-data-table th {
      font-weight: 600;
      padding: 0.5em;
  }
  
  .highcharts-data-table td,
  .highcharts-data-table th,
  .highcharts-data-table caption {
      padding: 0.5em;
  }
  
  .highcharts-data-table thead tr,
  .highcharts-data-table tr:nth-child(even) {
      background: #f8f8f8;
  }
  
  .highcharts-data-table tr:hover { background: #f1f7ff; }
  input[type="number"] { min-width: 50px; }

  #table_container table { width: 100% }
  #table_container th, #table_container td { border: 1px solid gray; text-align: center; } 
  #table_container th { background-color: #595959; color: white; } 
<style>
    body {
        background-color: #f8f9fa;
        padding-top: 70px;
        padding-bottom: 60px;
    }
    
    .main-container {
        display: flex;
        justify-content: center;
        width: 100%;
        padding-left: 220px;
        box-sizing: border-box;
    }

    .stat-card {
        margin: 2rem;
        max-width: 1400px;
        width: 100%;
    }
    
    .highcharts-figure,
    .highcharts-data-table table {
        min-width: 320px;
        max-width: 800px;
        margin: 1em auto;
    }
    
    div#chart_container {
        height: 400px;
    }
    
    .highcharts-data-table table {
        font-family: Verdana, sans-serif;
        border-collapse: collapse;
        border: 1px solid #ebebeb;
        margin: 10px auto;
        text-align: center;
        width: 100%;
        max-width: 500px;
    }
    
    .highcharts-data-table caption {
        padding: 1em 0;
        font-size: 1.2em;
        color: #555;
    }
    
    .highcharts-data-table th {
        font-weight: 600;
        padding: 0.5em;
    }
    
    .highcharts-data-table td,
    .highcharts-data-table th,
    .highcharts-data-table caption {
        padding: 0.5em;
    }
    
    .highcharts-data-table thead tr,
    .highcharts-data-table tr:nth-child(even) {
        background: #f8f8f8;
    }
    
    .highcharts-data-table tr:hover {
        background: #f1f7ff;
    }
    
    input[type="number"] {
        min-width: 50px;
    }
    
    div#table_container table {width: 100%}
    div#table_container th, div#table_container td {border: solid 1px gray; text-align: center;} 
    div#table_container th {background-color: #595959; color: white;} 
    
    @media (max-width: 768px) {
        .main-container {
            padding-left: 0;
            flex-direction: column;
            align-items: center;
        }
        .sidebar {
            position: relative;
            width: 100%;
            height: auto;
            border-right: none;
            border-bottom: 1px solid #ccc;
            top: 0;
        }
    }
</style>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/highcharts.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/exporting.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/export-data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/accessibility.js"></script> 
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/series-label.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/drilldown.js"></script>

<br><br>
<body class="bg-light">
<div class="container-fluid" style="transform: translateY(-5rem) !important;">
    <div class="row">
        <div class="col-md-10 py-5 px-4 offset-md-2">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h2 class="card-title text-center text-primary fw-bold mb-4">사원 통계정보 (차트)</h2>
                    <div class="d-flex justify-content-center mb-5">
                        <form name="searchFrm" class="form-inline">
                            <select name="searchType" id="searchType" class="form-select">
                                <option value="">통계 선택</option>
                                <option value="deptname">부서별 인원통계</option>
                                <option value="gender">성별 인원통계</option>
                                <option value="hireYear">입사연도별 인원추이</option>             
  								<option value="hireYearGender">입사연도×성별 스택</option>   
                            </select>
                        </form>
                    </div>

                    <div id="chart_container" class="highcharts-figure"></div>
                    <div id="table_container" class="mt-5"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer/footer.jsp" />
</body>
<script type="text/javascript">
$(function(){
  $('select#searchType').change(function(e){
    func_choice($(e.target).val());
  });

  // 초기 진입 시 기본값
  $('select#searchType').val("deptname").trigger("change");
});

// 공통 에러 핸들러
function ajaxError(request, status, error){
  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
}

// 메인 분기
function func_choice(searchTypeVal) {
  switch(searchTypeVal){

    case "":     // 통계 선택 안함
      $('div#chart_container').empty();
      $('div#table_container').empty();
      $('div.highcharts-data-table').empty();
      break;

    case "deptname":  // 부서별 인원통계 (pie)
      $.ajax({
        url: "<%= ctxPath%>/memberInfo/memberCntByDeptname",
        dataType:"json",
        success:function(json){
          $('div#chart_container').empty();
          $('div#table_container').empty();
          $('div.highcharts-data-table').empty();

          let resultArr = [];
          for (let i=0; i<json.length; i++) {
            let obj;
            if(i === 0) {
              obj = {name: json[i].department_name, y: Number(json[i].percentage), sliced: true, selected: true};
            } else {
              obj = {name: json[i].department_name, y: Number(json[i].percentage)};
            }
            resultArr.push(obj);
          }

          Highcharts.chart('chart_container', {
            chart: { type: 'pie' },
            title: { text: '우리회사 부서별 인원통계' },
            tooltip: { pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>' },
            accessibility: { point: { valueSuffix: '%' } },
            plotOptions: {
              pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %' }
              }
            },
            series: [{ name: '인원비율', colorByPoint: true, data: resultArr }]
          });

          // 테이블
          let v_html = `<table>
                          <tr>
                            <th>부서명</th>
                            <th>인원수</th>
                            <th>퍼센티지</th>
                          </tr>`;
          $.each(json, function(index, item) {
            v_html += `<tr>
                         <td>\${item.department_name}</td>
                         <td>\${item.cnt}</td>
                         <td>\${item.percentage} %</td>
                       </tr>`;
          });
          v_html += `</table>`;
          $('div#table_container').html(v_html);
        },
        error: ajaxError
      });
      break;

    case "gender":    // 성별 인원통계 (pie)
      $.ajax({
        url: "<%= ctxPath%>/memberInfo/memberCntByGender",
        dataType: "json",
        success:function(json) {
          $('div#chart_container').empty();
          $('div#table_container').empty();
          $('div.highcharts-data-table').empty();

          let resultArr = [];
          for (let i=0; i<json.length; i++) {
            let obj;
            if(i === 0) {
              obj = {name: json[i].gender, y: Number(json[i].percentage), sliced: true, selected: true};
            } else {
              obj = {name: json[i].gender, y: Number(json[i].percentage)};
            }
            resultArr.push(obj);
          }

          Highcharts.chart('chart_container', {
            chart: { type: 'pie' },
            title: { text: '우리회사 성별 인원통계' },
            tooltip: { pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>' },
            accessibility: { point: { valueSuffix: '%' } },
            plotOptions: {
              pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %' }
              }
            },
            series: [{ name: '인원비율', colorByPoint: true, data: resultArr }]
          });

          // 테이블
          let v_html = `<table>
                          <tr>
                            <th>성별</th>
                            <th>인원수</th>
                            <th>퍼센티지</th>
                          </tr>`;
          $.each(json, function(index, item) {
            v_html += `<tr>
                         <td>\${item.gender}</td>
                         <td>\${item.cnt}</td>
                         <td>\${item.percentage} %</td>
                       </tr>`;
          });
          v_html += `</table>`;
          $('div#table_container').html(v_html);
        },
        error: ajaxError
      });
      break;

    case "hireYear": // 입사연도별 인원 추이 (column)
      $.ajax({
        url: "<%= ctxPath%>/memberInfo/memberCntByHireYear",
        dataType: "json",
        success: function(json) {
          $('div#chart_container').empty();
          $('div#table_container').empty();
          $('div.highcharts-data-table').empty();

          const categories = json.map(r => r.hire_year);
          const counts     = json.map(r => Number(r.cnt));

          Highcharts.chart('chart_container', {
            chart: { type: 'column' },
            title: { text: '입사연도별 인원 추이' },
            xAxis: { categories, crosshair: true },
            yAxis: { min: 0, title: { text: '인원수' } },
            tooltip: { shared: true },
            plotOptions: { column: { pointPadding: 0.2, borderWidth: 0 } },
            series: [{ name: '입사 인원', data: counts }]
          });

          // 테이블
          let html = `<table>
                        <tr>
                          <th>입사연도</th>
                          <th>인원수</th>
                          <th>퍼센티지</th>
                        </tr>`;
          $.each(json, function(_, item){
            html += `<tr>
                       <td>\${item.hire_year}</td>
                       <td>\${item.cnt}</td>
                       <td>\${item.percentage} %</td>
                     </tr>`;
          });
          html += `</table>`;
          $('div#table_container').html(html);
        },
        error: ajaxError
      });
      break;

    case "hireYearGender": // 입사연도×성별 스택 (column, stacked)
      $.ajax({
        url: "<%= ctxPath%>/memberInfo/memberCntByHireYearGender",
        dataType: "json",
        success: function(json) {
          $('div#chart_container').empty();
          $('div#table_container').empty();
          $('div.highcharts-data-table').empty();

          const years   = [...new Set(json.map(r => r.hire_year))];
          const genders = [...new Set(json.map(r => r.gender))];

          const series = genders.map(g => ({
            name: g,
            data: years.map(y => {
              const row = json.find(r => r.hire_year === y && r.gender === g);
              return row ? Number(row.cnt) : 0;
            })
          }));

          Highcharts.chart('chart_container', {
            chart: { type: 'column' },
            title: { text: '입사연도×성별 인원(스택)' },
            xAxis: { categories: years },
            yAxis: { min: 0, title: { text: '인원수' }, stackLabels: { enabled: true } },
            legend: { align: 'center' },
            tooltip: { shared: true },
            plotOptions: { column: { stacking: 'normal' } },
            series: series
          });

          // 테이블 (EL 충돌 방지: 모든 템플릿 리터럴 변수는 \${...} 처리)
          let html = `<table><tr><th>연도</th>\${genders.map(g=>`<th>\${g}</th>`).join('')}<th>합계</th></tr>`;
          years.forEach(y => {
            let rowSum = 0;
            let tds = '';
            genders.forEach(g => {
              const rec = json.find(r => r.hire_year === y && r.gender === g);
              const v = rec ? Number(rec.cnt) : 0;
              rowSum += v;
              tds += `<td>\${v}</td>`;
            });
            html += `<tr><td>\${y}</td>\${tds}<td>\${rowSum}</td></tr>`;
          });
          html += `</table>`;
          $('div#table_container').html(html);
        },
        error: ajaxError
      });
      break;

    default:
      $('div#chart_container').empty();
      $('div#table_container').empty();
      $('div.highcharts-data-table').empty();
      break;
  }
}
</script>


<jsp:include page="../footer/footer.jsp" />