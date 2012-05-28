<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="static in.partake.view.util.Helper.h" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);

    String ident = (String) request.getParameter("ident");
%>

<div id="<%= ident %>-whole">
<table class="table table-striped">
    <thead>
        <tr><th>&nbsp;</th><th>イベントタイトル</th><th>開催日</th><th>チケット名</th><th>参加ステータス</th></tr>
    </thead>
    <tbody id="<%= ident %>-tbody">
    </tbody>
</table>
</div>
<div id="<%= ident %>-none">
    <p>イベントがありません。</p>
</div>

<div id="<%= ident %>-pagination" class="pagination pagination-centered"></div>


<script>
(function() {
    var ident = '<%= h(ident) %>';

    function createTable(nthPage, ticketStatuses) {
        if (!ticketStatuses || !ticketStatuses.length) {
            $('#' + ident + '-none').show();
            return;
        }

        $('#' + ident + '-none').hide();
        var tbody = $('#' + ident + '-tbody');
        tbody.empty();

        for (var i = 0; i < ticketStatuses.length; ++i) {
            var ticketStatus = ticketStatuses[i];
            var ticket = ticketStatus.ticket;
            var event = ticketStatus.event;
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
                td.text(event.beginDateText);
                td.appendTo(tr);
            }

            {
                var td = $('<td></td>');
                td.text(ticket.name);
                td.appendTo(tr);
            }

            {
                var td = $('<td></td>');
                switch (ticketStatus.status) {
                case 'enrolled':
                    td.text('参加'); break;
                case 'enrolledOnWaitingList':
                    td.text('参加 (補欠)'); break;
                case 'reserved':
                    td.text('仮参加'); break;
                case 'reservedOnWaitingList':
                    td.text('仮参加 (補欠)'); break;
                case 'cancelled':
                    td.text('キャンセル済'); break;
                default:
                    td.text('不明'); break;
                }
                td.appendTo(tr);
            }

            tr.appendTo(tbody);
        }
    }

    function update(nthPage) {
        partake.account.getTickets((nthPage - 1) * 10, 10)
        .done(function (json) {
            createTable(nthPage, json.ticketStatuses);
            var lst = partakeUI.pagination($('#' + ident + '-pagination'), nthPage, json.totalTicketCount, 10);
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
