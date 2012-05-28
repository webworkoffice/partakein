<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Collections"%>
<%@page import="in.partake.model.dto.auxiliary.EnqueteQuestion"%>
<%@page import="java.util.Map"%>
<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.model.dto.auxiliary.AttendanceStatus"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="in.partake.base.Pair"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.event.ShowParticipantsAction"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="static in.partake.view.util.Helper.h"%>

<%
    ShowParticipantsAction action = (ShowParticipantsAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders = action.getTicketAndHolders();
    int index = Integer.parseInt(request.getParameter("index"));

    EventTicket ticket = action.getTicketAndHolders().get(index).getFirst();
    EventTicketHolderList holders = action.getTicketAndHolders().get(index).getSecond();
    Map<String, String[]> userTicketInfoMap = action.getUserTicketInfoMap();

    List<UserTicketEx> ps = new ArrayList<UserTicketEx>();
    ps.addAll(holders.getEnrolledParticipations());
    ps.addAll(holders.getSpareParticipations());


%>

<table class="table table-striped">
    <colgroup>
        <col width="30px"><col width="60px"><col width="100px"><col width="80px"><col width="300px"><col width="150px"><col width="150px">
        <%-- Another ticket information if any --%>
        <% for (int i = 0; i < action.getTicketAndHolders().size(); ++i) { if (i == index) continue; %>
            <col width="200px">
        <% } %>
        <%-- Enquete information if nay --%>
        <% if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) { %>
            <% for (EnqueteQuestion question : event.getEnquetes()) { %>
                <col width="200px">
            <% } %>
        <% } %>
    </colgroup>
    <thead>
        <tr>
            <th rowspan="2">&nbsp;</th>
            <th rowspan="2">順番</th><th rowspan="2">名前</th><th rowspan="2">予約状況</th><th rowspan="2">コメント</th><th rowspan="2">登録日時</th><th rowspan="2">実際の出欠状況</th>
            <%-- Another ticket information if any --%>
            <% if (action.getTicketAndHolders().size() > 1) { %>
                <th colspan="<%= action.getTicketAndHolders().size() - 1%>">他のチケットの状況</th>
            <% } %>
            <%-- Enquete information if nay --%>
            <% if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) { %>
                <th colspan="<%= event.getEnquetes().size() - 1 %>">アンケートの回答</th>
            <% } %>
        </tr>
        <tr>
            <%-- Another ticket information if any --%>
            <% for (int i = 0; i < action.getTicketAndHolders().size(); ++i) { if (i == index) continue; %>
                <th><%= action.getTicketAndHolders().get(i).getFirst().getName() %></th>
            <% } %>
            <%-- Enquete information if nay --%>
            <% if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) { %>
                <% for (EnqueteQuestion question : event.getEnquetes()) { %>
                    <th><%= question.getText() %></th>
                <% } %>
            <% } %>
        </tr>
    </thead>
    <tbody>

    <%
        int order = 0;
            for (UserTicketEx p : ps) {
    %>
    <tr id="attendant-<%= h(p.getUserId()) %>">
        <td>[<a onclick="removeAttendant('<%= h(p.getUserId()) %>', '<%= h(p.getTicketId().toString()) %>')">&times;</a>]</td>
        <td><%= ++order %></td>
        <td><%=h(p.getUser().getTwitterScreenName())%></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td class="print-del"><%= h(p.getAppliedAt().toHumanReadableFormat()) %></td>
        <td class="print-del"><form>
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getTicketId().toString()) %>', 'unknown')" name="present-<%= h(p.getUserId()) %>" value="unknown" <%= AttendanceStatus.UNKNOWN.equals(p.getAttendanceStatus()) ? "checked" : "" %> /> 未選択<br />
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getTicketId().toString()) %>', 'present')" name="present-<%= h(p.getUserId()) %>" value="present" <%= AttendanceStatus.PRESENT.equals(p.getAttendanceStatus()) ? "checked" : "" %> /> 出席
            <input type="radio" onchange="changeAttendance('<%= h(p.getUserId()) %>', '<%= h(p.getTicketId().toString()) %>', 'absent')" name="present-<%= h(p.getUserId()) %>" value="absent"   <%= AttendanceStatus.ABSENT.equals(p.getAttendanceStatus())  ? "checked" : "" %> /> 欠席<br />
            <span id="attendance-status-<%= h(p.getTicketId().toString()) %>-<%= h(p.getUserId()) %>"></span>
        </form></td>
        <%-- Another ticket information if any --%>
        <% for (int i = 0; i < action.getTicketAndHolders().size(); ++i) { if (i == index) continue; %>
            <td><%= userTicketInfoMap.get(p.getUserId())[i] != null ? userTicketInfoMap.get(p.getUserId())[i] : "-" %></td>
        <% } %>
        <%-- Enquete information if nay --%>
        <% if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) { %>
            <% for (EnqueteQuestion question : event.getEnquetes()) {
                List<String> answers = p.getEnqueteAnswers().get(question.getId()); %>
                <td>
                <%= answers != null && !answers.isEmpty() ? StringUtils.join(answers.iterator(), ',') : "" %></td>
            <% } %>
        <% } %>

    </tr>
    <% } %>
</tbody>
</table>

