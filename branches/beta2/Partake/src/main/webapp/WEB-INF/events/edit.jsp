<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	EventEditAction action = (EventEditAction) request.getAttribute(Constants.ATTR_ACTION);
	EventEx event = action.getEvent();
%>

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

<div class="row">
	<div class="span9">
		<jsp:include page="/WEB-INF/events/_edit_innerform.jsp" />
	</div>
	<div class="span3">
		<div class="span3 well fixed">
			<% if (event.isPreview()) { %>
			<div class="spinner-container">
				<input id="event-modify-form-save" type="button" class="btn btn-primary span3" value="イベントを保存する" />
			</div>
			<p class="help-block">イベントを保存します。保存しただけではまだ公開されません。</p>
			<p></p>
			<div class="spinner-container">
				<input id="event-modify-form-publish" type="button" class="btn btn-danger span3" value="イベントを公開する" />
			</div>
			<p class="help-block">イベントを公開します。</p>
			<% } else { %>
			<div class="spinner-container">
				<input id="event-modify-form-save" type="button" class="btn btn-primary span3" value="イベントを更新する" />
			</div>
			<p class="help-block">イベントを更新します。</p>
			<% } %>
		</div>
	</div>
</div>

<script>
function submitEvent() {
	var spinner = partakeUI.spinner(document.getElementById('event-modify-form-save'));
	spinner.show();
	
	$('#event-modify-form-save').attr('disabled', '');
	$('#event-modify-form-publish').attr('disabled', '');
	
	var argArray = $('#event-form').serializeArray({ checkboxesAsBools: true });
	var arg = {};
	for (var i = 0; i < argArray.length; ++i)
		arg[argArray[i].name] = argArray[i].value;
	
	console.log(arg);

	return partake.event.modify('<%= h(event.getId()) %>', arg)
	.always(function (xhr) {
		spinner.hide();
		$('#event-modify-form-save').removeAttr('disabled');
		$('#event-modify-form-publish').removeAttr('disabled');
		
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

$('#event-modify-form-save').click(function() {
	$('#draft').val('true');
	submitEvent()
	.done(function (json) {
		location.href = "/events/" + json.eventId;
	});
});

$('#event-modify-form-publish').click(function() {
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
