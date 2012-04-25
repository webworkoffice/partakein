<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.CommentEx"%>
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
    List<CommentEx> comments = action.getComments();
    boolean deadlineOver = action.isDeadlineOver();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
    ParticipationStatus status = action.getParticipationStatus();
    EventReminder reminderStatus = action.getEventReminder();
    List<EventRelationEx> eventRelations = action.getRelations();
%>

<%
	EventTicketHolderList participationList = action.getParticipationList();
    List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
    List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
    List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
%>

<h3>開催日時</h3>
<p>開催日時: <%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %></p>
<p>申込期間: <%= Helper.readableDate(event.getDeadline() == null ? event.getBeginDate() : event.getDeadline()) %></p>


<h3>開催場所</h3>
<p>会場: <%= StringUtils.isBlank(event.getPlace()) ? "未定" : h(event.getPlace()) %></p>
<% if (!StringUtils.isEmpty(event.getAddress())) { %>
<div class="event-map"><a href="http://maps.google.co.jp/maps?q=<%= h(Util.encodeURIComponent(event.getAddress())) %>">
    <img src="http://maps.google.co.jp/maps/api/staticmap?size=280x200&center=<%= h(Util.encodeURIComponent(event.getAddress())) %>&zoom=17&sensor=false" />
</a></div>
<p>住所: <%= h(event.getAddress()) %></p>
<% } %>

<h3>主催者</h3>
<p><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
    <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
        <%= escapeTwitterResponse(event.getOwner().getTwitterLinkage().getName()) %>
        (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
    <% } else { %>
        <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
    <% } %>
</a></p>

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

<h3>その他</h3>
<p>カテゴリ: <%= event.getCategory() != null ? EventCategory.getReadableCategoryName(event.getCategory()) : "-" %></p>
