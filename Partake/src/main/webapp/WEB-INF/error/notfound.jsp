<%@page import="in.partake.resource.Constants"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>リソースが見つかりませんでした</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
    <jsp:param name="NO_HEADER_MESSAGES" value="true" />
</jsp:include>
<div class="container">

<div class="page-header">
    <h1>リソースが見つかりませんでした</h1>
</div>

<p>お探しのリソースが見つかりませんでした。URL を確認してください。</p>
<img class="musangas" src="/images/sorry.png" alt="" />

<p><a href="/">トップに戻る</a></p>
</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
