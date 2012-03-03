<%@page import="in.partake.model.dto.UserPreference"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>

<%
	UserEx user = (UserEx)request.getSession().getAttribute(Constants.ATTR_USER);
	MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);
	UserPreference pref = action.getPreference();
	List<String> associatedOpenIds = action.getOpenIds();
%>

<h2>カレンダー</h2>

<p>自分の参加・管理イベントを ics ファイル (カレンダー) で受信することが出来ます。</p>
<p>以下が、あなたのカレンダーID（URL）です。これを、普段使っている Google カレンダーなどにインポートすることができます。</p>

<div class="row"><form class="form-horizontal"><fieldset>
	<div class="control-group">
	   	<label class="control-label">カレンダー URL</label>
	    <div class="controls">
			<% if (user.getCalendarId() != null && !"".equals(user.getCalendarId())) { %>
			    <input id="calendarURL" type="text" value="<%= h(PartakeProperties.get().getTopPath()) %>/calendars/<%= h(user.getCalendarId()) %>.ics" class="span6" />
			<% } else { %>
				<input id="calendarURL" type="text" value="あなたのカレンダー ID はまだ生成されていません。" class="span6" />
			<% } %>	        
	    </div>
	</div>
</fieldset></form></div>

<h3>カレンダーIDを再生成する</h3>

<p>カレンダー ID がまだ割り当てられていない場合や不意にカレンダー ID を知られてしまった場合などに、カレンダー ID を再生成できます。</p>
<p>これまでのカレンダー URL は無効になるため、お使いのカレンダーアプリケーションを再設定する必要があります。</p>
<form>
    <p class="spinner-container">
    	<input id="revokeCalendarURLButton" type="button" value="カレンダー ID を再生成する" />            
    	<span id="revokeCalendarURLMessage" class="text-info"></span>
    </p>
</form>

<script>
function callRevokeCalendar() {
	var spinner = partakeUI.spinner(document.getElementById('revokeCalendarURLButton'));
	var button = $('#revokeCalendarURLButton');

	spinner.show();
	button.attr('disabled', '');
	partake.revokeCalendar()
	.always(function (xhr) {
		spinner.hide();
		button.removeAttr('disabled');
	})
	.done(function (json) {
		if (json.calendarId) {
			$('#calendarURL').val('<%= h(PartakeProperties.get().getTopPath()) %>/calendars/' + json.calendarId + '.ics');
		} else {
			location.reload();
		}
		
		$('#revokeCalendarURLMessage').text("カレンダー ID を再生成しました。");
		$('#revokeCalendarURLMessage').hide();
		$('#revokeCalendarURLMessage').fadeIn("fast");
	}).fail(function (xhr) {		
		$('#revokeCalendarURLMessage').text("カレンダー ID の生成に失敗しました。");
		$('#revokeCalendarURLMessage').hide();
		$('#revokeCalendarURLMessage').fadeIn("fast");
	});
};
$('#revokeCalendarURLButton').click(callRevokeCalendar);
</script>