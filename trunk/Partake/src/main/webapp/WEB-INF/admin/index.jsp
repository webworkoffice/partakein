<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="in.partake.service.UserService"%>
<%@page import="in.partake.service.UserService.UserCount"%>
<%@page import="static in.partake.util.Util.h"%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>Administrator Mode</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>Administrator Mode</h1>
<%
	UserService service = UserService.get();
	UserCount count = service.countUsers();
	NumberFormat format = NumberFormat.getInstance();
%>
	<h2>Count of users</h2>
	<dl>
		<dt>User</dt><dd><%= format.format(count.user) %></dd>
		<dt>Active User (who sign in the last 30 days)</dt><dd><%= format.format(count.activeUser) %></dd>
	</dl>


<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>