<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="in.partake.resource.Constants"%>
<%@ page import="static in.partake.util.Util.h"%>

<script type="text/javascript">  $(function() { $("form.openid:eq(0)").openid(); });</script>

<% 
    String redirectURL = (String)request.getAttribute(Constants.ATTR_REDIRECTURL);
    if (redirectURL == null) {
        redirectURL = (String)request.getAttribute(Constants.ATTR_CURRENT_URL); 
    } 
%>

<%-- display: none をここにかくのは見苦しいのでなんとかしたい --%>
<div id="signin-dialog" title="Sign in with OpenID" style="display: none">
<div id="openid-dialog">
<h1><img src="<%= request.getContextPath() %>/images/openidico.png" alt="" />
 OpenIDでログイン
</h1>
<p>なんらかの理由で Twitter でログインできない場合に、Open ID でもログインすることができます。</p>
<p><span><span>T</span>witter ID と Open ID の 結び付け設定 はお済みですか？<br /></span>
まだの方は、Twitterでログインして設定してください。<br />
<span><span>T</span>witter</span> をメッセージ基盤として利用しているため、Twitter アカウントとの結びつけが必要となります。
</p>
<div class="bold">どのIDでログインしますか？</div>
    <jsp:include page="/WEB-INF/internal/_openid_innerform.jsp" >
        <jsp:param name="callingURL" value="/auth/loginByOpenID" />
        <jsp:param name="usesToken" value="false" />
    </jsp:include>
</div></div>