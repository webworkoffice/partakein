<%@page import="in.partake.controller.action.event.PrintParticipantsAction"%>
<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="in.partake.model.dto.auxiliary.EventRelation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.UserTicketApplicationEx"%>
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
    PrintParticipantsAction action = (PrintParticipantsAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    // EventTicketHolderList participationList = action.getParticipationList();

    List<UserTicketApplicationEx> enrolledParticipations = new ArrayList<UserTicketApplicationEx>(); // participationList.getEnrolledParticipations();
    List<UserTicketApplicationEx> spareParticipations = new ArrayList<UserTicketApplicationEx>(); // participationList.getSpareParticipations();
    List<UserTicketApplicationEx> cancelledParticipations = new ArrayList<UserTicketApplicationEx>(); // participationList.getCancelledParticipations();

    List<UserTicketApplicationEx> ps = new ArrayList<UserTicketApplicationEx>();
    ps.addAll(enrolledParticipations);
    ps.addAll(spareParticipations);
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title><%=h(event.getTitle())%> - 参加者リスト - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
    <h1>参加者リスト</h1>
</div>

<ul>
    <li><a href="/events/<%=h(event.getId())%>">イベントに戻る</a></li>
    <li><a href="#" onclick="window.print()">印刷する</a></li>
    <li><a href="/events/participants/<%=event.getId()%>.csv">CSVで出力する(UTF-8)</a></li>
</ul>

<h3><%=h(event.getTitle())%> - 参加者リスト</h3>

<table class="table table-striped">
    <colgroup>
        <col width="32px" /><col width="85px" /><col width="58px" /><col width="150px" /><col width="30px" />
        <%
            for (EventRelationEx eventRelation : event.getEventRelations()) {
        %>
            <%
                if (eventRelation == null) { continue; }
            %>
            <col width="60px" />
        <%
            }
        %>
        <col width="60px" />
    </colgroup>
<thead>
    <tr>
        <th>順番</th><th>名前</th><th>予約状況</th><th>コメント</th><th>優先度</th>
        <%
            {
                int cnt = 0;
                for (EventRelationEx eventRelation : event.getEventRelations()) {
        %>
                    <%
                        if (eventRelation == null) { continue; }
                    %>
                    <th>関連イベント <%=++cnt%> <a href="<%=h(eventRelation.getEvent().getEventURL())%>">*</a></th>
                <%
                    }
                        }
                %>
    </tr>
</thead>
<tbody>
    <%
        int order = 0;
        for (UserTicketApplicationEx p : ps) {
    %>
    <tr>
        <td><%= ++order %></td>
        <td><%= h(p.getUser().getScreenName()) %></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td><%= p.getPriority() > 0 ? String.format(" 優先 %d", p.getPriority()) : "-" %></td>
        <%
            for (EventRelation eventRelation : event.getEventRelations()) { %>
            <% if (eventRelation == null) { continue; } %>
            <% if (p.getRelatedEventIds().contains(eventRelation.getEventId())) { %>
                <td>出席</td>
            <% } else { %>
                <td>欠席</td>
            <% } %>
        <% } %>
    </tr>
    <% } %>
</tbody>
</table>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
