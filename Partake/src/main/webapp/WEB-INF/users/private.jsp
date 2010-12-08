<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.util.Util.h"%>

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

<h1>このユーザーはプライベートモードに設定されています</h1>

<p>このユーザーはプライベートモードに設定されているため、プロフィールを表示することが出来ません。</p>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>