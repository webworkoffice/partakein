<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%@taglib prefix="s" uri="/struts-tags" %>

<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.model.dto.auxiliary.UserPermission"%>
<%@page import="in.partake.model.dto.Enrollment"%>
<%@page import="in.partake.service.UserService"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.model.dto.Comment"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.util.Util"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.resource.Constants"%>

<%@page import="static in.partake.view.Helper.h"%>
<%@page import="static in.partake.view.Helper.cleanupHTML"%>
<%@page import="static in.partake.view.Helper.escapeTwitterResponse"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.EventRelationEx"%>


<%
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	ParticipationStatus status = (ParticipationStatus)request.getAttribute(Constants.ATTR_PARTICIPATION_STATUS);
	Boolean deadlineOver = (Boolean)request.getAttribute(Constants.ATTR_DEADLINE_OVER);
	EventReminder reminderStatus = (EventReminder) request.getAttribute(Constants.ATTR_REMINDER_STATUS);
	List<EventRelationEx> eventRelations = (List<EventRelationEx>) request.getAttribute(Constants.ATTR_EVENT_RELATIONS);
%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<% if (event.getFeedId() != null) { %>
		<link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="/feed/event/<%= event.getFeedId() %>" />
	<% } %>
	<title><%= h(event.getTitle()) %> - [PARTAKE]</title>

<% if (event.getBackImageId() != null) { %>
<style>
body {
	background-image: url("<%= request.getContextPath()%>/events/images/<%= event.getBackImageId() %>");
	background-repeat: repeat;
}
</style>
<% } %>	
</head>
<body class="event">
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>
    <% if (event.isPrivate()) { %><img src="<%= request.getContextPath() %>/images/private.png"　title="非公開イベント" /><% } %>
    <%= h(event.getTitle()) %>
</h1>
<% if (!StringUtils.isEmpty(event.getSummary())) { %>
	<p class="summary"><%= h(event.getSummary()) %></p>
<% } %>

<div class="event-wrapper">
<% if (event.getFeedId() != null) { %>
    <p class="event-feed-rss cler"><a href="/feed/event/<%= event.getFeedId() %>"><img src="<%= request.getContextPath() %>/images/feed-icon-15x15.png" /></a></p>
<% } %>

<div class="event-content">

<% if (event.getForeImageId() != null) { %>
<div class="event-image">
	<img id="event-image-image" src="<%= request.getContextPath()%>/events/images/<%= event.getForeImageId() %>" />
</div>
<% } %>

