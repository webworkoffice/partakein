<%@page import="static in.partake.view.Helper.h"%>
<%@page import="in.partake.resource.Constants"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>ユーザーからのリクエストにエラーがありました [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
	<jsp:param value="NO_HEADER_MESSAGES" name="true" />
</jsp:include>

<h1>ユーザーからのリクエストにエラーがありました。</h1>

<p>ご迷惑をおかけしております。ユーザーからのリクエストにエラーがありました。</p>

<% if (session.getAttribute(Constants.ATTR_ERROR_DESCRIPTION) != null) { %>
	<p><%= h((String) session.getAttribute(Constants.ATTR_ERROR_DESCRIPTION)) %></p>
	<% session.removeAttribute(Constants.ATTR_ERROR_DESCRIPTION); %>
<% } else {%>
	<p>残念ながら、詳細なエラーメッセージはアプリケーションによって指定されませんでした。そのうち指定されるようになるでしょう。</p> 
<% } %>

<p>(上級者向け) このエラーは、次のような場合に起こる可能性があります。</p>
<ul>
	<li>多重投稿防止のためのトークンチェックにひっかかった。</li>
	<li>必要なパラメータがサーバーに渡されていない。(POST リクエストを新しいタブで開きながら投げようとしたときに起きる場合があります。)</li>
	<li>登録されていない event ID などが渡された。</li>
	<li>CSRF (Cross-site Request Forgery) が発生した。</li>
</ul>

<p>正当なリクエストであると思われるにも関わらずエラーが発生する場合、 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。<br />
あるいは、<a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> へ登録してください。</p>
<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>