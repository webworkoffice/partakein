<%@page import="in.partake.resource.Constants"%>

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
<div class="container"><div class="content-body">

<div class="page-header">
    <h1>このユーザーはプライベートモードに設定されています</h1>
</div>

<div class="row">
    <div class="span12">
        <p>このユーザーのマイページは「非公開」に設定されているため、表示することが出来ません。</p>
        <img class="musangas" src="<%= request.getContextPath() %>/images/sorry.png" alt="" />
    </div>
</div>

</div></div>
<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
