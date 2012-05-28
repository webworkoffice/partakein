<%@page import="org.apache.commons.lang.StringUtils"%>
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
<body class="with-sub-nav">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true">
    <jsp:param name="NAVIGATION" value="privacy" />
</jsp:include>

<div class="page-header">
    <h1>プライバシー</h1>
</div>

<form id="event-form" class="form-horizontal" onsubmit="return false;">
    <div id="passcode" class="control-group">
        <label for="passcode-checkbox" class="control-label">パスコード設定</label>
        <div class="controls">
            <div class="input-prepend">
                <div class="add-on">
                    <input id="passcode-checkbox" type="checkbox" name="passcodeCheckbox" <%= StringUtils.isBlank(event.getPasscode()) ? "" : "checked" %> />
                </div><input type="text" id="passcode-input" name="passcode" class="span8"
                    placeholder="パスコードを設定"
                    value="<%= h(event.getPasscode() != null ? event.getPasscode() : "") %>" />
                <span id="passcode-help" class="help-inline"></span>
            </div>
            <p class="help-block">パスコードを設定すると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</p>
        </div>
    </div>
    <script>
    function checkPasscode() {
        if ($('#passcode-checkbox').is(':checked')) {
            $('#passcode-input').removeAttr('disabled');
        } else {
            $('#passcode-input').attr('disabled', '');
        }
    }
    checkPasscode();
    $('#passcode-checkbox').change(checkPasscode);
    </script>

    <div class="form-actions">
        <input id="passcode-submit" type="button" class="btn btn-primary" value="保存">
        <span id="submit-info" class="text-info"></span>
    </div>
    <script>
    $('#passcode-submit').click(function(e) {
        var eventId = '<%= event.getId() %>';

        var passcode = '';
        if ($('#passcode-checkbox').is(':checked')) {
            passcode = $('#passcode-input').val();
            if (passcode == '') {
                $('#passcode').addClass('error');
                $('#passcode-help').text('パスコードが設定されていません。');
                return;
            }
        } else {
            passcode = '';
        }
        $('#passcode').removeClass('error');
        $('#passcode-help').empty();

        partake.event.modify(eventId, { passcode: passcode })
        .done(function (json) {
            $('#submit-info').hide();
            $('#submit-info').text('保存しました');
            $('#submit-info').fadeIn(500);
        })
        .fail(partake.defaultFailHandler);
    });
    </script>
</form>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
