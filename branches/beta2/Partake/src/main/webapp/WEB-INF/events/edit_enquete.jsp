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

<style>
table .no-border td, table .no-border th {
    border-top: none;
}

.show-if-hover {
    display: none;
}
.show-if-hover-parent:hover .show-if-hover {
    display: block;
}
</style>

<div class="row">
    <div class="span-half">&nbsp;</div>
    <div class="span6">質問</div>
    <div class="span3">形式</div>
    <div class="span2-half">&nbsp;</div>
</div>

<script>
function show(id) {
    $('#' + id + '-body').show();
}
</script>

<div id="hogehoge" style="border-top: 1px solid;">
    <div id="hogehoge-head" class="row">
        <div class="span-half">&nbsp;</div>
        <div class="span6">質問文</div>
        <div class="span3">テキストボックス</div>
        <div class="span2">
            <input type="button" class="btn" value="編集" onclick="show('hogehoge');" />
            <input type="button" class="btn" value="削除" />
        </div>
    </div>
    <div id="hogehoge-body" style="display: none;">
        <form class="form-horizontal"><fieldset>
            <div class="control-group">
                <label class="control-label" for="input01">質問文</label>
                <div class="controls">
                    <input type="text" class="span7" id="input01">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="input01">回答形式</label>
                <div class="controls">
                    <select id="hogehoge-answertype">
                        <option value="text">テキスト</option>
                        <option value="datetime">日付</option>
                        <option value="checkbox">チェックボックス</option>
                        <option value="radio">ラジオボタン</option>
                    </select>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="input01">回答候補</label>
                <div class="controls">
                    <input type="text" /> <span class="help-inline"><a style="  font-size: 20px; font-weight: bold; line-height: 18px;">&times;</a> hoge</span>
                </div>
                <div class="controls">
                    <input type="text" /> <span class="help-inline">Something may have gone wrong</span>
                </div>
                <div class="controls">
                    <input type="text" /> <span class="help-inline">Something may have gone wrong</span>
                </div>
                <div class="controls">
                    <input type="button" value="項目を追加" />
                </div>
            </div>
        </fieldset></form>
        <script>
        $('#hogehoge-answertype').change(function () {

        });
        </script>
    </div>
</div>

<div class="row"><div class="span12" style="border-top: 1px solid;">
    <div class="span2 pull-right">
        <input type="button" class="btn btn-primary" value="新しい項目を追加">
    </div>
</div></div>
<div class="row"><div class="span12">
    <form class="form-horizontal"><fieldset>
        <div class="form-actions">
            <input type="button" class="btn btn-primary" value="保存">
        </div>
    </fieldset></form>
</div></div>


<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
