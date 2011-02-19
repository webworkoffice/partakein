<%@page import="java.util.List"%>
<%@page import="in.partake.controller.UsersPreferenceController"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx)request.getSession().getAttribute(Constants.ATTR_USER);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>ユーザー設定</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div id="setting-content">

<h1 id="pastel-line5ji" >
<img src="<%= request.getContextPath() %>/images/line-orange.png" alt="user setting" />ユーザー設定</h1>

<p>各種設定などが可能です。</p>

<div class="setting-subtitle">
 <h2><img src="<%= request.getContextPath() %>/images/openid.png" alt="" />OpenID でログインできるようにする</h2>
</div>

<div class="setting-set">
<h3><span class="bold"><span class="accent">＞ </span>Open ID の設定</span></h3>
<p>
何らかの理由でtwitter が使用できないとき、役立ちます。<br />設定には、Google や mixi などのOpenID と Twitterを結び付ける必要があります。下のリンクをクリックして twitter と OpenID を結びつけることができます！<br />
</p>
    <jsp:include page="/WEB-INF/internal/_openid_innerform.jsp" >
        <jsp:param name="callingURL" value="/auth/connectWithOpenID" />
        <jsp:param name="usesToken" value="true" />
    </jsp:include>
    
    <%-- TODO: 現在結び付けられている OpenID を表示する --%>
    <h3><span class="accent">＞ </span>現在、次の URL と結び付けられています。</h3>
    <p>
    <%
        UsersPreferenceController pref = (UsersPreferenceController) request.getSession().getAttribute(Constants.ATTR_ACTION);
        List<String> associatedOpenIds = pref.getAssociateOpenIds();
        if (associatedOpenIds != null && !associatedOpenIds.isEmpty()) {
            %> <ul> <%
            for (String openid : associatedOpenIds) { %>
                <li><%= openid %></li>
            <% } %>
            </ul>
    <% } %>
    </p>
</div>



<div class="setting-subtitle"> 
<h2><img src="<%= request.getContextPath() %>/images/setting.png" alt="">各種設定</h2>
</div>

<div class="setting-set">
<s:form method="post" action="setPreference">
	<s:token />
	<s:checkbox name="receivingTwitterMessage" />twitter 経由のリマインダーを受け取る (default:受け取る)<br />
	<s:checkbox name="profilePublic" />マイページを他人にも公開する (default：公開)<br />
	<s:checkbox name="tweetingAttendanceAutomatically" />イベントに参加するとき、自動的に参加をつぶやく (default：つぶやかない)<br />
	<s:submit value="この設定を保存する" />
</s:form>
</div>

<div class="setting-subtitle"> 
<h2><img src="<%= request.getContextPath() %>/images/calendar.png" alt="">カレンダーと RSS</h2></div>

<div class="setting-set">
<h3><span class="accent">＞ </span><span class="bold">自分の参加・管理イベントを ics ファイル (カレンダー) で受信、あるいは自分に関連するイベントに関するデータを RSS で取得することが出来ます。</span></h3>
<p>以下が、あなたのカレンダーID（URL）です。<br />これを、普段使っているカレンダーにインポートすればOK!</p>

<%-- NOTE: RSS の ID はカレンダー ID と共通です。 --%>
<% if (user.getCalendarId() != null && !"".equals(user.getCalendarId())) { %>
    <input type="text" value="http://partake.in<%= request.getContextPath() %>/calendars/<%= h(user.getCalendarId()) %>.ics" style="width: 80%;"/>
<%--
    <input type="text" value="http://partake.in<%= request.getContextPath() %>/feed/user/<%= h(user.getCalendarId()) %>" style="width: 80%;"/>
 --%>        
<% } %>

<p>IDは友人と共有することも可能です。</p>

<h3><span class="accent">＞ </span><span class="bold">カレンダーIDを再生成する</span></h3>
<p>不意にカレンダー ID を知られてしまった場合などに、カレンダー ID を再生成できます。<br />
ただし、これまでのカレンダー URL は無効になるので気をつけてください。
<s:form method="post" action="revokeCalendar">
	<s:token />
	<s:submit value="カレンダー ID を再生成する" />
</s:form>
</p>
</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>