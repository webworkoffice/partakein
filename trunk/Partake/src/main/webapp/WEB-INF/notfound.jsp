<%@page import="in.partake.resource.Constants"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>リソースが見つかりませんでした</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
	<jsp:param value="NO_HEADER_MESSAGES" name="true" />
</jsp:include>

<h1>リソースが見つかりませんでした</h1>

<p>お探しのリソースが見つかりませんでした。URL を確認してください。</p>
<p><a href="<%= request.getContextPath() %>/"><strong>トップに戻る</strong></a></p>
<img class="musangas" src="<%= request.getContextPath() %>/images/musangas.png" alt="" />
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>