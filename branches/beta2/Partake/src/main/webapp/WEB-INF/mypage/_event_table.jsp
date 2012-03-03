<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);
	
	String ident = (String) request.getParameter("ident");
	String queryType = (String) request.getParameter("queryType");
	String finished = (String) request.getParameter("finished");
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

<div id="<%= ident %>-pagenation" class="pagination pagination-centered"></div>


<script>
(function() {
	var queryType = '<%= queryType %>';
	var finished = '<%= finished %>';
	var ident = '<%= ident %>';
	
	function createTable(nthPage, participations) {
		if (!participations || !participations.length) {
			$('#' + ident + '-none').show();
			return;
		}
		
		$('#' + ident + '-none').hide();
		var tbody = $('#' + ident + '-tbody');
		tbody.empty();
		
		for (var i = 0; i < participations.length; ++i) {
			var participation = participations[i];
			var event = participation.event;
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
	       		var str = participation.numEnrolledUsers + "/" + event.capacity;
	       		td.text(str);
	       		td.appendTo(tr);
	       	}
	       	
	       	tr.appendTo(tbody);
		}
	}
	
	function addPagenation(text, nthPage, ul, active) {
		if (nthPage < 0 || !nthPage) {
			if (active)
				$('<li class="active"><a>' + text + '</a></li>').appendTo(ul);
			else
				$('<li class="disabled"><a>' + text + '</a></li>').appendTo(ul);
			return;
		}
		
		var li = $('<li></li>');
		var a = $("<a>" + text + "</a>");
		a.click(function() { update(nthPage); });
		a.appendTo(li);
		li.appendTo(ul);
	}
	
	function createPagenation(nthPage, numEvents) {
		var maxPageNum = Math.ceil(numEvents / 10); 
		if (maxPageNum == 0)
			maxPageNum = 1;
		
		var pagenation = $('#' + ident + '-pagenation');
		pagenation.empty();
		
		var ul = $('<ul></ul>');
		
		// 常に 11 個にしたい
		var beginPage = -1, endPage = -1;
		if (maxPageNum <= 11) {
			beginPage = 1;
			endPage = maxPageNum;
		} else {
			beginPage = nthPage - 3;
			endPage = nthPage + 3;
			if (beginPage < 1) {
				endPage = endPage + (1 - beginPage);
				beginPage = 1;
			}
			if (maxPageNum < endPage) {
				beginPage = beginPage - (endPage - maxPageNum);
				endPage = maxPageNum;
			}
			
			if (beginPage == 1)
				endPage += 2;
			else if (beginPage == 2) {
				beginPage = 1;
				endPage += 1;
			}
			
			if (endPage == maxPageNum)
				beginPage -= 2;
			else if (endPage + 1 == maxPageNum) {
				beginPage -= 1;
				endPage = maxPageNum;
			}
		}
		
		if (nthPage == 1)
			addPagenation('«', 0, ul);
		else
			addPagenation('«', nthPage - 1, ul);

		if (beginPage != 1) {
			addPagenation(1, 1, ul);
			addPagenation('…', 0, ul);
		}
	
		for (var i = beginPage; i <= endPage; ++i)
			addPagenation(i, i == nthPage ? 0 : i, ul, i == nthPage);

		if (endPage != maxPageNum) {
			addPagenation('…', 0, ul);
			addPagenation(maxPageNum, maxPageNum, ul);
		}
		
		if (nthPage == maxPageNum)
			addPagenation('»', 0, ul);
		else
			addPagenation('»', nthPage + 1, ul);
		
		ul.appendTo(pagenation);
	}
	
	function update(nthPage) {
		partake.account.getEvents(queryType, finished, (nthPage - 1) * 10, 10)
		.done(function (json) {
			console.log(json.numEvents);
			createTable(nthPage, json.participations);
			createPagenation(nthPage, json.numEvents);
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
	
	update(1);
})();

</script>