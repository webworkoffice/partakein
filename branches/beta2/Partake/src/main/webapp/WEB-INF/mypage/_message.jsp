<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="static in.partake.view.util.Helper.h" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);
%>

<div id="received-whole">
<table class="table table-striped">
    <colgroup>
        <col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
    </colgroup>
    <thead>
        <tr><th>&nbsp;</th><th>送信者</th><th>タイトル</th><th>送信日時</th></tr>
    </thead>
    <tbody id="received-tbody">
    </tbody>
</table>
</div>
<div id="received-none">
    <p>イベントがありません。</p>
</div>

<div id="received-pagination" class="pagination pagination-centered"></div>


<script>
(function() {
    var ident = 'received';

    function createTable(nthPage, eventStatuses) {
        if (!eventStatuses || !eventStatuses.length) {
            $('#' + ident + '-none').show();
            return;
        }

        $('#' + ident + '-none').hide();
        var tbody = $('#' + ident + '-tbody');
        tbody.empty();

        for (var i = 0; i < eventStatuses.length; ++i) {
            var eventStatus = eventStatuses[i];
            var event = eventStatus.event;
            var tr = $('<tr></tr>');

            if (event.isPrivate)
                $('<td><img src="/images/private.png" title="非公開イベント" /></td>').appendTo(tr);
            else
                $('<td>&nbsp;</td>').appendTo(tr);

            {
                var td = $('<td></td>')
                var a = $('<a></a>');
                a.attr('href', "/events/" + event.id);
                a.text(event.title);
                a.appendTo(td);
                td.appendTo(tr);
            }

            {
                var td = $('<td></td>');
                // var date = new Date();
                // date.setTime(event.beginDate);
                td.text(event.beginDate);
                td.appendTo(tr);
            }

            {
                var td = $('<td></td>');
                var numParticipants = eventStatus.numEnrolledUsers;
                if (eventStatus.isBeforeDeadline)
                    numParticipants += eventStatus.numReservedUsers;

                var str = numParticipants + "/" + event.capacity;

                td.text(str);
                td.appendTo(tr);
            }

            tr.appendTo(tbody);
        }
    }

    function update(nthPage) {
        partake.account.getMessages((nthPage - 1) * 10, 10)
        .done(function (json) {
            createTable(nthPage, json.eventStatuses);
            var lst = partakeUI.pagination($('#' + ident + '-pagination'), nthPage, json.totalMessagesCount, 10);
            for (var i = 0; i < lst.length; ++i) {
                lst[i].anchor.click((function(i) {
                    return function() { update(lst[i].pageNum); };
                })(i));
            }
        })
        .fail(partake.defaultFailHandler);
    }

    update(1);
})();

</script>
