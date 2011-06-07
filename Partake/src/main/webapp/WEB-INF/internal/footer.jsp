<%@page import="in.partake.resource.I18n"%>
<%@page import="in.partake.resource.PartakeProperties"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%-- <div id="content"> appear in header.jsp --%></div>
<div id="footer">
	<ul id="footer-navi">
		<li><a href="/contact"><%= I18n.t("page.contact") %></a></li>
		<li><a href="/termofuse"><%= I18n.t("page.termofuse") %></a></li>
		<li><a href="http://career.worksap.co.jp/newgraduate/geek/index.html"><%= I18n.t("page.recruitment") %></a></li>
		<li><a href="http://code.google.com/p/partakein/">Copyright &copy; The PARTAKE Committers</a></li>
		<li><a href="http://www.worksap.co.jp/">Powered by <img src="<%= request.getContextPath() %>/images/worksapplications.png" alt=""></a></li>
		<li><a href="http://aiit.ac.jp/"><img src="<%= request.getContextPath() %>/images/aiit-logo.png"></a></li>
	</ul>
</div>
<%-- <div id="wrapper"> appear in header.jsp --%></div>