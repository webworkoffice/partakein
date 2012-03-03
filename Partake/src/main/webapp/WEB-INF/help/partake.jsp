<%@page import="in.partake.resource.I18n"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>

<html>
<head>
	<jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
	<title>PARTAKE とは？ - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
	<h1>PARTAKE とは？</h1>
</div>

<div class="row">
	<h2>PARTAKE について</h2>
	<p>PARTAKE はイベントの告知・宣伝・参加が簡単にできるウェブサービスです。</p>
	
	PARTAKE!
1. イベントの告知ページを作りましょう
イベントの告知ページを簡単につくれます。[サンプル]
Twitterでの宣伝も簡単です。
2. 参加者の管理が簡単です
参加状況の把握、参加者リストの印刷などができます。
参加者にメッセージを送信できます。
3. イベントを検索して自分も参加しましょう
楽しそうなイベントを検索して参加しましょう。
新着イベントの RSS / iCal を配信中。
新着イベントをつぶやく公式ボットもいます。
ご要望をお聞きしています
ご要望・バグ報告は Issue Tracker まで。あるいは、
@partakein まで tweet をお願いします。
開発者を募集中。PARTAKE のソースは Google Code 
で(一部の画像を除き)公開中です。
FAQ はこちら。
</div>



<div class="top-explanations">
    <h2>PARTAKE!</h2>
	<div class="top-explanation">
		<h3><%= I18n.t("page.toppage.explanation.1") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.1.page") %><a href="<%= request.getContextPath() %>/events/demo">[<%= I18n.t("common.sample") %>]</a></li>
			<li><%= I18n.t("page.toppage.explanation.1.announcement") %></li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<h3><%= I18n.t("page.toppage.explanation.2") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.2.print") %></li>
			<li><%= I18n.t("page.toppage.explanation.2.message") %></li>
		</ul>
	</div>
	
	<div class="top-explanation">
		<h3><%= I18n.t("page.toppage.explanation.3") %></h3>
		<ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.3.search") %></li>
			<li><a href="/feed/"><%= I18n.t("page.toppage.explanation.3.feed") %></a></li>
			<li><a href="http://twitter.com/partake_bot"><%= I18n.t("page.toppage.explanation.3.bot") %></a></li>
		</ul>
	</div>
	
	<div class="top-explanation">
	   <h3><%= I18n.t("page.toppage.explanation.4") %></h3>
	   <ul class="top-feature">
			<li><%= I18n.t("page.toppage.explanation.4.issue") %></li>
			<li><%= I18n.t("page.toppage.explanation.4.developer") %></li>
			<li><%= I18n.t("page.toppage.explanation.4.faq") %></li>
	   </ul>
	</div>
</div>


<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>