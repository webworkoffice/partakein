<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="static in.partake.view.util.Helper.h" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);

    String ident = (String) request.getParameter("ident");
    String queryType = (String) request.getParameter("queryType");
%>

<div id="<%= ident %>-whole">
<table class="table table-striped">
    <colgroup>
        <col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
    </colgroup>
    <thead>
        <tr><th>&nbsp;</th><th>イベントタイトル</th><th>開催日</th><th>参加人数/定員</th></tr>
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
    var queryType = '<%= h(queryType) %>';
    var ident = '<%= h(ident) %>';

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

                var amountStr = eventStatus.isAmountInfinite ? '無制限' : eventStatus.amount;
                var str = numParticipants + "/" + amountStr;

                td.text(str);
                td.appendTo(tr);
            }

            tr.appendTo(tbody);
        }
    }

    function update(nthPage) {
        var SIZE_PER_PAGE = 30;
        partake.account.getEvents(queryType, (nthPage - 1) * SIZE_PER_PAGE, SIZE_PER_PAGE)
        .done(function (json) {
            createTable(nthPage, json.eventStatuses);
            var lst = partakeUI.pagination($('#' + ident + '-pagination'), nthPage, json.totalEventCount, SIZE_PER_PAGE);
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
