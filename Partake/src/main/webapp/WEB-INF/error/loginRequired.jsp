<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>ログインが必要です</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
    <jsp:param name="NO_HEADER_MESSAGES" value="true" />
</jsp:include>
<div class="container"><div class="content-body">
<div class="page-header">
    <h1>ログインが必要です</h1>
</div>

<p>PARTAKE では、Twitter アカウントを用いてログインすることができます。PARTAKE への登録は必要ありません。</p>

<p>次のボタンをクリックしてログインしてください。</p>

<% String url = (String)request.getAttribute(Constants.ATTR_REDIRECTURL); %>
<p><a href="<%= request.getContextPath() %>/auth/loginByTwitter?redirectURL=<%= url != null ? h(url) : "" %>"><img src="../images/signinwithtwitter.png" alt="Sign in with twitter"  class="cler" /></a></p>

<h2>Twitter アカウントを持っていませんか？</h2>

<p>PARTAKE では、Twitter アカウントをログインに使用します。</p>
<p><a href="https://twitter.com/signup">このページで  Twitter のアカウントを取得してください。</a></p>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
