<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.controller.UsersPreferenceController"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx)request.getSession().getAttribute(Constants.ATTR_USER);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>ユーザー設定</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>ユーザー設定</h1>
	<p>各種設定などが可能です。</p>
</div>

<h2>OpenID でログインできるようにする</h2>
<p>何らかの理由でtwitter が使用できない場合に、OpenID でログインできるようにします。設定には、Google や mixi などの OpenID と Twitter ID を結び付ける必要があります。</p>

<%  UsersPreferenceController pref = (UsersPreferenceController) request.getSession().getAttribute(Constants.ATTR_ACTION);
    List<String> associatedOpenIds = pref.getAssociateOpenIds(); %>

<div class="row"><form class="form-horizontal"><fieldset>
	<div class="control-group">
	   	<label class="control-label">Open ID</label>
	   	<div class="controls">
	   	<% if (associatedOpenIds != null && !associatedOpenIds.isEmpty()) { %>
			<% for (String openid : associatedOpenIds) { %>
				<p><%= h(openid) %>
		         	<a href="#" title="結びつけを解除する" onclick="removeOpenID('<%= h(openid) %>')">[&times;]</a>
				</p>
		    <% } %>
		<% } else { %>
			<p>現在どの OpenID とも結び付けられていません。</p>
		<% } %>
	   	</div>
	</div>
	<div class="control-group">
		<div class="controls">
			<input type="button" data-toggle="modal" href="#openid-connect-dialog" value="新しく OpenID と結びつける..." />		
		</div>
	</div>
</fieldset></form></div>

<script>
	function removeOpenID(ident) {
		if (!window.confirm(ident + ' の結びつけが解除されます。よろしいですか？'))
			return;
		$partake.removeOpenID(ident).success(function(json) {
			location.reload();
		}).error(function(json) {
			alert('OpenID の結びつけの解除に失敗しました。' + json.reason);			
		});
	}
</script>

	
<div id="openid-connect-dialog" class="modal" style="display:none">
	<div class="modal-header">
    	<a class="close" data-dismiss="modal">&times;</a>
    	<h3>OpenID と結びつけ</h3>
	</div>
  	<div class="modal-body">
  		<p>次の ID と結びつけ</p>
  		<form method="post" action="/auth/connectWithOpenID" class="inline-block">
  			<%= Helper.token() %>
  			<input type="hidden" name="openid_identifier" value="https://www.google.com/accounts/o8/id" />
			<input type="submit" value="Google" />
  		</form>
  		<form method="post" action="/auth/connectWithOpenID" class="inline-block">
  			<%= Helper.token() %>
  			<input type="hidden" name="openid_identifier" value="https://mixi.jp" />
			<input type="submit" value="Mixi" />
  		</form>
  		<form method="post" action="/auth/connectWithOpenID" class="inline-block">
  			<%= Helper.token() %>
  			<input type="hidden" name="openid_identifier" value="http://yahoo.co.jp" />
			<input type="submit" value="Yahoo Japan" />
  		</form>
  		<form method="post" action="/auth/connectWithOpenID" class="inline-block">
  			<%= Helper.token() %>
  			<input type="hidden" name="openid_identifier" value="http://livedoor.com/" />
			<input type="submit" value="Livedoor" />
  		</form>

		<p>はてな ID と結びつけ</p>
  		<form name="connectWithHatenaForm" method="post" action="/auth/connectWithOpenID" style="display:none">
  			<%= Helper.token() %>
  			<input type="hidden" id="connect-hatena-openid-identifier" name="openid_identifier" value="http://www.hatena.ne.jp/" />
			<input type="submit" value="はてな ID と結びつけ" />
  		</form>
  		<div>
  			<script>
  				function connectWithHatena() {
  					var name = $("#connect-hatena-username").val().replace(/^\s+|\s+$/g, "");
  					var ident = "http://www.hatena.ne.jp/" + name;
  					$("#connect-hatena-openid-identifier").val(ident);
  					document.connectWithHatenaForm.submit();
  				}
  			</script>
  			<input type="text" id="connect-hatena-username" value="" placeholder="はてな ID を入力" />
			<input type="button" value="はてな ID と結びつけ" onclick="connectWithHatena()" />  			
  		</div>

		<p>URL を使って結びつけ</p>
  		<form method="post" action="/auth/connectWithOpenID">
  			<%= Helper.token() %>
  			<input type="text" name="openid_identifier" value="" placeholder="http:// OpenID URL を入力" />
			<input type="submit" value="URL を使って結びつけ" />
  		</form>
  	</div>
