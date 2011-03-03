<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@page import="static in.partake.view.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title><%= h(user.getScreenName()) %> さんのマイページ - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div id="mypage-title">
	<h1><%= h(user.getScreenName()) %> さんのマイページ</h1>
	<p><%= h(user.getScreenName()) %> さんが管理している / 登録しているイベントを確認できます。</p>
</div>
				 
<div class="mypage-mine">
<h2><img src="<%= request.getContextPath() %>/images/drop-yellow.png" alt=""/>管理しているイベント</h2>
<% 
	List<Event> ownedEvents = (List<Event>)request.getAttribute(Constants.ATTR_OWNED_EVENTSET);
	if (ownedEvents != null && !ownedEvents.isEmpty()) { %>
		<table class="table0">
    <colgroup>
      <col width="30px" /><col width="100px" /><col width="50px" /><col width="50px" />
    </colgroup>
			<thead>
				<tr><th class="">　</th><th class="col1">イベントタイトル</th><th>開催日</th><th>参加予定人数/募集人数</th></tr>
			</thead>
			<tbody>
			    <% for (Event event : ownedEvents) { %>
			    <% if (event == null) { continue; /* TODO: should be logged. */} %>
			    <tr>
			        <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" /><% } %></td>
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

<div class="mypage-mine">
    <h2><img src="<%= request.getContextPath() %>/images/drop-orange.png" alt="" />参加予定のイベント</h2>
<% 
	List<Event> enrolledEvents = (List<Event>)request.getAttribute(Constants.ATTR_ENROLLED_EVENTSET);
	if (enrolledEvents != null && !enrolledEvents.isEmpty()) { %>
		<table class="table0">
    <colgroup>
      <col width="30px" /><col width="100px" /><col width="50px" /><col width="50px" />
    </colgroup>
			<thead>
				<tr><th></th><th class="col1">イベントタイトル</th><th>開催日</th><th>ステータス</th></tr>
			</thead>
			<tbody>
			    <% for (Event event : enrolledEvents) { %>
			    <% if (event == null) { continue; /* TODO: should be logged. */} %>
			    <tr>
			        <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" /><% } %></td>
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
<div class="mypage-mine">
    <h2><img src="<%= request.getContextPath() %>/images/drop-green.png" alt="" />終了したイベント</h2>
<%
    List<Event> finishedEvents = (List<Event>)request.getAttribute(Constants.ATTR_FINISHED_EVENTSET);
    if (finishedEvents != null && !finishedEvents.isEmpty()) { %>
        <table class="table0">
    <colgroup>
      <col width="30px" /><col width="100px" /><col width="50px" /><col width="50px" />
    </colgroup>
            <thead>
                <tr><th></th><th class="col1">イベントタイトル</th><th>開催日</th><th>ステータス</th></tr>
            </thead>
            <tbody>
                <% for (Event event : finishedEvents) { %>
                <% if (event == null) { continue; /* TODO: should be logged. */} %>
                <tr>
                    <td><% if (event.isPrivate()) { %><img src="<%= request.getContextPath()%>/images/private.png" /><% } %></td>
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

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
	
</body>
</html>