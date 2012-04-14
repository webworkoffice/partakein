<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="java.util.Date"%>
<%@page import="in.partake.base.TimeUtil"%>
<%@page import="in.partake.controller.action.AbstractPartakeAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.KeyValuePair"%>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);
    EventEx event = action.getEvent();
    assert event != null;
%>

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
        <input type="text" name="editors" class="span7" value="<%= h(event.getManagerScreenNames()) %>"/>
        <p class="help-block">自分以外にも編集者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。編集者はイベント削除以外のことを行うことが出来ます。</p>
        <p class="help-block">例： user1, user2, user3</p>
    </div>
</div>
