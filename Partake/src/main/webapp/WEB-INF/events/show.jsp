<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.controller.base.permission.UserPermission"%>
<%@page import="in.partake.model.dto.Enrollment"%>
<%@page import="in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.model.dto.Comment"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.base.Util"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.resource.Constants"%>

<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="static in.partake.view.util.Helper.cleanupHTML"%>
<%@page import="static in.partake.view.util.Helper.escapeTwitterResponse"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.EventRelationEx"%>

<%
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	ParticipationStatus status = (ParticipationStatus)request.getAttribute(Constants.ATTR_PARTICIPATION_STATUS);
	Boolean deadlineOver = (Boolean)request.getAttribute(Constants.ATTR_DEADLINE_OVER);
	EventReminder reminderStatus = (EventReminder) request.getAttribute(Constants.ATTR_REMINDER_STATUS);
	List<EventRelationEx> eventRelations = (List<EventRelationEx>) request.getAttribute(Constants.ATTR_EVENT_RELATIONS);
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null)
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL);
    Integer maxCodePointsOfMessage = (Integer) request.getAttribute(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE);
%>

<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<% if (event.getFeedId() != null) { %>
		<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="/feed/event/<%= event.getFeedId() %>" />
	<% } %>
	<% if (!StringUtils.isEmpty(event.getSummary())) { %>
		<meta name="description" content="<%= h(event.getSummary()) %>" />
	<% } else { %>
		<meta name="description" content="<%= h(Util.shorten(Util.removeTags(event.getDescription()), 128)) %>" />
	<% } %>
	<title><%= h(event.getTitle()) %> - [PARTAKE]</title>
<% if (event.getBackImageId() != null) { %>
	<style>
body {
	background-image: url("/images/<%= event.getBackImageId() %>");
	background-repeat: repeat;
}
	</style>
<% } %>

<script type="text/javascript">
  window.___gcfg = {lang: 'ja'};

  (function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/plusone.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(po, s);
  })();
</script>
</head>
<body class="event">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="event-body">

<div class="page-header">
	<h1>
    	<% if (event.isPrivate()) { %><img src="<%= request.getContextPath() %>/images/private.png" title="非公開イベント" /><% } %>
    	<%= h(event.getTitle()) %>
	</h1>
	<% if (!StringUtils.isEmpty(event.getSummary())) { %>
		<p><%= h(event.getSummary()) %></p>
	<% } %>
</div>

<div class="row clearfix">
	<!-- hatena -->
	<div class="pull-right">
	<div style="display:inline-block;">
		<a href="http://b.hatena.ne.jp/entry/" class="hatena-bookmark-button" data-hatena-bookmark-layout="standard" title="このエントリーをはてなブックマークに追加"><img src="http://b.st-hatena.com/images/entry-button/button-only.gif" alt="このエントリーをはてなブックマークに追加" width="20" height="20" style="border: none;" /></a>
		<script type="text/javascript" src="http://b.st-hatena.com/js/bookmark_button.js" charset="utf-8" async="async"></script>
	</div>
	
	<!-- facebook -->
	<iframe id="facebook-like-button" src="http://www.facebook.com/plugins/like.php?href=<%= h(Util.encodeURIComponent(event.getEventURL())) %>&amp;layout=button_count&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=21" scrolling="no" frameborder="0" allowTransparency="true" height="20" width="100"></iframe>

	<!--  twitter -->
	<div style="display:inline-block; width:105px" >
		<a href="http://twitter.com/share" class="twitter-share-button" data-count="horizontal" data-via="partakein" data-text="<%= h(event.getTitle())%> - [PARTAKE] <%= h(event.getHashTag()) %>" data-width="105px">Tweet</a>
		<script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>
	</div>

	<!-- +1 -->
	<div style="display:inline-block; width:70px;"><div class="g-plusone" data-size="medium" data-href="<%= h(event.getEventURL()) %>"></div></div>
	
	<!-- rss -->
	<% if (event.getFeedId() != null) { %>
	    <div style="display:inline-block;"><a href="/feed/event/<%= event.getFeedId() %>"><img src="<%= request.getContextPath() %>/images/feed-icon-15x15.png" /></a></div>
	<% } %>
	</div>
</div>

<div class="row">

