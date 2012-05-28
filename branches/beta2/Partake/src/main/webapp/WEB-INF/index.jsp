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
<div class="container">

<div class="row clearfix">
    <div class="span8">
        <h2>PARTAKE</h2>
        <p>PARTAKE (パーテイク) は、イベントの作成・参加管理・参加者への連絡が簡単にできる、イベント開催支援サービスです。</p>
        <p>飲み会のメンバー募集から、セミナーや勉強会の開催、あるいは大規模イベントの開催まで、PARTAKE は強力にイベントの開催を支援します。</p>
        <p><a data-toggle="modal" class="btn btn-large btn-info span6" href="#create-event-dialog" style="margin-bottom: 10px;">イベントを作る (無料)</a></p>
        <p><a href="/events/<%= h(Constants.DEMO_ID.toString()) %>" class="btn btn-large span6">デモを見る</a></p>
    </div>
    <div class="span16">
        <div id="toppage-carousel" class="carousel slide">
            <div class="carousel-inner">
                <div class="item" style="height: 300px;">
                    <img src="http://twitter.github.com/bootstrap/assets/img/bootstrap-mdo-sfmoma-02.jpg" alt="" style="height: 300px;">
                    <div class="carousel-caption">
                        <h4>まずは Twitter アカウントでログイン</h4>
                        <p>PARTAKE では登録は必要ありません。Twitter アカウントがあればログインすることができます。</p>
                    </div>
　　             </div>
                <div class="item active">
                    <img src="http://twitter.github.com/bootstrap/assets/img/bootstrap-mdo-sfmoma-01.jpg" alt="" style="height: 300px;">
                    <div class="carousel-caption">
                        <h4>イベントを探そう</h4>
                        <p><a href="/events/search">イベント検索ページ</a>から、登録されているイベントを検索することができます。</p>
                        <p>Twitter 上では<a href="http://twitter.com/partake_bot">公式ボット</a>がイベントが登録されるたびにつぶやきます。今すぐフォローしてイベントをチェック！</p>

                    </div>
                </div>
                <div class="item">
                    <img src="http://twitter.github.com/bootstrap/assets/img/bootstrap-mdo-sfmoma-03.jpg" alt="" style="height: 300px;">
                    <div class="carousel-caption">
                        <h4>イベントを作ろう</h4>
                        <p>PARTAKE では、必要な情報を入力するだけで簡単にイベントページを作ることができます。イベントの登録は無料です。</p>
                    </div>
                </div>
            </div>
            <a class="left carousel-control" href="#toppage-carousel" data-slide="prev">‹</a>
            <a class="right carousel-control" href="#toppage-carousel" data-slide="next">›</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/events/_search.jsp" flush="true">
    <jsp:param name="FORM_TYPE" value="simple" />
</jsp:include>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
