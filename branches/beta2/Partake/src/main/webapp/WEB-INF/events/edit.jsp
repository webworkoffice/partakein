<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	EventEditAction action = (EventEditAction) request.getAttribute(Constants.ATTR_ACTION);
	EventEx event = action.getEvent();
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>イベントを編集します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>イベントを編集します</h1>
</div>

<div class="row">
	<div class="span9">
		<jsp:include page="/WEB-INF/events/_edit_innerform.jsp" />
	</div>
	<div class="span3">
		<div class="fixed span3">
			<% if (event.isPreview()) { %>
			<input type="button" class="btn btn-primary span3" value="イベントを保存する" />
			<p class="help-block">イベントを保存します。保存しただけではまだ公開されません。</p>
			<p></p>
			<input type="button" class="btn btn-danger span3" value="イベントを公開する" />
			<p class="help-block">イベントを公開します。</p>
			<% } else { %>
			<input type="button" class="btn btn-primary span3" value="イベントを更新する" />
			<p class="help-block">イベントを更新します。</p>
			<% } %>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
