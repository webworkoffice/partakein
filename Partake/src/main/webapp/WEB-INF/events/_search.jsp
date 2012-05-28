<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<% if ("simple".equalsIgnoreCase((String) request.getParameter("FORM_TYPE"))) { %>
<div>
    <form class="well form-search">
        <p style="text-align: center;"><span class="span3" style="float: none; padding-right: 20px;">イベント検索</span>
            <input id="search-term" type="text" class="span6 search-query">
            <button id="search-button" type="button" class="btn btn-primary span3" style="float: none">Search</button>
        </p>
    </form>
</div>
<script>
    function doRenderNoResults() {
        $('#searched-events').append($(
            '<div class="span12">' +
            '<p>ヒットしませんでした。別の単語で試してみてください。</p>' +
            '<p><a href="/events/search">より詳しい検索はこちらから。</a></p>' +
            '</div>'
        ));
    }
</script>
<% } else { %>

<% } %>

<div id="searched-events" class="row" style="position: relative;">
</div>

<div id="template" class="span3 masonry-box" style="margin-bottom: 5px; display:none;"><div class="thumbnail">
    <a id="template-image-link" href="/events/some-id">
        <img id="template-image" src="/images/thumbnail/id" alt=""  width="220" height="220" />
    </a>
    <div class="caption">
        <h5><a id="template-title" href="/events/some-id">title</a></h5>
        <p id="template-date"></p>
        <p id="template-summary"></p>
    </div>
</div></div>
<form style="display:none">
    <%-- This is a hack to enable a browser 'back' button. --%>
    <textarea id="textareaToSavetemporaryJSON"></textarea>
</form>

<script>
$('#searched-events').masonry({
    itemSelector : '.masonry-box'
});

function cloneTemplate(newPrefix) {
    var template = $('#template');
    var cloned = template.clone(true);
    cloned.find("[id^=template]").each(function() {
        var id = $(this).attr('id').replace('template', newPrefix);
        $(this).attr('id', id);
    });
    cloned.attr('id', newPrefix);
    cloned.show();
    return cloned;
}

var idx = 0;
function render(json) {
    var events = json.events;
    $('#searched-events').empty();

    if (events.length == 0) {
        doRenderNoResults();
        return;
    }

    var appended = [];
    for (var i = 0; i < events.length; ++i) {
        var prefix = "e" + ++idx;
        var template = cloneTemplate(prefix);
        var event = events[i];

        $('#searched-events').append(template);
        $('#' + prefix + '-image-link').attr('href', '/events/' + event.id);
        if (event.foreImageId)
            $('#' + prefix + '-image').attr('src', '/images/thumbnail/' + event.foreImageId);
        else
            $('#' + prefix + '-image').attr('src', '/images/no-image.png');
        $('#' + prefix + '-title').attr('href', '/events/' + event.id);
        $('#' + prefix + '-title').text(event.title);
        $('#' + prefix + '-summary').text(event.summary);
        $('#' + prefix + '-date').text(event.beginDateText);

        appended.push(template);
    }

    $('#searched-events').imagesLoaded(function() {
        $('#searched-events').masonry('reload')
    });
}

function doSearch() {
    var query = $('#search-term').val();
    partake.event.search(query, 'all', 'createdAt', true, 30)
    .done(function(json) {
        $('#textareaToSavetemporaryJSON').val($.toJSON(json));
        console.log($.toJSON(json));
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

$(function() {
    try {
        var jsonText = $('#textareaToSavetemporaryJSON').val();
        if (jsonText == null || jsonText == "") {
            // No value was saved.
            doSearch();
            return;
        }

        var json = $.parseJSON(jsonText);
        render(json);
    } catch (e) {
        console.log(e);
        // Do nothing.
    }
});


$('#search-button').click(doSearch);
</script>
