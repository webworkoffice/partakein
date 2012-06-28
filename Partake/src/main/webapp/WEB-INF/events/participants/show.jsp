<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="in.partake.base.Pair"%>
<%@page import="in.partake.controller.action.event.ShowParticipantsAction"%>
<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.auxiliary.AttendanceStatus"%>
<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.model.dto.Message"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%
    ShowParticipantsAction action = (ShowParticipantsAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders = action.getTicketAndHolders();
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>参加者一覧</title>
</head>

<body class="with-sub-nav">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<div class="subnav subnav-fixed"><div class="container">
    <ul class="nav nav-pills nav-stacked-if-phone">
        <li><a href="#" onclick="window.print()">印刷する</a></li>
        <li><a href="/events/participants/csv/<%=event.getId()%>.csv">CSVで出力する(UTF-8)</a></li>
        <li class="pull-right"><a href="/events/<%=h(event.getId())%>">イベントに戻る</a></li>
    </ul>
</div></div>

<div class="content-body">

<div class="page-header">
    <h1>参加者一覧</h1>
</div>

<h3><%=h(event.getTitle())%> - 参加者リスト</h3>

<div class="tabbable">
    <ul class="nav nav-pills nav-stacked-if-phone">
        <% for (int i = 0; i < ticketAndHolders.size(); ++i) { %>
            <% Pair<EventTicket, EventTicketHolderList> ticketAndHolder = ticketAndHolders.get(i); %>
            <li <%= i == 0 ? "class='active'" : "" %>><a href="#ticket<%= i %>" data-toggle="tab"><%= ticketAndHolder.getFirst().getName() %></a></li>
        <% } %>
    </ul>
    <div class="tab-content">
        <% for (int i = 0; i < ticketAndHolders.size(); ++i) { %>
            <% Pair<EventTicket, EventTicketHolderList> ticketAndHolder = ticketAndHolders.get(i); %>
            <div id="ticket<%= i %>" class="tab-pane <%= i == 0 ? "active" : "" %>">
                <jsp:include page="/WEB-INF/events/participants/_show_participants_table.jsp" flush="true">
                    <jsp:param name="index" value="<%= i %>" />
                </jsp:include>
            </div>
        <% } %>
    </div>
</div>

<script type="text/javascript">
function removeAttendant(userId, ticketId) {
    if (!window.confirm('参加者を削除しようとしています。この操作は取り消せません。削除しますか？'))
        return;

    partake.ticket.removeAttendant(userId, ticketId)
    .done(function (json) {
        location.reload();
    })
    .fail(partake.defaultFailHandler);
}

function changeAttendance(userId, ticketId, status) {
    var id = ticketId + '-' + userId;
    partake.ticket.changeAttendance(userId, ticketId, status)
    .done(function(json) {
        $("#attendance-status-" + id).html("保存しました");
    })
    .fail(function(xhr) {
        $("#attendance-status-" + id).html("保存時にエラーが発生しました");
    })
}
</script>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
