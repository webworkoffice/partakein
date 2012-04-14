<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.DirectMessageEx"%>
<%@page import="in.partake.model.CommentEx"%>
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
    List<CommentEx> comments = action.getComments();
    List<DirectMessageEx> messages = action.getMessages();
    boolean deadlineOver = action.isDeadlineOver();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
    ParticipationStatus status = action.getParticipationStatus();
    EventReminder reminderStatus = action.getEventReminder();
    int maxCodePointsOfMessage = action.getRestCodePoints();
%>

<%
    ParticipationList participationList = action.getParticipationList();
    List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
    List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
    List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
%>


<p>定員 <%= event.getCapacity() != 0 ? String.valueOf(event.getCapacity()) : "-" %></p>
<div class="well">
    <h3>参加者数</h3>
    <ul>
        <li>参加: <%= enrolledParticipations.size() %> 人 (仮 <%= participationList.getReservedEnrolled() %> 人)
　／　補欠: <%= spareParticipations.size() %> 人 (仮 <%= participationList.getReservedSpare() %> 人)</li>
    </ul>
</div>

<h3><img src="<%= request.getContextPath() %>/images/circle.png" />参加者一覧 (<%= enrolledParticipations.size() %> 人)</h3>
<% if (enrolledParticipations != null && enrolledParticipations.size() > 0) { %>
    <ol>
    <% for (EnrollmentEx participation : enrolledParticipations) { %>
        <%-- TODO: 仮参加は色をかえるべき --%>
        <% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
                    <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
                <% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
                : <%= h(participation.getComment()) %>
            </li>
        <% } else { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
                <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <img src="<%= request.getContextPath() %>/images/reserved1.png" title="仮参加" alt="仮参加者" />
                <% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
                <% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
                : <%= h(participation.getComment()) %>
            </li>
        <% } %>
    <% 	} %>
    </ol>
<% } else { %>
    <p>現在参加者はいません。</p>
<% } %>

<% if (spareParticipations != null && spareParticipations.size() > 0) { %>
    <h3><img src="<%= request.getContextPath() %>/images/square.png" />補欠者一覧 (<%= spareParticipations.size() %> 人)</h3>
    <ul>
    <% for (EnrollmentEx participation : spareParticipations) { %>
        <% 		// TODO: 仮参加は色をかえるべき		 %>
        <% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
                <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
                <% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
                : <%= h(participation.getComment()) %>
            </li>
        <% } else { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
                <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
                </a>
                <% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
                <% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
                <img src="<%= request.getContextPath() %>/images/reserved1.png" title="仮参加" alt="仮参加者" />
                : <%= h(participation.getComment()) %>
            </li>
        <% } %>
    <% 	} %>
    </ul>
<% } %>

<% if (cancelledParticipations != null && cancelledParticipations.size() > 0) { %>
    <h3><img src="<%= request.getContextPath() %>/images/cross.png" />キャンセル一覧 (<%= cancelledParticipations.size() %> 人)</h3>
    <ul>
    <% for (EnrollmentEx participation : cancelledParticipations) { %>
        <% if (ParticipationStatus.RESERVED.equals(participation.getStatus())) { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>"><%= h(participation.getUser().getTwitterLinkage().getScreenName()) %></a> (仮参加後の参加表明なし) : <%= h(participation.getComment()) %>
            </li>
        <% } else { %>
            <li>
                <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>"><%= h(participation.getUser().getTwitterLinkage().getScreenName()) %></a> : <%= h(participation.getComment()) %>
            </li>
        <% } %>
    <% 	} %>
    </ul>
<% } %>

