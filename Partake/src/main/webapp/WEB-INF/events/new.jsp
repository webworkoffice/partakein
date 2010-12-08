<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	   
	<title>新しいイベントを作成します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1 id="event-form-titleline"><img src="<%= request.getContextPath() %>/images/line-yellow.png" alt="">新しいイベントを作成します</h1>

<s:form method="post" action="%{#request.contextPath}/events/create" enctype="multipart/form-data">
	<s:token />
	<%@ include file="/WEB-INF/events/inner-form.jsp" %>

	<s:submit id="event-edit-submit" type="image" src="%{#request.contextPath}/images/button-eventform.png" label="イベントを作成する" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/mce-init.js"></script>
</body>
</html>
