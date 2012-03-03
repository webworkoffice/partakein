<%@page import="in.partake.util.Util"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.auxiliary.AttendanceStatus"%>
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
<%@page import="static in.partake.view.util.Helper.h"%>
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
    <script type="text/javascript">
    function removeAttendant(eventId, userId) {
		if (!window.confirm('参加者を削除しようとしています。この操作は取り消せません。削除しますか？')) { return; }
        
    	document.getElementById('eventIdForRemoveAttendantForm').value = eventId;
    	document.getElementById('userIdForRemoveAttendantForm').value = userId;
    	
    	document.removeAttendantForm.submit();
    }

    function makeAttendantVIP(eventId, userId, vip) {
    	document.getElementById('eventIdForMakeAttendantVIPForm').value = eventId;
    	document.getElementById('userIdForMakeAttendantVIPForm').value = userId;
    	document.getElementById('vipForMakeAttendantVIPForm').value = vip;
    	
    	document.makeAttendantVIPForm.submit();	
    }    

    function changeAttendance(userId, eventId, status) {
        $partake.changeAttendance(userId, eventId, status)
        .success(function(json) {
            $("#attendance-status-" + userId).html("保存しました");
        })
		.error(function(json) {
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

<div>
	<h3>優先度マーク</h3>
	<ul>
		<li><img class="adjust1" src="<%= request.getContextPath() %>/images/star.png" alt="優先参加" /> ：優先参加マーク。関連イベントに参加することにより、優先参加権を得た人です。</li>
		<li><img src="<%= request.getContextPath() %>/images/crown.png" alt="VIP" /> ：VIPマーク。あなたがVIPに指名した人は、誰よりも優先的に参加させることができます。</li>
	</ul>
</div>

<h3><%= h(event.getTitle()) %> - 参加者リスト</h3>

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
    for (EnrollmentEx p : ps) {
        %>
    <tr id="attendant-<%= h(p.getUserId()) %>">
        <td><%= ++order %></td>
        <td><%= h(p.getUser().getScreenName()) %></td>
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
		        <li><a href="#" onclick="removeAttendant('<%= h(p.getEventId()) %>', '<%= h(p.getUserId()) %>')">削除する</a></li>
		        <% if (p.isVIP()) { %>
		        	<li><a href="#" onclick="makeAttendantVIP('<%= h(p.getEventId()) %>', '<%= h(p.getUserId()) %>', 'false')">VIP 指定解除</a></li>
		        <% } else { %>
		        	<li><a href="#" onclick="makeAttendantVIP('<%= h(p.getEventId()) %>', '<%= h(p.getUserId()) %>', 'true')">VIP 指定</a></li>
		        <% } %>
	        </ul>
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

<s:form method="post" id="removeAttendantForm" name="removeAttendantForm" action="removeAttendant" style="display: none;">
	<%= Helper.token() %>
	<s:hidden name="eventId" id="eventIdForRemoveAttendantForm" value="" />
	<s:hidden name="userId"  id="userIdForRemoveAttendantForm" value="" />					
	<s:submit value="削除する" />
</s:form>

<s:form method="post" id="makeAttendantVIPForm" name="makeAttendantVIPForm" action="makeAttendantVIP" style="display: none;">
	<%= Helper.token() %>
	<s:hidden name="eventId" id="eventIdForMakeAttendantVIPForm" value="" />
	<s:hidden name="userId"  id="userIdForMakeAttendantVIPForm" value="" />		
	<s:hidden name="vip"     id="vipForMakeAttendantVIPForm" value="" />			
	<s:submit value="VIP にする" />
</s:form>

<s:form method="post" id="changeAttendanceForm" name="changeAttendanceForm" action="changeAttendance" style="display: none;">
	<%= Helper.token() %>
    <s:hidden name="eventId" id="eventIdForChangeAttendanceForm" value="" />
    <s:hidden name="userId"  id="userIdForChangeAttendanceForm" value="" />     
    <s:hidden name="status"  id="statusForChangeAttendanceForm" value="" />            
    <s:submit value="出席を変更" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>