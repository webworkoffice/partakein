<%@page import="in.partake.base.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.EventCommentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
%>

<% if (!StringUtils.isEmpty(event.getHashTag())) { %>
<script src="http://widgets.twimg.com/j/2/widget.js"></script>
<div class="event-twitter-hashtag-stream">
<script>
new TWTR.Widget({
    version: 2,
    type: 'search',
    search: '<%= h(event.getHashTag()) %>',
    interval: 6000,
    title: 'Twitter Hashtag Live Feed',
    subject: '<%= h(event.getHashTag()) %>',
    width: 'auto',
    height: 400,
    theme: {
        shell: {
        background: 'none',
        color: '#7c7c7c'
        },
        tweets: {
        background: '#ffffff',
        color: '#444444',
        links: '#1985b5'
        }
    },
    features: {
        scrollbar: true,
        loop: false,
        live: true,
        hashtags: true,
        timestamp: true,
        avatars: true,
        toptweets: true,
        behavior: 'all'
    }
    }).render().start();
</script>
</div>
<% } else { %>
<p>ツイートを表示するにはハッシュタグを設定してください。</p>
<% } %>



