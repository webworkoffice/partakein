<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);

    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null)
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL);

    EventEx event = action.getEvent();
%>

<%-- 参加登録フォーム --%>
<div id="event-enroll-dialog" class="modal" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>イベントに<strong>参加</strong>しようとしています</h3>
    </div>
    <div class="modal-body"><form>
        <div id="enrollment-explanation" style="display: block">
            <h3>参加する</h3>
            <ul>
                <li>開始時刻の２４時間前に<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
                <li>リマインダーはマイページで受信拒否が設定できます。</li>
            </ul>
        </div>

        <div id="reservation-explanation" style="display: none">
            <h3>仮参加する</h3>
            <ul>
                <li><strong>仮参加を放置した場合、キャンセル</strong>になってしまいます。</li>
                <li>参加を確定させるためには、締切り(設定されていない場合は開始３時間前)までに<strong>改めて参加の申込み</strong>をしてください。</li>
                <li>締切りの24,12時間前になると<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
                <li>リマインダーは右上のマイページで受信拒否が設定できます。</li>
            </ul>
        </div>

        <div class="controls">
            <label class="checkbox">
                <input type="checkbox" id="reservation-checkbox" name="reservation-checkbox">仮参加にする [<a id="reservation-help">?</a>]
            </label>
        </div>

        <label class="control-label" for="event-enroll-dialog-comment">参加コメント</label>
        <textarea id="event-enroll-dialog-comment" name="comment" class="span7" rows="4">よろしくお願いします。</textarea>
    </form></div>
    <div class="modal-footer spinner-container">
        <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
        <a href="#" id="event-enroll-dialog-submit" class="btn btn-danger">参加</a>
    </div>
    <script>
    $('#reservation-help').popover({
        title: '仮参加',
        content: '再び参加登録しなければ自動的にキャンセルになる登録です。行きたいけど行けないかも……' +
            'というときには仮参加として登録すると、主催者はその事実を把握できるようになり便利です。'
    });
    </script>
    <script>
        function onChangeReservationExplanation() {
            if ($('#reservation-checkbox').is(':checked')) {
                $('#enrollment-explanation').hide();
                $('#reservation-explanation').show();
                $('#event-enroll-dialog-submit').text('仮参加');
                $('#event-enroll-dialog-submit').addClass('btn-info');
                $('#event-enroll-dialog-submit').removeClass('btn-danger');
            } else {
                $('#enrollment-explanation').show();
                $('#reservation-explanation').hide();
                $('#event-enroll-dialog-submit').text('参加');
                $('#event-enroll-dialog-submit').removeClass('btn-info');
                $('#event-enroll-dialog-submit').addClass('btn-danger');
            }
        }
        $('#reservation-checkbox').change(onChangeReservationExplanation);
        onChangeReservationExplanation();
    </script>
    <script>
        $('#event-enroll-dialog-submit').click(function() {
            var spinner = partakeUI.spinner(document.getElementById('event-enroll-dialog-submit'));
            var eventId = '<%= h(event.getId()) %>';
            var comment = $('#event-enroll-dialog-comment').val();

            var status = 'enroll';
            if ($('#reservation-checkbox').is(':checked')) {
                status = 'reserve';
            }

            spinner.show();
            $('#event-enroll-dialog-submit').attr('disabled');

            partake.event.enroll(eventId, status, comment)
            .always(function (xhr) {
                spinner.hide();
                $('#event-enroll-dialog-submit').removeAttr('disabled');
            })
            .done(function (json) {
                location.reload();
            })
            .fail(partake.defaultFailHandler);
        });
    </script>
</div>
