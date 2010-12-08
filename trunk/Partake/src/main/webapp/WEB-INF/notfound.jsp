<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>リソースが見つかりませんでした</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header-nomessage.jsp" flush="true" />

<h1>リソースが見つかりませんでした</h1>

<p>お探しのリソースが見つかりませんでした。URL を確認してください。</p>
<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>