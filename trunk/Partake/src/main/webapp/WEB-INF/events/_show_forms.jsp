<%@page import="in.partake.model.dto.auxiliary.TicketReservationEnd"%>
<%@page import="in.partake.base.TimeUtil"%>
<%@page import="in.partake.model.dto.EventTicket"%>
<%@page import="in.partake.model.dto.UserTicket"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.dto.auxiliary.EnqueteQuestion"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);

    EventEx event = action.getEvent();

    List<String> enqueteIds = new ArrayList<String>();
    List<String> enqueteTypes = new ArrayList<String>();
    if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) {
        for (EnqueteQuestion qs : event.getEnquetes()) {
            enqueteIds.add(qs.getId().toString());
            enqueteTypes.add(qs.getAnswerType().toString());
        }
    }

    JSONObject userTicketMap = new JSONObject();
    for (Entry<UUID, UserTicket> entry : action.getUserTicketMap().entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null)
            userTicketMap.put(entry.getKey().toString(), JSONObject.fromObject(entry.getValue().toJSON()));
    }

    JSONObject reservationForbiddenMap = new JSONObject();
    JSONObject reservationTimeoverMap = new JSONObject();
    for (EventTicket ticket : event.getTikcets()) {
        reservationForbiddenMap.put(ticket.getId(), TicketReservationEnd.TILL_NONE.equals(ticket.getReservationEnd()));
        boolean b = TimeUtil.getCurrentDateTime().isAfter(ticket.acceptsReservationTill(event));
        reservationTimeoverMap.put(ticket.getId().toString(), b);
    }
%>

<script>
// ----- Enrollment Data
var enrollments = <%= userTicketMap.toString() %>;

// ----- Enquete data
var enqueteIds = <%= JSONArray.fromObject(enqueteIds).toString() %>;
var enqueteTypes = <%= JSONArray.fromObject(enqueteTypes).toString() %>;

// ----- Reservation Acceptance
var reservationForbiddenMap = <%=reservationForbiddenMap.toString() %>;
var reservationTimeoverMap = <%= reservationTimeoverMap.toString() %>;

