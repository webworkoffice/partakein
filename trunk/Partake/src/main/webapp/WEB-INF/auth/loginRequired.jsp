<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.util.Util.h"%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>ログインが必要です</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div id="login-content">

<h1><img id="login-titleline" src="<%= request.getContextPath() %>/images/line-yellow.png" alt="">ログインが必要です</h1>

<p>ここから先に進むにはログインが必要です。</p>

<div class="setting-subtitle"> 		
<h2><img id="" src="<%= request.getContextPath() %>/images/bird1.png" alt="">Twitter account でログインしましょう。</h2>
</div>

<div class="setting-set">	
<p>Partake では登録は必要ありません。お手持ちの twitter アカウント でログインすることができます。<br>次のボタンをクリックしてログインしてください。</p>

<img id="" src="<%= request.getContextPath() %>/images/click.png" alt="">

<% String url = (String)request.getAttribute(Constants.ATTR_REDIRECTURL); %>
<p><a href="<%= request.getContextPath() %>/auth/loginByTwitter?redirectURL=<%= url != null ? h(url) : "" %>"><img src="../images/signinwithtwitter.png" alt="Sign in with twitter" /></a></p>
</div>

<div class="setting-subtitle">
<h2><img id="" src="<%= request.getContextPath() %>/images/bird2.png" alt="">Twitter アカウントを持っていませんか？</h2>
</div>

<div class="setting-calendar"> 
<p>Partake では、twitter アカウントをログインに使用します。</p>
<p><a href="https://twitter.com/signup">このページで  Twitter のアカウントを取得してください。</a></p>
</div>

</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>