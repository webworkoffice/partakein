<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="java.util.Collection"%>
<%@page import="in.partake.controller.PartakeActionSupport"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="in.partake.resource.Constants"%>
<%@ page import="in.partake.resource.I18n"%>
<%@ page import="in.partake.util.Util"%>
<%@ page import="static in.partake.view.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null) {
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL); 
    } 
%>

<div id="wrapper" class="rad">

<div id="header">
	<div id="sitetitle"><a href="<%= request.getContextPath() %>/"><img src="<%= request.getContextPath() %>/images/logo.png" alt="PARTAKE" /></a></div>

 	<div id="menu">
		<ul id="header-navi">
		<% if (user != null) { %>
			<li class="head-gr"><a href="<%= request.getContextPath() %>/events/new"><%= I18n.t("page.event.create") %></a></li>
			<li class="head-gr"><a href="<%= request.getContextPath() %>/events/search"><%= I18n.t("page.event.search") %></a></li>
			<li class="head-gr"><a href="<%= request.getContextPath() %>/mypage"><%= I18n.t("page.mypage") %></a></li>
			<% if (user.isAdministrator()) { %>
			<li class="head-gr"><a href="<%= request.getContextPath() %>/admin/"><%= I18n.t("page.admin") %></a></li>
			<% } %>
			<li id="loggedin-menu">
				<img src="<%= h(user.getTwitterLinkage().getProfileImageURL()) %>" class="profile-image rad sdw" alt="profile image" width="48" height="48" />
				<span class="loggedin-name">
					<a href="<%= request.getContextPath() %>/users/<%= h(user.getId()) %>"><%= h(user.getTwitterLinkage().getScreenName()) %></a>
				</span>
				<span class="loggedin-link">
					<a href="<%= request.getContextPath() %>/preference"><%= I18n.t("page.preference") %></a>
					<a href="<%= request.getContextPath() %>/auth/logout"><%= I18n.t("common.logout") %></a>
				</span>
			</li>
		<% } else { %>
			<li><a href="<%= request.getContextPath() %>/events/search"><%= I18n.t("page.event.search") %></a></li>
			<li id="sign-in-with-twitter-menu">
				<div id="signin-with-twitter-part">
					<form action="<%= request.getContextPath() %>/auth/loginByTwitter">
						<input type="hidden" name="redirectURL" value="<%= h(redirectURL) %>" />
						<input type="image" src="<%= request.getContextPath() %>/images/signinwithtwitter.png" value="Sign in with Twitter" class="signinwithtwitter cler" />
					</form>
				</div>
				<div id="signin-with-openid-part">
					<a href="#" id="open-signin-dialog"><img src="<%= request.getContextPath() %>/images/openidico.png" class="signinwithopenid" /><%= I18n.t("common.login.openid") %></a>
				</div>
			</li>
		<% } %>
		</ul>
	</div>
</div>

<jsp:include page="_openid_form.jsp" />

<div id="content"><%-- </div> will appear in footer.jsp --%>

<%-- header-nomessages.jsp をけす　 --%>
<% if (!"true".equals(request.getAttribute(Constants.ATTR_NO_HEADER_MESSAGES))) { %>
<div class="message">
    <%-- action error --%>
    <s:fielderror />
    <s:actionerror />

    <%-- warning / error --%>
    <% 
        PartakeActionSupport pas = (PartakeActionSupport) request.getSession().getAttribute(Constants.ATTR_ACTION);
        if (pas != null) {
            Collection<String> errors = pas.getErrorMessages();
            if (!errors.isEmpty()) {
                out.print("<ul class=\"errorMessage\">");
                for (String message : errors) {
                    out.print("<li>");
                    out.print(h(message));
                    out.print("</li>");
                }
                out.print("</ul>");
            }

            
            Collection<String> warnings = pas.getWarningMessages();
            if (!warnings.isEmpty()) {
                out.print("<ul class=\"warningMessage\">");
                for (String message : warnings) {
                    out.print("<li>");
                    out.print(h(message));
                    out.print("</li>");
                }
                out.print("</ul>");
            }
        }
    %>

    <%-- action message --%>
    <s:actionmessage />    
</div>
<% } %>

