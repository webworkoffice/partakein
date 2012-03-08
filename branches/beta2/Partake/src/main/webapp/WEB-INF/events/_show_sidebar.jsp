<%@page import="in.partake.model.dto.EventReminder"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.ParticipationList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.controller.base.permission.UserPermission"%>
<%@page import="in.partake.model.DirectMessageEx"%>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	List<CommentEx> comments = (List<CommentEx>)(request.getAttribute(Constants.ATTR_COMMENTSET));
	List<DirectMessageEx> messages = (List<DirectMessageEx>) request.getAttribute(Constants.ATTR_MESSAGESET);
	Boolean deadlineOver = (Boolean)request.getAttribute(Constants.ATTR_DEADLINE_OVER);
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null)
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL); 
	ParticipationStatus status = (ParticipationStatus)request.getAttribute(Constants.ATTR_PARTICIPATION_STATUS);
	EventReminder reminderStatus = (EventReminder) request.getAttribute(Constants.ATTR_REMINDER_STATUS);
    Integer maxCodePointsOfMessage = (Integer) request.getAttribute(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE);
%>

<aside class="span3"><!-- SIDEBAR BEGIN -->
	<%-- Owner はイベントを削除できる。Manager はイベントを編集することが出来る。 --%>
	<% if (event.hasPermission(user, UserPermission.EVENT_EDIT)) { %>
	<div class="well">
		<h3><img src="<%= request.getContextPath() %>/images/gear.png"/>イベント管理</h3>
		<ul>
			<li><a href="/events/edit/<%= h(event.getId()) %>">イベントを編集</a></li>
			<% if (event.hasPermission(user, UserPermission.EVENT_REMOVE)) { %>
			    <li><a data-toggle="modal" href="#event-delete-dialog">イベントを削除</a></li>
			<% } %>
		</ul>
		<h3><img src="<%= request.getContextPath() %>/images/momonga1.png"/>参加者管理</h3>
	    <ul>
	         <li><a data-toggle="modal" href="#message-send-dialog">参加者へメッセージ送信</a></li>
	         <li><a href="<%= request.getContextPath() %>/events/showParticipants/<%= h(event.getId()) %>">参加ステータスを編集</a></li>
	         <li><a href="<%= request.getContextPath() %>/events/printParticipants/<%= h(event.getId()) %>">参加者リストを出力</a></li>
	    </ul>
	
		<h3><img src="<%= request.getContextPath() %>/images/mail.png"/>リマインダー</h3>
	    <dl>
	        <dt>締切24時間前(仮参加者向)</dt>
	            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeDeadlineOneday()) %></dd>
	        <dt>締切12時間前(仮参加者向)</dt>
	            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeDeadlineHalfday()) %></dd>
	        <dt>イベント１日前</dt>
	            <dd><%= Helper.readableReminder(reminderStatus.getSentDateOfBeforeTheDay()) %></dd>
	    </dl>
	
		<div id="event-delete-dialog" class="modal" style="display:none">
			<div class="modal-header">
		    	<a class="close" data-dismiss="modal">&times;</a>
		    	<h3>イベントを削除しようとしています</h3>
			</div>
		  	<div class="modal-body">
		  		<p>イベントを削除しようとしています。<strong>この操作は取り消せません。</strong></p>
				<p>本当に削除しますか？</p>
		  	</div>
		  	<div class="modal-footer spinner-container">
			    <a href="#" id="event-delete-dialog-submit-button" class="btn btn-danger">削除</a>
			    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
		  	</div>
			<script>
				function removeEvent() {
					var eventId = '<%= h(event.getId()) %>';
					var spinner = partakeUI.spinner(document.getElementById('event-delete-dialog-submit-button'));
					var button = $('#event-delete-dialog-submit-button');

					spinner.show();
					button.attr('disabled', '');

					partake.event.remove(eventId)
					.always(function () {
						spinner.hide();
						button.removeAttr('disabled');
					})
					.done(function (json) {
						location.href = "/";
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
				$('#event-delete-dialog-submit-button').click(removeEvent);
			</script>		  	
		</div>
	
	    <div id="message-send-dialog" class="modal" style="display:none">
	    	<div class="modal-header">
	    		<a class="close" data-dismiss="modal">&times;</a>
	    		<h3>参加者にメッセージを送ります</h3>
	    	</div>
	    	<div class="modal-body">
		    	<%-- TODO: maxCodePointsOfMessage should not be null. --%>
		        <p>参加者に twitter 経由でメッセージを送ることができます。メッセージは、長くとも<%= maxCodePointsOfMessage != null ? maxCodePointsOfMessage.intValue() : 0 %>文字以内で記述してください。最大で１時間３回１日５回まで送ることができます。</p>
		        <form>
		            <textarea id="message-send-dialog-textarea" name="message" class="span7" rows="4"></textarea>
		        </form>
		        <p>残り <span id="message_length"><%= maxCodePointsOfMessage != null ? maxCodePointsOfMessage.intValue() : 0 %></span> 文字</p>
	        </div>
	        <div class="modal-footer spinner-container">
	        	<a href="#" id="message-send-dialog-submit-button" class="btn btn-danger">送信</a>
	        	<a href="#" class="btn" data-dismiss="modal">キャンセル</a>
	        </div>
<script>
function sendMessage() {
	var eventId = '<%= h(event.getId()) %>';
	var message = $('#message-send-dialog-textarea').val();
	
	var spinner = partakeUI.spinner(document.getElementById('message-send-dialog-submit-button'));
	var button = $('#message-send-dialog-submit-button');

	spinner.show();
	button.attr('disabled', '');

	partake.message.sendMessage(eventId, message)
	.always(function () {
		spinner.hide();
		button.removeAttr('disabled');
	})
	.done(function (json) {
		$('#message-send-dialog').modal('hide');
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
$('#message-send-dialog-submit-button').click(sendMessage);

function onMessageChange() {
	var textarea = $('#message-send-dialog-textarea');
	var submitButton = $('#message-send-dialog-submit-button');
	var messageSpan = $('#message_length');
	var left = <%= maxCodePointsOfMessage != null ? maxCodePointsOfMessage.intValue() : 0 %> - codePointCount(textarea.val());
	
	messageSpan.text(left).css('color', left > 20 ? '#000' : '#f00');
	if (left < 0)
		submitButton.attr('disabled', '');
	else
		submitButton.removeAttr('disabled');
}
$('#message-send-dialog-textarea').keydown(onMessageChange).keyup(onMessageChange);
</script>
	    </div>
	</div>
	<% } %>

	<div class="well">
	<% if (deadlineOver) { %>
		<p>締め切りを過ぎているため<br />参加変更が行えません</p>
	<% } else { %>
		<% if (user == null) {%>
			<%-- login してない場合はなにもできない  --%>
			<div class="guest">
				<form action="/auth/loginByTwitter">
					<a href="/auth/loginByTwitter?redirectURL=<%= h(redirectURL) %>"><strong>Twitterでログイン</strong>して<br />参加しよう！</a>
				</form>
				<input type="button" class="btn btn-danger span3" value="参加" disabled />
				<input type="button" class="btn btn-info span3" value="▲仮参加" disabled />
			</div>
		<% } else if (ParticipationStatus.ENROLLED.equals(status)) { %>
			<%-- なんか stamp みたいな感じで「参加登録済み」とかいうアイコンを出せないモノだろうか。 --%>
			<p><strong>参加登録済みです。</strong></p>
			<% if (event.canReserve()) { %>
				<input type="button" class="btn btn-info span3" data-toggle="modal" data-target="#event-reserve-dialog" value="▲仮参加" />
				<input type="button" class="btn span3" data-toggle="modal" data-target="#event-cancel-dialog" value="辞退" />
            <% } else { %>
            	<input type="button" class="btn span3" data-toggle="modal" data-target="#event-cancel-dialog" value="辞退" />
                <p>締切間際には仮参加登録は行えません。</p>
            <% } %>
			<ul>
			    <li><a href="#comment-change-form" data-toggle="modal" >参加コメントを編集する</a></li>
			</ul>
		<% } else if (ParticipationStatus.RESERVED.equals(status) && !event.isReservationTimeOver()) { %>
			<p><strong>仮参加登録中です。</strong></p>
				<input type="button" class="btn span3 btn-danger" data-toggle="modal" data-target="#event-enroll-dialog" value="参加" />
				<input type="button" class="btn span3" data-toggle="modal" data-target="#event-cancel-dialog" value="辞退" />
			<ul>
			    <li><a href="#comment-change-dialog" data-toggle="modal">参加コメントを編集する</a></li>
			</ul>
		<% } else { %>
			<% List<Event> requiredEvents = (List<Event>) request.getAttribute(Constants.ATTR_REQUIRED_EVENTS); %>
			<% if (requiredEvents != null && !requiredEvents.isEmpty()) { %>
				<p>参加登録するためには、次のイベントに<br />登録していることが必要です。</p>
				<ul>
					<% for (Event ev : requiredEvents) { %>
						<li><a href="<%= h(ev.getEventURL()) %>"><%= h(ev.getTitle()) %></a></li>
					<% } %>
				</ul>
			<% } else { %>
				<input type="button" class="btn span3 btn-danger" data-toggle="modal" data-target="#event-enroll-dialog" value="参加" />
			
				<% if (event.canReserve()) { %>
					<input type="button" class="btn btn-info span3" data-toggle="modal" data-target="#event-reserve-dialog" value="▲仮参加" />
				<% } else { %>
				    <p>締切間際には仮参加登録は行えません。</p>
				<% } %>
			<% } %>
		<% } %>

		<%-- 参加登録フォーム --%>
		<div id="event-enroll-dialog" class="modal" style="display:none">
			<div class="modal-header">
		    	<a class="close" data-dismiss="modal">&times;</a>
		    	<h3>イベントに<strong>参加</strong>しようとしています</h3>
			</div>
		  	<div class="modal-body">
		  		<ul>
			        <li>開始時刻の２４時間前に<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
			        <li>リマインダーは右上の「設定」リンクからたどれるページで受信拒否が設定できます。</li>
				</ul>
		
		  		<form class="form-horizontal">
					<textarea id="event-enroll-dialog-comment" name="comment" class="span7">よろしくお願いします。</textarea>
		  		</form>
		  	</div>
		  	<div class="modal-footer spinner-container">
			    <a href="#" id="event-enroll-dialog-submit" class="btn btn-danger">参加</a>
			    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
		  	</div>
		</div>

		<%-- 仮参加フォーム --%>
		<div id="event-reserve-dialog" class="modal" style="display:none">
			<div class="modal-header">
		    	<a class="close" data-dismiss="modal">&times;</a>
		    	<h3>イベントに<strong>仮参加</strong>しようとしています</h3>
			</div>
		  	<div class="modal-body">
		  		<ul>
					<li><strong>放置した場合、キャンセル</strong>になってしまいます。</li>
					<li>参加を確定させるためには、締切り(設定されていない場合は開始３時間前)までに<strong>改めて参加の申込み</strong>をしてください。</li>
					<li>締切りの24,12時間前になると<strong>リマインダー</strong>が<em>自分自身からの</em>ダイレクトメッセージで届きます。</li>
					<li>リマインダーは右上の「設定」リンクからたどれるページで受信拒否が設定できます。</li>
				</ul>
		
		  		<form class="form-horizontal">
					<textarea id="event-reserve-dialog-comment" name="comment" class="span7">よろしくお願いします。</textarea>
		  		</form>
		  	</div>
		  	<div class="modal-footer spinner-container">
			    <a href="#" id="event-reserve-dialog-submit" class="btn btn-danger">▲仮参加</a>
			    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
		  	</div>
		</div>
		
		<%-- キャンセルフォーム --%>
		<div id="event-cancel-dialog" class="modal" style="display:none">
			<div class="modal-header">
		    	<a class="close" data-dismiss="modal">&times;</a>
		    	<h3>イベントへの参加を<strong>辞退</strong>しようとしています</h3>
			</div>
		  	<div class="modal-body">
		  		<ul>
					<li>参加を辞退すると確保していた順番は取り消されます。</li>
				</ul>		
		  		<form class="form-horizontal">
					<textarea id="event-cancel-dialog-comment" name="comment" class="span7">よろしくお願いします。</textarea>
		  		</form>
		  	</div>
		  	<div class="modal-footer spinner-container">
			    <a href="#" id="event-cancel-dialog-submit" class="btn btn-danger">辞退</a>
			    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
		  	</div>
		</div>
		
		<script>
		function addEventListenerToDialog(status) {
		  	$('#event-' + status + '-dialog-submit').click(function() {
		  		var spinner = partakeUI.spinner(document.getElementById('event-' + status + '-dialog-submit'));
		  		var eventId = '<%= h(event.getId()) %>';
		  		var comment = $('#event-' + status + '-dialog-comment').val();
		  		
		  		spinner.show();
		  		$('#event-' + status + '-dialog-submit').attr('disabled');
		  		
		  		partake.event.enroll(eventId, status, comment)
		  		.always(function (xhr) {
			  		spinner.hide();
			  		$('#event-' + status + '-dialog-submit').removeAttr('disabled');		  			
		  		})
		  		.done(function (json) {
		  			location.reload();
		  		})
		  		.fail(function (xhr) {
					try {
						var json = $.parseJSON(xhr.responseText);
						alert(json.reason);
					} catch (e) {
						alert('レスポンスが JSON 形式ではありません。');
					}		  			
		  		});
		  	});
		}
		addEventListenerToDialog('enroll');
		addEventListenerToDialog('reserve');
		addEventListenerToDialog('cancel');
		</script>
		
				
		<%-- コメント変更フォーム --%>
		<div id="change-comment-form" class="dialog-ui" title="コメント変更フォーム" style="display: none">
            <s:form method="post" action="changeComment">
                <%= Helper.tokenTags() %>
                <s:hidden name="eventId" value="%{eventId}" />
                <p>コメントを変更します</p>
                <s:label for="comment" value="COMMENT" />:<br />
                <s:textarea name="comment" id="comment" /><br />
                <s:submit value="コメント変更"  />
            </s:form>
		</div>
		
		<div id="comment-change-dialog" class="modal" style="display:none">
			<div class="modal-header">
		    	<a class="close" data-dismiss="modal">&times;</a>
		    	<h3>コメントを変更します</h3>
			</div>
		  	<div class="modal-body">
		  		<form method="post" name="commentChangeForm" action="/events/cancel">
		  			<%= Helper.tokenTags() %>
		  			<input type="hidden" name="eventId" value="<%= h(event.getId()) %>" />
		  			<%-- TODO: 元のコメントが入ってない --%>
                	<textarea name="comment" id="comment"></textarea>
		  		</form>
		  	</div>
		  	<div class="modal-footer">
			    <a href="#" class="btn btn-primary" onclick="document.commentChangeForm.submit()">変更</a>
			    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
		  	</div>
		</div>
	<% } %>
	</div>
	
<%
	ParticipationList participationList = (ParticipationList) request.getAttribute(Constants.ATTR_PARTICIPATIONLIST);
	List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
	List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
	List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
%>
	<div class="well">
		<h3>参加者数</h3>
		<ul>
			<li>参加: <%= enrolledParticipations.size() %> 人 (仮 <%= participationList.getReservedEnrolled() %> 人)
	　／　補欠: <%= spareParticipations.size() %> 人 (仮 <%= participationList.getReservedSpare() %> 人)</li>
		</ul>
	</div>

	<div class="well">
	<h3><img src="<%= request.getContextPath() %>/images/circle.png" />参加者一覧 (<%= enrolledParticipations.size() %> 人)</h3>
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
					<img src="<%= request.getContextPath() %>/images/reserved1.png" title="仮参加" alt="仮参加者" />
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
		<h3><img src="<%= request.getContextPath() %>/images/square.png" />補欠者一覧 (<%= spareParticipations.size() %> 人)</h3>
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
	<% } %>

	<% if (cancelledParticipations != null && cancelledParticipations.size() > 0) { %>
		<h3><img src="<%= request.getContextPath() %>/images/cross.png" />キャンセル一覧 (<%= cancelledParticipations.size() %> 人)</h3>
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
</aside><!-- SIDEBAR END -->
