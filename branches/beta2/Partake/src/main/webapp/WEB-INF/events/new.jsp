<%@page import="in.partake.model.EventEx"%>
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

<div class="row">
	<div class="span9">
		<jsp:include page="/WEB-INF/events/_edit_innerform.jsp" />
	</div>
	<div class="span3">
		<div class="fixed span3 spinner-container">
			<input id="event-create-form-save" type="button" class="btn btn-primary span3" value="イベントを保存する" />
			<p class="help-block">イベントをドラフトとして保存します。保存しただけではまだ公開されません。</p>
			<p></p>
			<input id="event-create-form-publish" type="button" class="btn btn-danger span3" value="イベントを公開する" />
			<p class="help-block">イベントを公開して、他の人が参加できるようにします。</p>
		</div>
	</div>
</div>

<script>
function submitEvent() {
	var spinner = partakeUI.spinner(document.getElementById('event-create-form-save'));
	spinner.show();
	
	$('#event-create-form-save').attr('disabled', '');
	$('#event-create-form-publish').attr('disabled', '');
	
	var argArray = $('#event-form').serializeArray({ checkboxesAsBools: true });
	var arg = {};
	for (var i = 0; i < argArray.length; ++i)
		arg[argArray[i].name] = argArray[i].value;
	console.log(arg);

	return partake.event.create(arg)
	.always(function (xhr) {
		spinner.hide();
		$('#event-create-form-save').removeAttr('disabled');
		$('#event-create-form-publish').removeAttr('disabled');
		
		// Remove all input classes anyway
		$('#event-form div').removeClass('error');
	})
	.fail(function (xhr) {
		var json = $.parseJSON(xhr.responseText);
		if (!json.errorParameters) {
			alert(json.reason);
			return;
		}
		
		for (var key in json.errorParameters) {
			var e = $('#' + key);
			e.addClass('error');
		}
	});
}

$('#event-create-form-save').click(function() {
	$('#draft').val('true');
	submitEvent()
	.done(function (json) {
		location.href = "/events/" + json.eventId;
	});
});

$('#event-create-form-publish').click(function() {
	$('#draft').val('false');
	submitEvent()
	.done(function (json) {
		location.href = "/events/" + json.eventId;
	})
});
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
