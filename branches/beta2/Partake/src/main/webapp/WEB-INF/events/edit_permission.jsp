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
    <title>アクセス権とプライバシー</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true" />

<div class="page-header">
    <h1>アクセス権とプライバシー</h1>
</div>

<form id="event-form" class="form-horizontal">
    <div id="secret" class="control-group">
        <label for="secret" class="control-label">非公開設定</label>
        <div class="controls">
            <label class="checkbox"><input type="checkbox" id="secret-checkbox" name="secret" <%= event.isPrivate() ? "checked" : "" %> />非公開にする</label>
            <p class="help-block">非公開設定にすると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</p>
        </div>
    </div>
    <div id="passcode" class="control-group">
        <label for="passcode" class="control-label">パスコード</label>
        <div class="controls">
            <input type="text" id="passcode-text" name="passcode" value="<%= h(event.getPasscode()) %>"/>
        </div>
    </div>
    <script>
    function checkPasscode() {
        if ($('#secret-checkbox').is(':checked')) {
            $('#passcode-text').removeAttr('disabled');
        } else {
            $('#passcode-text').attr('disabled', '');
        }
    }
    checkPasscode();
    $('#secret-checkbox').change(checkPasscode);
    </script>

    <div id="editors" class="control-group">
        <label for="editors" class="control-label">編集者</label>
        <div class="controls">
            <input type="text" name="editors" class="span7" value="<%= event.getEditors() %>"/>
            <p class="help-block">自分以外にも編集者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。編集者はイベント削除以外のことを行うことが出来ます。</p>
            <p class="help-block">例： user1, user2, user3</p>
        </div>
    </div>

    <div class="form-actions">
        <input type="button" class="btn btn-primary" value="保存">
    </div>
</form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
