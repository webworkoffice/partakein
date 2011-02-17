<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="in.partake.model.dto.EventRelation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@page import="in.partake.model.dto.Message"%>
<%@page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%
    EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
    ParticipationList participationList = (ParticipationList) request.getAttribute(Constants.ATTR_PARTICIPATIONLIST);
    
    List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
    List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
    List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
    
    List<EnrollmentEx> ps = new ArrayList<EnrollmentEx>();
    ps.addAll(enrolledParticipations);
    ps.addAll(spareParticipations);
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title><%= h(event.getTitle()) %> - 参加者リスト - [PARTAKE]</title>
</head>
<body id="printout">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<h1 id="pastel-line11ji"><img src="<%= request.getContextPath() %>/images/line-green.png" alt="">参加者リスト</h1>  
<div id="content-adjust">

<p class="output">
<a href="#" onclick="window.print()">印刷する</a>
<a href="<%= request.getContextPath() %>/events/participants/<%= event.getId() %>.csv">CSVで出力する(UTF-8)</a>
</p>
<h2><%= h(event.getTitle()) %> - 参加者リスト</h2>

<table class="table0">
    <colgroup><col width="32px" /></colgroup>
    <colgroup><col width="85px" /></colgroup>
    <colgroup><col width="48px" /></colgroup> 
    <colgroup><col width="150px" /></colgroup>
    <colgroup><col width="30px" /></colgroup>
    <% for (EventRelationEx eventRelation : event.getEventRelations()) { %>
		<colgroup><col width="60px" /></colgroup>
	<% } %>
    <colgroup><col width="60px" /></colgroup>
<thead>
    <tr>
    	<th>順番</th><th>名前</th><th>予約状況</th><th>コメント</th><th>優先度</th>
    	<%  {
	    		int cnt = 0;
	    		for (EventRelationEx eventRelation : event.getEventRelations()) { %>
	    			<th>関連イベント <%= ++cnt %> <a href="<%= h(eventRelation.getEvent().getEventURL()) %>">*</a></th>
	    		<% }
    	} %>
    </tr>
</thead>
<tbody>
    <% 
    int order = 0;
    for (EnrollmentEx p : ps) { %>
    <tr>
        <td><%= ++order %></td>
        <td><%= h(p.getUser().getScreenName()) %></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td><%= p.getPriority() > 0 ? String.format(" 優先 %d", p.getPriority()) : "-" %></td>
       	<% 
       		for (EventRelation eventRelation : event.getEventRelations()) { %>
       		<% if (p.getRelatedEventIds().contains(eventRelation.getDstEventId())) { %>
       			<td>出席</td>
       		<% } else { %>
       			<td>欠席</td>
       		<% } %>
       	<% } %>        
    </tr>
    <% } %>
</tbody>
</table>
</div>    
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>