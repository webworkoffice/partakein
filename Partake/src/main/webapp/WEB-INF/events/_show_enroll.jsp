<%@page import="in.partake.model.dto.UserTicket"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Map"%>
<%@page import="in.partake.base.DateTime"%>
<%@page import="in.partake.base.TimeUtil"%>
<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);

    EventEx event = action.getEvent();
    List<EventTicket> tickets = action.getTickets();

    DateTime now = TimeUtil.getCurrentDateTime();
%>

<% if (tickets == null || tickets.isEmpty()) { %>
<div class="enroll-bar">
    <p>このイベントにはチケットが登録されていません。</p>
</div>
<% } else {
    for (EventTicket ticket : tickets) { %>
    <% UserTicket userTicket = action.getUserTicketMap().get(ticket.getId());
    ParticipationStatus status = ParticipationStatus.NOT_ENROLLED;
    if (userTicket != null)
        status = userTicket.getStatus(); %>
<div class="enroll-bar">
    <div class="row clearfix">
        <div class="span6">
            <p style="font-size: 20px; line-height: 40px;"><%= ticket.getName() %></p>
        </div>
        <div class="span10">
            <p>定員 <%= ticket.isAmountInfinite() ? "制限なし" : String.valueOf(ticket.getAmount()) %></p>
            <p>申込期間 <%= Helper.readableApplicationDuration(ticket, event) %></p>
        </div>

        <div class="span8" style="height: 50px;"><div class="row">
        <% if (!ticket.acceptsApplication(event, now)) { %>
            <a href="#" class="btn btn-flat span8 p2-height disabled">申込期間外です</a>
        <% } else if (user == null) { %>
            <a href="#" class="btn btn-flat span8 p2-height disabled">参加するためにはログインが必要です</a>
        <% } else if (ParticipationStatus.ENROLLED.equals(status) || ParticipationStatus.RESERVED.equals(status)) { %>
            <a href="#" class="btn button-apply-ticket span8 p2-height" data-ticket="<%= h(ticket.getId().toString()) %>">申込変更</a>
        <% } else { %>
            <a href="#" class="btn btn-danger-flat button-apply-ticket span8 p2-height" data-ticket="<%= h(ticket.getId().toString()) %>">参加申込</a>
        <% } %>
        </div></div>
    </div>
</div>
    <% } %>
<% } %>

<%-- NOTE: DO NOT APPEND script here. This jsp will be shown many times in one page. Write script in _show_forms.jsp --%>

