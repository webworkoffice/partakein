<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>権限がありません [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
    <jsp:param name="NO_HEADER_MESSAGES" value="true" />
</jsp:include>
<div class="container"><div class="content-body">

<div class="page-header">
    <h1>権限がありません</h1>
</div>

<p>実行権限がない操作を行おうとしました。</p>
<p><a href="<%= request.getContextPath() %>/">トップに戻る</a></p>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
