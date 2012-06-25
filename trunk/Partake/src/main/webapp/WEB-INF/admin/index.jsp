<%@page import="in.partake.controller.action.admin.AdminPageAction"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.text.NumberFormat"%>
<%
    AdminPageAction action = (AdminPageAction) request.getAttribute(Constants.ATTR_ACTION);
%>
<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>Administrator Mode</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container"><div class="content-body">

<div class="page-header">
    <h1>Administrator Mode</h1>
</div>


<div class="row">
    <div class="span12">
        <h2>Count of users</h2>
        <dl>
            <dt>User</dt><dd><%= action.getCountUser() %></dd>
        </dl>

        <h2>Count of events</h2>
        <dl>
            <dt>event</dt><dd><%= action.getCountEvent() %></dd>
            <dt>public event</dt><dd><%= action.getCountPublicEvent() %></dd>
            <dt>private event</dt><dd><%= action.getCountPrivateEvent() %></dd>
            <dt>published event</dt><dd><%= action.getCountPublishedEvent() %></dd>
            <dt>draft event</dt><dd><%= action.getCountDraftEvent() %></dd>
        </dl>

<%-- 		<h2>Count of Hatena bookmarks</h2>
        <dl>
            <dt>Sum of all pages</dt><dd><%= format.format(hatenaBookmarkCount) %></dd>
        </dl>
 --%>	</div>
    <div class="span12">
        <h2>いろんなリンク</h2>
        <p><a href="/admin/recreateEventIndex">Luceneインデックス の再生成</a></p>
    </div>
</div>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
