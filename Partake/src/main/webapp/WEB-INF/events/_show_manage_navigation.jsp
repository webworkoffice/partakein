<%@page import="in.partake.model.dto.auxiliary.NotificationType"%>
<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.EventCommentEx"%>
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
    List<EventCommentEx> comments = action.getComments();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
%>

<div class="subnav subnav-fixed"><div class="container">
    <ul class="nav nav-pills nav-stacked-if-phone">
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><img class="hidden-phone-inline" src="/images/gear.png"/> イベント編集<b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="/events/edit/basic/<%= h(event.getId()) %>">イベントを編集</a></li>
                <li><a href="/events/edit/ticket/<%= h(event.getId()) %>">チケットを編集</a></li>
                <li><a href="/events/edit/enquete/<%= h(event.getId()) %>">アンケートを編集</a></li>
                <li><a href="/events/edit/privacy/<%= h(event.getId()) %>">プライバシーを編集</a></li>
                <li class="divider"></li>
                <li><a id="copy-event">コピーして新しいイベントを作成</a></li>
                <% if (EventRemovePermission.check(event, user)) { %>
                <li><a data-toggle="modal" href="#event-delete-dialog">イベントを削除</a></li>
                <% } else { %>
                <% } %>
            </ul>
            <script>
            $('#copy-event').click(function() {
                var eventId = '<%= h(event.getId()) %>';
                partake.event.copy(eventId)
                .done(function (json) {
                    location.href = "/events/edit/basic/" + json.eventId;
                })
                .fail(partake.defaultFailHandler);
            })
            </script>
        </li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"><img class="hidden-phone-inline" src="/images/momonga1.png"/> 参加者管理<b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="/events/participants/<%= h(event.getId()) %>">参加者一覧を表示</a></li>
                <li><a href="/events/participants/csv/<%= h(event.getId()) %>">参加者リストを CSV (UTF-8) で出力</a></li>
            </ul>
        </li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"><b><img class="hidden-phone-inline" src="/images/mail.png"/></b> メッセージ管理<b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a data-toggle="modal" href="#message-send-dialog">参加者へメッセージ送信</a></li>
                <%-- <li><a href="#">メッセージ送信状況</a></li> --%>
                <li class="divider"></li>
                <li><a data-toggle="modal" href="#event-reminder-dialog">リマインダーなどの送付状況</a></li>
            </ul>
        </li>

        <% if (event.isDraft()) { %>
        <li class="pull-right">
            <a data-toggle="modal" href='#event-publish-dialog'>この内容でイベントを公開</a>
        </li>
        <% } %>
    </ul>
</div></div>

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

<div id="event-publish-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>イベントを公開します</h3>
    </div>
    <div class="modal-body">
        <p>イベントを公開することで、次のことができるようになります。</p>
        <ul>
            <li><strong>ユーザーが参加登録できる</strong>ようになります。</li>
            <li>プライベートイベントでなければ、<strong>検索結果に現れる</strong>ようになります。</li>
        </ul>
        <p>逆に、次のことができなくなります。</p>
        <ul>
            <li>イベントを再び非公開にすることは出来ません（プライベートイベントに変更することは可能です）</li>
            <li>アンケートの編集ができなくなります。</li>
            <li>（既に参加者がいる）チケットは削除できなくなります。</li>
        </ul>
    </div>
    <div class="modal-footer spinner-container">
        <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
        <a href="#" id="event-publish-button" class="btn btn-danger">公開する</a>
    </div>
    <script>
    $('#event-publish-button').click(function() {
        var eventId = '<%= h(event.getId()) %>';
        partake.event.publish(eventId)
        .done(function(json) {
            $('#event-publish-dialog').modal('hide');
            location.href = '/events/' + eventId;
        })
        .fail(partake.defaultFailHandler);
    });
    </script>
</div>

<div id="message-send-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>参加者にメッセージを送ります</h3>
    </div>
    <div class="modal-body">
        <%-- TODO: maxCodePointsOfMessage should not be null. --%>
        <p>参加者に twitter 経由でメッセージを送ることができます。題名は 100 文字以下、メッセージは 1000 文字以内で記述してください。最大で１時間３回１日５回まで送ることができます。</p>
        <form>
            <label for="message-send-dialog-subject">題名</label>
            <input id="message-send-dialog-subject" type="text" name="title" />
            <label for="message-send-dialog-body">本文</label>
            <textarea id="message-send-dialog-body" name="message" class="span14" rows="4"></textarea>
        </form>
        <p>残り <span id="message-length">1000</span> 文字</p>
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
            var left = 1000 - textarea.val().length;

            messageSpan.text(left).css('color', left > 20 ? '#000' : '#f00');
            if (left < 0)
                submitButton.attr('disabled', '');
            else
                submitButton.removeAttr('disabled');
        }
        $('#message-send-dialog-body').keydown(onMessageChange).keyup(onMessageChange);
    </script>
</div>

<div id="event-reminder-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>リマインダーなどの送付状況</h3>
    </div>
    <div class="modal-body">
        <div id="event-reminder-dialog-spinner"><img src="/img/spinner.gif"></div>
        <ul id="event-reminder-list">
        </ul>
    </div>
    <div class="modal-footer spinner-container">
        <a href="#" data-dismiss="modal" class="btn btn-primary">OK</a>
    </div>
    <script>
    var isEventReminderRetrievalInvoked = false;
    $('#event-reminder-dialog').on('show', function() {
        var eventId = '<%= h(event.getId()) %>';
        if (isEventReminderRetrievalInvoked)
            return;
        isEventReminderRetrievalInvoked = true;
        $('#event-reminder-dialog-spinner').show();
        $('#event-reminder-list').hide();
        partake.event.getNotifications(eventId, 0, 10)
        .done(function(json) {
            $('#event-reminder-list').empty();
            if (json.notifications.length == 0) {
                $('#event-reminder-list').append($('<li>このイベントに関する通知はまだ何も送付されていません。</li>'));
            } else {
                for (var i = 0; i < json.notifications.length; ++i) {
                    var type = null;
                    switch (json.notifications[i].notificationType) {
                    case '<%= NotificationType.EVENT_ONEDAY_BEFORE_REMINDER.toString() %>':
                        type = '一日前通知';
                        break;
                    case '<%= NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION.toString() %>':
                        type = '仮参加者に対する一日前の催促';
                        break;
                    case '<%= NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION.toString() %>':
                        type = '仮参加者に対する半日前の催促';
                        break;
                    case '<%= NotificationType.BECAME_TO_BE_ENROLLED.toString() %>':
                        type = '繰り上がり';
                        break;
                    case '<%= NotificationType.BECAME_TO_BE_CANCELLED.toString() %>':
                        type = '繰り下がり';
                        break;
                    }

                    if (type) {
                        var li = $('<li></li>');
                        var span1 = $('<span></span>').text(type);
                        var span2 = $('<span></span>').text(new Date(json.notifications[i].createdAt));
                        li.append(span1).append(span2);
                        $('#event-reminder-list').append(li);
                    }
                }
            }
            $('#event-reminder-dialog-spinner').hide();
            $('#event-reminder-list').show();
        })
        .fail(partake.defaultFailHanlder)
        .fail(function() {
            isEventReminderRetrievalInvoked = false;
        });
    });
    </script>
</div>
