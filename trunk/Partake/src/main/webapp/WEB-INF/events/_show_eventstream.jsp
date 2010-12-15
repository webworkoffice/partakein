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
<li class="present" id="tab-a1"><a href="#news1" _fcksavedurl="#news1">Comments</a></li>
<li><a href="#news2" _fcksavedurl="#news2">Twitter Feed!</a></li>
</ul>
<div id="news1">
<h2>Comments</h2>
<div class="event-comments">
		<% while (commentIterator.hasNext()) { %>
			<% CommentEx comment = commentIterator.next(); if (comment == null) { continue; } %>
			<div class="comment">
				<p><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>"><%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
				: <%= Helper.readableDate(comment.getCreatedAt()) %></p>
				<p><%= h(comment.getComment()) %></p>
			</div>
		<% } %>
		</div>
		<div class="comment-form">
	        <% if (user != null) { %>
		        <s:form action="comment">
			        <s:token />
			        <s:hidden name="eventId" value="%{eventId}" />Yout comment:<br>
			        <textarea id="comment" name="comment"></textarea><br />
			        <%-- <s:checkbox name="alsoCommentsToTwitter" />コメントを twitter にも同時投稿する (まだ動きません)<br /> --%>
			        <s:submit type="image" src="%{#request.contextPath}/images/postcomment.png" value="コメントを投稿"  />
			    </s:form>           	
			<% } else { %>
	            <p>コメントを投稿するにはログインしてください。</p>
			<% } %>
		</div>
	
</div>
<div id="news2">
<p>
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
	  height: 345,
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
</p>
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