<div class="span9">
	<% if (event.getForeImageId() != null) { %>
	<div class="event-image">
		<img id="event-image-image" src="/images/<%= event.getForeImageId() %>" />
	</div>
	<% } %>

	<div class="row">
		<div class="span6">
		<table class="table table-striped">
			<tr><th>日時</th><td><%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %></td></tr>
			<tr><th>申込締切</th><td><%= Helper.readableDate(event.getDeadline() == null ? event.getBeginDate() : event.getDeadline()) %></td></tr>
			<tr><th>カテゴリ</th><td><%= event.getCategory() != null ? EventCategory.getReadableCategoryName(event.getCategory()) : "-" %></td></tr>
			<tr><th>定員</th><td><%= event.getCapacity() != 0 ? String.valueOf(event.getCapacity()) : "-" %></td></tr>
			<tr><th>会場</th><td><%= h(StringUtils.isEmpty(event.getPlace()) ? "-" : event.getPlace()) %></td></tr>
			<tr><th>住所</th><td><%= h(StringUtils.isEmpty(event.getAddress()) ? "-" : event.getAddress()) %></td></tr>
			<% if (!StringUtils.isEmpty(event.getUrl())) { %>
				<tr><th>URL</th><td><a href="<%= h(event.getUrl()) %>"><%= h(event.getUrl()) %></a></td></tr>
			<% } %>
	        <tr><th>管理者</th>
	            <td><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
	                <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
	                    <%= escapeTwitterResponse(event.getOwner().getTwitterLinkage().getName()) %>
	                    (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
	                <% } else { %>
	                    <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
	                <% } %>
	        </a></td></tr>
	        <% if (!StringUtils.isEmpty(event.getHashTag())) { %>
	        	<tr><th>ハッシュタグ：</th><td><a href="http://twitter.com/#search?q=<%= Util.encodeURIComponent(event.getHashTag()) %>"><%= h(event.getHashTag()) %></a></td></tr>            
	        <% } %>
	        
	        <% String shortenURL = event.getShortenedURL(); %>
	        <tr><th>短縮 URL</th><td><a href="<%= h(shortenURL) %>"><%= h(shortenURL) %></a></td></tr>
	           
	        <% if (eventRelations != null && !eventRelations.isEmpty()) { %>
	        	<tr><th>関連イベント</th>
	           	<% for (EventRelationEx eventRelation : eventRelations) { %>
	               <td>
	                   <img src="<%= request.getContextPath() %>/images/mark.png" class="" alt="" />
	                   <a href="<%= h(eventRelation.getEvent().getEventURL()) %>"><%= h(eventRelation.getEvent().getTitle()) %></a>
	                   <p><% if (eventRelation.isRequired()) { %><img src="<%= request.getContextPath() %>/images/attention.png" alt="" /> この関連イベントへの参加が必須です<% } %>
	                      <% if (eventRelation.hasPriority()) { %><img src="<%= request.getContextPath() %>/images/star.png" alt="" /> 参加すると本イベントへ優先的に参加可<% } %>
	               	   </p>
	               </td>
	           <% } %>
	           </tr>
	        <% } %>
		</table>
		</div>
		
		<div class="span3">
		    <% if (!StringUtils.isEmpty(event.getAddress())) { %>
		    <div class="event-map"><a href="http://maps.google.co.jp/maps?q=<%= h(Util.encodeURIComponent(event.getAddress())) %>">
		        <img src="http://maps.google.co.jp/maps/api/staticmap?size=240x200&center=<%= h(Util.encodeURIComponent(event.getAddress())) %>&zoom=17&sensor=false" />
		    </a></div>
		    <% } %>
	    </div>		
	</div>
	
	<div class="event-description">
		<%= cleanupHTML(event.getDescription()) %>
	</div>
	
	<jsp:include page="/WEB-INF/events/_show_eventstream.jsp" flush="true" />	 
</div>

<jsp:include page="/WEB-INF/events/_show_sidebar.jsp" flush="true" />
 
</div><%-- end of .span9 --%>

</div><%-- end of event-body --%>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />

<script>
var message=$('textarea#send_message');
function handler(e){
	var left = <%= maxCodePointsOfMessage.intValue() %> - codePointCount(message.val());
	$('span#message_length').text(left).css('color', left > 20 ? '#000' : '#f00').parent().find('input[type=submit]').attr('disabled', left < 0 ? 'disabled' : '');
}
message.keydown(handler).keyup(handler);<%-- keydownだけではctrl-BS時に表示があわなくなる、keyupだけではBS長押し時に表示があわなくなる --%>
</script>


</body>
</html>