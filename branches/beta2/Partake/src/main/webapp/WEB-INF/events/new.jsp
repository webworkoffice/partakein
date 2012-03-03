<%@page import="in.partake.view.util.Helper"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>イベントを作成します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>イベントを作成します</h1>
</div>

<form id="event-create-form" method="post" class="form-horizontal" action="commit">
	<%= Helper.tokenTags() %>
	<div class="row">
		<div class="span9">
			<%@ include file="/WEB-INF/events/_edit_innerform.jsp" %>
		</div>
		<div class="span3">
			<div class="fixed span3 spinner-container">
				<input id="event-create-form-publish" type="button" class="btn btn-danger span3" value="イベントを公開する" />
				<p class="help-block">イベントを公開して、他の人が参加できるようにします。</p>
				<p></p>
				<input id="event-create-form-save" type="button" class="btn btn-primary span3" value="イベントを保存する" />
				<p class="help-block">イベントをドラフトとして保存します。保存しただけではまだ公開されません。</p>
			</div>
		</div>
	</div>
</form>

<script>
$('#event-create-form-save').click(function() {
	var spinner = partakeUI.spinner(document.getElementById('event-create-form-save'));
	spinner.show();
	
	$('#event-create-form-save').attr('disabled', '');
	$('#event-create-form-publish').attr('disabled', '');
	
	var argArray = $('#event-create-form').serializeArray();
	var arg = {};
	for (var i = 0; i < argArray.length; ++i)
		arg[argArray[i].name] = argArray[i].value;
	console.log(arg);

	// Creates begin date.
	
	// Creates end date.
	// Creates deadline.
	
	partake.event.create(arg)
	.always(function (xhr) {
		spinner.hide();
		$('#event-create-form-save').removeAttr('disabled');
		$('#event-create-form-publish').removeAttr('disabled');
		
		// Remove all input classes anyway
		$('#event-create-form input').removeClass('error');
		$('#event-create-form textarea').removeClass('error');
		$('#event-create-form select').removeClass('error');
	})
	.done(function (json) {
		alert('OK');
	})
	.fail(function (xhr) {
		var json = $.parseJSON(xhr.responseText);
		if (!json.errorParameters) {
			alert(json.reason);
			return;
		}
		
		for (var key in json.errorParameters) {
			console.log(key);
			console.log("    " + json.errorParameters[key]);
			var e = $('#' + key);
			e.addClass('error');
		}
	});
});
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
