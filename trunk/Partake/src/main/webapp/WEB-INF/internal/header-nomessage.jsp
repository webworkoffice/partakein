<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="java.util.Collection"%>
<%@page import="in.partake.controller.PartakeActionSupport"%>
<%@ page import="in.partake.resource.Constants"%>
<%@ page import="in.partake.util.Util"%>
<%@ page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null) {
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL); 
    } 
%>

<div id="wrapper">

<div id="header">
    <div id="sitetitle"><a href="<%= request.getContextPath() %>/"><img src="<%= request.getContextPath() %>/images/logo.png" alt="PARTAKE" /></a></div>

    <div id="menu">
        <ul id="header-navi">
        <% if (user != null) { %>
            <li><a href="<%= request.getContextPath() %>/events/new">イベント作成</a></li>
            <li><a href="<%= request.getContextPath() %>/events/search">イベント検索</a></li>
            <li><a href="<%= request.getContextPath() %>/mypage">マイページ</a></li>
            <% if (user.isAdministrator()) { %>
            <li>管理</li>
            <% } %>
            <li id="loggedin-menu">
                <img src="<%= h(user.getTwitterLinkage().getProfileImageURL()) %>" class="profile-image" alt="profile image" width="48" height="48" />
                <span class="loggedin-name">
                    <a href="<%= request.getContextPath() %>/users/<%= h(user.getId()) %>"><%= h(user.getTwitterLinkage().getScreenName()) %></a>
                </span>
                <span class="loggedin-link">
                    <a href="<%= request.getContextPath() %>/preference">設定</a>
                    <a href="<%= request.getContextPath() %>/auth/logout">ログアウト</a>
                </span>             
            </li>
        <% } else { %>
            <li><a href="<%= request.getContextPath() %>/events/search">イベント検索</a></li>
            <li id="sign-in-with-twitter-menu">
                <div id="signin-with-twitter-part">
                    <form action="/auth/loginByTwitter">
                        <input type="hidden" name="redirectURL" value="<%= h(redirectURL) %>" />
                        <input type="image" src="<%= request.getContextPath() %>/images/signinwithtwitter.png" value="Sign in with Twitter" class="signinwithtwitter" />
                    </form>
                </div>
                <div id="signin-with-openid-part">
                    <a href="#" id="open-signin-dialog"><img src="<%= request.getContextPath() %>/images/signinwithopenid.png" class="signinwithopenid" /></a>
                </div>
            </li>
        <% } %>
        </ul>
    </div>
</div>

<%-- display: none をここにかくのは見苦しいのでなんとかしたい --%>
<div id="signin-dialog" title="Sign in with OpenID" style="display: none">
    <p>Open ID でサインインします。 Twitter アカウントと結び付けられている必要があります。</p>
    <p>Twitter アカウントと Open ID が結び付けられていない場合、Open ID ではログイン出来ません。Twitter アカウントでログイン後、Open ID と結びつけを行って下さい。</p>
    <ul class="signin-dialog-left">
        <li><form id="signinwithgoogle" name="signinwithgoogle" action="/auth/loginByOpenID" method="post">
            <input type="hidden" name="redirectURL" value="<%= h(redirectURL) %>" />
            <input type="hidden" name="openID" value="http://www.google.com/accounts/o8/id" />
            <a href="#" onclick="document.signinwithgoogle.submit()">Google アカウントでサインイン</a>
        </form></li>
    </ul>
    
</div>

<div id="content"><%-- </div> will appear in footer.jsp --%>


