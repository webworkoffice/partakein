<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>イベントを編集します</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1 id="pastel-line10ji"><img src="<%= request.getContextPath() %>/images/line-orange.png" alt="">イベントを編集します</h1>

<s:form method="post" action="%{#request.contextPath}/events/commit" enctype="multipart/form-data"><%-- create じゃなくて commit なのに注意 --%>
	<s:token />
	<s:hidden id="eventId" name="eventId" value="%{eventId}"/><%-- new.jsp とここが違う。なんか共通化するとエラーがでる。なんで？ --%>
	<%@ include file="/WEB-INF/events/inner-form.jsp" %>

    <s:submit id="event-edit-submit" type="image" src="%{#request.contextPath}/images/button-eventedit.png" label="イベント情報を変更する" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/mce-init.js"></script>
</body>
</html>
