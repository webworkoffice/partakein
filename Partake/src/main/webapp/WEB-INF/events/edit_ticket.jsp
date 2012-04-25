<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);
    EventEx event = action.getEvent();
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>チケットの編集</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true" />

<div class="page-header">
    <h1>チケットの編集</h1>
</div>

<p>イベントのチケットを編集します。</p>

<div id="tickets" class="control-group form-inline">
    <label class="control-label">チケット１</label>
    <div class="controls">
        <table class="table">
            <colgroup>
                <col class="span2" /><col class="span4" /><col class="span1" /><col class="span4" />
            </colgroup>
            <tr><td>チケット名</td>
                <td colspan="3">
                    <input type="hidden" name="ticketId[]" />
                    <input type="text" name="ticketName[]" class="span6" />
                </td>
            </tr>
            <tr><td>募集期間</td>
                <td>
                    <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="fromNow" checked />今から</label><br>
                    <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="beforeNDays" />イベントの <input type="text" class="span1" name="ticketApplicationStartDay[]" value="0" /> 日前から</label><br>
                    <label class="radio"><input type="radio" name="ticketApplicationStart[]" value="custom" />次の期間</label>
                </td>
                <td>
                    〜
                </td>
                <td>
                    <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="justBeforeEvent" checked />イベントが始まるまで</label><br>
                    <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="justAfterEvent" checked />イベントが終わるまで</label><br>
                    <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="beforeNDays" />イベントの <input type="text" class="span1" name="ticketApplicationPeriodEndDay[]" value="0" /> 日前まで</label><br>
                    <label class="radio"><input type="radio" name="ticketApplicationEnd[]" value="custom" />次の期間</label>
                </td>
            </tr>
            <tr><td>価格</td><td colspan="3">
                <label class="radio"><input type="radio" name="ticketPrice[]" value="free" checked />無料</label>
                <label class="radio"><input type="radio" name="ticketPrice[]" value="nonFree" />
                    <input type="text" class="span2" name="ticketPriceText[]" value="1000" />円 (会場で支払い)</label>
            </td></tr>
            <tr><td>数量</td><td colspan="3">
                <label class="radio"><input type="radio" name="ticketAmount[]" value="unlimited" checked />無制限</label>
                <label class="radio"><input type="radio" name="ticketAmount[]" value="limited" />
                    <input type="text" class="span2" name="ticketAmountText[]" value="10" />枚</label>
            </td></tr>
            <tr><td colspan="4">削除</td></tr>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
