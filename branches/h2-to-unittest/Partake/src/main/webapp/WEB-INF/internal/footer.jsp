<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.I18n"%>
<%@page import="in.partake.resource.PartakeProperties"%>

</div><%-- <div id="container"> appear in header.jsp --%>

<footer><div class="container">
	<div class="row">
		<div class="span3">
			<h3>PARTAKE</h3>
	    	<p><a href="/contact"><%= I18n.t("page.contact") %></a></p>
			<p><a href="/termofuse"><%= I18n.t("page.termofuse") %></a></p>
		</div>
		<div class="span6">
			<h3>About</h3>
			<p><a href="http://code.google.com/p/partakein/">Copyright &copy; The PARTAKE Committers</a></p>
			<p><a href="http://www.worksap.co.jp/">Powered by <img src="<%= request.getContextPath() %>/images/worksapplications.png" alt=""></a>
				and <a href="http://aiit.ac.jp/"><img src="<%= request.getContextPath() %>/images/aiit-logo.png"></a></p>
			<p><a href="http://career.worksap.co.jp/newgraduate/geek/index.html">[PR] <%= I18n.t("page.recruitment") %></a></p>
		</div>
		<div class="span3">
			<p><a href="#">Back to top</a></p>
		</div>
	</div>
</div></footer>