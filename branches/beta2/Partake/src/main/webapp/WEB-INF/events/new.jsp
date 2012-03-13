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
	<div class="spinner-container pull-right">
		<input id="event-create-form-save" type="button" class="btn btn-primary span1-half" value="保存" />
		<input id="event-create-form-publish" type="button" class="btn btn-danger span1-half" value="公開" />
	</div>
	<h1>イベントを作成します</h1>
	<script>
	$('#event-create-form-save').popover({
		placement: 'bottom',
		title: 'イベントを保存します',
		content: 'イベントをドラフトとして保存します。保存しただけではまだ公開されません。'
	});
	$('#event-create-form-publish').popover({
		placement: 'bottom',
		title: 'イベントを公開します',
		content: 'イベントを公開して、他の人が参加できるようにします。'
	});
	</script>
</div>

<div class="row">
	<div class="span9">
		<jsp:include page="/WEB-INF/events/_edit_innerform.jsp" />
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
		
		var focusKey = null;
		for (var key in json.errorParameters) {
			if (!focusKey)
				focusKey = key;
			var e = $('#' + key);
			e.addClass('error');
		}
		
		if (focusKey)
			location.hash = focusKey;
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
	});
});
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
