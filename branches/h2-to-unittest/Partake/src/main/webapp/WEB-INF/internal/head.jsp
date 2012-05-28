<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />

<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.10/themes/start/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css?version=<%= Helper.getCssVersion() %>" media="screen,print" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<%= Helper.javascript(
        "/js/script.js",
        "/js/partake.js",
        "/js/partake-ui.js",
        "/js/scrolltopcontrol.js",
        "/js/jquery.switchHat.js",
        "/js/bootstrap.min.js"
        ) %>        
<script type="text/javascript">
	$partake = createPartakeClient('<%= Helper.getSessionToken() %>');
	partake = $partake;
	partakeUI = createPartakeUIClient();
</script>
<link rel="shortcut icon" href="<%= request.getContextPath() %>/images/favicon.ico">

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
