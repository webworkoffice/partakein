<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.KeyValuePair"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>フィードリスト - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>フィードリスト</h1>
</div>

<h2>RSS 配信</h2>
<p>PARTAKE では、新着イベントを RSS でフィードしています。カテゴリごとに RSS を受信することもできます。</p>
<p>ただし、パスワードが設定されているイベントは配信されません。</p>

<ul>
    <li><a href="<%= request.getContextPath() %>/feed/all">全て</a></li>
    <li><a href="<%= request.getContextPath() %>/feed/upcoming/all">近日開催</a></li><%-- TODO 「新着イベント」と「近日開催されるイベント」は分けて配置すべき？ --%>
    <% for (KeyValuePair kv : EventCategory.CATEGORIES) { %>
    <li><a href="<%= request.getContextPath() %>/feed/category/<%= kv.getKey() %>"><%= kv.getValue() %></a></li>
    <% } %>
</ul>

<h2>iCal 配信</h2>
<p>PARTAKE では、イベントを iCal 形式でも配信しています。</p>
<p>ただし、パスワードが設定されているイベントは配信されません。</p>

<ul>
    <li><a href="<%= request.getContextPath() %>/calendars/all">全て</a></li>
    <% for (KeyValuePair kv : EventCategory.CATEGORIES) { %>
    <li><a href="<%= request.getContextPath() %>/calendars/category/<%= kv.getKey() %>"><%= kv.getValue() %></a></li>
    <% } %>    
</ul>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>