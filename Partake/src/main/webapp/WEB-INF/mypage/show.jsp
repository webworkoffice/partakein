<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<% UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER); %>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title><%= h(user.getScreenName()) %> - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1><%= h(user.getScreenName()) %></h1>
</div>

<div class="row tabbable">
	<div class="span3"><div class="well" style="padding: 8px 0;">
		<ul class="nav nav-list tabs">
			<li class="nav-header">イベント</li>
			<li class="active"><a href="#event-owner" data-toggle="tab">管理イベント</a></li>
			<li><a href="#event-participant" data-toggle="tab">参加予定イベント</a></li>
			<li><a href="#event-finished" data-toggle="tab">終了イベント</a></li>
			<li class="nav-header">ユーザー</li>
			<li><a id="account-link" href="#account" data-toggle="tab">アカウントリンク</a></li>
			<li><a href="#calendar" data-toggle="tab">カレンダー</a></li>
			<li><a href="#preference" data-toggle="tab">設定</a></li>
		</ul>
	</div></div>
	<div class="span9 tab-content">
		<div class="tab-pane active" id="event-owner">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="manager" />
				<jsp:param name="queryType" value="manager" />
				<jsp:param name="finished" value="all" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="event-participant">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="participants" />
				<jsp:param name="queryType" value="participants" />
				<jsp:param name="finished" value="false" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="event-finished">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="finished" />
				<jsp:param name="queryType" value="participants" />
				<jsp:param name="finished" value="true" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="account">
			<jsp:include page="/WEB-INF/mypage/_account.jsp" flush="true" />
		</div>
		<div class="tab-pane" id="calendar">
			<jsp:include page="/WEB-INF/mypage/_calendar.jsp" flush="true" />
		</div>
		<div class="tab-pane" id="preference">
			<jsp:include page="/WEB-INF/mypage/_preference.jsp" flush="true" />
		</div>
	</div>
</div>

<script>
(function() {
	if (location.hash == "#account")
		$('#account-link').tab('show');
})();
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
	
</body>
</html>