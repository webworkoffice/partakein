<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
    
    
<!DOCTYPE html>
<html lang="ja">
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>パスコードを入れてください</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<h1>イベントを表示するためにパスコードを入れてください。</h1>

<s:form method="post" action="%{#request.contextPath}/events/passcode">
	<s:token />
    <s:hidden id="eventId" name="eventId" value="%{eventId}" />
    <label for="passcode">PassCode:</label><s:textfield id="passcode" name="passcode" label="Passcode"/><br />
    <s:submit />
</s:form>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>