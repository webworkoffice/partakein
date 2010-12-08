<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<% 
    String callingURL = request.getParameter("callingURL");
    boolean usesToken = "true".equals(request.getParameter("usesToken"));
%>

<form method="post" action="<%= callingURL %>" class="openid"><div>
    <% if (usesToken) { %><s:token /> <% } %>
    <ul class="providers">
        <li class="direct" title="Yahoo"><img src="<%= request.getContextPath() %>/images/yahoo-openid.png" alt="icon" /><span>http://yahoo.co.jp</span></li>
        <li class="direct" title="Google"><img src="<%= request.getContextPath() %>/images/google-openid.png" alt="icon" /><span>https://www.google.com/accounts/o8/id</span></li> 
        <li class="direct" title="mixi"><img src="<%= request.getContextPath() %>/images/mixi-openid.png" alt="icon" /><span>https://mixi.jp</span></li> 
        <li class="direct" title="Livedoor"><img src="<%= request.getContextPath() %>/images/livedoor-openid.png" alt="icon" /><span>http://livedoor.com/</span></li>
        <li class="username" title="HatenaのID"><img src="<%= request.getContextPath() %>/images/hatena-openid.png" alt="icon" /><span>http://www.hatena.ne.jp/<strong>username</strong>/</span></li> 
        <li class="openid" title="OpenID"><img src="<%= request.getContextPath() %>/images/openid-openid.png" alt="icon" /><span><strong>http://{your-openid-url}</strong></span></li> 
    </ul></div>
 
    <fieldset>
        <label for="openid_username"><span>user name</span> を入れてください。</label> 
        <div><span></span><input type="text" name="openid_username" /><span></span> 
        <input type="submit" value="Login" /></div> 
    </fieldset>

    <fieldset>
        <label for="openid_identifier"><a href="http://www.openid.ne.jp/">OpenID</a> を入れてください。</label> 
        <div><input type="text" name="openid_identifier" /> 
        <input type="submit" value="Sign in" /></div> 
    </fieldset> 
</form>
