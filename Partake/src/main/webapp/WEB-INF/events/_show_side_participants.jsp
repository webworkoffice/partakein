<%@page import="java.util.UUID"%>
<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
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
    List<EventTicket> tickets = action.getTickets();
    Map<UUID, EventTicketHolderList> ticketHoldersMap = action.getTicketHolderListMap();
%>

<%--
    1. 各チケットの参加者を表示する
--%>

<% for (EventTicket ticket : tickets) {
    EventTicketHolderList list = ticketHoldersMap.get(ticket.getId()); %>
<div>
    <h2><%= h(ticket.getName()) %></h2>
    <table class="table table-bordered">
        <colgroup><col class="span3"><col class="span4"></colgroup>
        <tr><th>参加者</th><td><%= list.getEnrolledParticipations().size() %> 人 (仮参加者 <%= list.getReservedEnrolled() %> 人)</td></tr>
        <tr><th>補欠</th><td><%= list.getSpareParticipations().size() %> 人 (仮参加者 <%= list.getReservedSpare() %> 人)</td></tr>
        <tr><th>キャンセル</th><td><%= list.getCancelledParticipations().size() %>人</td></tr>
    </table>

    <h3>参加者</h3>
    <% if (list.getEnrolledParticipations().isEmpty()) { %>
    <p>現在参加者はいません</p>
    <% } else { %>
    <ol>
        <% for (UserTicketEx userTicket : list.getEnrolledParticipations()) { %>
            <li>
                <img class="userphoto" src="<%=h(userTicket.getUser().getProfileImageURL())%>" alt="">
                <a href="/users/<%=h(userTicket.getUserId())%>">
                    <%= h(userTicket.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (userTicket.getStatus().equals(ParticipationStatus.RESERVED)) { %><img src="/images/reserved1.png" title="仮参加" alt="仮参加者" /><% } %>
                : <%= h(userTicket.getComment()) %>
            </li>
        <% } %>
    </ol>
    <% } %>
</div>

<div id="list-<%= h(ticket.getId().toString()) %>">
    <h3>補欠</h3>
    <% if (list.getSpareParticipations().isEmpty()) { %>
    <p>現在補欠者はいません</p>
    <% } else { %>
    <ol>
        <% for (UserTicketEx userTicket : list.getSpareParticipations()) { %>
            <li>
                <img class="userphoto" src="<%=h(userTicket.getUser().getProfileImageURL())%>" alt="">
                <a href="/users/<%=h(userTicket.getUserId())%>">
                    <%= h(userTicket.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (userTicket.getStatus().equals(ParticipationStatus.RESERVED)) { %><img src="/images/reserved1.png" title="仮参加" alt="仮参加者" /><% } %>
                : <%= h(userTicket.getComment()) %>
            </li>
        <% } %>
    </ol>
    <% } %>

    <h3>キャンセル</h3>
    <% if (list.getCancelledParticipations().isEmpty()) { %>
    <p>現在補欠者はいません</p>
    <% } else { %>
    <ol>
        <% for (UserTicketEx userTicket : list.getCancelledParticipations()) { %>
            <li>
                <img class="userphoto" src="<%=h(userTicket.getUser().getProfileImageURL())%>" alt="">
                <a href="/users/<%=h(userTicket.getUserId())%>">
                    <%= h(userTicket.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (userTicket.getStatus().equals(ParticipationStatus.RESERVED)) { %><img src="/images/reserved1.png" title="仮参加" alt="仮参加者" /><% } %>
                : <%= h(userTicket.getComment()) %>
            </li>
        <% } %>
    </ol>
    <% } %>
</div>
<% } %>


