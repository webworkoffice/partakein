<%@page import="java.util.ArrayList"%>
<%@page import="in.partake.model.ParticipationEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
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
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/events/editAttendants.js"></script>
    <title>参加者のステータスを編集</title>
</head>
<body id="status-edit">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<h1 id="pastel-line13ji"><img src="<%= request.getContextPath() %>/images/line-yellowgreen.png" alt="">参加者のステータスを編集</h1>  
<div id="content-adjust">
<p>
特定の参加者を削除したり、優先的に参加できるようにしたりします。<br />
</p>
<p>＜優先度マーク＞<br />
<img class="adjust1" src="<%= request.getContextPath() %>/images/star.png" alt="優先参加" /> ：優先参加マーク。関連イベントに参加することにより、優先参加権を得た人です。<br />
<img src="<%= request.getContextPath() %>/images/crown.png" alt="VIP" /> ：VIPマーク。あなたがVIPに指名した人は、誰よりも優先的に参加させることができます。<br />
</p>
<h2><img src="<%= request.getContextPath() %>/images/feature-04.png" alt="" /><%= h(event.getTitle()) %> - 参加者リスト</h2>

<table class="table0">
    <colgroup><col width="32px" /></colgroup>
    <colgroup><col width="85px" /></colgroup>
    <colgroup><col width="58px" /></colgroup> 
    <colgroup><col width="150px" /></colgroup>
    <colgroup><col width="100px" /></colgroup>
    <colgroup><col width="60px" /></colgroup> 
    <colgroup><col width="120px" /></colgroup>
    <colgroup><col width="120px" /></colgroup>
<thead>
    <tr><th>順番</th><th>名前</th><th>予約状況</th><th>コメント</th><th class="print-del">登録日時</th><th>優先度</th><th class="print-del">操作</th><th>実際の出欠状況</th></tr>
</thead>
<tbody>
    <% 
    int order = 0;
    for (ParticipationEx p : ps) {
        %>
    <tr id="attendant-<%= h(p.getUserId()) %>">
        <td><%= ++order %></td>
        <td><%= h(p.getUser().getScreenName()) %></td>
        <td><%= ParticipationStatus.ENROLLED.equals(p.getStatus()) ? "参加" : "仮参加" %></td>
        <td><%= h(p.getComment()) %></td>
        <td class="print-del"><%= h(p.getModifiedAt().toString()) %></td>
        <td>
	        <%--　↓イメージを伝えるために、アイコンをベタっと貼ってます1/18 --%>
	        <img src="<%= request.getContextPath() %>/images/star.png" alt="優先" />
	        <%= p.getPriority() > 0 ? String.format("%d", p.getPriority()) : "-" %>
        </td>
        <td class="print-del">
	        <ul class="status-control">
		        <li><a href="#" title="参加者リストから削除します" onclick="deleteAttendant('<%= h(p.getUserId()) %>')">削除する</a></li>
		        <li><a href="#" title="最も優先度を高くします" onclick="makeAttendantVIP('<%= h(p.getUserId()) %>')">VIPにする</a></li>
	        </ul>
        </td>
        <%-- ちょっと ajax 使ってみる予定。 --%>
        　　　　<td class="print-del">
	        <input type="radio" name="present-<%= h(p.getUserId()) %>" value="unknown"　checked="checked"> 未選択<br />
	        <input type="radio" name="present-<%= h(p.getUserId()) %>" value="present"> 出席
	        <input type="radio" name="present-<%= h(p.getUserId()) %>" value="absent"> 欠席
        </td>
    </tr>
    <% } %>
</tbody>
</table>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>