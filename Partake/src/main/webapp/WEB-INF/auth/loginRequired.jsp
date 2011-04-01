<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.Helper.h"%>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>ログインが必要です</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div id="login-content">

<h1 id="pastel-line10ji"><img src="<%= request.getContextPath() %>/images/line-pink.png" alt="">ログインが必要です</h1>


<div class="setting-subtitle"> 		
<h2><img id="" src="<%= request.getContextPath() %>/images/bird1.png" alt="">Twitter アカウント でログインしましょう。</h2>
</div>

<div class="setting-set">	
<p>PARTAKE では登録は必要ありません。お手持ちの Twitter アカウント でログインすることができます。<br>次のボタンをクリックしてログインしてください。</p>

<img id="" src="<%= request.getContextPath() %>/images/click.png" alt="">

<% String url = (String)request.getAttribute(Constants.ATTR_REDIRECTURL); %>
<p><a href="<%= request.getContextPath() %>/auth/loginByTwitter?redirectURL=<%= url != null ? h(url) : "" %>"><img src="../images/signinwithtwitter.png" alt="Sign in with twitter"  class="cler" /></a></p>
</div>

<div class="setting-subtitle">
<h2><img id="" src="<%= request.getContextPath() %>/images/bird2.png" alt="">Twitter アカウントを持っていませんか？</h2>
</div>

<div class="setting-set"> 
<p>PARTAKE では、Twitter アカウントをログインに使用します。</p>
<p><a href="https://twitter.com/signup">このページで  Twitter のアカウントを取得してください。</a></p>
</div>

</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>