<%@page import="in.partake.base.KeyValuePair"%>
<%@page import="in.partake.controller.action.event.EventSearchAction"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@ page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventSearchAction action = (EventSearchAction) request.getAttribute(Constants.ATTR_ACTION);
%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>イベント検索 - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<div class="page-header">
    <h1>イベント検索</h1>
    <p>タイトル、カテゴリ、本文などからイベントを検索します。</p>
</div>

<jsp:include page="/WEB-INF/events/_search.jsp" flush="true" />

</div>
</body>
</html>
