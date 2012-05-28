<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" type="text/css" href="/css/jquery-ui-1.8.16.custom.css" />
<link rel="stylesheet" type="text/css" href="/css/jquery-ui-timepicker-addon.css" />
<link rel="stylesheet" type="text/css" href="/css/style.css?version=<%= Helper.getCssVersion() %>" media="screen,print" />
<link rel="shortcut icon" href="/images/favicon.ico">

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/i18n/jquery.ui.datepicker-ja.min.js"></script>
<%-- <script type="text/javascript" src="http://www.google.com/jsapi"></script> --%>

<%-- Support HTML5 elements for IE6-8 support --%>
<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<%= Helper.javascript(
        "/js/partake.js",
        "/js/partake-ui.js",
        "/js/vendor/jquery.ui.widget.js",
        "/js/jquery.iframe-transport.js",
        "/js/jquery.fileupload.js",
        "/js/jquery-ui-timepicker-addon.js",
        "/js/jquery-ui-timepicker-ja.js",
        "/js/jquery.json.2.3.js",
        "/js/jquery.masonry.min.js",
        "/js/jquery.fixup.js",
        "/js/bootstrap.min.js"
        ) %>

<script type="text/javascript">
    partake = createPartakeClient('<%= Helper.getSessionToken() %>');
    partakeUI = createPartakeUIClient();
    $partake = partake; // For backward compatibility.
</script>

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

