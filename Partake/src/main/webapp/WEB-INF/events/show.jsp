<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%@taglib prefix="s" uri="/struts-tags" %>

<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.ParticipationEx"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.model.dto.UserPermission"%>
<%@page import="in.partake.model.dto.Participation"%>
<%@page import="in.partake.service.UserService"%>
<%@page import="in.partake.model.dto.EventCategory"%>
<%@page import="in.partake.model.dto.EventNotificationStatus"%>
<%@page import="in.partake.model.dto.Comment"%>
<%@page import="in.partake.model.dto.ParticipationStatus"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.util.Util"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.resource.Constants"%>

<%@page import="static in.partake.util.Util.h"%>
<%@page import="static in.partake.util.Util.escapeURI"%>
<%@page import="static in.partake.util.Util.cleanupHTML"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.EventRelationEx"%>


<%
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	ParticipationStatus status = (ParticipationStatus)request.getAttribute(Constants.ATTR_PARTICIPATION_STATUS);
	Boolean deadlineOver = (Boolean)request.getAttribute(Constants.ATTR_DEADLINE_OVER);
	EventNotificationStatus notificationStatus = (EventNotificationStatus)(request.getAttribute(Constants.ATTR_NOTIFICATION_STATUS));
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

<h1><%= h(event.getTitle()) %></h1>
<% if (!Util.isEmpty(event.getSummary())) { %>
	<p class="summary"><%= h(event.getSummary()) %></p>
<% } %>

