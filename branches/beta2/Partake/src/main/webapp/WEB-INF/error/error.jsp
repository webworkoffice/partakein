<%@page import="in.partake.controller.action.errorpage.StaticErrorPageAction"%>
<%@page import="in.partake.resource.UserErrorCode"%>
<%@page import="in.partake.resource.ServerErrorCode"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.session.PartakeSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>エラーが発生しました [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
    <jsp:param name="NO_HEADER_MESSAGES" value="true" />
</jsp:include>
<div class="container">

<div class="page-header">
    <h1>エラーが発生しました</h1>
</div>

<p>ご迷惑をおかけしております。サーバーの内部でエラーが発生しました。</p>

<p>次のような原因が考えられます。</p>
<ul>
    <li>twitter が不安定であるなどの理由で、twitter へのリクエストが正常に動作しなかった。</li>
    <li>データベースとの接続がうまくいっていない。</li>
</ul>

<p>操作をやり直してみてください。それでもエラーが発生する場合 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。</p>
<p>もしくは、<a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> へ登録していただいてもかまいません。(日本語でも英語でもかまいません。)</p>

<%
    StaticErrorPageAction action = (StaticErrorPageAction) request.getAttribute(Constants.ATTR_ACTION);
    ServerErrorCode serverErrorCode = action.getServerErrorCode();
%>

<h3>エラー詳細 (上級者向け / 管理者向け)</h3>
<p>このエラーは、ユーザーからのリクエストを正常に受け付けたが処理中にサーバー内部で予期せぬエラーが発生した場合に発生します。</p>
<% if (serverErrorCode != null) { %>
    <p>サーバー内部のエラー詳細は以下の通りでした。</p>
    <dl>
        <dt>エラーコード</dt><dd><%= serverErrorCode.toString() %></dd>
        <dt>エラーメッセージ</dt><dd><%= serverErrorCode.getReasonString() %></dd>
    </dl>
<% } %>

<p><a href="/">トップに戻る</a></p>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
