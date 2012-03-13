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
			<li class="nav-header">管理イベント</li>
			<li class="active"><a href="#event-owner" data-toggle="tab">主催イベント</a></li>
			<li><a href="#event-draft" data-toggle="tab">下書き中のイベント</a></li>
			<li><a href="#event-editor" data-toggle="tab">編集権のあるイベント</a></li>
			<li class="nav-header">参加イベント</li>
			<li><a href="#event-enrolled" data-toggle="tab">登録イベント</a></li>
			<li class="nav-header">ユーザー設定</li>
			<li><a id="account-link" href="#account" data-toggle="tab">アカウントリンク</a></li>
			<li><a href="#calendar" data-toggle="tab">カレンダー</a></li>
			<li><a href="#preference" data-toggle="tab">各種設定</a></li>
		</ul>
	</div></div>
	<div class="span9 tab-content">
		<div class="tab-pane active" id="event-owner">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="owner" />
				<jsp:param name="queryType" value="owner" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="event-draft">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="draft" />
				<jsp:param name="queryType" value="draft" />
			</jsp:include>
		</div>
		<div class="tab-pane" id="event-editor">
			<jsp:include page="/WEB-INF/mypage/_event_table.jsp" flush="true">
				<jsp:param name="ident" value="editor" />
				<jsp:param name="queryType" value="editor" />
			</jsp:include>
		</div>
		
 		<div class="tab-pane" id="event-participant">
			<jsp:include page="/WEB-INF/mypage/_enrollment_table.jsp" flush="true">
				<jsp:param name="ident" value="enrollment" />
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