<div class="event-information">
		<dl id="main-event">
			<dt>日時：</dt>
				<dd><%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %></dd>
			<dt>申込締切：</dt>
				<dd><%= Helper.readableDate(event.getDeadline() == null ? event.getBeginDate() : event.getDeadline()) %></dd>
			<dt>カテゴリ：</dt>
				<dd><%= event.getCategory() != null ? EventCategory.getReadableCategoryName(event.getCategory()) : "-" %></dd>
			<dt>定員：</dt>
				<dd><%= event.getCapacity() != 0 ? String.valueOf(event.getCapacity()) : "-" %></dd>
			<dt>会場：</dt>
				<dd><%= h(StringUtils.isEmpty(event.getPlace()) ? "-" : event.getPlace()) %></dd>
			<dt>住所：</dt>
				<dd><%= h(StringUtils.isEmpty(event.getAddress()) ? "-" : event.getAddress()) %></dd>
			<dt>URL：</dt>
				<dd><% if (!StringUtils.isEmpty(event.getUrl())) { %>
					<a href="<%= h(event.getUrl()) %>"><%= h(event.getUrl()) %></a>             
				<% } else { %>
				    -
				<% } %></dd>
	        <dt>管理者：</dt>
	            <dd><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
	                <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
	                    <%= escapeTwitterResponse(event.getOwner().getTwitterLinkage().getName()) %>
	                    (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
	                <% } else { %>
	                    <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
	                <% } %>
	                </a></dd>
	        <% if (!StringUtils.isEmpty(event.getHashTag())) { %>
	        <dt>ハッシュタグ：</dt>
	            <dd><a href="http://twitter.com/#search?q=<%= Util.encodeURIComponent(event.getHashTag()) %>"><%= h(event.getHashTag()) %></a></dd>
	        <% } %>         
	        <dt>このページへの<br>短縮 URL：</dt>
	           <% String shortenURL = event.getShortenedURL(); %>
	           <dd><a href="<%= h(shortenURL) %>"><%= h(shortenURL) %></a></dd>
	        <% if (eventRelations != null && !eventRelations.isEmpty()) { %>
	        <dt>関連イベント：</dt>
	           <% for (EventRelationEx eventRelation : eventRelations) { %>
	               <dd>
	                   <img src="<%= request.getContextPath() %>/images/mark.png" class=""　alt="" />
	                   <a href="<%= h(eventRelation.getEvent().getEventURL()) %>"><%= h(eventRelation.getEvent().getTitle()) %></a>
	                   <p><% if (eventRelation.isRequired()) { %><img src="<%= request.getContextPath() %>/images/attention.png" alt="" /> この関連イベントへの参加が必須です<% } %>
                       <% if (eventRelation.hasPriority()) { %><img src="<%= request.getContextPath() %>/images/star.png" alt="" /> 参加すると本イベントへ優先的に参加可<% } %>
	               </p>
	               </dd>
	           <% } %>
	        <% }%>
		</dl>
		
	    <% if (!StringUtils.isEmpty(event.getAddress())) { %>
	    <div class="event-map"><a href="http://maps.google.co.jp/maps?q=<%= h(Util.encodeURIComponent(event.getAddress())) %>">
	        <img src="http://maps.google.co.jp/maps/api/staticmap?size=200x200&center=<%= h(Util.encodeURIComponent(event.getAddress())) %>&zoom=17&sensor=false" />      
	    </a></div>
	    <% } %>
</div>

<div class="event-description">
	<%= cleanupHTML(event.getDescription()) %>
</div>

<jsp:include page="_show_eventstream.jsp" />

</div>

<div class="event-participation">

<%-- Owner はイベントを削除できる。Manager はイベントを編集することが出来る。 --%>
<% if (event.hasPermission(user, UserPermission.EVENT_EDIT)) { %>
<div class="event-owner-information rad">
	<h2><img src="<%= request.getContextPath() %>/images/gear.png"/>イベント管理</h2>
	<ul>
		<%-- <li><a href="#" onclick="document.questionnaireForm.submit();">アンケートを作成/編集</a></li> --%>
		<li><a href="#" onclick="document.eventEditForm.submit();">イベントを編集</a></li>
		<% if (event.hasPermission(user, UserPermission.EVENT_REMOVE)) { %>
		    <li><a id="open-event-delete-form" href="#">イベントを削除</a></li>
		<% } %>
	</ul>
	<h2><img src="<%= request.getContextPath() %>/images/momonga1.png"/>参加者管理</h2>
    <ul>
         <li><a id="open-message-form" href="#">参加者へメッセージを送信</a></li>
         <li><a href="<%= request.getContextPath() %>/events/showParticipants/<%= h(event.getId()) %>">参加者のステータスを編集</a></li>
         <li><a href="<%= request.getContextPath() %>/events/printParticipants/<%= h(event.getId()) %>">参加者リストを出力</a></li>
    </ul>

	<h2><img src="<%= request.getContextPath() %>/images/mail.png"/>リマインダー送付時刻</h2>
    <dl>
        <dt>締切24時間前(仮参加者向)</dt>
            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeDeadlineOneday()) %></dd>
        <dt>締切12時間前(仮参加者向)</dt>
            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeDeadlineHalfday()) %></dd>
        <dt>イベント１日前</dt>
            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeTheDay()) %></dd>
    </dl>
	
	<div id="questionnaire-form" style="display: none">
		<s:form method="post" name="questionnaireForm" action="makeQuestionnaire">
			<s:hidden name="eventId" value="%{eventId}" />
			<s:submit value="アンケートを作成する" />
		</s:form>
	</div>
	<div id="event-edit-form" style="display: none">
        <s:form method="post" name="eventEditForm" action="edit">
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="イベントを編集する" />
        </s:form>
	</div>
    <div id="event-delete-form" title="イベントを削除しようとしています" style="display: none">
        <p>イベントを削除しようとしています。この操作は取り消せません。本当に削除しますか？</p>
        <s:form method="post" action="destroy">
			<%= Helper.token() %>
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="イベントを削除する" />
        </s:form>
    </div>

    <div id="message-form" title="参加者にメッセージを送信します">
        <p>参加者に twitter 経由でメッセージを送ることが出来ます。メッセージは、１００文字以内で記述してください。最大で１時間３回１日５回まで送ることが出来ます。</p>
        <s:form method="post" action="send">
            <%= Helper.token() %>
            <s:hidden name="eventId" value="%{eventId}" />
            <s:textarea name="message"></s:textarea>
            <s:submit value="メッセージ送信" />
        </s:form>
    </div>
