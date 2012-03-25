<%@page import="in.partake.controller.action.toppage.ToppageAction"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.resource.I18n"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="java.util.List"%>
<%@page import="static in.partake.view.util.Helper.h"%>

<%
    ToppageAction action = (ToppageAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    List<Event> recentEvents = action.getRecentEvents();
%>

<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="/feed/all" />
	<title>[PARTAKE]</title>
</head>
<body>

<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="hero-unit">
    <h1>PARTAKE</h1>
    <p>PARTAKE は、イベントの作成・参加管理・参加者への連絡が簡単にできる、イベント開催支援サービスです。</p>
    <p><a href="/events/demo" class="btn btn-primary btn-large">デモを見る</a></p>
</div>

<div class="row">
	<div class="span6">
		<h3><%=I18n.t("page.toppage.explanation.1")%></h3>
		<ul class="top-feature">
			<li><%=I18n.t("page.toppage.explanation.1.page")%><a href="<%=request.getContextPath()%>/events/demo">[<%=I18n.t("common.sample")%>]</a></li>
			<li><%=I18n.t("page.toppage.explanation.1.announcement")%></li>
		</ul>
	
		<h3><%=I18n.t("page.toppage.explanation.2")%></h3>
		<ul class="top-feature">
			<li><%=I18n.t("page.toppage.explanation.2.print")%></li>
			<li><%=I18n.t("page.toppage.explanation.2.message")%></li>
		</ul>

		<h3><%=I18n.t("page.toppage.explanation.3")%></h3>
		<ul class="top-feature">
			<li><%=I18n.t("page.toppage.explanation.3.search")%></li>
			<li><a href="<%=request.getContextPath()%>/feed/"><%=I18n.t("page.toppage.explanation.3.feed")%></a></li>
			<li><a href="http://twitter.com/partake_bot"><%=I18n.t("page.toppage.explanation.3.bot")%></a></li>
		</ul>
	
	   <h3><%=I18n.t("page.toppage.explanation.4")%></h3>
	   <ul class="top-feature">
			<li><%=I18n.t("page.toppage.explanation.4.issue")%></li>
			<li><%=I18n.t("page.toppage.explanation.4.developer")%></li>
			<li><%=I18n.t("page.toppage.explanation.4.faq")%></li>
	   </ul>
	</div><%-- end of span6 --%>

	<div class="span6"><div class="tabbable">
		<%-- ログインしていれば、直近のイベントを表示する --%>
		<ul class="nav nav-tabs">
			<%
			    if (user != null) {
			%>
			<li class="active"><a href="#registered-events" data-toggle="tab">登録イベント</a></li>
			<li><a href="#managing-events" data-toggle="tab">管理イベント</a></li>
			<li><a href="#new-events" data-toggle="tab">新着</a></li>
			<%-- <li><a href="#recent-events" data-toggle="tab">締切間近</a></li>  --%>
			<%
			    } else {
			%>
			<li class="active"><a href="#new-events" data-toggle="tab">新着</a></li>
			<%-- <li><a href="#recent-events" data-toggle="tab">締切間近</a></li>  --%>
			<%
			    }
			%>
		</ul>
		
		<%-- TODO: This source code should be more beautiful. --%>
		<div class="tab-content top-page-events">
			<%
			    if (user != null) {
			%>
			<%-- 登録イベント --%>
			<% List<Event> enrolled = action.getEnrolledEvents(); %>
			<div id="registered-events" class="tab-pane active">
				<%
				    if (enrolled != null && !enrolled.isEmpty()) {
				%>
					<%
					    for (int i = 0; i < 3 && i < enrolled.size(); ++i) {
					%>
						<%
						    Event event = enrolled.get(i);
						%>
						<%
						    if (event == null) {
						                    continue;
						                }
						%>
						<div class="well thin"><div class="row event">
							<div class="event-image span-onehalf">
								<%
								    if (event.getForeImageId() != null) {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/events/images/<%=event.getForeImageId()%>" alt="" /></a>
								<%
								    } else {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/images/no-image.png" alt="" /></a>
								<%
								    }
								%>
							</div>
							<div class="span5">
								<h3><a href="<%=request.getContextPath()%>/events/<%=event.getId()%>"><%=h(event.getTitle())%></a></h3>
								<p><%=h(event.getSummary())%>
								<%
								    if (event.getBeginDate() != null) {
								%>
									<br /><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%>
								<%
								    }
								%></p>
							</div>
						</div></div>
					<%
					    }
					%>
				<%
				    } else {
				%>
					<p><%=I18n.t("page.toppage.recent.entry.empty")%></p>
				<%
				    }
				%>
				<p class="more"><a href="<%=request.getContextPath()%>/mypage"><%=I18n.t("page.toppage.recent.more")%></a></p>
			</div>
		
			<%-- 管理イベント --%>
			<% List<Event> owned = action.getOwnedEvents(); %>
			<div id="managing-events" class="tab-pane">
				<%
				    if (owned != null && !owned.isEmpty()) {
				%>
					<%
					    for (int i = 0; i < 3 && i < owned.size(); ++i) {
					%>
						<%
						    Event event = owned.get(i);
						%>
						<%
						    if (event == null) {
						                    continue;
						                }
						%>
						<div class="well thin"><div class="row event">
							<div class="event-image span-onehalf">
								<%
								    if (event.getForeImageId() != null) {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/images/<%=event.getForeImageId()%>" alt="" /></a>
								<%
								    } else {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/images/no-image.png" alt="" /></a>
								<%
								    }
								%>
							</div>
							<div class="span5">
								<h3><a href="<%=request.getContextPath()%>/events/<%=event.getId()%>"><%=h(event.getTitle())%></a></h3>
								<p><%=h(event.getSummary())%>
								<%
								    if (event.getBeginDate() != null) {
								%>
									<br /><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%>
								<%
								    }
								%></p>
							</div>
						</div></div>
					<%
					    }
					%>
				<%
				    } else {
				%>
					<p><%=I18n.t("page.toppage.recent.admin.empty")%></p>
				<%
				    }
				%>
				<p class="more"><a href="<%=request.getContextPath()%>/mypage"><%=I18n.t("page.toppage.recent.more")%></a></p>
			</div>
			<%
			    }
			%>
		
			<%-- 新着イベント --%>
			<div id="new-events" class="tab-pane<%=user != null ? "" : " active"%>">
				<%
				    if (recentEvents != null) {
				%>
					<%
					    for (Event event : recentEvents) {
					%>
						<%
						    if (event == null) {
						                continue;
						            }
						%>
						<div class="well thin"><div class="row event">
							<div class="event-image span-onehalf">
								<%
								    if (event.getForeImageId() != null) {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/events/images/<%=event.getForeImageId()%>" alt="" /></a>
								<%
								    } else {
								%>
									<a href="<%=request.getContextPath()%>/events/<%=event.getId()%>">
									<img class="rad sdw cler" src="/images/no-image.png" alt="" /></a>
								<%
								    }
								%>
							</div>
							<div class="span5">
								<h3><a href="<%=request.getContextPath()%>/events/<%=event.getId()%>"><%=h(event.getTitle())%></a></h3>
								<p><%=h(event.getSummary())%>
								<%
								    if (event.getBeginDate() != null) {
								%>
									<br /><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%>
								<%
								    }
								%></p>
							</div>
						</div></div>
					<%
					    }
					%>
				<%
				    }
				%>
				<p class="more"><a href="/events/search"><%=I18n.t("page.toppage.recent.more")%></a></p>
			</div>
		</div>
	</div></div><%-- end of span6 and --%>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>