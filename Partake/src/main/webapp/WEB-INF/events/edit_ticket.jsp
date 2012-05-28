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

<div class="page-header">
    <h1>チケット</h1>
    <p>イベントのチケットを編集します。</p>
</div>

<div class="row" style="margin-bottom: 10px;">
    <div class="span12 offset1">チケット名</div>
    <div class="span6">数量</div>
</div>

<div class="row"><div id="ticket-list" class="span24" style="border-bottom: 1px solid; margin-bottom: 10px;">
</div></div>

<div id="template" style="display: none; border-top: 1px solid; padding-top: 10px; padding-bottom: 10px;">
    <div id="template-head" class="row">
        <div id="template-question-text" class="span12 offset1">チケット名を入力してください。</div>
        <div id="template-question-type" class="span6">0/0</div>
        <div class="span5">
            <a href="#" id="template-show-edit"><i class="icon-pencil"></i>編集</a>
            <a href="#" id="template-remove"><i class="icon-remove"></i>削除</a>
        </div>
    </div>
    <div id="template-body" class="row" style="display: none;">
        <div class="span18 offset1">
            <form class="form-horizontal"><fieldset>
                <div class="control-group">
                    <label class="control-label">チケット名</label>
                    <div class="controls">
                        <input id="template-ticket-name" type="text" class="span12">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">募集開始日時</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="fromNow" checked />今から</label>
                        <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="beforeNDays" />イベントの <input type="text" class="span2" name="ticketApplicationStartDay[]" value="0" /> 日前から</label>
                        <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="custom" />次の日付
                            <input type="text" type="text" name="" />
                        </label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">募集終了日時</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="justBeforeEvent" checked />イベントが始まるまで</label>
                        <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="justAfterEvent" checked />イベントが終わるまで</label>
                        <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="beforeNDays" />イベントの <input type="text" class="span2" name="ticketApplicationPeriodEndDay[]" value="0" /> 日前まで</label>
                        <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="custom" />次の日付
                            <input type="text" type="text" name="" />
                        </label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">価格</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="ticketPrice[]" value="free" checked />無料</label>
                        <label class="radio"><input type="radio" name="ticketPrice[]" value="nonFree" />
                        <input type="text" class="span4" name="ticketPriceText[]" value="1000" />円 (会場で支払い)</label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">チケット枚数</label>
                    <div class="controls">
                        <label class="radio"><input type="radio" name="ticketAmount[]" value="unlimited" checked />無制限</label>
                        <label class="radio"><input type="radio" name="ticketAmount[]" value="limited" />
                            <input type="text" class="span2" name="ticketAmountText[]" value="10" />枚</label>
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
function didUpdateFromForm(prefix) {
    var question = $('#' + prefix + '-question-input').val();
    if (question && question != "")
        $('#' + prefix + '-question-text').text(question);
    else
        $('#' + prefix + '-question-text').text('チケット名を入力して下さい');
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

var initialData = [];


for (var i = 0; i < initialData.length; ++i) {
    var data = initialData[i];
    var prefix = "i" + i;
    var cloned = cloneTemplate(prefix, true);
    $('#ticket-list').append(cloned);

    $('#' + prefix + '-question-input').val(data.question);
    $('#' + prefix + '-answertype').val(data.type);
    $('#' + prefix + '-answertype').change();

    for (var j = 0; j < data.options.length; ++j) {
        var v = addItem(prefix);
        var input = v.find("input");
        input.val(data.options[j]);
    }

    didUpdateFromForm(prefix);
}
</script>

<form><fieldset>
    <div class="form-actions">
        <input type="button" id="enquete-submit" class="btn btn-primary" value="保存">
        <span id="enquete-submit-info" class="text-info"></span>
    </div>
</fieldset></form>

<script>
$('#enquete-submit').click(function() {
    var list = $('#question-list').children();
    var questions = [];
    var types = [];
    var options = [];

    list.each(function(i) {
        var elem = $(this);
        var prefix = elem.attr('id');
        var question = $('#' + prefix + '-question-input').val();
        var option = $('#' + prefix + '-options input').map(function() {
            return $(this).val();
        }).get();

        questions.push(question);
        types.push(type);
        options.push($.toJSON(option));
    });

    var eventId = '<%= h(event.getId()) %>';
    partake.event.modifyTicket(eventId, questions, types, options)
    .done(function (json) {
        $('#enquete-submit-info').hide();
        $('#enquete-submit-info').text('保存しました');
        $('#enquete-submit-info').fadeIn(500);
    })
    .fail(partake.defaultFailHandler);
});
</script>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
