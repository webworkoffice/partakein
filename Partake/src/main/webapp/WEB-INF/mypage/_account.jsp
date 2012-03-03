<%@page import="in.partake.model.dto.UserPreference"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx)request.getSession().getAttribute(Constants.ATTR_USER);
	MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);
	UserPreference pref = action.getPreference();
	List<String> associatedOpenIds = action.getOpenIds();
%>

<h2>OpenID でログインできるようにする</h2>
<p>何らかの理由でtwitter が使用できない場合に、OpenID でログインできるようにします。設定には、Google や mixi などの OpenID と Twitter ID を結び付ける必要があります。</p>


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
			<input type="button" data-toggle="modal" data-target="#openid-connect-dialog" value="新しく OpenID と結びつける..." />		
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
