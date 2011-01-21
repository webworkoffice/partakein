<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.ParticipationEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.ParticipationStatus"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.dto.EventReminderStatus"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@page import="in.partake.model.dto.DirectMessage"%>
<%@page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%
    EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
    ParticipationList participationList = (ParticipationList) request.getAttribute(Constants.ATTR_PARTICIPATIONLIST);
    
    List<ParticipationEx> enrolledParticipations = participationList.getEnrolledParticipations();
    List<ParticipationEx> spareParticipations = participationList.getSpareParticipations();
    List<ParticipationEx> cancelledParticipations = participationList.getCancelledParticipations();
    
    List<ParticipationEx> ps = new ArrayList<ParticipationEx>();
    ps.addAll(enrolledParticipations);
    ps.addAll(spareParticipations);
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title><%= h(event.getTitle()) %> - 参加者リスト - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />


<h1><%= h(event.getTitle()) %> - 参加者リスト</h1>
<div class="output">
<ul>
<li><a href="<%= request.getContextPath() %>/events/participants/print">印刷ページを開く</a></li>
<li><a href="<%= request.getContextPath() %>/events/participants/<%= event.getId() %>.csv">CSVで出力(UTF-8)</a>
</li>
</ul>
</div>
<table class="mypage-tbl">
    <colgroup> 
      <col width="25px" /><col width="60px" /><col width="40px" /> 
      <col width="100px" /><col width="50px" /><col width="50px" /> 
      <col width="100px" />
    </colgroup>
<thead>
    <tr><th>順番</th><th>名前</th><th>参加状態</th><th>コメント</th><th>参加日時</th><th>優先</th><th>操作</th></tr>
</thead>
<tbody>
    <% 
    int order = 0;
    for (ParticipationEx p : ps) { %>
    <tr>
        <td><%= ++order %></td>
        <td><%= h(p.getUser().getScreenName()) %></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td><%= h(p.getModifiedAt().toString()) %></td>
        <td><%= p.getPriority() > 0 ? String.format("優先 (%d)", p.getPriority()) : "-" %></td>
        <td>
            参加者リストから取り除く / 優先順位を上げる
        </td>
    </tr>
    <% } %>
</tbody>
</table>
    
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>