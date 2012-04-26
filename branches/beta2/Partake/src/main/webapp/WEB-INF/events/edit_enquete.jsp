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
    <title>アンケート</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true" />

<div class="page-header">
    <h1>アンケート</h1>
    <p>イベント参加時にアンケートを取ることができます。</p>
</div>

<div class="row" style="margin-bottom: 10px;">
    <div class="span6 offset-half">質問</div>
    <div class="span3">回答形式</div>
</div>

<div class="row"><div id="question-list" class="span12" style="border-bottom: 1px solid; margin-bottom: 10px;">
</div></div>

<div id="template" style="display: none; border-top: 1px solid; padding-top: 10px; padding-bottom: 10px;">
    <div id="template-head" class="row">
        <div id="template-question-text" class="span6 offset-half">質問文を入力してください。</div>
        <div id="template-question-type" class="span3">テキスト</div>
        <div class="span2-half">
            <a href="#" id="template-show-edit"><i class="icon-pencil"></i>編集</a>
            <a href="#" id="template-remove"><i class="icon-remove"></i>削除</a>
        </div>
    </div>
    <div id="template-body" class="row" style="display: none;">
        <div class="span9 offset-half">
            <form class="form-horizontal"><fieldset>
                <div class="control-group">
                    <label class="control-label">質問文</label>
                    <div class="controls">
                        <input id="template-question-input" type="text" class="span6">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">回答形式</label>
                    <div class="controls">
                        <select id="template-answertype">
                            <option value="text">テキスト (短め / １行のテキストボックスが表示されます)</option>
                            <option value="textarea">テキスト (長め / 複数行のテキストボックスが表示されます)</option>
                            <option value="datetime">日付 (日付選択に適した形式で表示されます)</option>
                            <option value="checkbox">チェックボックス (複数の選択肢からいくつでも選べます)</option>
                            <option value="radio">ラジオボタン (複数の選択肢から１つだけ選べます)</option>
                        </select>
                    </div>
                </div>
                <div class="control-group" id="template-options" style="display: none">
                    <label class="control-label">選択肢</label>
                    <div id="template-item" class="controls">
                        <input type="text" /> <span class="help-inline"> <a style="font-size: 20px; font-weight: bold; line-height: 18px;">&times;</a></span>
                    </div>
                    <div class="controls">
                        <a id="template-add-item">＋項目を追加</a>
                    </div>
                </div>
            </fieldset></form>
        </div>
        <div class="span2-half">
            <a href="#" id="template-hide-edit"><i class="icon-ok"></i>編集終了</a>
        </div>
    </div>
</div>

<div class="row"><div class="span11 offset-half">
    <form class="form-horizontal"><fieldset>
        <a id="add-new-question">＋ 新しい質問を追加</a>
    </fieldset></form>
</div></div>

<script>
$('#template-hide-edit, #template-head').click(function() {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));

    var question = $('#' + prefix + '-question-input').val();
    if (question && question != "")
        $('#' + prefix + '-question-text').text(question);
    else
        $('#' + prefix + '-question-text').text('質問文を入力してください。');

    $('#' + prefix + '-body').toggle();
    $('#' + prefix + '-head').toggle();
});
$('#template-remove').click(function() {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));
    $('#' + prefix).remove();
});
$('#template-answertype').change(function () {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));

    var v = $(this).val();
    if (v == 'text') {
        $('#' + prefix + '-options').hide();
        $('#' + prefix + '-question-type').text('テキスト (１行)');
    } else if (v == 'textarea') {
        $('#' + prefix + '-options').hide();
        $('#' + prefix + '-question-type').text('テキスト (複数行)');
    } else if (v == 'datetime') {
        $('#' + prefix + '-options').hide();
        $('#' + prefix + '-question-type').text('日付');
    } else if (v == 'checkbox') {
        $('#' + prefix + '-options').show();
        $('#' + prefix + '-question-type').text('チェックボックス');
    } else if (v == 'radio') {
        $('#' + prefix + '-options').show();
        $('#' + prefix + '-question-type').text('ラジオボタン');
    }
});
$('#template-item a').click(function() {
    var v = $(this).parent().parent();
    v.remove();
});
$('#template-add-item').click(function() {
    var id = $(this).attr('id');
    var prefix = id.substr(0, id.indexOf('-'));

    var v = $('#template-item').clone(true);
    v.removeAttr('id');
    v.insertBefore($('#' + prefix + '-add-item').parent());
});

$(function() {
    var idx = 0;
    $('#add-new-question').click(function() {
        idx += 1;
        var template = $('#template');
        var clone = template.clone(true);
        var newPrefix = "q" + idx;
        clone.find("[id^=template]").each(function() {
            var id = $(this).attr('id').replace('template', newPrefix);
            $(this).attr('id', id);
        });
        clone.attr('id', newPrefix);
        clone.show();
        $('#question-list').append(clone);
    });
});
</script>

<form><fieldset>
    <div class="form-actions">
        <input type="button" id="enquete-submit" class="btn btn-danger-flat" value="保存">
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
        var type = $('#' + prefix + '-answertype').val();
        var option = $('#' + prefix + '-options input').map(function() {
            return $(this).val();
        }).get();

        questions.push(question);
        types.push(type);
        options.push($.toJSON(option));
    });

    var eventId = '<%= h(event.getId()) %>';
    partake.event.modifyEnquete(eventId, questions, types, options)
    .done(function (json) {
        $('#enquete-submit-info').hide();
        $('#enquete-submit-info').text('保存しました');
        $('#enquete-submit-info').fadeIn(500);
    })
    .fail(partake.defaultFailHandler);
});
</script>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
