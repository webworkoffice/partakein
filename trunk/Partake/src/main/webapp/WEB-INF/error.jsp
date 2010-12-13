<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>エラーが発生しました [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>エラーが発生しました</h1>

<p>ご迷惑をおかけしております。操作をやり直してみたください。それでもエラーが発生する場合、 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。
あるいは、<a href="http://code.google.com/p/partakein/issues/list">Issue Tracker</a> へ登録していただいてもかまいません。(日本語でも英語でもかまいません。)</p>
<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>