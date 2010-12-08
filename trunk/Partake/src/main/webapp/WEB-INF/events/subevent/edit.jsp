<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>サブイベントを編集する</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1 id="event-edit-titleline"><img src="<%= request.getContextPath() %>/images/line-orange.png" alt="">サブイベントを編集する</h1>

<s:form method="post" action="%{#request.contextPath}/events/commitSubevent" enctype="multipart/form-data"><%-- create じゃなくて commit なのに注意 --%>
	<s:token />
	<s:hidden id="id" name="id" value="%{id}"/><%-- new.jsp とここが違う。なんか共通化するとエラーがでる。なんで？ --%>
	<%@ include file="/WEB-INF/events/subevent/inner-form.jsp" %>
    <s:submit id="event-edit-submit" type="image" src="%{#request.contextPath}/images/button-eventedit.png" label="イベント情報を変更する" />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/mce-init.js"></script>
</body>
</html>
