<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.resource.Constants"%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>お問い合わせ</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container"><div class="content-body">

<div class="page-header">
    <h1>お問い合わせ</h1>
</div>

<p>お問い合わせ、ご要望などは、twitter で <a href="http://twitter.com/partakein">@partakein</a> までお送りください。</p><br>

<h2>Powered By</h2>

<div class="biglogo">
<img src="<%= request.getContextPath() %>/images/works-biglogo.jpg">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="<%= request.getContextPath() %>/images/aiit-biglogo.gif">
</div>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
