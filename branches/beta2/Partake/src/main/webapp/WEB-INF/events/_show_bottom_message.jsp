<%@page import="in.partake.model.EventMessageEx"%>
<%@page import="in.partake.model.dto.EventMessage"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.base.Util"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>

<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.resource.Constants"%>

<%@page import="static in.partake.view.util.Helper.h"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    List<EventMessageEx> messages = action.getEventMessages();
%>

<div class="event-comments">
<% if (messages != null) { %>
    <% for (EventMessageEx message : messages) { %>
        <% if (message == null) continue; %>
        <div class="comment">
            <p><a href="<%= request.getContextPath() %>/users/<%= h(message.getSenderId()) %>"><%= h(message.getSender().getScreenName()) %></a>
            : <%= Helper.readableDate(message.getCreatedAt()) %></p>
            <p><%= h(message.getMessage().getBody()) %></p>
        </div>
    <% } %>
<% } %>
</div>

