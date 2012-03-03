<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.event.EventPasscodeAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>パスコードを入れてください</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<% EventPasscodeAction action = (EventPasscodeAction) request.getAttribute(Constants.ATTR_ACTION); %>

<div class="page-header">
	<h1>イベントを表示するためにパスコードを入れてください。</h1>
</div>

<div class="row">
	<div class="span12">
		<form method="post" action="/events/passcode" id="passcode-checking-form">
			<%= Helper.tokenTags() %>
			<input type="hidden" name="eventId" value="<%= h(action.getEventId()) %>" />
		    <label for="passcode">パスコード:</label><input id="passcode-checking" name="passcode" autofocus="autofocus" /><br />
		    <input type="submit" />
		</form>
	</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>