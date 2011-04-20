<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.util.KeyValuePair"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>フィードリスト - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div id="feedlist">
<h1><img src="<%= request.getContextPath() %>/images/rsslist.png" alt="" />フィードリスト</h1>

<p>PARTAKE では、新着イベントを RSS でフィードしています。カテゴリごとに RSS を受信することもできます。<br>ただし、パスワードが設定されているイベントは配信されません。</p>

<ul>
    <li><a href="<%= request.getContextPath() %>/feed/all">全て</a></li>
    <li><a href="<%= request.getContextPath() %>/feed/upcoming">近日開催</a></li>
    <% for (KeyValuePair kv : EventCategory.CATEGORIES) { %>
    <li><a href="<%= request.getContextPath() %>/feed/category/<%= kv.getKey() %>"><%= kv.getValue() %></a></li>
    <% } %>
</ul>

<p>PARTAKE では、イベントを iCal 形式でも配信しています。<br />ただし、パスワードが設定されているイベントは配信されません。</p>

<ul>
    <li><a href="<%= request.getContextPath() %>/calendars/all">全て</a></li>
    <% for (KeyValuePair kv : EventCategory.CATEGORIES) { %>
    <li><a href="<%= request.getContextPath() %>/calendars/category/<%= kv.getKey() %>"><%= kv.getValue() %></a></li>
    <% } %>    
</ul>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>