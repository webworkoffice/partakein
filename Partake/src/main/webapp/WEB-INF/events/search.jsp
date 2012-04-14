<%@page import="in.partake.base.KeyValuePair"%>
<%@page import="in.partake.controller.action.event.EventSearchAction"%>
<%@page import="in.partake.model.dto.Event"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="java.util.List"%>
<%@ page import="static in.partake.view.util.Helper.h"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%
    EventSearchAction action = (EventSearchAction) request.getAttribute(Constants.ATTR_ACTION);
%>

<!DOCTYPE html>

<html lang="ja">
<head>
    <jsp:include page="/WEB-INF/internal/head.jsp" flush="true" />
    <title>イベント検索 - [PARTAKE]</title>
</head>
<body>
<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />

<div class="page-header">
    <h1>イベント検索</h1>
    <p>タイトル、カテゴリ、本文などからイベントを検索します。</p>
</div>

<div class="well event-search">
    <form id="search-form" class="form-horizontal">
        <fieldset>
            <%-- <legend>タイトル、本文からイベントを検索</legend>  --%>
            <div class="control-group">
                <label class="control-label">検索語句</label>
                <div class="controls">
                    <input type="text" id="search-term" name="searchTerm" />
                    <input id="search-form-button" type="button" class="btn btn-primary" alt="Search" value="Search" />
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">オプション</label>
                <div class="controls">
                    <div class="form-inline">
                        <span class="event-search-inline">カテゴリ</span><select id="category" name="category">
                        <% for (KeyValuePair kv : action.getCategories()) { %>
                            <option value="<%= h(kv.getKey()) %>"><%= h(kv.getValue()) %></option>
                        <% } %>
                        </select>
                        <span class="event-search-inline">並べ替え</span><select id="sort-order" name="sortOrder">
                        <% for (KeyValuePair kv : action.getSortOrders()) { %>
                            <option value="<%= h(kv.getKey()) %>"><%= h(kv.getValue()) %></option>
                        <% } %>
                        </select>
                    </div>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <div class="form-inline">
                        <label class="checkbox"><input type="checkbox" id="before-deadline-only" name="beforeDeadlineOnly" checked />締め切り前のイベントのみを検索する</label>
                    </div>
                </div>
            </div>
        </fieldset>
    </form>
    <form style="display:none">
        <%-- This is a hack to enable a browser 'back' button. --%>
        <textarea id="textareaToSavetemporaryJSON"></textarea>
    </form>
    <script>
    function formatDate(date) {
        return "YYYY-MM-DD HH:MM";
    }

    function createLine(event) {
        var row = $('<div class="row searched-event"></div>');

        var image = $('<div class="span2 event-image"></div>');
        var a = $('<a></a>').attr('href', '/events/' + event.id);
        if (event.foreImageId && event.foreImageId != "") {
            var img = $('<img alt="" />');
            img.attr('src', '/images/' + event.foreImageId);
            a.append(img);
        } else {
            var img = $('<img alt="" />');
            img.attr('src', '/images/no-image.png');
            a.append(img);
        }
        image.append(a);

        var span = $('<div class="span10"></div>');
        span.append($('<h3></h3>').append($('<a />').attr('href', '/events/' + event.id).text(event.title)));
        span.append($('<p></p>').text(event.summary));
        span.append($('<p></p>').text('場所:' + event.place));
        span.append($('<p></p>').text('日時:' + formatDate(new Date(event.beginDate))));

        row.append(image).append(span);

        return row;
    }

    function render(json) {
        var events = json.events;
        $('#searched-events').empty();

        $('#searched-events').append($('<h2>検索結果</h2>'));
        if (events.length == 0) {
            $('#searched-events').append($('<p>ヒットしませんでした。別の単語で試してみてください。</p>'));
            return;
        }
        for (var i = 0; i < events.length; ++i) {
            var line = createLine(events[i]);
            $('#searched-events').append(line);
        }
    }

    function doSearch() {
        var query = $('#search-term').val();
        var category = $('#category option:selected').val();
        var sortOrder = $('#sort-order option:selected').val();
        var beforeDeadlineOnly = $('#before-deadline-only').is(':checked');

        partake.event.search(query, category, sortOrder, beforeDeadlineOnly, 100)
        .done(function (json) {
            $('#textareaToSavetemporaryJSON').val($.toJSON(json));
            render(json);
        })
        .fail(partake.defaultFailHandler);
    }

    $('#search-term').keypress(function(e) {
        if (e.which == 13) { // If enterkey is pressed.
            doSearch();
            return false;
        }
    });

    $('#search-form-button').click(doSearch);



    $(function() {
        try {
            console.log($('#textareaToSavetemporaryJSON').val());
            var json = $.parseJSON($('#textareaToSavetemporaryJSON').val());
            console.log(json);
            render(json);
        } catch (e) {
            console.log(e);
            // Do nothing.
        }
    });
    </script>
</div>

<div id="searched-events" class="searched-events">
</div>

<jsp:include page="/WEB-INF/internal/footer.jsp" flush="true" />
</body>
</html>
