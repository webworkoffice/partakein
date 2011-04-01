<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.resource.I18n"%>
<%@page import="in.partake.view.Helper"%>

<%@page import="java.util.List"%>

<%@page import="static in.partake.view.Helper.h"%>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	List<Event> recentEvents = (List<Event>)request.getAttribute(Constants.ATTR_RECENT_EVENTS);
%>

<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="/feed/all" />

	<title>[PARTAKE]</title>
</head>
<body>

<div id="right-tweet">
	<a href="http://twitter.com/home?status=PARTAKE%20http://partake.in/" target="_blank"></a>
</div>

<div id="right-momonga">
<a href="http://twitter.com/#!/partakein"></a>
</div>

<div id="right-rss">
<a href="<%= request.getContextPath() %>/feedlist"></a>
</div>

<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="top-introduction">	
    <p class="top-introduce"><%=I18n.t("page.toppage.intro") %></p>
<div class="rollover1">	
	<a href="<%= request.getContextPath() %>/events/search"><img src="<%= request.getContextPath() %>/images/top-search.png" alt="<%= I18n.t("page.toppage.button.search") %>" id="top-search-button" /></a>
</div>
<div class="rollover2">
	<a href="<%= request.getContextPath() %>/events/new"><img src="<%= request.getContextPath() %>/images/top-form.png" alt="<%= I18n.t("page.toppage.button.create") %>" id="top-form-button" /></a>
</div>
<div class="sample">
	<a href="<%= request.getContextPath() %>/events/demo"><img src="<%= request.getContextPath() %>/images/sample1.gif" alt="<%= I18n.t("page.toppage.button.sample") %>" id=""></a>
</div>
</div>

<div class="top-explanations">
    <h2><img src="<%= request.getContextPath() %>/images/line-orange.png" alt=""/>PARTAKE!</h2>
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-01.png" alt="" />
		<h3><%= I18n.t("page.toppage.explanation.1") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.1.page") %><a href="<%= request.getContextPath() %>/events/demo">[<%= I18n.t("common.sample") %>]</a></li>
			<li><%= I18n.t("page.toppage.explanation.1.announcement") %></li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-02.png" alt="" />
		<h3><%= I18n.t("page.toppage.explanation.2") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.2.print") %></li>
			<li><%= I18n.t("page.toppage.explanation.2.message") %></li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-03.png" alt="" />
		<h3><%= I18n.t("page.toppage.explanation.3") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.3.search") %></li>
			<li><a href="<%= request.getContextPath() %>/feedlist"><%= I18n.t("page.toppage.explanation.3.feed") %></a></li>
			<li><a href="http://twitter.com/partake_bot"><%= I18n.t("page.toppage.explanation.3.bot") %></a></li>
		</ul>
	</div>
	<div class="top-explanation">
	   <img src="<%= request.getContextPath() %>/images/feature-04.png" alt="" />
	   <h3><%= I18n.t("page.toppage.explanation.4") %></h3>
	   <ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.4.issue") %></li>
			<li><%= I18n.t("page.toppage.explanation.4.developer") %></li>
			<li><%= I18n.t("page.toppage.explanation.4.faq") %></li>
	   </ul>
	</div>
</div>

<%-- ログインしていれば、直近のイベントを表示する --%>
<% if (user != null) { %>
	<div class="top-user-events rad">
		<h2>登録イベント</h2>
		<% List<Event> enrolled = (List<Event>) request.getAttribute(Constants.ATTR_ENROLLED_EVENTSET); %>		
		<h2><%= I18n.t("page.toppage.recent.entry") %></h2>
		<% List<Event> enrolled = (List<Event>) request.getAttribute(Constants.ATTR_ENROLLED_EVENTSET); %>
		<% if (enrolled != null && !enrolled.isEmpty()) { %>
		  <ul>
			<% for (int i = 0; i < 3 && i < enrolled.size(); ++i) { %>
				<% Event event = enrolled.get(i); %>
				<li><a href="<%= h(event.getEventURL()) %>"><%= h(event.getTitle()) %></a></li>
			<% } %>
			</ul>            
			<p class="more"><a href="<%= request.getContextPath() %>/mypage">&raquo;more...</a></p>
			</ul>
			<p class="more"><a href="<%= request.getContextPath() %>/mypage"><%= I18n.t("page.toppage.recent.more") %></a></p>
		<% } else { %>
			<p><%= I18n.t("page.toppage.recent.entry.empty") %></p>
		<% } %>
		<h2><%= I18n.t("page.toppage.recent.admin") %></h2>
		<% List<Event> owned = (List<Event>) request.getAttribute(Constants.ATTR_OWNED_EVENTSET); %>		
		<h2><%= I18n.t("page.toppage.recent.admin") %></h2>
		<% List<Event> owned = (List<Event>) request.getAttribute(Constants.ATTR_OWNED_EVENTSET); %>
		<% if (owned != null && !owned.isEmpty()) { %>
			<ul>
			<% for (int i = 0; i < 3 && i < owned.size(); ++i) { %>
				<% Event event = owned.get(i); %>
				<li><a href="<%= h(event.getEventURL()) %>"><%= h(event.getTitle()) %></a></li>
			<% } %>
			</ul>            
			<p class="more"><a href="<%= request.getContextPath() %>/mypage">&raquo;more...</a></p>
			</ul>
			<p class="more"><a href="<%= request.getContextPath() %>/mypage"><%= I18n.t("page.toppage.recent.more") %></a></p>
		<% } else { %>
			<p><%= I18n.t("page.toppage.recent.admin.empty") %></p>
		<% } %>
	</div>
<% } %>

<div class="top-recent-events rad">
	<img src="<%= request.getContextPath() %>/images/recent-events.png" />
	<% if (recentEvents != null) { %>
		<% for (Event event : recentEvents) { %>
			<% if (event == null) { continue; } %>
			<% String classPrefix = "top-recent-event"; %>
			<div class="top-recent-event">
				<h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
				<div class="<%= h(classPrefix) %>-image">
					<% if (event.getForeImageId() != null) { %>
					<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
					<img class="rad sdw cler" src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
					<% } else { %>
					<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
					<img class="rad sdw cler" src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
					<% } %>
				</div>
				<div class="<%= h(classPrefix) %>-content">
					<p><%= h(event.getSummary()) %></p>
					<dl>
						<% if (!StringUtils.isEmpty(event.getPlace())) { %>
						<dt><%= I18n.t("event.place") %>：</dt><dd><%= h(event.getPlace()) %></dd>
						<% } %>
						<% if (event.getBeginDate() != null) { %>
						<dt><%= I18n.t("event.time") %>：</dt><dd><%= Helper.readableDate(event.getBeginDate()) %></dd>
						<% } %>
					</dl>
				</div>
            </div>
			</div>
		<% } %>
	<% } %>
	<p class="more"><a href="<%= request.getContextPath() %>/events/search"><%= I18n.t("page.toppage.recent.more") %></a></p>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>