<div class="event-wrapper">
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
				<dd><%= h(Util.isEmpty(event.getPlace()) ? "-" : event.getPlace()) %></dd>
			<dt>住所：</dt>
				<dd><%= h(Util.isEmpty(event.getAddress()) ? "-" : event.getAddress()) %></dd>
			<dt>URL：</dt>
				<dd><% if (!Util.isEmpty(event.getUrl())) { %>
					<a href="<%= h(event.getUrl()) %>"><%= h(event.getUrl()) %></a>             
				<% } else { %>
				    -
				<% } %></dd>
	        <dt>管理者：</dt>
	            <dd><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
	                <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
	                    <%= h(event.getOwner().getTwitterLinkage().getName()) %>
	                    (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
	                <% } else { %>
	                    <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
	                <% } %>
	                </a></dd>
	        <% if (!Util.isEmpty(event.getHashTag())) { %>
	        <dt>ハッシュタグ：</dt>
	            <dd><a href="http://twitter.com/#search?q=<%= escapeURI(event.getHashTag()) %>"><%= h(event.getHashTag()) %></a></dd>
	        <% } %>         
	        <dt>このページへの<br>短縮 URL：</dt>
	           <% String shortenURL = Util.bitlyShortURL(event.getEventURL()); %>
	           <dd><a href="<%= h(shortenURL) %>"><%= h(shortenURL) %></a></dd>
	        <% if (eventRelations != null && !eventRelations.isEmpty()) { %>
	        <dt>関連イベント</dt>
	           <% for (EventRelationEx eventRelation : eventRelations) { %>
	               <dd>
	                   <a href="<%= h(eventRelation.getEvent().getEventURL()) %>"><%= h(eventRelation.getEvent().getTitle()) %></a>
                       <% if (eventRelation.isRequired()) { %>(このイベントへの登録が必須です)<% } %>
                       <% if (eventRelation.hasPriority()) { %>(このイベントへ登録した方は優先的にこのイベントへ参加できます)<% } %>
	               </dd>
	           <% } %>
	        <% }%>
		</dl>
		
	    <% if (!Util.isEmpty(event.getAddress())) { %>
	    <div class="event-map"><a href="http://maps.google.co.jp/maps?q=<%= h(escapeURI(event.getAddress())) %>">
	        <img src="http://maps.google.co.jp/maps/api/staticmap?size=200x200&center=<%= h(escapeURI(event.getAddress())) %>&zoom=17&sensor=false" />      
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
<div class="event-owner-information">
	<h2><img src="<%= request.getContextPath() %>/images/gear.png"/>イベント管理</h2>
	<ul>
		<li><a href="#" onclick="document.eventEditForm.submit();">イベントを編集する</a></li>
		<% if (event.hasPermission(user, UserPermission.EVENT_REMOVE)) { %>
		    <li><a id="open-event-delete-form" href="#">イベントを削除する</a></li>
		<% } %>
    </ul>
    <ul>
		<li><a id="open-message-form" href="#">参加者へメッセージを送信する</a></li>
		<li><a id="open-twitter-promotion-form" href="#">twitter で宣伝する</a></li>
	</ul>
	<%--
	<ul>
	    <li><a id="open-reminder-reset-form" href="#">リマンダー送付状況をリセットする</a></li>
	</ul>
	 --%>
	<ul>
	    <li><a href="<%= request.getContextPath() %>/events/participants/<%= event.getId() %>.csv">参加者リスト (CSV 形式, UTF-8)</a></li>
	</ul>
	<h2><img src="<%= request.getContextPath() %>/images/mail.png"/>リマインダー送付状況</h2>
	<ul>
        <li>締切２４時間前メッセージ：　<%= notificationStatus.isBeforeDeadlineOneday() ? "送付済" : "未送付" %></li>
        <li>締切１２時間前メッセージ：　<%= notificationStatus.isBeforeDeadlineHalfday() ? "送付済" : "未送付" %></li>
        <li>イベント１日前メッセージ：　<%= notificationStatus.isBeforeTheDay() ? "送付済" : "未送付" %></li>
	</ul>
	
	<div id="event-edit-form" style="display: none">
        <s:form method="post" name="eventEditForm" action="%{#request.contextPath}/events/edit">
			<s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="イベントを編集する" />
        </s:form>
	</div>
    <div id="event-delete-form" title="イベントを削除しようとしています" style="display: none">
        <p>イベントを削除しようとしています。この操作は取り消せません。本当に削除しますか？</p>
        <s:form method="post" action="%{#request.contextPath}/events/destroy">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="イベントを削除する" />
        </s:form>
    </div>
    
	<div id="subevent-new-form" style="display: none">
		<s:form method="post" name="eventSubeventNewForm" action="%{#request.contextPath}/events/newSubevent">
			<s:token />
			<s:hidden name="eventId" value="%{eventId}" />
			<s:submit value="サブイベントを追加する" />
		</s:form>
	</div>
    <div id="subevent-edit-form" style="display: none">
        <s:form method="post" name="eventSubeventEditForm" action="%{#request.contextPath}/events/editSubevent">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="サブイベントを編集する" />
        </s:form>
    </div>
    <div id="subevent-delete-form" title="サブイベントを削除しようとしています" style="display: none">
        <p>サブイベントを削除しようとしています。この操作は取り消せません。本当に削除しますか？</p>
        <s:form method="post" name="eventSubeventDeleteForm" action="%{#request.contextPath}/events/destroySubevent">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:submit value="サブイベントを削除する" />
        </s:form>
    </div>
	
	

    <div id="message-form" title="参加者にメッセージを送信します">
        <p>参加者に twitter 経由でメッセージを送ることが出来ます。メッセージは、１００文字以内で記述してください。最大で１時間３回１日５回まで送ることが出来ます。</p>
        <s:form method="post" action="%{#request.contextPath}/events/send">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:textarea name="message"></s:textarea>
            <s:submit value="メッセージ送信" />
        </s:form>
    </div>
    <div id="twitter-promotion-form" title="twitter に宣言を流す">
        <p>twitter に宣伝を流すことが出来ます。</p>
        <s:form method="post" action="%{#request.contextPath}/events/twitterPromotion">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <s:textarea name="message" value="%{event.defaultTwitterPromotionMessage}"></s:textarea>
            <s:submit value="宣伝を流す" />
        </s:form>
    </div>
    <%-- 
    <div id="reminder-reset-form" title="リマインダー送付状況をリセットする">
        <p>リマインダーを未送付の状態にします。</p>
        <p>開催日付を誤るなどしてリマインダーが送られてしまった状態になった場合に利用できます。</p>
        <s:form method="post" action="%{#request.contextPath}/events/resetReminder">
            <s:token />
            <s:hidden name="eventId" value="%{eventId}" />
            <ul>
                <li><s:checkbox name="isBeforeDeadlineOneday"></s:checkbox>締切２４時間前メッセージを未送付の状態にする</li>
                <li><s:checkbox name="isBeforeDeadlineHalfday"></s:checkbox>締切１２時間前メッセージを未送付の状態にする</li>
                <li><s:checkbox name="isBeforeTheDay"></s:checkbox>イベント１日前メッセージを未送付の状態にする</li>
            </ul>
            <s:submit value="リマインダーをリセットする" />
        </s:form>        
    </div>
    --%>
</div>
<% } %>

