<%@page import="in.partake.view.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>新しいイベントを作成します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1 id="pastel-line13ji"><img src="<%= request.getContextPath() %>/images/line-yellow.png" alt="">新しいイベントを作成します</h1>

<s:form method="post" action="create" enctype="multipart/form-data">
	<%= Helper.token() %>
	<%@ include file="/WEB-INF/events/inner-form.jsp" %>

	<s:submit id="event-edit-submit" type="image" src="/images/button-eventform.png" label="イベントを作成する" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce_jquery/jquery.tinymce.js"></script>
<script type="text/javascript">
$(function() {
    $('textarea').tinymce({
        // Location of TinyMCE script
        script_url: "<%= request.getContextPath() %>/js/tiny_mce_jquery/tiny_mce.js",

        theme: "advanced",
        language: "ja",
        plugins: "inlinepopups,searchreplace,spellchecker,style,table,xhtmlxtras",

        theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
        theme_advanced_buttons2: "cut,copy,paste,|,search,replace,|,undo,redo,|,bullist,numlist,|,outdent,indent,blockquote,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor",
        theme_advanced_buttons3: "tablecontrols,|,hr,|,cite,abbr,acronym,del,ins,|,sub,sup,|,styleprops,spellchecker",
        theme_advanced_toolbar_location: "top",
        theme_advanced_toolbar_align: "left",
        theme_advanced_statusbar_location: "bottom",
        theme_advanced_resizing: true
    });
});
</script>
</body>
</html>