</div>
<% } %>

<div class="event-enrollment rad">
	<% if (deadlineOver) { %>
		<p>締め切りを過ぎているため<br>参加変更が行えません</p>
	<% } else { %>
		<% if (user == null) {%>
			<%-- login してない場合はなにもできない --%>
			<p class="guest">参加を申込むためにはログインしてください&nbsp;&nbsp;<br />
			<img src="<%= request.getContextPath() %>/images/enroll.png" alt="参加登録" />
		    <img src="<%= request.getContextPath() %>/images/reserve.png" alt="仮参加登録" />
			</p>						
		<% } else if (ParticipationStatus.ENROLLED.equals(status)) { %>
			<%-- なんか stamp みたいな感じで「参加登録済み」とかいうアイコンを出せないモノだろうか。 --%>
			<p>参加登録済みです。</p>
			<% if (event.canReserve()) { %>
                <img id="open-reserve-form" src="<%= request.getContextPath() %>/images/reserve.png" alt="仮参加登録" />
                <img id="open-cancel-form" src="<%= request.getContextPath() %>/images/cancel.png" alt="参加キャンセル" />
            <% } else { %>
                <img id="open-cancel-form" src="<%= request.getContextPath() %>/images/cancel.png" alt="参加キャンセル" />
                <p>締切間際には仮参加登録は行えません。</p>
            <% } %>
			<ul>
			    <li><a id="open-change-comment-form" href="#" >参加コメントを編集する</a></li>
			</ul>
		<% } else if (ParticipationStatus.RESERVED.equals(status) && !event.isReservationTimeOver()) { %>
			<p>仮参加登録中です。</p>
			<img id="open-enroll-form" src="<%= request.getContextPath() %>/images/enroll.png" alt="参加登録" />
			<img id="open-cancel-form" src="<%= request.getContextPath() %>/images/cancel.png" alt="参加キャンセル" />
			<ul>
			    <li><a id="open-change-comment-form" href="#" >参加コメントを編集する</a></li>
			</ul>
		<% } else { %>
			<% List<Event> requiredEvents = (List<Event>) request.getAttribute(Constants.ATTR_REQUIRED_EVENTS); %>
			<% if (requiredEvents != null && !requiredEvents.isEmpty()) { %>
				<p>
				参加登録するためには、次のイベントに<br />登録していることが必要です。
				</p>
				<ul>
					<% for (Event ev : requiredEvents) { %>
						<li><a href="<%= h(ev.getEventURL()) %>"><%= h(ev.getTitle()) %></a></li>
					<% } %>
				</ul>
			<% } else { %>
				<img id="open-enroll-form" src="<%= request.getContextPath() %>/images/enroll.png" alt="参加登録" />
				<% if (event.canReserve()) { %>
				    <img id="open-reserve-form" src="<%= request.getContextPath() %>/images/reserve.png" alt="仮参加登録" />
				<% } else { %>
				    <p>締切間際には仮参加登録は行えません。</p>
				<% } %>
			<% } %>
		<% } %>
		
		<%-- 参加登録フォーム --%>
		<div id="enroll-form" class="dialog-ui" title="参加登録フォーム" style="display: none">
			<s:form method="post" action="enroll">
				<%= Helper.token() %>
				<s:hidden name="eventId" value="%{eventId}" />
				<p><img src="<%= request.getContextPath() %>/images/momonga-mini.png"/>&nbsp;イベントに<strong>参加</strong>しようとしています。</p>
				<div class="dotted-box">
				    <ul>
				        <li>開始時刻の２４時間前に<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
				        <li>リマインダーは右上の「設定」リンクからたどれるページで受信拒否が設定できます。</li>
				    </ul>
				</div>
				<s:label for="comment" value="COMMENT" />:<br />
				<s:textarea name="comment" id="comment" value="よろしくお願いします。"/><br />
				<s:submit value="参加登録" />
			</s:form>
		</div>
		<%-- 仮参加フォーム --%>
		<div id="reserve-form" class="dialog-ui" title="仮参加登録フォーム" style="display: none">
			<s:form method="post" action="reserve">
				<%= Helper.token() %>
				<s:hidden name="eventId" value="%{eventId}" />
				<p><img src="<%= request.getContextPath() %>/images/momonga-mini.png"/>&nbsp;イベントに<strong>仮参加</strong>しようとしています。</p>
				<div class="dotted-box">
					<ul>
						<li><strong>放置した場合、キャンセル</strong>になってしまいます。</li>
						<li>参加を確定させるためには、締切り(設定されていない場合は開始３時間前)までに<strong>改めて参加の申込み</strong>をしてください。</li>
						<li>締切りの24,12時間前になると<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
						<li>リマインダーは右上の「設定」リンクからたどれるページで受信拒否が設定できます。</li>
					</ul>
				</div>
				<s:label for="comment" value="COMMENT" />:<br />
				<s:textarea name="comment" id="comment" value="よろしくお願いします。"/><br />
				<s:submit value="仮参加登録" />
			</s:form>
		</div>
		<%-- キャンセルフォーム --%>
		<div id="cancel-form" class="dialog-ui" title="参加キャンセルフォーム" style="display: none">
            <s:form method="post" action="cancel">
                <%= Helper.token() %>
                <s:hidden name="eventId" value="%{eventId}" />
                <p><img src="<%= request.getContextPath() %>/images/momonga-mini.png"/>&nbsp;イベントへの参加を<strong>キャンセル</strong>しようとしています。</p>
                <p>参加をいったんキャンセルすると確保していた順番は取り消されます。</p>
                <s:label for="comment" value="COMMENT" />:<br />
                <s:textarea name="comment" id="comment" value="参加できなくなりました。"/><br />
                <s:submit value="参加キャンセル"  />
            </s:form>
		</div>
		<%-- コメント変更フォーム --%>
		<div id="change-comment-form" class="dialog-ui" title="コメント変更フォーム" style="display: none">
            <s:form method="post" action="changeComment">
                <%= Helper.token() %>
                <s:hidden name="eventId" value="%{eventId}" />
                <p>コメントを変更します。</p>
                <s:label for="comment" value="COMMENT" />:<br />
                <s:textarea name="comment" id="comment" /><br />
                <s:submit value="コメント変更"  />
            </s:form>		    
		</div>
	<% } %>
