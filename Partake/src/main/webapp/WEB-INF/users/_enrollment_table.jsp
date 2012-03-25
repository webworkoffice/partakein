<%@page import="in.partake.controller.action.user.ShowAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="static in.partake.view.util.Helper.h" %>

<%
	ShowAction action = (ShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserEx user = action.getUser();

	String ident = (String) request.getParameter("ident");
%>

<div id="<%= ident %>-whole">
<table class="table table-striped">
    <colgroup>
    	<col class="span1" /><col class="span6" /><col class="span3" /><col class="span2" />
	</colgroup>
	<thead>
		<tr><th>&nbsp;</th><th>イベントタイトル</th><th>開催日</th><th>参加ステータス</th></tr>
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
	var userId = '<%= h(user.getId()) %>';
	
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
	       		td.text(event.beginDate);
	       		td.appendTo(tr);
	       	}
	    	
	       	{
	       		var td = $('<td></td>');
	       		switch (eventStatus.status) {
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
		partake.user.getEnrollments(userId, (nthPage - 1) * 10, 10)
		.done(function (json) {
		    createTable(nthPage, json.eventStatuses);
			var lst = partakeUI.pagination($('#' + ident + '-pagination'), nthPage, json.numTotalEvents, 10);
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