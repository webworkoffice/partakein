<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>権限がありません [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
	<jsp:param value="NO_HEADER_MESSAGES" name="true" />
</jsp:include>

<div class="page-header">
	<h1>権限がありません</h1>
</div>

<div class="row">
	<div class="span12">
		<p>実行権限がない操作を行おうとしました。</p>
		<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>
	</div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>