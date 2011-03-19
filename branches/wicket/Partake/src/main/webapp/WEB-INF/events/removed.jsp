<%@page import="in.partake.resource.Constants"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>

<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>このイベントは削除されました</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true">
	<jsp:param value="NO_HEADER_MESSAGES" name="true" />
</jsp:include>

<h1>このイベントは削除されました</h1>

<p>お探しのイベントは管理者によって削除されました。</p>
<p><a href="<%= request.getContextPath() %>/"><strong>トップに戻る</strong></a></p>
<img class="musangas" src="<%= request.getContextPath() %>/images/musangas.png" alt="" />
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>