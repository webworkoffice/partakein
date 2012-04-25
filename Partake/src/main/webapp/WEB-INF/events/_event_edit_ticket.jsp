<%@page import="in.partake.controller.action.event.AbstractEventEditAction"%>
<%@page import="java.util.Date"%>
<%@page import="in.partake.base.TimeUtil"%>
<%@page import="in.partake.controller.action.AbstractPartakeAction"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>
<%@page import="in.partake.controller.action.event.EventEditAction"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.KeyValuePair"%>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    AbstractEventEditAction action = (AbstractEventEditAction) request.getAttribute(Constants.ATTR_ACTION);
    EventEx event = action.getEvent();
    assert event != null;
%>


