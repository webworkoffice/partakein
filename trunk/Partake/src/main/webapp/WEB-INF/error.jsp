<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>エラーが発生しました [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
	<jsp:param value="NO_HEADER_MESSAGES" name="true" />
</jsp:include>

<h1>エラーが発生しました</h1>

<p>ご迷惑をおかけしております。サーバーの内部でエラーが発生しました。次のような原因が考えられます。</p>
<ul>
	<li>twitter が不安定であるなどの理由で、twitter へのリクエストが正常に動作しなかった。</li>
	<li>データベースとの接続がうまくいっていない。</li>
</ul>

<p>操作をやり直してみてください。それでもエラーが発生する場合、 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。
<br />あるいは、<a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> へ登録していただいてもかまいません。(日本語でも英語でもかまいません。)</p>

<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>