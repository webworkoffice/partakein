<%@page import="in.partake.resource.UserErrorCode"%>
<%@page import="in.partake.servlet.PartakeSession"%>
<%@page import="static in.partake.view.util.Helper.h"%>
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

<div class="page-header">
	<h1>ユーザーからのリクエストにエラーがありました</h1>
</div>

<p>ご迷惑をおかけしております。ユーザーからのリクエストにエラーがありました。</p>

<p>このエラーは、ユーザーからのリクエストが不正であると検知された場合に発生します。次のような場合に起こる可能性があります。</p>
<ul>
	<li>多重投稿防止のためのトークンチェックにひっかかった。</li>
	<li>必要なパラメータがサーバーに渡されていない。(POST リクエストを新しいタブで開きながら投げようとしたときに起きる場合があります。)</li>
	<li>登録されていないイベント ID など、不正なパラメータが渡された。</li>
	<li>CSRF (Cross-site Request Forgery) の発生を検知し、未然に防止した。</li>
</ul>

<p>操作をやり直してみてください。それでもエラーが発生する場合 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。</p>
<p>もしくは、<a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> へ登録していただいてもかまいません。(日本語でも英語でもかまいません。)</p>

<%
    PartakeSession partakeSession = (PartakeSession) session.getAttribute(Constants.ATTR_PARTAKE_SESSION);
	UserErrorCode userErrorCode = partakeSession != null ? partakeSession.getLastUserErrorCode() : null;
%>

<h2>エラー詳細 (上級者向け / 管理者向け)</h2>
<p>このエラーは、ユーザーからのリクエストが不正であると判断された場合に発生します。</p>
<% if (userErrorCode != null) { %>
	<p>エラー詳細は以下の通りでした。</p>
	<dl>
		<dt>エラーコード</dt><dd><%= userErrorCode.toString() %></dd>
		<dt>エラーメッセージ</dt><dd><%= userErrorCode.getReasonString() %></dd>
	</dl>
<% } %>

<p><a href="/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>