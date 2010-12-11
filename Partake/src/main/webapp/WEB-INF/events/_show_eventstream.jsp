<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.view.Helper"%>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>

<%@page import="static in.partake.util.Util.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
	EventEx event = (EventEx) request.getAttribute(Constants.ATTR_EVENT);
	DataIterator<CommentEx> commentIterator = (DataIterator<CommentEx>)(request.getAttribute(Constants.ATTR_COMMENTSET));
%>

<script src="http://widgets.twimg.com/j/2/widget.js"></script>
<div class="event-stream">
	<div class="event-comments">
		<h2><img src="<%= request.getContextPath()%>/images/comment-title.png"/></h2>
		<% while (commentIterator.hasNext()) { %>
			<% CommentEx comment = commentIterator.next(); if (comment == null) { continue; } %>
			<div class="comment">
				<p><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>"><%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
				: <%= Helper.readableDate(comment.getCreatedAt()) %></p>
				<p><%= h(comment.getComment()) %></p>
			</div>
		<% } %>
		
		<div class="comment-form">
	        <% if (user != null) { %>
		        <s:form action="comment">
			        <s:token />
			        <s:hidden name="eventId" value="%{eventId}" />
			        <textarea id="comment" name="comment"></textarea><br />
			        <%-- <s:checkbox name="alsoCommentsToTwitter" />コメントを twitter にも同時投稿する (まだ動きません)<br /> --%>
			        <s:submit type="image" src="%{#request.contextPath}/images/postcomment.png" value="コメントを投稿"  />
			    </s:form>           	
			<% } else { %>
	            <p>コメントを投稿するにはログインしてください。</p>
			<% } %>
		</div>
	</div>
	
	<div class="event-twitter-uri-stream">
<script>
new TWTR.Widget({
	  version: 2,
	  type: 'search',
	  search: '<%= h(event.getEventURL()) %>',
	  interval: 6000,
	  title: 'Twitter Hashtag Live Feed',
	  subject: '<%= h(event.getTitle()) %>',
	  width: 'auto',
	  height: 300,
	  theme: {
	    shell: {
	      background: '#8ec1da',
	      color: '#ffffff'
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
	  height: 300,
	  theme: {
	    shell: {
	      background: '#8ec1da',
	      color: '#ffffff'
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