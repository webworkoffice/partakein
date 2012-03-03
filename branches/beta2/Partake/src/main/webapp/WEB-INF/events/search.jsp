<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@ page import="static in.partake.view.util.Helper.h"%>
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

<div class="page-header">
	<h1>イベント検索</h1>
	<p>タイトル、カテゴリ、本文などからイベントを検索します。</p>
</div>

<div class="well event-search">
	<form class="form-horizontal" action="">
		<fieldset>
			<%-- <legend>タイトル、本文からイベントを検索</legend>  --%>
			<div class="control-group">
				<label class="control-label">検索語句</label>
            	<div class="controls">
              		<s:textfield cssClass="large" id="searchBox" name="searchTerm" size="30" type="text" />
              		<input type="submit" class="btn btn-primary" alt="Search" value="Search" />
            	</div>
			</div>
			<div class="control-group">
            	<label class="control-label">オプション</label>
            	<div class="controls">
		            <div class="form-inline">
	           			<span class="event-search-inline">カテゴリ</span><s:select id="category" name="category" cssClass="medium" list="categories" listKey="key" listValue="value"></s:select>
						<span class="event-search-inline">並べ替え</span><s:select id="sortOrder" name="sortOrder" cssClass="medium" list="sortOrders" listKey="key" listValue="value"></s:select>
	   				</div>
	   			</div>
	   		</div>
	   		<div class="control-group">
	   			<div class="controls">
	   				<div class="form-inline">
	   					<label class="checkbox"><s:checkbox id="beforeDeadlineOnly" name="beforeDeadlineOnly" />締め切り前のイベントのみを検索する</label>
		            </div>		        
		        </div>
            </div>
		</fieldset>
	</form>
</div>

<div class="searched-events">
<% if (events == null) { %>
	<h2>最近登録されたイベント</h2>	
	<% if (recentEvents != null) {
		for (Event event : recentEvents) { %>
            <% if (event == null) { continue; } %>
           	<div class="row searched-event">
           		<div class="span2 event-image">
				    <% if (event.getForeImageId() != null) { %>
						<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img class="rad sdw cler" src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
				    <% } else { %>
						<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img class="rad sdw cler" src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
				    <% } %>
				</div>
	            <div class="span10">
	            	<h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
					<p><%= h(event.getSummary()) %></p>
					<p>場所：<%= h(event.getPlace()) %></p>
					<p>日時：<%= Helper.readableDate(event.getBeginDate()) %></p>
				</div>
			</div>
		<% } %>
	<% } else { %>
		<p>最近登録された締め切り前のイベントはありません。</p>
	<% } %>
<% } else if (events.isEmpty()) { %>
	<h2>検索結果</h2>
	<p>ヒットしませんでした。別の単語で試してみてください。</p>
<% } else { %>
	<h2>検索結果</h2>
	<% for (Event event : events) { %>
		<% if (event == null) { continue; } %>
		<div class="row searched-event">
			<div class="span2">
                <% if (event.getForeImageId() != null) { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
                    <img class="rad sdw cler" src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
                <% } else { %>
<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
                    <img class="rad sdw cler" src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
                <% } %>
            </div>
            <div class="span10">
            	<h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
				<p><%= h(event.getSummary()) %></p>
				<p>場所：<%= h(event.getPlace()) %></p>
				<p>日時：<%= Helper.readableDate(event.getBeginDate()) %></p>
			</div>
		</div>
	<% } %>
<% } %>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>