<%@page import="in.partake.view.util.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>イベントを編集します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>イベントを編集します</h1>
</div>

<s:form method="post" cssClass="form-horizontal" action="commit" enctype="multipart/form-data"><%-- create じゃなくて commit なのに注意 --%>
	<%= Helper.token() %>
	<s:hidden id="eventId" name="eventId" value="%{eventId}"/><%-- new.jsp とここが違う。なんか共通化するとエラーがでる。なんで？ --%>
	<div class="row">
		<div class="span9">
			<%@ include file="/WEB-INF/events/inner-form.jsp" %>
		</div>
		<div class="span1">
			&nbsp;
		</div>
		<div class="span2">
			<div class="fixed span2">
				<s:submit cssClass="btn btn-danger" value="イベントを変更する" />
				<p class="help-block">このボタンでイベントが変更されます。</p>
				<p class="help-block">テストをしたい場合、非公開イベントとして作成すると良いでしょう。</p>
			</div>
			&nbsp;
		</div>
	</div>
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
