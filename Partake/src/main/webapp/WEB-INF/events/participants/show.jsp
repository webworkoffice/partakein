<%@page import="in.partake.controller.action.event.ShowParticipantsAction"%>
<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.auxiliary.AttendanceStatus"%>
<%@page import="in.partake.model.dto.auxiliary.EventRelation"%>
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
    // EventTicketHolderList participationList = action.getParticipationList();

    List<UserTicketEx> enrolledParticipations = new ArrayList<UserTicketEx>(); // participationList.getEnrolledParticipations();
    List<UserTicketEx> spareParticipations = new ArrayList<UserTicketEx>(); // participationList.getSpareParticipations();
    List<UserTicketEx> cancelledParticipations = new ArrayList<UserTicketEx>(); // participationList.getCancelledParticipations();

    List<UserTicketEx> ps = new ArrayList<UserTicketEx>();
    ps.addAll(enrolledParticipations);
    ps.addAll(spareParticipations);
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <script type="text/javascript">
    function removeAttendant(userId, eventId) {
        if (!window.confirm('参加者を削除しようとしています。この操作は取り消せません。削除しますか？'))
            return;

        partake.event.removeAttendant(userId, eventId)
        .done(function (json) {
            location.reload();
        })
        .fail(partake.defaultFailHandler);
    }

    function makeAttendantVIP(userId, eventId, vip) {
        partake.event.makeAttendantVIP(userId, eventId, vip)
        .done(function (json) {
            location.reload();
        })
        .fail(partake.defaultFailHandler);
    }

    function changeAttendance(userId, eventId, status) {
        partake.event.changeAttendance(userId, eventId, status)
        .done(function(json) {
            $("#attendance-status-" + userId).html("保存しました");
        })
        .fail(function(xhr) {
            $("#attendance-status-" + userId).html("保存時にエラーが発生しました");
        })
    }

    </script>
    <title>参加者のステータスを編集</title>
</head>

<body id="status-edit">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
    <h1>参加者のステータスを編集</h1>
    <p>特定の参加者を優遇したり、参加を拒否したりすることができます。</p>
</div>

<ul>
    <li><a href="/events/<%=h(event.getId())%>">イベントに戻る</a></li>
</ul>

<div>
    <h3>優先度マーク</h3>
    <ul>
        <li><img class="adjust1" src="<%=request.getContextPath()%>/images/star.png" alt="優先参加" /> ：優先参加マーク。関連イベントに参加することにより、優先参加権を得た人です。</li>
        <li><img src="<%=request.getContextPath()%>/images/crown.png" alt="VIP" /> ：VIPマーク。あなたがVIPに指名した人は、誰よりも優先的に参加させることができます。</li>
    </ul>
</div>

<h3><%=h(event.getTitle())%> - 参加者リスト</h3>

<table class="table table-striped">
    <colgroup>
        <col width="32px" /><col width="85px" /><col width="58px" /><col width="150px" /><col width="100px" /><col width="50px" /><col width="120px" /><col width="120px" />
    </colgroup>
    <thead>
        <tr><th>順番</th><th>名前</th><th>予約状況</th><th>コメント</th><th>登録日時</th><th>優先度</th><th>操作</th><th>実際の出欠状況</th></tr>
    </thead>
    <tbody>

    <%
        int order = 0;
            for (UserTicketEx p : ps) {
    %>
    <tr id="attendant-<%= h(p.getUserId()) %>">
        <td><%= ++order %></td>
        <td><%=h(p.getUser().getTwitterScreenName())%></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td class="print-del"><%= h(p.getModifiedAt().toString()) %></td>
        <td>
            <% if (p.isVIP()) { %>
                <img src="<%= request.getContextPath() %>/images/crown.png" alt="VIPマーク" />
            <% } else if (p.getPriority() > 0) {
                for (int i = 0; i < p.getPriority(); ++i) { %>
                    <img src="<%= request.getContextPath() %>/images/star.png" alt="優先マーク" />
                <% } %>
            <% } else { %>
                -
            <% } %>
        </td>
        <td class="print-del">
            <ul class="status-control">
                <li><a href="#" onclick="removeAttendant('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>')">削除する</a></li>
                <% if (p.isVIP()) { %>
                    <li id="vip-<%= h(p.getUserId()) %>"><a href="#" onclick="makeAttendantVIP('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>', 'false')">VIP 指定解除</a></li>
                <% } else { %>
                    <li id="nonvip-<%= h(p.getUserId()) %>"><a href="#" onclick="makeAttendantVIP('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>', 'true')">VIP 指定</a></li>
                <% } %>
            </ul>
            <script>

            </script>
        </td>
        <td class="print-del">
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>', 'unknown')" name="present-<%= h(p.getUserId()) %>" value="unknown" <%= AttendanceStatus.UNKNOWN.equals(p.getAttendanceStatus()) ? "checked" : "" %> /> 未選択<br />
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>', 'present')" name="present-<%= h(p.getUserId()) %>" value="present" <%= AttendanceStatus.PRESENT.equals(p.getAttendanceStatus()) ? "checked" : "" %> /> 出席
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getEventId()) %>', 'absent')" name="present-<%= h(p.getUserId()) %>" value="absent"   <%= AttendanceStatus.ABSENT.equals(p.getAttendanceStatus())  ? "checked" : "" %> /> 欠席<br />
            <span id="attendance-status-<%= h(p.getUserId()) %>"></span>
        </td>
    </tr>
    <% } %>
</tbody>
</table>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
