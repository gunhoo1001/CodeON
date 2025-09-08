<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxPath = request.getContextPath();
%>

<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<title>일정 캘린더</title>

<link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.css" rel="stylesheet" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- FullCalendar -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/google-calendar@6.1.7/index.global.min.js"></script>

<style>
:root{
  --bg:#f7f8fa; --card:#ffffff; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --brand:#0071bd;
  --radius:16px; --shadow: 0 8px 24px rgba(0,0,0,.08);
}
*{box-sizing:border-box}
body{margin:0; background:var(--bg); color:var(--text); font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR",Arial,Helvetica,sans-serif;}
.wrapper{max-width:1200px; margin:40px auto; padding:0 20px;}
.layout{display:grid; grid-template-columns:280px 1fr; gap:24px;}
.aside{background:var(--card); border:1px solid var(--line); border-radius:var(--radius); box-shadow:var(--shadow); padding:24px; position:sticky; top:24px; height:fit-content;}
.aside h2{margin:0 0 16px; font-size:18px}
.btn-nav{width:100%; text-align:left; padding:10px 12px; border:1px solid var(--line); background:#fff; border-radius:12px; margin-bottom:10px; font-size:14px; color:var(--text); transition:all .18s ease; display:flex; align-items:center; gap:8px;}
.btn-nav:hover{border-color:#cbd5e1; transform:translateY(-1px)}
.btn-nav.active{border-color:var(--brand); box-shadow:0 0 0 3px rgba(0,113,189,.12); color:var(--brand); font-weight:600}
.badge{display:inline-flex; align-items:center; gap:8px; font-size:12px; color:var(--muted); margin-top:8px}
.badge .dot{width:10px; height:10px; border-radius:3px; display:inline-block}

.main{background:var(--card); border:1px solid var(--line); border-radius:var(--radius); box-shadow:var(--shadow); padding:16px; position:relative}

.fc .fc-toolbar-title{font-size:18px; font-weight:700}
.fc .fc-button{border-radius:10px !important; border:none !important; background:#eef2ff !important; color:#1f2937 !important}
.fc .fc-button-primary:not(:disabled).fc-button-active,.fc .fc-button-primary:not(:disabled):active{background:#e0e7ff !important}
.fc .fc-daygrid-day,.fc .fc-timegrid-slot{border-color:#f0f2f5}
.fc-theme-standard td,.fc-theme-standard th{border-color:#f0f2f5}
.fc .fc-daygrid-day-frame{padding:6px}
.fc .fc-event{border-radius:10px; border:none; padding:2px 6px; font-weight:600}
.fc .fc-day-today{background:#fafafa !important;}
.loading{position:absolute; inset:0; display:none; align-items:center; justify-content:center; background:rgba(255,255,255,.65); border-radius:var(--radius);}
.spinner{width:36px; height:36px; border:3px solid #d1d5db; border-top-color:var(--brand); border-radius:50%; animation:spin 1s linear infinite;}
@keyframes spin{to{transform:rotate(360deg)}}
</style>
</head>

<body>

<input type="hidden" value="${sessionScope.loginuser.memberSeq}" id="fk_userid" />
<input type="hidden" value="${sessionScope.loginuser.fkDepartmentSeq}" id="memberDept" />

<div class="wrapper">
  <div class="layout">

    <aside class="aside">
      <h2>캘린더</h2>
      <button class="btn-nav" data-cat="사내"><span>🏢</span><span>사내 일정</span></button>
      <button class="btn-nav" data-cat="부서"><span>👥</span><span>부서 일정</span></button>
      <button class="btn-nav" data-cat="개인"><span>🧑</span><span>개인 일정</span></button>
    </aside>

    <main class="main">
      <div id="calendar"></div>
      <div class="loading" id="loading"><div class="spinner"></div></div>
    </main>

  </div>
</div>

<script>
let calendar;
let currentCategory = '사내';  // 기본은 사내 일정

function normalizeCategory(raw){
  const x=(raw||'').toString().trim().toLowerCase();
  if(x.includes('사내')) return '사내';
  if(x.includes('부서')) return '부서';
  if(x.includes('개인')||x.includes('내')) return '개인';
  return raw||'';
}

function defaultColorByType(normType){
  switch(normType){
    case '사내': return '#6b46c1';
    case '부서': return '#2563eb';
    case '개인': return '#16a34a';
    default: return '#3788d8';
  }
}

const PLACEHOLDER_SET = new Set(['', '#000000', '#3788d8', 'transparent', 'null']);
function resolveColor(dbColor, normType){
  const c=(dbColor||'').toString().trim().toLowerCase();
  if(!c || PLACEHOLDER_SET.has(c)) return defaultColorByType(normType);
  return dbColor;
}

function updateActiveButtons(){
  document.querySelectorAll('.btn-nav').forEach(btn=>{
    btn.classList.toggle('active', btn.dataset.cat===currentCategory);
  });
}

function initCalendar(){
  const calendarEl=document.getElementById('calendar');
  const loadingEl=document.getElementById('loading');

  calendar=new FullCalendar.Calendar(calendarEl,{
    initialView:'dayGridMonth',
    locale:'ko',
    height:'auto',
    headerToolbar:{left:'prev,next today',center:'title',right:'dayGridMonth,timeGridWeek,timeGridDay'},
    googleCalendarApiKey:"AIzaSyASM5hq3PTF2dNRmliR_rXpjqNqC-6aPbQ",
    eventSources:[{googleCalendarId:'ko.south_korea#holiday@group.v.calendar.google.com',color:'white',textColor:'red'}],
    loading:isLoading=>{loadingEl.style.display=isLoading?'flex':'none'},

    events:function(fetchInfo,successCallback,failureCallback){
      const userid=$("#fk_userid").val();
      const dept=$("#memberDept").val();
      let url="";
      let data={_:Date.now()};

      if(currentCategory==='개인'){
        url='<%= ctxPath %>/Calendar/selectCalendar';
        data.calendarUser=userid;
      }
      else if(currentCategory==='부서'){
        url='<%= ctxPath %>/Calendar/selectDeptCalendar';
        data.fkDepartmentSeq=dept;
      }
      else if(currentCategory==='사내'){
        url='<%= ctxPath %>/Calendar/selectCompanyCalendar';
      }

      $.ajax({
        url:url, type:'GET', data:data, dataType:'json',
        success:function(json){
          const events=json.map(item=>{
            const normType=normalizeCategory(item.calendarType);
            const color=resolveColor(item.calendarColor,normType);
            return {
              id:item.calendarSeq, 
			  title:item.calendarName,
              start:item.calendarStart, 
			  end:item.calendarEnd,
			  allDay: !item.calendarStart.includes('T'),
              url:'<%= ctxPath %>/Calendar/detailCalendar?calendarSeq='+item.calendarSeq,
              color:color, backgroundColor:color, borderColor:color,
              extendedProps:{
                type:normType,
                location:item.calendarLocation,
                content:item.calendarContent
              }
            };
          });
          successCallback(events);
        },
        error:function(req){ console.error("Ajax 오류:",req.responseText); failureCallback(req);}
      });
    },

    eventDidMount:function(info){
      const loc=info.event.extendedProps.location?` @ ${info.event.extendedProps.location}`:'';
      info.el.title=`${info.event.title}${loc}`;
    },

    dateClick:function(info){
    	 let seq="";
    	 if(currentCategory==='사내') seq=1;
    	 else if(currentCategory==='부서') seq=2;
    	 else if(currentCategory==='개인') seq=3;
    	
    	window.location.href="<%= ctxPath %>/Calendar/addCalendarForm?date=" + info.dateStr + "&bigCategorySeq=" + seq;
    }
  });

  calendar.render();
  updateActiveButtons();
}

document.addEventListener('click',function(e){
  const btn=e.target.closest('.btn-nav'); if(!btn) return;
  const cat=btn.dataset.cat; if(!cat||cat===currentCategory) return;
  currentCategory=cat; updateActiveButtons(); calendar.refetchEvents();
});

document.addEventListener('DOMContentLoaded',initCalendar);
</script>

</body>
</html>

<jsp:include page="../footer/footer.jsp" />
