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
  <a href="<%= request.getContextPath() %>/sign/add">결재 양식 작성</a>
  <a href="<%= request.getContextPath() %>/sign/inbox">결재하기</a>
  <a href="<%= request.getContextPath() %>/sign/sent">문서함</a>
  <a href="<%= request.getContextPath() %>/sign/history">결재함</a>
  <a href="<%= request.getContextPath() %>/sign/setting">환경설정</a>
</div>
