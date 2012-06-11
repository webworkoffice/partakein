<%@page import="in.partake.model.UserMessageEx"%>
<%@page import="in.partake.model.dto.UserReceivedMessage"%>
<%@page import="in.partake.controller.action.message.ShowAction"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.model.dto.User"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<%
    ShowAction action = (ShowAction) request.getAttribute(Constants.ATTR_ACTION);
    UserMessageEx message = action.getMessage();
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>メッセージ - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<div class="page-header">
    <h1>メッセージ</h1>
</div>

<h2><%= h(message.getMessage().getSubject()) %></h2>

<p>イベント「<%= h(message.getEvent().getTitle()) %>」に関するメッセージ</p>
<p>送信者「<%= h(message.getSender().getScreenName()) %>」</p>

<p><%= h(message.getMessage().getBody()) %></p>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
