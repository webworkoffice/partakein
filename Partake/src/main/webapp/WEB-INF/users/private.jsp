<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.base.Util.h"%>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>    
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>このユーザーはプライベートモードに設定されています</title>
</head>
<body>

<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1><img src="<%= request.getContextPath() %>/images/private.png" alt="" />このユーザーはプライベートモードに設定されています</h1>

<p>このユーザーのマイページは「非公開」に設定されているため、表示することが出来ません。</p>
<img class="musangas" src="<%= request.getContextPath() %>/images/sorry.png" alt="" />

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>