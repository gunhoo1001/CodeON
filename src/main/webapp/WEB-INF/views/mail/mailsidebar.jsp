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
  <a href="<%= request.getContextPath() %>/mail/write">메일쓰기</a>
  <a href="<%= request.getContextPath() %>/mail/list">전체 메일함</a>
  <a href="<%= request.getContextPath() %>/mail/send">보낸 메일함</a>
  <a href="<%= request.getContextPath() %>/mail/receive">받은 메일함</a>
</div>
