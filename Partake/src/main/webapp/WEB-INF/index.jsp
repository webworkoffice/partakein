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

    List<Event> leftEvents = user != null ? action.getOwnedEvents() : action.getRecentEvents();
    List<Event> rightEvents = user != null ? action.getEnrolledEvents() : action.getRecentEvents();
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

<div class="row">
    <div class="span8">
        <% if (user == null) { %>
            <div class="hero-unit">
                <h1>Welcome to PARTAKE!</h1>
                <p>PARTAKE (パーテイク) は、イベントの作成・参加管理・参加者への連絡が簡単にできる、イベント開催支援サービスです。</p>
                <p>
                    <a href="/events/demo" class="btn btn-primary btn-large">デモを見る</a>
                    <a href="/events/search" class="btn btn-primary btn-large">イベントを探す</a>
                    <% if (user == null) { %>
                        <a href="/auth/loginByTwitter" class="btn btn-primary btn-large">ログイン</a>
                    <% } %>
                </p>
            </div>
        <% } else { %>
            <div class="row">
                <div class="span4">
                    <h3>参加中のイベント</h3>
                    <% for (Event event : action.getEnrolledEvents()) {
                            if (event == null)
                                continue;
                    %>
                    <div class="well thin">
                        <div class="event-image clearfix" style="width:260px; height:100px; position:relative; overflow: hidden;">
                            <% if (event.getForeImageId() != null) { %>
                                <a href="/events/<%= event.getId() %>"><img src="/images/thumbnail/<%= event.getForeImageId() %>" alt=""
                                style="position: absolute; top:0; left:0; right:0; bottom:0; margin:auto; width: 260px"
                                /></a>
                            <% } %>
                        </div>
                        <h3><a href="/events/<%= event.getId() %>"><%=h(event.getTitle())%></a></h3>
                        <p><%=h(event.getSummary())%></p>
                        <% if (event.getBeginDate() != null) { %>
                            <p><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%></p>
                        <% } %>
                    </div>
                    <% } %>
                </div>

                <div class="span4">
                    <h3>管理中のイベント</h3>
                    <% for (Event event : action.getOwnedEvents()) {
                            if (event == null)
                                continue;
                    %>
                    <div class="well thin">
                        <div class="event-image clearfix" style="width:260px; height:100px; position:relative; overflow: hidden;">
                            <% if (event.getForeImageId() != null) { %>
                                <a href="/events/<%= event.getId() %>"><img src="/images/thumbnail/<%= event.getForeImageId() %>" alt=""
                                style="position: absolute; top:0; left:0; right:0; bottom:0; margin:auto; width: 260px"
                                /></a>
                            <% } %>
                        </div>

                        <h3><a href="/events/<%= event.getId() %>"><%=h(event.getTitle())%></a></h3>
                        <p><%=h(event.getSummary())%></p>
                        <% if (event.getBeginDate() != null) { %>
                            <p><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%></p>
                        <% } %>
                    </div>
                    <% } %>
                </div>
            </div>
        <% } %>

        <div class="row">
            <div class="span4">
                <h3>新着イベント</h3>
                <% for (Event event : action.getRecentEvents()) {
                        if (event == null)
                            continue;
                %>
                <div class="well thin">
                    <div class="event-image clearfix" style="width:260px; height:100px; position:relative; overflow: hidden;">
                        <% if (event.getForeImageId() != null) { %>
                            <a href="/events/<%= event.getId() %>"><img src="/images/thumbnail/<%= event.getForeImageId() %>" alt=""
                            style="position: absolute; top:0; left:0; right:0; bottom:0; margin:auto; width: 260px"
                            /></a>
                        <% } %>
                    </div>

                    <h3><a href="/events/<%= event.getId() %>"><%=h(event.getTitle())%></a></h3>
                    <p><%=h(event.getSummary())%></p>
                    <% if (event.getBeginDate() != null) { %>
                        <p><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%></p>
                    <% } %>
                </div>
                <% } %>
            </div>

            <div class="span4">
                <h3>もうすぐ開催！</h3>
                <% for (Event event : action.getRecentEvents()) {
                        if (event == null)
                            continue;
                %>
                <div class="well thin">
                    <div class="event-image clearfix" style="width:260px; height:100px; position:relative; overflow: hidden;">
                        <% if (event.getForeImageId() != null) { %>
                            <a href="/events/<%= event.getId() %>"><img src="/images/thumbnail/<%= event.getForeImageId() %>" alt=""
                            style="position: absolute; top:0; left:0; right:0; bottom:0; margin:auto; width: 260px"
                            /></a>
                        <% } %>
                    </div>
                    <h3><a href="/events/<%= event.getId() %>"><%=h(event.getTitle())%></a></h3>
                    <p><%=h(event.getSummary())%></p>
                    <% if (event.getBeginDate() != null) { %>
                        <p><%=I18n.t("event.time")%>：<%=Helper.readableDate(event.getBeginDate())%></p>
                    <% } %>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <div class="span4">
        <h2>PARTAKE とは？</h2>

        <p>PARTAKE は、簡単にイベントを作成・集客・参加管理を行うことができるツールです。</p>
        <p>飲み会のメンバー募集から、セミナーや勉強会の開催、あるいは大規模イベントの開催まで、PARTAKE は強力にイベントの開催を支援します！</p>

        <h3>1. 告知ページが簡単に作れます</h3>
        <ul>
            <li>PARTAKE なら、必要事項をフォームに記入するだけ。イベントの告知ページが簡単に作れます。</li>
        </ul>

        <h3>2. Twitter と連携</h3>
        <ul>
            <li>PARTAKE ではアカウントの登録は必要ありません。twitter のアカウントでログインできます。</li>
        </ul>

        <h3>3. 参加者管理も簡単</h3>
        <ul>
            <li>参加者には自動的にリマインダーが送付されます。</li>
            <li>参加者にメッセージを送信できます。</li>
            <li>参加状況の把握、参加者リストの印刷などができます。</li>
        </ul>

        <h3>4. イベントの検索も簡単</h3>
        <ul>
            <li><a href="/events/search">検索ページからイベントを検索しよう！</a></li>
            <li>PARTAKE は <a href="/feed/">RSS や ics も配信</a>しています。</li>
            <li>Twitter 上では<a href="http://twitter.com/partake_bot">公式ボット</a>がイベントが登録されるたびにつぶやきます。今すぐフォローしてイベントをチェック！</li>
        </ul>

        <h3>5. PARTAKE は開発者の味方</h3>
        <ul>
            <li>PARTAKE なら、イベント登録、イベント検索、イベント参加など、豊富な API を用意！</li>
            <li>PARTAKE のソースは <a href="http://code.google.com/p/partakein/">Google Code 上で公開中</a>です。API が足りないって？　自分で足せますよ！</li>
        </ul>

        <h3>6. 他にも質問があります！</h3>
        <ul>
            <li><a href="http://code.google.com/p/partakein/wiki/FAQ">PARTAKE に関する FAQ はこちら。</a></li>
            <li>ご要望・バグ報告は <a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> まで。</li>
        </ul>
    </div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
