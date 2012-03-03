<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.model.dto.auxiliary.UserPermission"%>
<%@page import="in.partake.model.DirectMessageEx"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>

<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	List<CommentEx> comments = (List<CommentEx>)(request.getAttribute(Constants.ATTR_COMMENTSET));
	List<DirectMessageEx> messages = (List<DirectMessageEx>) request.getAttribute(Constants.ATTR_MESSAGESET);
%>

<script src="http://widgets.twimg.com/j/2/widget.js"></script>

<div class="tabbable event-nav">
	<ul class="nav nav-tabs">
		<li class="active"><a href="#news1" title="コメントボード" data-toggle="tab">掲示板</a></li>
		<li><a href="#news2" title="管理者からのメッセージ" data-toggle="tab">管理者から</a></li>
		<li><a href="#news3" title="このイベントのハッシュタグがついたTweet" data-toggle="tab">みんなのTweet</a></li>
	</ul>
	<div class="tab-content row">
		<div id="news1" class="tab-pane span9 active">
			<s:form action="removeComment" id="removeCommentForm" name="removeCommentForm">
				<%= Helper.tokenTags() %>
				<s:hidden id="removeCommentId" name="commentId" value="" />
				<s:hidden name="eventId" value="%{eventId}" />
			</s:form>
			<script>
				function removeComment(anchorElem, commentId) {
					var spinner = partakeUI.spinner(anchorElem);
					if (!spinner.is(":hidden"))
						return;
					
					spinner.show();
					if (window.confirm('メッセージを削除しようとしています。この操作は取り消せません。本当に削除しますか？')) {
						partake.event.removeComment(commentId, '<%= h(event.getId()) %>')
						.always(function () {
							spinner.hide();
						})
						.done(function (json) {
							$('#comment-' + commentId).remove();
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
				}
			</script>
			<% for (CommentEx comment : comments) { %>
				<% if (comment == null) { continue; } %>
				<div class="comment" id="comment-<%= h(comment.getId()) %>">
					<p class="spinner-container"><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>"><%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
					: <%= Helper.readableDate(comment.getCreatedAt()) %>
					<% if (user != null && (event.hasPermission(user, UserPermission.EVENT_REMOVE_COMMENT) || user.getId().equals(comment.getUserId()))) { %>
						<a href="#" title="コメントを削除" onclick="removeComment(this, '<%= h(comment.getId()) %>')">[x]</a>
					<% } %></p>
					<% if (comment.isHTML()) { %>
					    <p><%= Helper.cleanupHTML(comment.getComment()) %></p>
					<% } else { %>
					    <p><%= h(comment.getComment()) %></p>
					<% } %>
				</div>
			<% } %>
			<div class="comment-form">
		        <% if (user != null) { %>
			        <form class="spinner-container">
						<%= Helper.tokenTags() %>
				        <strong>コメント</strong><br />
				        <textarea id="commentForm-commentEdit" name="comment" class="span9"></textarea><br />
				        <input id="comment-form-submit" type="button" class="btn btn-primary" value="コメントを投稿"  />
				    </form>
<script>
function postComment() {
	var spinner = partakeUI.spinner(document.getElementById('comment-form-submit'));
	var eventId = '<%= h(event.getId()) %>';
	var comment = $('#commentForm-commentEdit').val();

	spinner.show();
	$('#commentForm-commentEdit').attr('disabled', '');
	$('#comment-form-submit').attr('disabled', '');
	
	partake.event.postComment(eventId, comment)
	.always(function (xhr) {
		spinner.hide();
		$('#commentForm-commentEdit').removeAttr('disabled');
		$('#comment-form-submit').removeAttr('disabled');
	})
	.done(function (json) {
		// TODO: Recreating comment board is better. 
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
}
$('#comment-form-submit').click(postComment);
</script>
				<% } else { %>
		            <p>コメントを投稿するにはログインしてください。</p>
				<% } %>
			</div>			
		</div>
		<div id="news2" class="tab-pane span9">
			<div class="event-comments">
			<% if (messages != null) { %>
				<% for (DirectMessageEx message : messages) { %>
					<% if (message == null) continue; %>
					<div class="comment">
						<p><a href="<%= request.getContextPath() %>/users/<%= h(message.getUserId()) %>"><%= h(message.getSender().getScreenName()) %></a>
						: <%= Helper.readableDate(message.getCreatedAt()) %></p>
						<p><%= h(message.getMessage()) %></p>
					</div>	
				<% } %>
			<% } %>
			</div>
		</div>
		<div id="news3" class="tab-pane span9">
			<% if (!StringUtils.isEmpty(event.getHashTag())) { %>
			<div class="event-twitter-hashtag-stream">	
			<script>
			new TWTR.Widget({
				  version: 2,
				  type: 'search',
				  search: '<%= h(event.getHashTag()) %>',
				  interval: 6000,
				  title: 'Twitter Hashtag Live Feed',
				  subject: '<%= h(event.getHashTag()) %>',
				  width: 'auto',
				  height: 450,
				  theme: {
				    shell: {
				      background: 'none',
				      color: '#7c7c7c'
				    },
				    tweets: {
				      background: '#ffffff',
				      color: '#444444',
				      links: '#1985b5'
				    }
				  },
				  features: {
				    scrollbar: true,
				    loop: false,
				    live: true,
				    hashtags: true,
				    timestamp: true,
				    avatars: true,
				    toptweets: true,
				    behavior: 'all'
				  }
				}).render().start();
			</script>
			</div>
			<% } else { %>
			<p>ツイートを表示するにはハッシュタグを設定してください。</p>
			<% } %>
		</div>
	</div>
</div>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce_jquery/jquery.tinymce.js"></script>
<script type="text/javascript">
$(function() {
    $('#commentForm-commentEdit').tinymce({
        // Location of TinyMCE script
        script_url: "<%= request.getContextPath() %>/js/tiny_mce_jquery/tiny_mce.js",

        theme: "advanced",
        language: "ja",
        width: "550",
        
        plugins: "inlinepopups,searchreplace,spellchecker,style,table,xhtmlxtras",
        // plugins : "spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",

        theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
        theme_advanced_buttons2: "cut,copy,paste,|,bullist,numlist,|,outdent,indent,blockquote,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor",
        theme_advanced_buttons3: "tablecontrols,|,hr,|,sub,sup,|,styleprops,spellchecker",

        theme_advanced_toolbar_location: "top",
        theme_advanced_toolbar_align: "left",
        theme_advanced_statusbar_location: "bottom",
        theme_advanced_resizing: true
    });
});
</script>