$('.button-apply-ticket').click(function(e) {
    // 1. Set initial data for the dialog.
    var ticketId = $(this).data('ticket');
    $('#event-enroll-dialog').data('ticket', ticketId);
    onShowingApplicationDialog(ticketId);

    // 2. Show the dialog.
    $('#event-enroll-dialog').modal('show');
});
</script>

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
                <li>参加を確定させるためには、締切りまでに<strong>改めて参加の申込み</strong>をしてください。</li>
                <li>締切りの24,12時間前になると<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
                <li>リマインダーはマイページで受信拒否が設定できます。</li>
            </ul>
        </div>

        <div class="controls">
            <label class="checkbox">
                <input type="checkbox" id="reservation-checkbox" name="reservation-checkbox">仮参加にする [<a id="reservation-help">?</a>]
                <span id="reservation-timeover" class="help-block">このチケットの仮参加締め切りを過ぎています。</span>
                <span id="reservation-forbidden" class="help-block">このチケットでは仮参加登録ができません。</span>
            </label>
        </div>

        <label class="control-label" for="event-enroll-dialog-comment">参加コメント</label>
        <textarea id="event-enroll-dialog-comment" name="comment" class="span14" rows="4">よろしくお願いします。</textarea>

        <% if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) { %>
            <h3>アンケート</h3>
            <p>このイベントにはアンケートが設定されています。</p>

            <% for (int i = 0; i < event.getEnquetes().size(); ++i) {
                EnqueteQuestion question = event.getEnquetes().get(i); %>
            <fieldset>
            <label><%= h(question.getText()) %></label>
            <% switch (question.getAnswerType()) {
            case TEXT:
                %><input type="text" class="span14" name="enqueteAnswer-<%= h(question.getId().toString()) %>"><%
                        break;
            case TEXTAREA:
                %><textarea name="enqueteAnswer-<%= h(question.getId().toString()) %>" class="span14" rows="4"></textarea><%
                break;
            case CHECKBOX:
                for (String option : question.getOptions()) {
                    %><label class="checkbox"><input type="checkbox" name="enqueteAnswer-<%= h(question.getId().toString()) %>" value="<%= h(option) %>"><%= h(option) %></label><%
                }
                break;
            case RADIOBUTTON:
                for (String option : question.getOptions()) {
                    %><label class="radio"><input type="radio" name="enqueteAnswer-<%= h(question.getId().toString()) %>" value="<%= h(option) %>"><%= h(option) %></label><%
                }
                break;
            }
            %>
            </fieldset>
            <% } %>
        <% } %>
    </form></div>
    <div class="modal-footer spinner-container">
        <a href="#" id="event-enroll-dialog-decline" class="btn btn-danger pull-left">辞退する</a>
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
        function onShowingApplicationDialog(ticketId) {
            var reservationForbiddenMap = <%=reservationForbiddenMap.toString() %>;
            var reservationTimeoverMap = <%= reservationTimeoverMap.toString() %>;

            var reserveable = false;
            if (reservationForbiddenMap[ticketId]) {
                $('#reservation-timeover').hide();
                $('#reservation-forbidden').show();
                $('#reservation-checkbox').attr('disabled', 'true');
            } else if (reservationTimeoverMap[ticketId]) {
                $('#reservation-timeover').show();
                $('#reservation-forbidden').show();
                $('#reservation-checkbox').attr('disabled', 'true');
            } else {
                $('#reservation-timeover').hide();
                $('#reservation-forbidden').hide();
                $('#reservation-checkbox').removeAttr('disabled');
                reserveable = true;
            }

            var userTicket = enrollments[ticketId]; // userTicket may be null.
            if (userTicket && userTicket.status.toLowerCase() == "reserved" && reserveable)
                $('#reservation-checkbox').attr('checked', 'true');
            else
                $('#reservation-checkbox').removeAttr('checked');
            $('#event-enroll-dialog-comment').val(userTicket ? userTicket.comment : 'よろしくお願いします。');

            for (var i = 0; i < enqueteIds.length; ++i) {
                var inputOrTextAreas = $('[name="enqueteAnswer-' + enqueteIds[i] + '"]');
                if (enqueteTypes[i] == 'radiobutton' || enqueteTypes[i] == 'checkbox')
                    inputOrTextAreas.val(userTicket && userTicket.enqueteAnswers ? userTicket.enqueteAnswers[enqueteIds[i]] : []);
                else
                    inputOrTextAreas.val(userTicket && userTicket.enqueteAnswers ? userTicket.enqueteAnswers[enqueteIds[i]] : []);
            }

            if (userTicket && (userTicket.status.toLowerCase() == 'enrolled' || userTicket.status.toLowerCase() == 'reserved'))
                $('#event-enroll-dialog-decline').show();
            else
                $('#event-enroll-dialog-decline').hide();

            onChangeReservationExplanation();
        }

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

        function applyTicket(status) {
            var spinner = partakeUI.spinner(document.getElementById('event-enroll-dialog-submit'));
            var ticketId = $('#event-enroll-dialog').data('ticket');
            var comment = $('#event-enroll-dialog-comment').val();
            var enqueteAnswers = {};

            for (var i = 0; i < enqueteIds.length; ++i) {
                var inputOrTextAreas = $('[name="enqueteAnswer-' + enqueteIds[i] + '"]');
                if (enqueteTypes[i] == 'radiobutton' || enqueteTypes[i] == 'checkbox')
                    inputOrTextAreas = inputOrTextAreas.filter(function() { return $(this).is(':checked') || $(this).is(':selected'); });

                var answers = [];
                inputOrTextAreas.each(function() {
                    answers.push($(this).val());
                });

                enqueteAnswers[enqueteIds[i]] = answers;
            }

            spinner.show();
            $('#event-enroll-dialog-submit').attr('disabled');

            partake.ticket.apply(ticketId, status, comment, enqueteAnswers)
            .always(function (xhr) {
                spinner.hide();
                $('#event-enroll-dialog-submit').removeAttr('disabled');
            })
            .done(function (json) {
                location.reload();
            })
            .fail(partake.defaultFailHandler);
        }

        $('#event-enroll-dialog-decline').click(function() {
            applyTicket('cancel');
        });

        $('#event-enroll-dialog-submit').click(function() {
            var status = 'enroll';
            if ($('#reservation-checkbox').is(':checked')) {
                status = 'reserve';
            }

            applyTicket(status);
        });
    </script>
</div>
