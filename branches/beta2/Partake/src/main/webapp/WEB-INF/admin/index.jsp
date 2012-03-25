<%@page import="in.partake.controller.action.admin.AdminPageAction"%>
<%@page import="in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade"%>
<%@page import="in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade.EventCount"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="java.text.NumberFormat"%>
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
	AdminPageAction action = (AdminPageAction) request.getAttribute(Constants.ATTR_ACTION);
%>

<div class="row">
	<div class="span6">
		<h2>Count of users</h2>
		<dl>
			<dt>User</dt><dd><%= action.getCountUser() %></dd>
			<dt>Active User (who sign in the last 30 days)</dt><dd><%= action.getCountActiveUser() %></dd>
		</dl>
	
		<h2>Count of events</h2>
		<dl>
			<dt>event</dt><dd><%= action.getCountEvent() %></dd>
			<dt>public event</dt><dd><%= action.getCountPublicEvent() %></dd>
			<dt>private event</dt><dd><%= action.getCountPrivateEvent() %></dd>
			<dt>published event</dt><dd><%= action.getCountPublishedEvent() %></dd>
			<dt>draft event</dt><dd><%= action.getCountDraftEvent() %></dd>
		</dl>
	
<%-- 		<h2>Count of Hatena bookmarks</h2>
		<dl>
			<dt>Sum of all pages</dt><dd><%= format.format(hatenaBookmarkCount) %></dd>
		</dl>
 --%>	</div>
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