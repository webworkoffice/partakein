<%@page import="java.util.ArrayList"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Map"%>
<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
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
<%@page import="static in.partake.view.util.Helper.escapeTwitterResponse"%>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    List<EventCommentEx> comments = action.getComments();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
    List<EventRelationEx> eventRelations = action.getRelations();
%>

<h3>主催者</h3>
<p><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
    <img src="<%= h(event.getOwner().getTwitterLinkage().getProfileImageURL()) %>" class="profile-image" alt="" width="20" height="20" />
    <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
        <%= escapeTwitterResponse(event.getOwner().getTwitterLinkage().getName()) %>
        (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
    <% } else { %>
        <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
    <% } %>
</a></p>

<h3>開催日時</h3>
<p>開催日時: <%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %></p>
<p>申込期間: ほげほげ</p>

<h3>開催場所</h3>
<p>会場: <%= StringUtils.isBlank(event.getPlace()) ? "未定" : h(event.getPlace()) %></p>
<% if (!StringUtils.isBlank(event.getAddress())) { %>
<p>住所: <%= h(event.getAddress()) %></p>
<div class="event-map"><a href="http://maps.google.co.jp/maps?q=<%= h(Util.encodeURIComponent(event.getAddress())) %>">
    <img src="http://maps.google.co.jp/maps/api/staticmap?size=280x200&center=<%= h(Util.encodeURIComponent(event.getAddress())) %>&zoom=17&sensor=false" />
</a></div>
<% } %>

<% if (eventRelations != null && !eventRelations.isEmpty()) { %>
    <h3>関連イベント</h3>
    <% for (EventRelationEx eventRelation : eventRelations) { %>
        <img src="/images/mark.png" class="" alt="" />
        <a href="<%= h(eventRelation.getEvent().getEventURL()) %>"><%= h(eventRelation.getEvent().getTitle()) %></a>
        <p><% if (eventRelation.isRequired()) { %><img src="<%= request.getContextPath() %>/images/attention.png" alt="" /> この関連イベントへの参加が必須です<% } %>
            <% if (eventRelation.hasPriority()) { %><img src="<%= request.getContextPath() %>/images/star.png" alt="" /> 参加すると本イベントへ優先的に参加可能<% } %>
            </p>
    <% } %>
<% } %>
