<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    List<CommentEx> comments = action.getComments();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
%>

<div class="row" style="background-color: #FFD">
    <div class="span9">
        <ul class="nav nav-pills nav-stacked-if-phone subnav">
            <li class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><img class="hidden-phone-inline" src="/images/gear.png"/> イベント編集<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <%-- <li><a href="#">イベント閲覧情報</a></li>
                    <li class="divider"></li> --%>
                    <li><a href="/events/edit/<%= h(event.getId()) %>">イベントを編集</a></li>
                    <li><a href="/events/new?eventId=<%= h(event.getId()) %>">コピーして新しいイベントを作成</a></li>
                    <% if (EventRemovePermission.check(event, user)) { %>
                    <li><a data-toggle="modal" href="#event-delete-dialog">イベントを削除</a></li>
                    <% } else { %>
                    <% } %>
                </ul>
            </li>
            <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"><img class="hidden-phone-inline" src="/images/momonga1.png"/> 参加者管理<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a href="/events/printParticipants/<%= h(event.getId()) %>">参加者リストを出力</a></li>
                    <li><a href="/events/showParticipants/<%= h(event.getId()) %>">参加ステータスを編集</a></li>
                </ul>
            </li>
            <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"><b><img class="hidden-phone-inline" src="/images/mail.png"/></b> メッセージ管理<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    <li><a data-toggle="modal" href="#message-send-dialog">参加者へメッセージ送信</a></li>
                    <%-- <li><a href="#">メッセージ送信状況</a></li> --%>
                    <li class="divider"></li>
                    <li><a data-toggle="modal" href="#event-reminder-dialog">リマインダー送付状況</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>

<div id="event-delete-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>イベントを削除しようとしています</h3>
    </div>
    <div class="modal-body">
        <p>イベントを削除しようとしています。<strong>この操作は取り消せません。</strong></p>
        <p>本当に削除しますか？</p>
    </div>
    <div class="modal-footer spinner-container">
        <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
        <a href="#" id="event-delete-dialog-submit-button" class="btn btn-danger">削除</a>
    </div>
    <script>
        function removeEvent() {
            var eventId = '<%= h(event.getId()) %>';
            var spinner = partakeUI.spinner(document.getElementById('event-delete-dialog-submit-button'));
            var button = $('#event-delete-dialog-submit-button');

            spinner.show();
            button.attr('disabled', '');

            partake.event.remove(eventId)
            .always(function () {
                spinner.hide();
                button.removeAttr('disabled');
            })
            .done(function (json) {
                location.href = "/";
            })
            .fail(function (xhr) {
                try {
                    var json = $.parseJSON(xhr.responseText);
                    alert(json.reason);
                } catch (e) {
                    alert('レスポンスが JSON 形式ではありません。');
                }
            });
        }
        $('#event-delete-dialog-submit-button').click(removeEvent);
    </script>
</div>

<div id="message-send-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>参加者にメッセージを送ります</h3>
    </div>
    <div class="modal-body">
        <%-- TODO: maxCodePointsOfMessage should not be null. --%>
        <p>参加者に twitter 経由でメッセージを送ることができます。メッセージは、長くとも 500 文字以内で記述してください。最大で１時間３回１日５回まで送ることができます。</p>
        <form>
            <label>題名<input id="message-send-dialog-subject" type="text" name="title" /></label>
            <label>本文<textarea id="message-send-dialog-body" name="message" class="span7" rows="4"></textarea></label>
        </form>
        <p>残り <span id="message-length">500</span> 文字</p>
    </div>
    <div class="modal-footer spinner-container">
        <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
        <a href="#" id="message-send-dialog-submit-button" class="btn btn-danger">送信</a>
    </div>
    <script>
        function sendMessage() {
            var eventId = '<%= h(event.getId()) %>';
            var subject = $('#message-send-dialog-subject').val();
            var body = $('#message-send-dialog-body').val();

            var spinner = partakeUI.spinner(document.getElementById('message-send-dialog-submit-button'));
            var button = $('#message-send-dialog-submit-button');

            spinner.show();
            button.attr('disabled', '');

            partake.message.sendMessage(eventId, subject, body)
            .always(function () {
                spinner.hide();
                button.removeAttr('disabled');
            })
            .done(function (json) {
                $('#message-send-dialog').modal('hide');
            })
            .fail(partake.defaultFailHandler);
        }
        $('#message-send-dialog-submit-button').click(sendMessage);

        function onMessageChange() {
            var textarea = $('#message-send-dialog-body');
            var submitButton = $('#message-send-dialog-submit-button');
            var messageSpan = $('#message-length');
            var left = 500 - codePointCount(textarea.val());

            messageSpan.text(left).css('color', left > 20 ? '#000' : '#f00');
            if (left < 0)
                submitButton.attr('disabled', '');
            else
                submitButton.removeAttr('disabled');
        }
        $('#message-send-dialog-textarea').keydown(onMessageChange).keyup(onMessageChange);
    </script>
</div>