</div>

<div class="event-promotion rad">

	<!--  twitter -->

	<a href="http://twitter.com/share" class="twitter-share-button" data-count="horizontal" data-via="partakein" data-text="<%= h(event.getTitle())%> - [PARTAKE] <%= h(event.getHashTag()) %>">Tweet</a>

	<script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>

	<!-- facebook -->

	<iframe id="facebook-like-button" src="http://www.facebook.com/plugins/like.php?href=<%= h(Util.encodeURIComponent(event.getEventURL())) %>&amp;layout=button_count&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=21" scrolling="no" frameborder="0" allowTransparency="true"></iframe>

</div>


 
<%
	ParticipationList participationList = (ParticipationList) request.getAttribute(Constants.ATTR_PARTICIPATIONLIST);
	List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
	List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
	List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
%>



<div class="event-participants rad">
<div class="event-status">
	<h2>参加者数</h2>
	<ul>
		<li>参加: <%= enrolledParticipations.size() %> 人 (仮 <%= participationList.getReservedEnrolled() %> 人)
　／　補欠: <%= spareParticipations.size() %> 人 (仮 <%= participationList.getReservedSpare() %> 人)</li>
	</ul>
</div>

	<h2><img src="<%= request.getContextPath() %>/images/circle.png" />参加者一覧 (<%= enrolledParticipations.size() %> 人)</h2>
	<% if (enrolledParticipations != null && enrolledParticipations.size() > 0) { %>
		<ul>
		<% for (EnrollmentEx participation : enrolledParticipations) { %>
			<%-- TODO: 仮参加は色をかえるべき --%>
			<% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
				<li>
					<img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					    <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
					<% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } else { %>
				<li>
					<img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<img src="<%= request.getContextPath() %>/images/reserved1.png"　title="仮参加" alt="仮参加者" />
					<% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
					<% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } %>
		<% 	} %>
		</ul>
	<% } else { %>
		<p>現在参加者はいません。</p>
	<% } %>
	
	<% if (spareParticipations != null && spareParticipations.size() > 0) { %>
		<h2><img src="<%= request.getContextPath() %>/images/square.png" />補欠者一覧 (<%= spareParticipations.size() %> 人)</h2>
		<ul>
		<% for (EnrollmentEx participation : spareParticipations) { %>
			<% 		// TODO: 仮参加は色をかえるべき		 %>
			<% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
				<li>
					<img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
					<% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } else { %>
				<li>
					<img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.isVIP()) { %><img src="<%= request.getContextPath() %>/images/crown.png" title="VIPです（主催者が設定しました）" alt="VIP 参加者" />
					<% } else if (participation.getPriority() > 0) { %><img src="<%= request.getContextPath() %>/images/star.png" title="優先(関連イベント参加者)" alt="優先参加者" /><% } %>
					<img src="<%= request.getContextPath() %>/images/reserved1.png" title="仮参加" alt="仮参加者" />
					: <%= h(participation.getComment()) %>
				</li>
			<% } %>
		<% 	} %>
		</ul>
	<% } else { %>
	<% } %>
	
	<% if (cancelledParticipations != null && cancelledParticipations.size() > 0) { %>
		<h2><img src="<%= request.getContextPath() %>/images/cross.png" />キャンセル一覧 (<%= cancelledParticipations.size() %> 人)</h2>
		<ul>
		<% for (EnrollmentEx participation : cancelledParticipations) { %>
		    <% if (ParticipationStatus.RESERVED.equals(participation.getStatus())) { %>
                <li>
                    <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                    <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>"><%= h(participation.getUser().getTwitterLinkage().getScreenName()) %></a> (仮参加後の参加表明なし) : <%= h(participation.getComment()) %>
                </li>           		    
		    <% } else { %>
                <li>
                    <img class="userphoto" src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                    <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>"><%= h(participation.getUser().getTwitterLinkage().getScreenName()) %></a> : <%= h(participation.getComment()) %>
                </li>           
		    <% } %>
		<% 	} %>
		</ul>	
	<% } %>
</div>
</div>
</div><%-- ENF OF event wrapper --%>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>