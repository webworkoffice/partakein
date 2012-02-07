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
    List<String> associatedOpenIds = pref.getAssociateOpenIds();
    if (associatedOpenIds != null && !associatedOpenIds.isEmpty()) { %>
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
	<p>現在次の URL と結び付けられています。</p>
	<ul> <% for (String openid : associatedOpenIds) { %>
		<li><%= h(openid) %>
         	<a href="#" title="結びつけを解除する" onclick="removeOpenID('<%= h(openid) %>')">[x]</a>
		</li>
    <% } %></ul>
<% } else { %>
	<p>現在どの OpenID とも結び付けられていません。</p>
<% } %>

<p><input type="button" data-toggle="modal" href="#openid-connect-dialog" value="OpenID と結びつける" /></p>
	
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

		<p>はてな ID でログイン</p>
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
<div class="row"><s:form method="post" cssClass="form-horizontal" action="setPreference"><fieldset>
	<%-- <legend>Example form legend</legend> --%>
	<%= Helper.token() %>
	<div class="control-group">
    	<label class="control-label">設定項目</label>
        <div class="controls">
              <label class="checkbox">
              		<s:checkbox name="receivingTwitterMessage" />
					twitter 経由のリマインダーを受け取る (default:受け取る)
              </label>
              <label class="checkbox">
              		<s:checkbox name="profilePublic" />
					マイページを他人にも公開する (default：公開)
              </label>
              <label class="checkbox">
              		<s:checkbox name="tweetingAttendanceAutomatically" />
	 				イベントに参加するとき、自動的に参加をつぶやく (default：つぶやかない)
              </label>
			  <s:submit value="この設定を保存する" />
        </div>
    </div>
</fieldset></s:form></div>

<h2>カレンダー</h2>

<p>自分の参加・管理イベントを ics ファイル (カレンダー) で受信することが出来ます。</p>
<p>以下が、あなたのカレンダーID（URL）です。これを、普段使っている Google カレンダーなどにインポートすることができます。</p>
<%-- NOTE: RSS の ID はカレンダー ID と共通です。 --%>
<% if (user.getCalendarId() != null && !"".equals(user.getCalendarId())) { %>
    <input type="text" value="http://partake.in<%= request.getContextPath() %>/calendars/<%= h(user.getCalendarId()) %>.ics" style="width: 80%;"/><%-- TODO use in.partake.toppath from properties file --%>
<%--
    <input type="text" value="http://partake.in<%= request.getContextPath() %>/feed/user/<%= h(user.getCalendarId()) %>" style="width: 80%;"/>
 --%>
<% } %>

<h3>カレンダーIDを再生成する</h3>

<p>不意にカレンダー ID を知られてしまった場合などに、カレンダー ID を再生成できます。ただし、これまでのカレンダー URL は無効になるので気をつけてください。</p>

<s:form method="post" action="revokeCalendar">
	<%= Helper.token() %>
	<s:submit value="カレンダー ID を再生成する" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>