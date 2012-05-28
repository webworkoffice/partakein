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
    2. 補欠、キャンセル者はデフォルトでは表示せず、「補欠者、キャンセル者も表示する」をクリックすることで表示される
--%>

<% for (EventTicket ticket : tickets) {
    EventTicketHolderList list = ticketHoldersMap.get(ticket.getId()); %>
<div>
    <h3><%= h(ticket.getName()) %> (参加者)</h3>
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

<% if (!list.getSpareParticipations().isEmpty() || !list.getCancelledParticipations().isEmpty()) { %>
<p><a onclick="$('#list-<%= h(ticket.getId().toString()) %>').show()">補欠・キャンセル済の参加者を表示する</a></p>
<% } %>
<div id="list-<%= h(ticket.getId().toString()) %>" style="display: none">
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


