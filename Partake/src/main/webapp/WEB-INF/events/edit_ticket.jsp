<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.model.dto.EventTicket"%>
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
    <title>チケット</title>
</head>
<body class="with-sub-nav">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true">
    <jsp:param name="NAVIGATION" value="ticket" />
</jsp:include>

<div class="content-body">

<div class="page-header">
    <h1>チケット</h1>
    <p>イベントのチケットを編集します。</p>
</div>

<div class="row" style="margin-bottom: 10px;">
    <div class="span12 offset1"><strong>チケット名</strong></div>
    <div class="span6"><strong>数量</strong></div>
</div>

<div class="row"><div id="ticket-list" class="span24" style="border-bottom: 1px solid; margin-bottom: 10px;">
</div></div>

<div id="template" style="display: none; border-top: 1px solid; padding-top: 10px; padding-bottom: 10px;">
    <div id="template-head" class="row">
        <div id="template-ticket-summary-name" class="span12 offset1">チケット名を入力してください。</div>
        <div id="template-ticket-summary-amount" class="span6">無制限</div>
        <div class="span5">
            <a href="#" id="template-show-edit"><i class="icon-pencil"></i>編集</a>
            <a href="#" id="template-remove"><i class="icon-remove"></i>削除</a>
        </div>
    </div>
    <div id="template-body" class="row" style="display: none;">
        <div class="span18 offset1">
            <form id="template-form" class="form-horizontal"><fieldset>
                <input id="template-id" type="hidden" name="id" value="">
                <div class="control-group">
                    <label class="control-label">チケット名</label>
                    <div class="controls">
                        <input id="template-name" name="name" type="text" class="span12">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">募集開始日時</label>
                    <div class="controls">
                        <label class="radio"><input id="template-start-anytime" type="radio" name="applicationStart" value="anytime" checked />今から</label>
                        <label class="radio"><input id="template-start-before" type="radio" name="applicationStart" value="from_nday_before" />イベントの <input type="text" class="span2" name="applicationStartDayBeforeEvent" value="0" /> 日前から</label>
                        <label class="radio"><input id="template-start-custom" type="radio" name="applicationStart" value="from_custom_day" />次の日付から
                            <input type="text" type="text" name="customApplicationStartDate" class="edit-date" placeholder="YYYY-MM-DD hh:mm" />
                        </label>
                        <p class="help-block">YYYY-MM-DD hh:mm 形式で入力します。</p>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">募集終了日時</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="applicationEnd" value="till_time_before_event" checked />イベントが始まるまで</label>
                        <label class="radio"><input type="radio" name="applicationEnd" value="till_time_after_event" checked />イベントが終わるまで</label>
                        <label class="radio"><input type="radio" name="applicationEnd" value="till_nday_before" />イベントの <input type="text" class="span2" name="applicationEndDayBeforeEvent" value="0" /> 日前まで</label>
                        <label class="radio"><input type="radio" name="applicationEnd" value="till_custom_day" />次の日付まで
                            <input type="text" type="text" name="customApplicationEndDate" class="edit-date" placeholder="YYYY-MM-DD hh:mm"/>
                        </label>
                        <p class="help-block">YYYY-MM-DD hh:mm 形式で入力します。</p>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">仮参加締切日時</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="reservationEnd" value="till_time_before_application" checked />申込締切と同じ</label>
                        <label class="radio"><input type="radio" name="reservationEnd" value="till_nhour_before" />申込締切の <input type="text" class="span2" name="reservationEndHourBeforeApplication" value="0" /> 時間前まで</label>
                        <label class="radio"><input type="radio" name="reservationEnd" value="till_none" />仮参加を認めない</label>
                        <label class="radio"><input type="radio" name="reservationEnd" value="till_custom_day" />次の日付まで
                            <input type="text" type="text" name="customReservationEndDate" class="edit-date" placeholder="YYYY-MM-DD hh:mm" />
                        </label>
                        <p class="help-block">YYYY-MM-DD hh:mm 形式で入力します。</p>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">価格</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="priceType" value="free" checked />無料</label>
                        <label class="radio"><input type="radio" name="priceType" value="nonfree" />
                        <input type="text" class="span4" name="price" value="1000" />円 (会場で支払い)</label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">チケット枚数</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="amountType" value="unlimited" checked />無制限</label>
                        <label class="radio"><input type="radio" name="amountType" value="limited" />
                            <input type="text" class="span2" name="amount" value="10" />枚</label>
                    </div>
                </div>
            </fieldset></form>
        </div>
        <div class="span5">
            <a href="#" id="template-hide-edit"><i class="icon-ok"></i>編集終了</a>
        </div>
    </div>
</div>

<div class="row"><div class="span11 offset1">
    <form class="form-horizontal"><fieldset>
        <a id="add-new-ticket">＋ 新しいチケットを追加</a>
    </fieldset></form>
</div></div>

<script>
$('.edit-date').datetimepicker({
    dateFormat: 'yy-mm-dd'
});

