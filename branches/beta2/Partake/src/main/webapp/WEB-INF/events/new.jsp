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

<div class="tabbable">
    <ul class="nav nav-pills subnav">
        <li class="active"><a data-toggle="tab" href="#tab-basic">基本情報</a></li>
        <li><a data-toggle="tab" href="#tab-ticket">チケット</a></li>
        <li><a data-toggle="tab" href="#tab-access">アクセス権</a></li>
        

        <li class="pull-right spinner-container"><a id="event-create-form-save" href="#" class="btn btn-primary">保存</a></li>
        <li class="pull-right spinner-container"><a id="event-create-form-publish" href="#" class="btn btn-danger">公開</a></li>
    </ul>
    <form id="event-form" class="form-horizontal"><div class="tab-content">
        <div id="tab-basic" class="tab-pane active">
            <jsp:include page="/WEB-INF/events/_event_edit_basic.jsp" />
        </div>
        <div id="tab-ticket" class="tab-pane">
            <jsp:include page="/WEB-INF/events/_event_edit_ticket.jsp" />
        </div>
        <div id="tab-access" class="tab-pane">
            <jsp:include page="/WEB-INF/events/_event_edit_access.jsp" />
        </div>
    </div></form>

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
