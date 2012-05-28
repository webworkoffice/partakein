<%@page import="in.partake.controller.action.user.ShowAction"%>
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
    UserEx user = action.getUser();
%>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title><%=h(user.getTwitterScreenName())%> - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<div class="page-header">
    <h1><%=h(user.getTwitterScreenName())%></h1>
</div>

<div class="row tabbable">
    <div class="span6"><div class="well" style="padding: 8px 0;">
        <ul class="nav nav-list tabs">
            <li class="nav-header">イベント</li>
            <li class="active"><a href="#event-owner" data-toggle="tab">主催イベント</a></li>
            <li><a href="#event-enrolled" data-toggle="tab">登録イベント</a></li>
        </ul>
    </div></div>
    <div class="span18 tab-content">
        <div class="tab-pane active" id="event-owner">
            <jsp:include page="/WEB-INF/users/_event_table.jsp" flush="true">
                <jsp:param name="ident" value="owner" />
                <jsp:param name="queryType" value="owner" />
            </jsp:include>
        </div>
        <div class="tab-pane" id="event-enrolled">
            <jsp:include page="/WEB-INF/users/_enrollment_table.jsp" flush="true">
                <jsp:param name="ident" value="enrollment" />
            </jsp:include>
        </div>
    </div>
</div>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