function didUpdateFromForm(prefix) {
    var ticketName = $('#' + prefix + ' input[name="name"]').val();
    if (ticketName && ticketName != "")
        $('#' + prefix + "-ticket-summary-name").text(ticketName);
    else
        $('#' + prefix + "-ticket-summary-name").text('チケット名を入力してください。');

    var ticketAmountType = $('#' + prefix + ' input[name="amountType"]:checked').val();
    var ticketAmount = $('#' + prefix + ' input[name="amount"]').val();
    if (ticketAmountType == "limited" && ticketAmount != null)
        $('#' + prefix + "-ticket-summary-amount").text(ticketAmount);
    else
        $('#' + prefix + "-ticket-summary-amount").text('無制限');
}

$('#template-hide-edit, #template-head').click(function() {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));

    didUpdateFromForm(prefix);
    $('#' + prefix + '-body').toggle();
    $('#' + prefix + '-head').toggle();
});
$('#template-remove').click(function() {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));
    $('#' + prefix).remove();
});

function cloneTemplate(newPrefix) {
    var template = $('#template');
    var cloned = template.clone(true);
    cloned.find("[id^=template]").each(function() {
        var id = $(this).attr('id').replace('template', newPrefix);
        $(this).attr('id', id);
    });
    cloned.attr('id', newPrefix);
    cloned.show();
    return cloned;
}

$(function() {
    var idx = 0;
    $('#add-new-ticket').click(function() {
        idx += 1;
        var newPrefix = "q" + idx;
        var cloned = cloneTemplate(newPrefix);
        $('#ticket-list').append(cloned);
    });
});

var initialData = <%= Util.toSafeJSONArray(event.getTikcets()) %>;

for (var i = 0; i < initialData.length; ++i) {
    var data = initialData[i];
    var prefix = "i" + i;
    var cloned = cloneTemplate(prefix, true);
    $('#ticket-list').append(cloned);

    $('#' + prefix + ' input[name="id"]').val(data.id);
    $('#' + prefix + ' input[name="eventId"]').val(data.eventId);
    $('#' + prefix + ' input[name="name"]').val(data.name);

    $('#' + prefix + ' input[name="applicationStart"]').val([data.applicationStart]);
    $('#' + prefix + ' input[name="applicationStartDayBeforeEvent"]').val(data.applicationStartDayBeforeEvent);
    $('#' + prefix + ' input[name="customApplicationStartDate"]').val(data.customApplicationStartDateText);

    $('#' + prefix + ' input[name="applicationEnd"]').val([data.applicationEnd]);
    $('#' + prefix + ' input[name="applicationEndDayBeforeEvent"]').val(data.applicationEndDayBeforeEvent);
    $('#' + prefix + ' input[name="customApplicationEndDate"]').val(data.customApplicationEndDateText);

    $('#' + prefix + ' input[name="reservationEnd"]').val([data.reservationEnd]);
    $('#' + prefix + ' input[name="reservationEndHourBeforeApplication"]').val(data.reservationEndHourBeforeApplication);
    $('#' + prefix + ' input[name="customReservationEndDate"]').val(data.customReservationEndDate);

    $('#' + prefix + ' input[name="priceType"]').val([data.priceType]);
    $('#' + prefix + ' input[name="price"]').val(data.price);

    $('#' + prefix + ' input[name="amountType"]').val([data.amountType]);
    $('#' + prefix + ' input[name="amount"]').val(data.amount);

    didUpdateFromForm(prefix);
}
</script>

<form><fieldset>
    <div class="form-actions">
        <input type="button" id="ticket-submit" class="btn btn-primary" value="保存">
        <span id="ticket-submit-info" class="text-info"></span>
    </div>
</fieldset></form>

<script>
$('#ticket-submit').click(function() {
    function getValues(name) {
        var inputs = $('form input[name="' + name + '"]').filter(function() {
            return $(this).form().attr('id').indexOf('template') != 0;
        });

        var result = [];
        for (var i = 0; i < inputs.length; ++i)
            result.push($(inputs.get(i)).val());
        return result;
    }
    function getSelectedValues(name) {
        var inputs = $('form input[name="' + name + '"]').filter(function() {
            return $(this).form().attr('id').indexOf('template') != 0 && $(this).is(':checked');
        });

        var result = [];
        for (var i = 0; i < inputs.length; ++i)
            result.push($(inputs.get(i)).val());
        return result;
    }

    var names = ['id', 'name',
                 'applicationStartDayBeforeEvent', 'customApplicationStartDate',
                 'applicationEndDayBeforeEvent', 'customApplicationEndDate',
                 'reservationEndHourBeforeApplication', 'customReservationEndDate',
                 'price', 'amount'];
    var selectNames = ['applicationStart', 'applicationEnd', 'reservationEnd', 'priceType', 'amountType'];

    var tickets = {};
    for (var i = 0; i < names.length; ++i)
        tickets[names[i]] = getValues(names[i]);
    for (var i = 0; i < selectNames.length; ++i)
        tickets[selectNames[i]] = getSelectedValues(selectNames[i]);

    var eventId = '<%= h(event.getId()) %>';
    partake.event.modifyTicket(eventId, tickets)
    .done(function (json) {
        $('#ticket-submit-info').hide();
        $('#ticket-submit-info').text('保存しました');
        $('#ticket-submit-info').fadeIn(500);
    })
    .fail(partake.defaultFailHandler);
});
</script>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
