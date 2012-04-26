<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Map"%>
<%@page import="in.partake.model.EventRelationEx"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.Util"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.EnrollmentEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.CommentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="static in.partake.view.util.Helper.escapeTwitterResponse"%>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();

%>

<h3>主催者</h3>
<p><a href="<%= request.getContextPath() %>/users/<%= h(event.getOwnerId()) %>">
    <img src="<%= h(event.getOwner().getTwitterLinkage().getProfileImageURL()) %>" class="profile-image" alt="" width="20" height="20" />
    <% if (event.getOwner().getTwitterLinkage().getName() != null) { %>
        <%= escapeTwitterResponse(event.getOwner().getTwitterLinkage().getName()) %>
        (<%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>)
    <% } else { %>
        <%= h(event.getOwner().getTwitterLinkage().getScreenName()) %>
    <% } %>
</a></p>

<h3>開催日時</h3>
<p><%= Helper.readableDuration(event.getBeginDate(), event.getEndDate()) %>
<span class="label label-edit">日時を編集</span>
</p>

<h3>開催場所</h3>
<p id="place-show">
    会場：
    <span id="place-content"><%= h(event.getPlace()) %></span>
    <span id="place-edit" class="label label-edit edit-button">会場を編集</span>
</p>
<form id="place-form" action="javascript:$('#place-submit').click()" style="display: none;">
    <p><input type="text" class="span4" name="place" id="place-input" value="" placeholder="会場：　「武道館」「株式会社 PARTAKE ビル 2F」など"></p>
    <div class="edit-form-buttons">
        <input type="button" value="キャンセル" class="btn edit-cancel-button">
        <input id="place-submit" type="button" value="保存" class="btn edit-save-button">
    </div>
</form>
<script>
$('#place-edit').click(function(e) {
    $('#place-input').val($('#place-content').text());
});

$('#place-submit').click(function(e) {
    var form = $(enclosingForm(this));
    var id = removeSuffix(form.attr("id"), "-form");

    partake.event.modify(eventId, { place: $('#place-input').val() })
    .done(function (json) {
        $('#place-content').text($('#place-input').val());
        $('#' + id + '-form').hide();
        $('#' + id + '-show').show();
    })
    .fail(partake.defaultFailHandler);
});
</script>

<p id="address-show">
    住所：
    <span id="address-content"><%= h(event.getAddress()) %></span>
    <span id="address-edit" class="label label-edit edit-button">住所を編集</span>
</p>
<form id="address-form" action="javascript:$('#address-submit').click()" style="display: none;">
    <p><input type="text" class="span4" name="address" id="address-input" value="" placeholder="住所：　「東京都千代田区1-1-1」など"></p>
    <div class="edit-form-buttons">
        <input type="button" value="キャンセル" class="btn edit-cancel-button">
        <input id="address-submit" type="button" value="保存" class="btn edit-save-button">
    </div>
</form>
<script>
$('#address-edit').click(function(e) {
    $('#address-input').val($('#address-content').text());
});

$('#address-submit').click(function(e) {
    var form = $(enclosingForm(this));
    var id = removeSuffix(form.attr("id"), "-form");

    partake.event.modify(eventId, { address: $('#address-input').val() })
    .done(function (json) {
        $('#address-content').text($('#address-input').val());

        var address = $('#address-input').val();
        var mapURL = 'http://maps.google.co.jp/maps?q=' + encodeURIComponent(address);
        $('#address-anchor').attr('href', mapURL);

        var imgSrc = 'http://maps.google.co.jp/maps/api/staticmap?size=280x200&center=' +
            encodeURIComponent(address) +
            '&zoom=17&sensor=false';
        $('#address-img').attr('src', imgSrc);

        $('#' + id + '-form').hide();
        $('#' + id + '-show').show();
    })
    .fail(partake.defaultFailHandler);
});
</script>

<div class="event-map"><a id="address-anchor" href="http://maps.google.co.jp/maps?q=<%= h(Util.encodeURIComponent(event.getAddress())) %>">
    <img id="address-img" src="http://maps.google.co.jp/maps/api/staticmap?size=280x200&center=<%= h(Util.encodeURIComponent(event.getAddress())) %>&zoom=17&sensor=false" />
</a></div>

<h3>参考情報</h3>
<p id="url-show">
    URL ： <a id="url-content" href=""><%= h(event.getUrl()) %></a>
    <span id="url-edit" class="label label-edit edit-button">URLを編集</span>
</p>
<form id="url-form" action="javascript:$('#url-submit').click()" style="display: none;">
    <p><input type="text" class="span4" name="url" id="url-input" value="" placeholder="URL: http://partake.in/ など"></p>
    <div class="edit-form-buttons">
        <input type="button" value="キャンセル" class="btn edit-cancel-button">
        <input id="url-submit" type="button" value="保存" class="btn edit-save-button">
    </div>
</form>
<script>
$('#url-edit').click(function(e) {
    $('#url-input').val($('#url-content').text());
});

$('#url-submit').click(function(e) {
    var form = $(enclosingForm(this));
    var id = removeSuffix(form.attr("id"), "-form");

    partake.event.modify(eventId, { url: $('#url-input').val() })
    .done(function (json) {
        $('#url-content').text($('#url-input').val());
        $('#url-content').attr('href', $('#url-input').val());
        $('#' + id + '-form').hide();
        $('#' + id + '-show').show();
    })
    .fail(partake.defaultFailHandler);
});
</script>

<%--
<% if (event.getRelations() != null && !event.getR.isEmpty()) { %>
    <h3>関連イベント</h3>
    <% for (EventRelationEx eventRelation : eventRelations) { %>
        <img src="/images/mark.png" class="" alt="" />
        <a href="<%= h(eventRelation.getEvent().getEventURL()) %>"><%= h(eventRelation.getEvent().getTitle()) %></a>
        <p><% if (eventRelation.isRequired()) { %><img src="<%= request.getContextPath() %>/images/attention.png" alt="" /> この関連イベントへの参加が必須です<% } %>
            <% if (eventRelation.hasPriority()) { %><img src="<%= request.getContextPath() %>/images/star.png" alt="" /> 参加すると本イベントへ優先的に参加可能<% } %>
            </p>
    <% } %>
<% } %>
--%>

