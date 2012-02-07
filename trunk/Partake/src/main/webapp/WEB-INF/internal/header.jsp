<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="java.util.Collection"%>
<%@page import="in.partake.controller.PartakeActionSupport"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="in.partake.resource.Constants"%>
<%@ page import="in.partake.resource.I18n"%>
<%@ page import="in.partake.util.Util"%>
<%@ page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null) {
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL); 
    } 
%>

<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="brand" href="/">PARTAKE</a>
			<ul class="nav">
				<li><a href="/events/new">イベントを作る</a></li>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">イベントを見つける <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/events/search">イベント検索</a></li>
						<li><a href="/feedlist">RSS / iCal</a></li>
					</ul>
				</li>
			</ul>
			<ul class="nav pull-right">
			<% if (user != null) { %>
	            <li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown"><%= user.getScreenName() %> <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/mypage">
							<img src="<%= h(user.getTwitterLinkage().getProfileImageURL()) %>" class="profile-image rad sdw" alt="profile image" width="20" height="20" />
							<%= I18n.t("page.mypage") %>
						</a></li>
						<li><a href="/preference"><%= I18n.t("page.preference") %></a></li>
						<li class="divider"></li>
						<% if (user.isAdministrator()) { %>
							<li><a href="/admin/"><%= I18n.t("page.admin") %></a></li>
							<li class="divider"></li>
						<% } %>
						<li><a href="/auth/logout"><%= I18n.t("common.logout") %></a></li>
					</ul>
	            </li>
			<% } else { %>
	            <li class="dropdown" data-dropdown>
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">ログイン <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="#" onclick="document.loginByTwitterForm.submit();">Twitter でログイン</a>
						<form name="loginByTwitterForm" action="<%= request.getContextPath() %>/auth/loginByTwitter" style="display:none">
							<input type="hidden" name="redirectURL" value="<%= h(redirectURL) %>" />
						</form></li>
						<li><a data-toggle="modal" href="#openid-signin-dialog">Open ID でログイン</a></li>
					</ul>
	            </li>
	        <% } %>
			</ul>
		</div>
	</div>
</div>

<div id="openid-signin-dialog" class="modal" style="display:none">
	<div class="modal-header">
    	<a class="close" data-dismiss="modal">&times;</a>
    	<h3>OpenID でログイン</h3>
	</div>
  	<div class="modal-body">
  		<p>Twitter が落ちている場合などに、Open ID でもログインすることができます。</p>
  		<p>Twitter をメッセージ基盤として利用するため、Twitter アカウントとの結びつけが必要です。
  		Twitter ID と Open ID の結び付け設定は済んでいない場合、<a href="#" onclick="document.loginByTwitter.submit();">Twitter でログイン</a>してから設定してください。</p>
  		
  		<p>次の ID を使ってログイン</p>
  		<form method="post" action="/auth/loginByOpenID" class="inline-block">
  			<input type="hidden" name="openid_identifier" value="https://www.google.com/accounts/o8/id" />
			<input type="submit" value="Google" />
  		</form>
  		<form method="post" action="/auth/loginByOpenID" class="inline-block">
  			<input type="hidden" name="openid_identifier" value="https://mixi.jp" />
			<input type="submit" value="Mixi" />
  		</form>
  		<form method="post" action="/auth/loginByOpenID" class="inline-block">
  			<input type="hidden" name="openid_identifier" value="http://yahoo.co.jp" />
			<input type="submit" value="Yahoo Japan" />
  		</form>
  		<form method="post" action="/auth/loginByOpenID" class="inline-block">
  			<input type="hidden" name="openid_identifier" value="http://livedoor.com/" />
			<input type="submit" value="Livedoor" />
  		</form>

		<p>はてな ID でログイン</p>
  		<form name="loginByHatenaForm" method="post" action="/auth/loginByOpenID" style="display:none">
  			<input type="hidden" id="login-hatena-openid-identifier" name="openid_identifier" value="http://www.hatena.ne.jp/" />
			<input type="submit" value="はてなでログイン" />
  		</form>
  		<div>
  			<script>
  				function loginByHatena() {
  					var name = $("#login-hatena-username").val().replace(/^\s+|\s+$/g, "");
  					var ident = "http://www.hatena.ne.jp/" + name;
  					$("#login-hatena-openid-identifier").val(ident);
  					document.loginByHatenaForm.submit();
  				}
  			</script>
  			<input type="text" id="login-hatena-username" value="" placeholder="はてな ID を入力" />
			<input type="button" value="はてなでログイン" onclick="loginByHatena()" />  			
  		</div>

		<p>URL を使ってログイン</p>
  		<form method="post" action="/auth/loginByOpenID">
  			<input type="text" name="openid_identifier" value="" placeholder="http:// Open ID URL を入力" />
			<input type="submit" value="OpenID でログイン" />
  		</form>  		
  	</div>
</div>

<div class="container">

<%-- header-nomessages.jsp をけす　 --%>
<% if (!"true".equals(request.getAttribute(Constants.ATTR_NO_HEADER_MESSAGES))) { %>
<div class="message">
    <%-- action error: TODO: should be deleted. --%>
    <s:fielderror />
    <s:actionerror />

    <%-- warning / error --%>
    <% 
        PartakeActionSupport pas = (PartakeActionSupport) request.getSession().getAttribute(Constants.ATTR_ACTION);
        if (pas != null) {
            Collection<String> errors = pas.getErrorMessages();
            if (!errors.isEmpty()) {
                for (String message : errors) {
                    out.print("<div class=\"alert fade in\">");
                    out.print("<a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>");
                    out.print(h(message));
                    out.print("</div>");
                }                
            }
            
            Collection<String> warnings = pas.getWarningMessages();
            if (!warnings.isEmpty()) {
                for (String message : warnings) {
                    out.print("<div class=\"alert alert-warning fade in\">");
                    out.print("<a class=\"close\" data-dismiss=\"alert\" href=\"#\">&times;</a>");
                    out.print(h(message));
                    out.print("</div>");
                }
            }
        }
    %>

    <%-- action message --%>
    <s:actionmessage />
</div>
<% } %>

