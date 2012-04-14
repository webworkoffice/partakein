<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.EventReminder"%>
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
    ParticipationStatus status = action.getParticipationStatus();
    boolean deadlineOver = action.isDeadlineOver();
    EventReminder reminderStatus = action.getEventReminder();
    List<EventRelationEx> eventRelations = action.getRelations();
%>

<div style="padding-top: 10px;">
    <div class="row clearfix">
        <div class="span5">
            <p>開催期間 <%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %></p>
            <p>申込期間 ほげほげ</p>
        </div>
        <div class="span3">
            <p>定員 ほげほげ</p>
            <p>会場 ほげほげ</p>
        </div>

        <div class="row span4" style="height: 50px;">
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
            <a href="#" class="btn btn-danger-flat span4-width p2-height" data-toggle="modal" data-target="#event-enroll-dialog">参加申し込み</a>
        <% } %>
        </div>
    </div>
</div>
