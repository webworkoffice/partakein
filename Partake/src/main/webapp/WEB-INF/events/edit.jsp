<%@page import="in.partake.view.util.Helper"%>
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

<div class="page-header">
	<h1>イベントを編集します</h1>
</div>

<form method="post" class="form-horizontal" action="commit" enctype="multipart/form-data">
	<%= Helper.tokenTags() %>
	<s:hidden id="eventId" name="eventId" value="%{eventId}"/><%-- new.jsp とここが違う。なんか共通化するとエラーがでる。なんで？ --%>
	<div class="row">
		<div class="span9">
			<%@ include file="/WEB-INF/events/_edit_innerform.jsp" %>
		</div>
		<div class="span1">
			&nbsp;
		</div>
		<div class="span3">
			<div class="fixed span3">
				<input type="submit" class="btn btn-primary" value="イベントを保存する" />
				<p class="help-block">イベントをドラフトとして保存します。保存しただけではまだ公開されません。</p>
				<input type="submit" class="btn btn-danger" value="イベントを公開する" />
				<p class="help-block">イベントを公開します。</p>
			</div>
			&nbsp;
		</div>
	</div>
</form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
