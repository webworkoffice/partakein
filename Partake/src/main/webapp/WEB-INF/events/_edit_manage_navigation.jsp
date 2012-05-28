<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.controller.base.permission.EventRemovePermission"%>
<%@page import="in.partake.controller.base.permission.EventEditPermission"%>
<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.model.UserTicketEx"%>
<%@page import="in.partake.model.EventTicketHolderList"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.model.dto.Event"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.model.dto.auxiliary.ParticipationStatus"%>
<%@page import="in.partake.model.EventCommentEx"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.model.UserEx"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    String redirectURL = action.getRedirectURL();
    if (redirectURL == null)
        redirectURL = action.getCurrentURL();
%>

<div class="subnav subnav-fixed">
    <ul class="nav nav-pills nav-stacked-if-phone">
        <li <%= "basic".equals(request.getParameter("NAVIGATION")) ? "class='active'" : "" %>><a href="/events/edit/basic/<%= h(event.getId()) %>">基本内容</a></li>
        <li <%= "ticket".equals(request.getParameter("NAVIGATION")) ? "class='active'" : "" %>><a href="/events/edit/ticket/<%= h(event.getId()) %>">チケット</a></li>
        <li <%= "enquete".equals(request.getParameter("NAVIGATION")) ? "class='active'" : "" %>><a href="/events/edit/enquete/<%= h(event.getId()) %>">アンケート</a></li>
        <li <%= "privacy".equals(request.getParameter("NAVIGATION")) ? "class='active'" : "" %>><a href="/events/edit/privacy/<%= h(event.getId()) %>">プライバシー</a></li>
        <li class="pull-right"><a href="/events/<%= h(event.getId()) %>">編集を終了</a></li>
    </ul>
</div>

