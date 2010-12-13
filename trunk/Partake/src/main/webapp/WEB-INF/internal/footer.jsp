<%@page import="in.partake.resource.PartakeProperties"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%-- <div id="content"> appear in header.jsp --%></div>

<div id="footer">
    <ul id="footer-navi">
    	<li><a href="/contact">お問い合わせ</a></li>
	<li><a href="/termofuse">ご利用にあたって</a></li>
        <li><a href="http://career.worksap.co.jp/newgraduate/geek/index.html">技術者募集</a></li>
	<li><a href="http://www.worksap.co.jp/">Copyright © <img src="<%= request.getContextPath() %>/images/worksapplications.png" alt=""></a></li>
        <li><a href="http://aiit.ac.jp/"><img src="<%= request.getContextPath() %>/images/aiit-logo.png"></a></li>
	</ul>
</div>

<%-- if google analytics is installed, analytics code will be shown here. --%>
<% if (PartakeProperties.get().getGoogleAnalyticsCode() != null) {%>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '<%= PartakeProperties.get().getGoogleAnalyticsCode() %>']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
<% } %>

<%-- <div id="wrapper"> appear in header.jsp --%></div>