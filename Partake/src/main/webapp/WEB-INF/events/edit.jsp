<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);
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

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true" />

<div class="page-header">
    <h1>イベントを編集します</h1>
</div>

<div class="tabbable">
    <ul class="nav nav-pills subnav">
        <li class="active"><a data-toggle="tab" href="#tab-basic">基本情報編集</a></li>
        <li><a data-toggle="tab" href="#tab-access">アクセス権の設定</a></li>
    </ul>
    <form id="event-form" class="form-horizontal"><div class="tab-content">
        <div id="tab-basic" class="tab-pane active">
            <jsp:include page="/WEB-INF/events/_event_edit_basic.jsp" />
        </div>
        <div id="tab-access" class="tab-pane">
            <jsp:include page="/WEB-INF/events/_event_edit_access.jsp" />
        </div>
    </div></form>

    <%
        if (event.isDraft()) {
    %>
    <script>
    $('#event-modify-form-save').popover({
        placement: 'bottom',
        title: 'イベントを保存します',
        content: 'イベントをドラフトとして保存します。保存しただけではまだ公開されません。'
    });
    $('#event-modify-form-publish').popover({
        placement: 'bottom',
        title: 'イベントを公開します',
        content: 'イベントを公開して、他の人が参加できるようにします。'
    });
    </script>
    <% } else { %>
    <script>
    $('#event-modify-form-save').popover({
        placement: 'bottom',
        title: 'イベントを更新します',
        content: 'イベントを更新します。日時を変更すると参加状況が変化することがあります。'
    });
    </script>
    <% } %>
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
