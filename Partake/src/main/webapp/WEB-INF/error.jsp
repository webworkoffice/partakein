<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>エラーが発生しました。</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>エラーが発生しました</h1>

<p>ご迷惑をおかけしております。操作をやり直してみたください。それでもエラーが発生する場合、 twitter にて <a href="http://twitter.com/partakein">@partakein</a> までお問い合わせください。</p>
<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>