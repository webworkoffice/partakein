<%@page import="in.partake.service.EventService"%>
<%@page import="in.partake.service.EventService.EventCount"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="in.partake.service.UserService"%>
<%@page import="in.partake.service.UserService.UserCount"%>
<%@page import="static in.partake.base.Util.h"%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>Administrator Mode</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>Administrator Mode</h1>
</div>

<%
	UserCount userCount = UserService.get().countUsers();
	EventCount eventCount = EventService.get().countEvents();
	NumberFormat format = NumberFormat.getInstance();
	Integer hatenaBookmarkCount = (Integer) request.getAttribute(Constants.ATTR_BOOKMARK_COUNT);
%>

<div class="row">
	<div class="span6">
		<h2>Count of users</h2>
		<dl>
			<dt>User</dt><dd><%= format.format(userCount.user) %></dd>
			<dt>Active User (who sign in the last 30 days)</dt><dd><%= format.format(userCount.activeUser) %></dd>
		</dl>
	
		<h2>Count of events</h2>
		<dl>
			<dt>event</dt><dd><%= format.format(eventCount.numEvent) %></dd>
			<dt>public event</dt><dd><%= format.format(eventCount.numPublicEvent) %></dd>
			<dt>private event</dt><dd><%= format.format(eventCount.numPrivateEvent) %></dd>
		</dl>
	
		<h2>Count of Hatena bookmarks</h2>
		<dl>
			<dt>Sum of all pages</dt><dd><%= format.format(hatenaBookmarkCount) %></dd>
		</dl>
	</div>
	<div class="span6">
		<h2>いろんなリンク</h2>
		<ul>
			<li><a href="<%= request.getContextPath() %>/admin/recreateEventIndex">Luceneインデックス の再生成</a></li>
		</ul>
	</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>