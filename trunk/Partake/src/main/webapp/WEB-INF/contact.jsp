<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.util.Util.h"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>お問い合わせ</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>お問い合わせ</h1>

<p>お問い合わせ、ご要望などは、twitter で <a href="http://twitter.com/partakein">@partakein</a> までお送りください。</p><br>

<h2>このアプリケーションについて</h2>

<p>RDB が向くはずの分野に NoSQL を使ったらどうなるか、そういう好奇心でこのアプリケーションは開発されています。</p>
<p>PARTAKE は、バックエンドに Cassandra を採用しています。</p>

<div class="biglogo">
<img src="<%= request.getContextPath() %>/images/works-biglogo.jpg">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="<%= request.getContextPath() %>/images/aiit-biglogo.gif">
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>