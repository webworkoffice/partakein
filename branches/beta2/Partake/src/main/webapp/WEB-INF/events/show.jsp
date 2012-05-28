<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.EventCommentEx"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.model.dto.UserTicket"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.model.dto.EventComment"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.base.Util"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.resource.Constants"%>

<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="static in.partake.view.util.Helper.cleanupHTML"%>
<%@page import="static in.partake.view.util.Helper.escapeTwitterResponse"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.EventRelationEx"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);

    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null)
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL);

    EventEx event = action.getEvent();
    List<EventRelationEx> eventRelations = action.getRelations();
%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <% if (event.getFeedId() != null) { %>
        <link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="/feed/event/<%= event.getFeedId() %>" />
    <% } %>
    <% if (!StringUtils.isEmpty(event.getSummary())) { %>
        <meta name="description" content="<%= h(event.getSummary()) %>" />
    <% } else { %>
        <meta name="description" content="<%= h(Util.shorten(Util.removeTags(event.getDescription()), 128)) %>" />
    <% } %>
    <title><%= h(event.getTitle()) %> - [PARTAKE]</title>
<% if (event.getBackImageId() != null) { %>
    <style>
body {
    background-image: url("/images/<%= event.getBackImageId() %>");
    background-repeat: repeat;
}
    </style>
<% } %>

<script type="text/javascript">
  window.___gcfg = {lang: 'ja'};

  (function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/plusone.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(po, s);
  })();
</script>
</head>
<body
    <% if (user != null && EventEditPermission.check(event, user)) { %>
        class="with-sub-nav"
    <% } %>
>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<% if (user != null && EventEditPermission.check(event, user)) { %>
    <jsp:include page="/WEB-INF/events/_show_manage_navigation.jsp" flush="true" />
<% } %>

<div class="event-body">

<div class="page-header">
    <h1>
        <% if (event.isPrivate()) { %><img src="<%= request.getContextPath() %>/images/private.png" title="非公開イベント" /><% } %>
        <%= h(event.getTitle()) %>
        <% if (event.isDraft()) { %><span class="label label-important">このイベントはまだ公開されていません</span><% } %>
    </h1>
    <p>
        <% if (!StringUtils.isEmpty(event.getSummary())) { %>
            <%= h(event.getSummary()) %>
        <% } %>

    </p>
    <p>
        <span class="label label-info"><%= h(EventCategory.getReadableCategoryName(event.getCategory())) %></span>
    <p>
</div>

<div class="row">
    <!-- hatena -->
    <div class="pull-right">
        <div style="display:inline-block;">
            <a href="http://b.hatena.ne.jp/entry/" class="hatena-bookmark-button" data-hatena-bookmark-layout="standard" title="このエントリーをはてなブックマークに追加"><img src="http://b.st-hatena.com/images/entry-button/button-only.gif" alt="このエントリーをはてなブックマークに追加" width="20" height="20" style="border: none;" /></a>
            <script type="text/javascript" src="http://b.st-hatena.com/js/bookmark_button.js" charset="utf-8" async="async"></script>
        </div>

        <!-- facebook -->
        <iframe id="facebook-like-button" src="http://www.facebook.com/plugins/like.php?href=<%= h(Util.encodeURIComponent(event.getEventURL())) %>&amp;layout=button_count&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=21" scrolling="no" frameborder="0" allowTransparency="true" height="20" width="100"></iframe>

        <!--  twitter -->
        <div style="display:inline-block; width:105px" >
            <a href="http://twitter.com/share" class="twitter-share-button" data-count="horizontal" data-via="partakein" data-text="<%= h(event.getTitle())%> - [PARTAKE] <%= h(event.getHashTag()) %>" data-width="105px">Tweet</a>
            <script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>
        </div>

        <!-- +1 -->
        <div style="display:inline-block; width:70px;"><div class="g-plusone" data-size="medium" data-href="<%= h(event.getEventURL()) %>"></div></div>

        <!-- rss -->
        <% if (event.getFeedId() != null) { %>
            <div style="display:inline-block; vertical-align: top;"><a href="/feed/event/<%= event.getFeedId() %>"><img src="<%= request.getContextPath() %>/images/feed-icon-15x15.png" /></a></div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/events/_show_enroll.jsp" flush="true" />

<div class="row">
    <div class="span8 tabbable pull-right">
        <ul class="nav nav-tabs">
            <li class="active"><a href="#side-information" data-toggle="tab">イベント情報</a></li>
            <li><a href="#side-participants" data-toggle="tab">参加者情報</a></li>
        </ul>
        <div class="tab-content">
            <div class="tab-pane active" id="side-information">
                <jsp:include page="/WEB-INF/events/_show_side_information.jsp" flush="true" />
            </div>
            <div class="tab-pane" id="side-participants">
                <jsp:include page="/WEB-INF/events/_show_side_participants.jsp" flush="true" />
            </div>
        </div>
    </div>

    <div class="span16">
        <% if (event.getForeImageId() != null) { %>
        <div class="row">
            <div class="span16">
                <div class="event-image">
                    <img id="event-image-image" src="/images/<%= event.getForeImageId() %>" />
                </div>
            </div>
        </div>
        <% } %>

        <h3>イベント</h3>
        <div class="event-description" style="min-height: 200px;">
            <%= cleanupHTML(event.getDescription()) %>
        </div>

        <h3>コメント</h3>
        <div class="tabbable event-nav">
            <ul class="nav nav-tabs">
                <li class="active"><a href="#news1" title="コメントボード" data-toggle="tab">掲示板</a></li>
                <li><a href="#news2" title="管理者からのメッセージ" data-toggle="tab">管理者から</a></li>
                <% if (!StringUtils.isEmpty(event.getHashTag())) { %>
                <li><a href="#side-twitter" data-toggle="tab">Twitter</a></li>
                <% } %>
            </ul>
            <div class="tab-content">
                <div id="news1" class="tab-pane active">
                    <jsp:include page="/WEB-INF/events/_show_bottom_commentboard.jsp" flush="true" />
                </div>
                <div id="news2" class="tab-pane">
                    <jsp:include page="/WEB-INF/events/_show_bottom_message.jsp" flush="true" />
                </div>
                <div id="side-twitter" class="tab-pane">
                    <jsp:include page="/WEB-INF/events/_show_side_twitter.jsp" flush="true" />
                </div>
            </div>
        </div>
    </div><%-- end of .span16 --%>
</div>

<jsp:include page="/WEB-INF/events/_show_enroll.jsp" flush="true" />

</div><%-- end of event-body --%>

<jsp:include page="/WEB-INF/events/_show_forms.jsp" flush="true" />

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