</div>

<h2>各種設定</h2>
<div class="row"><form class="form-horizontal"><fieldset>
	<div class="control-group">
    	<label class="control-label">設定項目</label>
        <div class="controls">
            <label class="checkbox">
            	<input type="checkbox" id="receivingTwitterMessage" name="receivingTwitterMessage" <%= pref.isReceivingTwitterMessage() ? "checked" : "" %> />
				twitter 経由のリマインダーを受け取る (default:受け取る)
            </label>
            <label class="checkbox">
            	<input type="checkbox" id="profilePublic" name="profilePublic" <%= pref.isProfilePublic() ? "checked" : "" %>/>
				マイページを他人にも公開する (default：公開)
            </label>
            <label class="checkbox">
            	<input type="checkbox" id="tweetingAttendanceAutomatically" name="tweetingAttendanceAutomatically" <%= pref.isTweetingAttendanceAutomatically() ? "checked" : "" %>/>
	 			イベントに参加するとき、自動的に参加をつぶやく (default：つぶやかない)
            </label>
            <p class="spinner-container">
	            <input id="setPreferenceButton" type="button" value="この設定を保存する" />            
	            <span id="setPreferenceMessage" class="text-info"></span>
            </p>
            <script>
              	function callSetPreference() {
              		var spinner = partakeUI.spinner(document.getElementById('setPreferenceButton'));
              		var receivingTwitterMessage = $('#receivingTwitterMessage').is(':checked');
              		var profilePublic = $('#profilePublic').is(':checked');
              		var tweetingAttendanceAutomatically = $('#tweetingAttendanceAutomatically').is(':checked');
              		
              		spinner.show();
              		$('setPreferenceButton').attr('disabled', '');
              		partake.setPreference(receivingTwitterMessage, profilePublic, tweetingAttendanceAutomatically).success(function(json) {
              			$('#setPreferenceMessage').hide();
              			$('#setPreferenceMessage').text("設定を保存しました。");
              			$('#setPreferenceMessage').fadeIn("fast");

              			$('setPreferenceButton').removeAttr("disabled");
            			spinner.hide();
            		}).error(function(json) {
              			$('#setPreferenceMessage').hide();
              			$('#setPreferenceMessage').text("設定の保存に失敗しました。: " + json.reason);
              			$('#setPreferenceMessage').fadeIn("fast");
        				$('setPreferenceButton').removeAttr("disabled");
        				spinner.hide();
              		});
              	};
              	
              	$('#setPreferenceButton').click(callSetPreference);
            </script>            
        </div>
    </div>
</fieldset></form></div>

<h2>カレンダー</h2>

<p>自分の参加・管理イベントを ics ファイル (カレンダー) で受信することが出来ます。</p>
<p>以下が、あなたのカレンダーID（URL）です。これを、普段使っている Google カレンダーなどにインポートすることができます。</p>

<div class="row"><form class="form-horizontal"><fieldset>
	<div class="control-group">
	   	<label class="control-label">カレンダー URL</label>
	    <div class="controls">
			<% if (user.getCalendarId() != null && !"".equals(user.getCalendarId())) { %>
			    <input id="calendarURL" type="text" value="<%= h(PartakeProperties.get().getTopPath()) %>/calendars/<%= h(user.getCalendarId()) %>.ics" class="span9" />
			<% } else { %>
				<input id="calendarURL" type="text" value="あなたのカレンダー ID はまだ生成されていません。" class="span9" />
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
	partake.revokeCalendar().success(function (json) {
		if (json.calendarId) {
			$('#calendarURL').val('<%= h(PartakeProperties.get().getTopPath()) %>/calendars/' + json.calendarId + '.ics');
		} else {
			location.reload();
		}
		
		$('#revokeCalendarURLMessage').hide();
		$('#revokeCalendarURLMessage').text("カレンダー ID を再生成しました。");
		$('#revokeCalendarURLMessage').fadeIn("fast");

		spinner.hide();
		button.removeAttr('disabled');
	}).error(function (json) {		
		$('#revokeCalendarURLMessage').hide();
		$('#revokeCalendarURLMessage').text("カレンダー ID の生成に失敗しました。 : " + json.reason);
		$('#revokeCalendarURLMessage').fadeIn("fast");

		spinner.hide();
		button.removeAttr('disabled');
	});
};
$('#revokeCalendarURLButton').click(callRevokeCalendar);
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>