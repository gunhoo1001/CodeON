<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="chart-area" style="width:100%; height:900px;"></div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Highcharts -->
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/sankey.js"></script>
<script src="https://code.highcharts.com/modules/organization.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>

<jsp:include page="../header/header.jsp" />

<script>
$(function() {
    $.ajax({
        url: "${pageContext.request.contextPath}/company/organization/chartData",
        method: "GET",
        success: function(data) {

            const getColumn = (gradeSeq) => 5 - gradeSeq;
            const ceo = data.find(m => m.gradeSeq === 5);
            if (!ceo) { alert("사장 데이터 없음"); return; }

            const departments = {};
            data.forEach(m => {
                if (m.gradeSeq === 5) return;
                if (!departments[m.departmentName]) {
                    departments[m.departmentName] = [];
                }
                departments[m.departmentName].push(m);
            });

            const nodes = [{
                id: ceo.memberSeq.toString(),
                title: ceo.gradeName,
                name: ceo.memberName,
                column: getColumn(ceo.gradeSeq),
                color: '#1E90FF'
            }];
            const links = [];

            const deptColors = {
                '인사팀': '#E74C3C',
                '개발팀': '#3498DB',
                '기획팀': '#9B59B6',
                '영업팀': '#2ECC71',
                '고객지원팀': '#E67E22'
            };

            Object.keys(departments).forEach(deptName => {
                const members = departments[deptName];

                const head = members.find(m => m.gradeSeq === 4);
                if (head) {
                    links.push([ceo.memberSeq.toString(), head.memberSeq.toString()]);
                }

                members.forEach(m => {
                    if (!nodes.find(n => n.id === m.memberSeq.toString())) {
                        nodes.push({
                            id: m.memberSeq.toString(),
                            title: m.gradeName,
                            name: m.memberName + "<br/><span style='display:inline-block;margin-top:3px;padding:2px 6px;border-radius:6px;background:#fff;color:"+deptColors[deptName]+";font-size:12px;font-weight:bold;'>"+deptName+"</span>",

                            column: getColumn(m.gradeSeq),
                            color: deptColors[deptName] || '#95A5A6'
                        });
                    }
                });

                members.forEach(m => {
                    const parent = members.find(x => x.gradeSeq === m.gradeSeq + 1);
                    if (parent) {
                        links.push([parent.memberSeq.toString(), m.memberSeq.toString()]);
                    }
                });
            });

            Highcharts.chart("chart-area", {
                chart: {
                    inverted: true,
                    height: 900,
                    backgroundColor: '#fdfdfd'
                },
                title: {
                    text: "코드온 조직도",
                    style: { fontSize: '22px', fontWeight: 'bold' }
                },
                series: [{
                    type: 'organization',
                    keys: ['from', 'to'],
                    data: links,
                    nodes: nodes,
                    colorByPoint: false,
                    borderColor: '#fff',
                    dataLabels: {
                        color: '#fff',
                        nodeFormatter: function () {
                            return '<div style="padding:5px;font-size:14px;"><b>' 
                                + this.point.title + '</b><br/>' 
                                + this.point.name + '</div>';
                        }
                    },
                    nodeWidth: 100,
                    nodePadding: 30,
                    hangingIndent: 30,
                    layout: 'normal'
                }],
                tooltip: { outside: true },
                exporting: { allowHTML: true, enabled: true }
            });
        },
        error: function(xhr, status, err) {
            console.error("Ajax 오류:", err);
        }
    });
});
</script>

<br>
<br>
<br>
<jsp:include page="../footer/footer.jsp" />