<div class="event-promotion">
	<!--  twitter -->
	<a href="http://twitter.com/share" class="twitter-share-button" data-count="horizontal" data-via="partakein" data-lang="ja">Tweet</a>
	<script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>
	<!-- facebook -->
	<iframe id="facebook-like-button" src="http://www.facebook.com/plugins/like.php?href=<%= h(Util.encodeURIComponent(event.getEventURL())) %>&amp;layout=button_count&amp;show_faces=true&amp;width=450&amp;action=like&amp;colorscheme=light&amp;height=21" scrolling="no" frameborder="0" allowTransparency="true"></iframe>
</div>


<div class="event-enrollment">
	<% if (deadlineOver) { %>
		<p>締め切りを過ぎているため<br>参加変更が行えません</p>
	<% } else { %>
		<% if (user == null) {%>
			<%-- login してない場合はなにもできない --%>
			<p>参加を申し込むためには<br>ログインしてください。</p>
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
			<% List<EventEx> requiredEvents = (List<EventEx>) request.getAttribute(Constants.ATTR_REQUIRED_EVENTS); %>
			<% if (requiredEvents != null && !requiredEvents.isEmpty()) { %>
				<p>参加登録するためには、次のイベントに登録していることが必要です。</p>
				<ul>
					<% for (EventEx ev : requiredEvents) { %>
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
		<div id="enroll-form" title="参加登録フォーム" style="display: none">
			<s:form method="post" action="%{#request.contextPath}/events/enroll">
				<s:token />
				<s:hidden name="eventId" value="%{eventId}" />
				<p>イベントに<strong>参加</strong>しようとしています。</p>
				<s:label for="comment" value="COMMENT" />:<s:textfield name="comment" id="comment" value="よろしくお願いします。"/><br />
				<s:submit value="参加登録" />
			</s:form>
		</div>
		<%-- 仮参加フォーム --%>
		<div id="reserve-form" title="仮参加登録フォーム" style="display: none">
			<s:form method="post" action="%{#request.contextPath}/events/reserve">
				<s:token />
				<s:hidden name="eventId" value="%{eventId}" />
				<p>イベントに<strong>仮参加</strong>しようとしています。</p>
				<p>仮参加の場合一時的に順番のみが確保されます。参加を確定させるためには、締め切り(設定されていない場合は開始３時間前)までに再び参加申し込みしてください。放置した場合、キャンセル扱いとなります。</p>
				<s:label for="comment" value="COMMENT" />:<s:textfield name="comment" id="comment" value="よろしくお願いします。"/><br />
				<s:submit value="仮参加登録" />
			</s:form>
		</div>
		<%-- キャンセルフォーム --%>
		<div id="cancel-form" title="参加キャンセルフォーム" style="display: none">
            <s:form method="post" action="%{#request.contextPath}/events/cancel">
                <s:token />
                <s:hidden name="eventId" value="%{eventId}" />
                <p>イベントへの参加を<strong>キャンセル</strong>しようとしています。</p>
                <p>参加をいったんキャンセルすると確保していた順番は取り消されます。</p>
                <s:label for="comment" value="COMMENT" />:<s:textfield name="comment" id="comment" value="参加できなくなりました。"/><br />
                <s:submit value="参加キャンセル"  />
            </s:form>
		</div>
		<%-- コメント変更フォーム --%>
		<div id="change-comment-form" title="コメント変更フォーム" style="display: none">
            <s:form method="post" action="%{#request.contextPath}/events/changeComment">
                <s:token />
                <s:hidden name="eventId" value="%{eventId}" />
                <p>コメントを変更します。</p>
                <s:label for="comment" value="COMMENT" />:<s:textfield name="comment" id="comment" /><br />
                <s:submit value="コメント変更"  />
            </s:form>		    
		</div>
	<% } %>
</div>



<div class="event-participants">
	<%
	List<ParticipationEx> enrolledParticipations = (List<ParticipationEx>)request.getAttribute(Constants.ATTR_ENROLLED_PARTICIPATIONS);
	List<ParticipationEx> spareParticipations = (List<ParticipationEx>)request.getAttribute(Constants.ATTR_SPARE_PARTICIPATIONS);
	List<ParticipationEx> cancelledParticipations = (List<ParticipationEx>)request.getAttribute(Constants.ATTR_CANCELLED_PARTICIPATIONS);
	%>

	<h2><img src="<%= request.getContextPath() %>/images/circle.png" />参加者一覧 (<%= enrolledParticipations.size() %> 人)</h2>
	<% if (enrolledParticipations != null && enrolledParticipations.size() > 0) { %>
		<ul>
		<% for (ParticipationEx participation : enrolledParticipations) { %>
			<%-- TODO: 仮参加は色をかえるべき --%>
			<% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
				<li>
					<img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					    <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.getPriority() > 0) { %>(優先)<% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } else { %>
				<li>
					<img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					(仮参加)
					<% if (participation.getPriority() > 0) { %>(優先)<% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } %>
		<% 	} %>
		</ul>
	<% } else { %>
		<p>現在参加者はいません。</p>
	<% } %>
	
	<% if (spareParticipations != null && spareParticipations.size() > 0) { %>
		<h2><img src="<%= request.getContextPath() %>/images/triangle.png" />補欠者一覧 (<%= spareParticipations.size() %> 人)</h2>
		<ul>
		<% for (ParticipationEx participation : spareParticipations) { %>
			<% 		// TODO: 仮参加は色をかえるべき		 %>
			<% if (ParticipationStatus.ENROLLED.equals(participation.getStatus())) { %>
				<li>
					<img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.getPriority() > 0) { %>(優先)<% } %>
					: <%= h(participation.getComment()) %>
				</li>
			<% } else { %>
				<li>
					<img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
					<a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>">
					   <%= h(participation.getUser().getTwitterLinkage().getScreenName()) %>
					</a>
					<% if (participation.getPriority() > 0) { %>(優先)<% } %>
					(仮参加)
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
		<% for (ParticipationEx participation : cancelledParticipations) { %>
		    <% if (ParticipationStatus.RESERVED.equals(participation.getStatus())) { %>
                <li>
                    <img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
                    <a href="<%= request.getContextPath() %>/users/<%= h(participation.getUserId()) %>"><%= h(participation.getUser().getTwitterLinkage().getScreenName()) %></a> (仮参加後の参加表明なし) : <%= h(participation.getComment()) %>
                </li>           		    
		    <% } else { %>
                <li>
                    <img src="<%= h(participation.getUser().getTwitterLinkage().getProfileImageURL()) %>" alt="" />
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