<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title><%= h(user.getScreenName()) %> - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1><%= h(user.getScreenName()) %></h1>
	<p><%= h(user.getScreenName()) %> さんが管理している / 登録しているイベントを確認できます。</p>
</div>
				 
<div class="row">
	<div class="span9">
		<h3>管理しているイベント</h3>
		<% List<Event> ownedEvents = (List<Event>)request.getAttribute(Constants.ATTR_OWNED_EVENTSET);
			if (ownedEvents != null && !ownedEvents.isEmpty()) { %>
				<table class="table table-striped">
				    <colgroup>
				    	<col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
		 		    </colgroup>
					<thead>
						<tr><th>&nbsp;</th><th>イベントタイトル</th><th>開催日</th><th>参加人数/定員</th></tr>
					</thead>
					<tbody>
					    <% for (Event event : ownedEvents) { %>
					    <% if (event == null) { continue; /* TODO: should be logged. */} %>
					    <tr>
					        <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" title="非公開イベント" /><% } %></td>
					    	<td><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></td>
					    	<td><%= Helper.readableDate(event.getBeginDate()) %></td>
					    	<td><%= Helper.readableCapacity(event.fetchNumOfEnrolledUsers(), event.getCapacity()) %></td>
					    </tr>
						<% } %>
					</tbody>
				</table>
			<% } else if (ownedEvents != null) { %>
			   <p>管理しているイベントはありません</p>
			<% } else { %>
			   <p>エラー発生？</p> 
			<% }
		%>
	</div>
</div>

<div class="row">
	<div class="span9">
	    <h3>参加予定のイベント</h3>
		<% 
			List<Event> enrolledEvents = (List<Event>)request.getAttribute(Constants.ATTR_ENROLLED_EVENTSET);
			if (enrolledEvents != null && !enrolledEvents.isEmpty()) { %>
				<table class="table table-striped">
				    <colgroup>
				    	<col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
		 		    </colgroup>
					<thead>
						<tr><th></th><th class="col1">イベントタイトル</th><th>開催日</th><th>ステータス</th></tr>
					</thead>
					<tbody>
					    <% for (Event event : enrolledEvents) { %>
					    <% if (event == null) { continue; /* TODO: should be logged. */} %>
					    <tr>
					        <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" title="非公開イベント" /><% } %></td>
					    	<td><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></td>
					    	<td><%= Helper.readableDate(event.getBeginDate()) %></td>
					    	<td><%= Helper.enrollmentStatus(user, event) %></td>
					    </tr>
						<% } %>
					</tbody>
				</table>
		    <% } else if (enrolledEvents != null) { %>
		       <p>管理しているイベントはありません</p>
			<% } else { %>
				<p>エラー発生？</p>
			<% } %>
	</div>
</div>

<div class="row">
	<div class="span9">
		<h3>終了したイベント</h3>
		<% List<Event> finishedEvents = (List<Event>)request.getAttribute(Constants.ATTR_FINISHED_EVENTSET);
		    if (finishedEvents != null && !finishedEvents.isEmpty()) { %>
		        <table class="table table-striped">
		            <colgroup>
		            	<col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
		 		    </colgroup>
 			    <thead>
		                <tr><th></th><th class="col1">イベントタイトル</th><th>開催日</th><th>ステータス</th></tr>
		            </thead>
		            <tbody>
		                <% for (Event event : finishedEvents) { %>
		                <% if (event == null) { continue; /* TODO: should be logged. */} %>
		                <tr>
		                    <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" title="非公開イベント" /><% } %></td>
		                    <td><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></td>
		                    <td><%= Helper.readableDate(event.getBeginDate()) %></td>
		                    <td><%= Helper.enrollmentStatus(user, event) %></td>
		                </tr>
		                <% } %>
		            </tbody>
		        </table>
		    <% } else if (finishedEvents != null) { %>
		        <p>終了したイベントはありません</p>
		    <% } else { %>
		        <p>エラー発生？</p>
		    <% } %>
	</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
	
</body>
</html>