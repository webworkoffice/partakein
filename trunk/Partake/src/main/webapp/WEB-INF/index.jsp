<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.Helper"%>

<%@page import="java.util.List"%>

<%@page import="static in.partake.util.Util.h"%>

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

    <p class="top-introduce">
	<span class="big">P</span>ARTAKE はイベントの告知ページを作成したり、<br />
	イベントを検索して参加することができるシンプルなツールです。
	</p>
<div class="rollover1">	
	<a href="<%= request.getContextPath() %>/events/search"><img src="<%= request.getContextPath() %>/images/top-search.png" alt="イベントを検索する" id="top-search-button" /></a>
</div>
<div class="rollover2">
	<a href="<%= request.getContextPath() %>/events/new"><img src="<%= request.getContextPath() %>/images/top-form.png" alt="イベントを作成する" id="top-form-button" /></a>
</div>
<div class="sample">
	<a href="<%= request.getContextPath() %>/events/demo"><img src="<%= request.getContextPath() %>/images/sample1.gif" alt="サンプルを見る" id=""></a>
</div>
</div>

<div class="top-explanations">
    <h2><img src="<%= request.getContextPath() %>/images/line-orange.png" alt=""/>PARTAKE!</h2>
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-01.png" alt="" />
		<h3>1. イベントの告知ページを作りましょう</h3>
		<ul class="top-feature">
			<li>イベントの告知ページを簡単につくれます。<a href="<%= request.getContextPath() %>/events/demo">[サンプル]</a></li>
			<li>Twitterでの宣伝も簡単です。</li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-02.png" alt="" />
		<h3>2. 参加者の管理が簡単です</h3>
		<ul class="top-feature">
			<li>参加状況の把握、参加者リストの印刷などができます。</li>
			<li>参加者にメッセージを送信できます。</li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-03.png" alt="" />
		<h3>3. イベントを検索して自分も参加しましょう</h3>
		<ul class="top-feature">
			<li>楽しそうなイベントを検索して参加しましょう。</li>
			<li><a href="<%= request.getContextPath() %>/feedlist">新着イベントの RSS / iCal を配信中。</a></li>
			<li><a href="http://twitter.com/partake_bot">新着イベントをつぶやく公式ボット</a>もいます。
			</li>
		</ul>
	</div>
	
	<div class="top-explanation">
	   <img src="<%= request.getContextPath() %>/images/feature-04.png" alt="" />
	   <h3>ご要望をお聞きしていますよ☆</h3>
	   <ul class="top-feature">
	       <li>ご要望・バグ報告は <a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> まで。あるいは、<br><a href="http://twitter.com/partakein">@partakein</a> まで tweet をお願いします。</li>
	       <li>開発者を募集中。PARTAKE のソースは <a href="http://code.google.com/p/partakein/">Google Code</a> <br>で(一部の画像を除き)公開中です。</li>
	   </ul>
	</div>
</div>

<%-- ログインしていれば、直近のイベントを表示する --%>
<% if (user != null) { %>
	<div class="top-user-events rad">
		<h2>直近の登録イベント</h2>
		<% List<Event> enrolled = (List<Event>) request.getAttribute(Constants.ATTR_ENROLLED_EVENTSET); %>		
		<% if (enrolled != null && !enrolled.isEmpty()) { %>
		  <ul>
			<% for (int i = 0; i < 3 && i < enrolled.size(); ++i) { %>
				<% Event event = enrolled.get(i); %>
				<li><a href="<%= h(event.getEventURL()) %>"><%= h(event.getTitle()) %></a></li>
			<% } %>
			</ul>            
			<p class="more"><a href="<%= request.getContextPath() %>/mypage">more...</a></p>
		<% } else { %>
			<p>直近の登録イベントはありません。</p>
		<% } %>
		<h2>直近の管理イベント</h2>
		<% List<Event> owned = (List<Event>) request.getAttribute(Constants.ATTR_OWNED_EVENTSET); %>		
		<% if (owned != null && !owned.isEmpty()) { %>
		    <ul>
			<% for (int i = 0; i < 3 && i < owned.size(); ++i) { %>
				<% Event event = owned.get(i); %>
				<li><a href="<%= h(event.getEventURL()) %>"><%= h(event.getTitle()) %></a></li>
			<% } %>
			</ul>            
			<p class="more"><a href="<%= request.getContextPath() %>/mypage">more...</a></p>
		<% } else { %>
			<p>直近の管理イベントはありません。</p>
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
				        <img class="rad sdw" src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
				    <% } else { %>
				<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img class="rad sdw" src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
				    <% } %>
				</div>
				<div class="<%= h(classPrefix) %>-content">
				    <p><%= h(event.getSummary()) %></p>
				    <dl>
				        <dt>会場：</dt><dd><%= h(event.getPlace()) %></dd>
				        <dt>日時：</dt><dd><%= Helper.readableDate(event.getBeginDate()) %></dd>
				    </dl>
				</div>
            </div>
        <% } %>
    <% } %>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>