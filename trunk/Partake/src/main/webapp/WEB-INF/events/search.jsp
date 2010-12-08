<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@ page import="static in.partake.util.Util.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@taglib prefix="s" uri="/struts-tags" %>

<%
	List<Event> events = (List<Event>)request.getAttribute(Constants.ATTR_SEARCH_RESULT);
	List<Event> recentEvents = (List<Event>)request.getAttribute(Constants.ATTR_RECENT_EVENTS);
%>

<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>イベント検索 - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1 id="event-search-titleline"><img src="<%= request.getContextPath() %>/images/line-yellow.png" alt="" />イベントを検索します</h1>

<div id="event-search-form">
	<img src="<%= request.getContextPath() %>/images/search-icon.png" alt="" />タイトル、本文からイベントを検索します。
	<div class="line-space">
		<s:form action="%{#request.contextPath}/events/search">
            <dl>
                <dt>検索語句：</dt><dd><s:textfield id="searchTerm" name="searchTerm" /></dd>
                <dt>カテゴリ：</dt><dd><s:select id="category" name="category" list="categories" listKey="key" listValue="value"></s:select></dd>
                <dt>ソート順：</dt><dd><s:select id="sortOrder" name="sortOrder" list="sortOrders" listKey="key" listValue="value"></s:select></dd>
            </dl>
			<p><s:checkbox id="beforeDeadlineOnly" name="beforeDeadlineOnly" />締め切り前のイベントのみを検索する </p>
		    <div class="search-btn">
		        <input type="image" id="search_0" src="<%= request.getContextPath() %>/images/btn-search.png" alt="search"/>
		    </div>		
		</s:form>
	</div>
</div>

<% if (events == null) { %>
	<h2 class="event-searched-head">最近登録された締め切り前のイベント</h2>	
	<% if (recentEvents != null) {
		for (Event event : recentEvents) { %>
            <% if (event == null) { continue; } %>
            <div class="event-searched">
				<h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
				<div class="event-searched-image">
				    <% if (event.getForeImageId() != null) { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
				    <% } else { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
				    <% } %>
				</div>
				<div class="event-searched-content">
					<p><%= h(event.getSummary()) %></p>
					<dl>
						<dt>場所：</dt><dd><%= h(event.getPlace()) %></dd>
						<dt>日時：</dt><dd><%= Helper.readableDate(event.getBeginDate()) %></dd>
					</dl>
				</div>
			</div>
		<% } %>
	<% } else { %>
		<p>最近登録された締め切り前のイベントはありません。</p>
	<% } %>
<% } else if (events.isEmpty()) { %>
	<h2 class="event-searched-head">検索結果</h2>
	<p>検索にヒットしませんでした。</p>
<% } else { %>
	<h2 class="event-searched-head">検索結果</h2>
	<% for (Event event : events) { %>
		<% if (event == null) { continue; } %>
		<div class="event-searched">
			<h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
			<div class="event-searched-image">
                <% if (event.getForeImageId() != null) { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
                    <img src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
                <% } else { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
                    <img src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
                <% } %>
            </div>
            <div class="event-searched-content">
				<p><%= h(event.getSummary()) %></p>
				<dl>
					<dt>場所：</dt><dd><%= h(event.getPlace()) %></dd>
					<dt>日時：</dt><dd><%= Helper.readableDate(event.getBeginDate()) %></dd>
				</dl>
			</div>
		</div>
	<% } %>
<% } %>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>