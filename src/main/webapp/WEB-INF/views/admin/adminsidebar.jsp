<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
  .sidebar {
    position: fixed;
    top: 70px; /* 헤더 높이 */
    left: 0;
    width: 220px;
    height: calc(100vh - 70px - 60px);
    background-color: #fff;
    border-right: 1px solid #ccc;
    padding-top: 20px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
  }
  .sidebar a {
    padding: 12px 20px;
    text-decoration: none;
    color: #333;
    font-size: 16px;
    font-weight: 500;
    display: block;
  }
  .sidebar a:hover,
  .sidebar a.active {
    background-color: #f0f0f0;
    font-weight: 700;
  }
</style>

<div class="sidebar">
  <a href="<%= request.getContextPath() %>/member/register">직원 등록 하기</a>
  <a href="<%= request.getContextPath() %>/member/list">직원 목록 보기</a>
  <a href="<%= request.getContextPath() %>/member/attend">전사 근태 관리</a>
  <a href="<%= request.getContextPath() %>/member/chart">통계보기</a>
  <a href="<%= request.getContextPath() %>/member/chat">인사 도우미</a>
</div>
