<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.util.Util"%>
<%@page import="in.partake.model.dto.auxiliary.UserPermission"%>
<%@page import="in.partake.model.DirectMessageEx"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>

<%@page import="static in.partake.view.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	List<CommentEx> comments = (List<CommentEx>)(request.getAttribute(Constants.ATTR_COMMENTSET));
	List<DirectMessageEx> messages = (List<DirectMessageEx>) request.getAttribute(Constants.ATTR_MESSAGESET);
%>

<script src="http://widgets.twimg.com/j/2/widget.js"></script>

<script type="text/javascript">
var tab = {
	init: function(){
		var tabs = this.setup.tabs;
		var pages = this.setup.pages;
		
		for(i=0; i<pages.length; i++) {
			if(i !== 0) pages[i].style.display = 'none';
			tabs[i].onclick = function(){ tab.showpage(this); return false; };
		}
	},
	
	showpage: function(obj){
		var tabs = this.setup.tabs;
		var pages = this.setup.pages;
		var num;
		
		for(num=0; num<tabs.length; num++) {
			if(tabs[num] === obj) break;
		}
		
		for(var i=0; i<pages.length; i++) {
			if(i == num) {
				pages[num].style.display = 'block';
				tabs[num].className = 'present';
			}
			else{
				pages[i].style.display = 'none';
				tabs[i].className = null;
			}
		}
	}
}
</script>

<div id="tab-wrapper">
<ul id="tab">
	<li class="present" id="tab-a1"><a href="#news1" _fcksavedurl="#news1"　title="コメントボード">掲示板</a></li>
	<li><a href="#news2" _fcksavedurl="#news2"　title="管理者からのメッセージ">管理者から</a></li>
	<li><a href="#news3" _fcksavedurl="#news3" title="このイベントのハッシュタグがついたTweet">みんなのTweet</a></li>
</ul>
<div id="news1" class="rad">
	<h2></h2>
	<div id="event-comments-comment" class="event-comments rad">
		<s:form action="removeComment" id="removeCommentForm" name="removeCommentForm">
			<%= Helper.token() %>
			<s:hidden id="removeCommentId" name="commentId" value="" />
			<s:hidden name="eventId" value="%{eventId}" />
		</s:form>
		<script>
			function removeComment(commentId) {
				document.removeCommentForm.commentId.value = commentId;
				document.removeCommentForm.submit();
			}
		</script>
		<% for (CommentEx comment : comments) { %>
			<% if (comment == null) { continue; } %>
			<div class="comment">
				<p><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>"><%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
				: <%= Helper.readableDate(comment.getCreatedAt()) %>
				<% if (user != null && (event.hasPermission(user, UserPermission.EVENT_REMOVE_COMMENT) || user.getId().equals(comment.getUserId()))) { %>
					<a href="#" title="コメントを削除" onclick="removeComment('<%= h(comment.getId()) %>')">[x]</a>
				<% } %></p>
				<% if (comment.isHTML()) { %>
				    <%= Helper.cleanupHTML(comment.getComment()) %>
				<% } else { %>
				    <p><%= h(comment.getComment()) %></p>
				<% } %>
			</div>
		<% } %>		
	</div>
		<div class="comment-form">
	        <% if (user != null) { %>
		        <s:form action="comment">
					<%= Helper.token() %>
			        <s:hidden name="eventId" value="%{eventId}" /><br /><strong>Your comment:</strong><br />
			        <textarea id="commentForm-commentEdit" name="comment"></textarea><br />
			        <%-- <s:checkbox name="alsoCommentsToTwitter" />コメントを twitter にも同時投稿する (まだ動きません)<br /> --%>
			        <s:submit type="image" src="%{#request.contextPath}/images/postcomment.png" value="コメントを投稿"  />
			    </s:form>           	
			<% } else { %>
	            <p>コメントを投稿するにはログインしてください。</p>
			<% } %>
		</div>
	
</div>
<div id="news2" class="rad">
	<h2>管理者からのメッセージ</h2>
	<div class="event-comments">
	<% for (DirectMessageEx message : messages) { %>
		<div class="comment">
			<p><a href="<%= request.getContextPath() %>/users/<%= h(message.getUserId()) %>"><%= h(message.getSender().getScreenName()) %></a>
			: <%= Helper.readableDate(message.getCreatedAt()) %></p>
			<p><%= h(message.getMessage()) %></p>
		</div>	
	<% } %>
	</div>
</div>
<div id="news3" class="rad">
<h2>このイベントに関するTweet</h2>
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
	<% } %>
</div>

<script type="text/javascript">
  tab.setup = {
	tabs: document.getElementById('tab').getElementsByTagName('li'),
	
	pages: [
		document.getElementById('news1'),
		document.getElementById('news2'),
		document.getElementById('news3')
	]
}
tab.init();
</script>
</div>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
tinyMCE.init({
    theme: "advanced",
    mode: "exact",
    elements: "commentForm-commentEdit",
    language: "ja",
    width: "550",
    
    plugins: "safari,searchreplace,spellchecker,style,table,xhtmlxtras",
    
    theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
    theme_advanced_buttons2: "cut,copy,paste,|,bullist,numlist,|,outdent,indent,blockquote,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor",
    theme_advanced_buttons3: "tablecontrols,|,hr,|,sub,sup,|,styleprops,spellchecker",

    theme_advanced_toolbar_location: "top",
    theme_advanced_toolbar_align: "left",
    theme_advanced_statusbar_location: "bottom",
    theme_advanced_resizing: true
});
</script>

☆
<div class="comment">
　<p><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>">
　　　　　<%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
☆