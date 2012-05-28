<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);

    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null)
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL);

    EventEx event = action.getEvent();
    List<EventTicket> tickets = action.getTickets();
    List<EventRelationEx> eventRelations = action.getRelations();

    // TODO: check these.
    boolean deadlineOver = true;
    ParticipationStatus status = ParticipationStatus.ENROLLED;
%>

<% if (tickets == null || tickets.isEmpty()) { %>
<div class="enroll-bar">
    <p>このイベントにはチケットが登録されていません。</p>
</div>
<% } else {
    for (EventTicket ticket : tickets) { %>
<div class="enroll-bar">
    <div class="row clearfix">
        <div class="span6">
            <p style="font-size: 20px; line-height: 40px;"><%= ticket.getName() %></p>
        </div>
        <div class="span10">
            <p>定員 <%= ticket.isAmountInfinite() ? "制限なし" : String.valueOf(ticket.getAmount()) %></p>
            <p>申込期間 <%= Helper.readableDuration(ticket.acceptsFrom(event), ticket.acceptsTill(event)) %></p>
        </div>

        <div class="row span8" style="height: 50px;">
        <% if (deadlineOver) { %>
            <a href="#" class="btn btn-flat span4-width p2-height disabled">申込期間外です</a>
        <% } else if (user == null) { %>
            <a href="#" class="btn btn-flat span4-width p2-height disabled">参加するためにはログインが必要です</a>
        <% } else if (ParticipationStatus.ENROLLED.equals(status)) { %>
            <a href="#" class="btn btn-danger-flat span4-width p2-height" data-toggle="modal" data-target="#event-enroll-dialog">申込変更</a>
        <% } else if (ParticipationStatus.RESERVED.equals(status)) { %>
            <a href="#" class="btn btn-danger-flat span4-width p2-height" data-toggle="modal" data-target="#event-enroll-dialog">申込変更</a>
        <% } else if (false) { %>
            前提条件となるイベントに参加する必要があります。
        <% } else { %>
            <a href="#" class="btn btn-danger-flat span4-width p2-height" data-toggle="modal" data-target="#event-enroll-dialog">参加申込</a>
        <% } %>
        </div>
    </div>
</div>
    <% } %>
<% } %>
