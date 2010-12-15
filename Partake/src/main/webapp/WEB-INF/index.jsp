<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.Helper"%>

<%@page import="java.util.List"%>

<%@page import="static in.partake.util.Util.h"%>

<%
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
	<a href="<%= request.getContextPath() %>/events/demo"><img src="<%= request.getContextPath() %>/images/sample1.png" alt="サンプルを見る" id=""></a>
</div>
</div>

<div class="top-explanations">
    <h2><img src="<%= request.getContextPath() %>/images/line-orange.png" alt=""/>PARTAKE!</h2>
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-01.png" alt="" />
		<h3>1. イベントの告知ページを作りましょう</h3>
		<ul class="top-feature">
			<li>イベントの告知ページを簡単につくれます。</li>
			<li>twitter での宣伝も簡単です。</li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-02.png" alt="" />
		<h3>2. 参加者の管理も簡単です</h3>
		<ul class="top-feature">
			<li>参加状況の把握も簡単です。</li>
			<li>参加者にメッセージを送信できます。</li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<img src="<%= request.getContextPath() %>/images/feature-03.png" alt="" />
		<h3>3. イベントを検索して自分も参加しましょう</h3>
		<ul class="top-feature">
			<li>楽しそうなイベントを検索して参加しましょう。</li>
			<li>新着イベントの RSS も配信しています。<a href="<%= request.getContextPath() %>/feedlist"><img src="<%= request.getContextPath() %>/images/rss-btn.png" alt="RSS" /></a></li>
		</ul>
	</div>
	
	<div class="top-explanation">
	   <h3>ご要望をお聞きしています</h3>
	   <ul class="top-feature">
	       <li>バグを発見したり、ご要望がある場合は <a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> までお寄せください。日本語で記述していただいて構いません。あるいは、<a href="http://twitter.com/partakein">@partakein</a> まで tweet をお願いします。</li>
	       <li>開発者を募集しています。PARTAKE のソースは <a href="http://code.google.com/p/partakein/">Google Code</a> で(極一部の画像を除いて)公開中です。</li>
	   </ul>
	</div>
</div>

<div class="top-recent-events">
    <h2><img class="top-pin" src="<%= request.getContextPath() %>/images/pin.png" alt="" />新着イベント</h2>
    <% if (recentEvents != null) { %>
        <% for (Event event : recentEvents) { %>
            <% if (event == null) { continue; } %>
            <% String classPrefix = "top-recent-event"; %>
            <div class="top-recent-event">
	            <h3><a href="<%= request.getContextPath() %>/events/<%= event.getId() %>"><%= h(event.getTitle()) %></a></h3>
				<div class="<%= h(classPrefix) %>-image">
				    <% if (event.getForeImageId() != null) { %>
				<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" alt="" /></a>
				    <% } else { %>
				<a href="<%= request.getContextPath() %>/events/<%= event.getId() %>">
				        <img src="<%= request.getContextPath() %>/images/no-image.png" alt="" /></a>
				    <% } %>
				</div>
				<div class="<%= h(classPrefix) %>-content">
				    <p><%= h(event.getSummary()) %></p>
				    <dl>
				        <dt>場所：</dt><dd><%= h(event.getPlace()) %></dd>
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