<%@page import="in.partake.controller.action.event.EventShowAction"%>
<%@page import="in.partake.controller.base.permission.RemoveCommentPermission"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="in.partake.base.Util"%>
<%@page import="in.partake.model.DirectMessageEx"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="in.partake.model.CommentEx"%>
<%@page import="in.partake.model.dao.DataIterator"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="in.partake.model.EventEx"%>

<%@page import="static in.partake.view.util.Helper.h"%>

<%
    UserEx user = (UserEx) request.getSession().getAttribute(Constants.ATTR_USER);
    EventShowAction action = (EventShowAction) request.getAttribute(Constants.ATTR_ACTION);

    EventEx event = action.getEvent();
    List<CommentEx> comments = action.getComments();
    List<DirectMessageEx> messages = action.getMessages();
%>


<% for (CommentEx comment : comments) { %>
    <% if (comment == null) { continue; } %>
    <div class="comment" id="comment-<%= h(comment.getId()) %>">
        <p class="spinner-container"><a href="<%= request.getContextPath() %>/users/<%= h(comment.getUserId()) %>"><%= h(comment.getUser().getTwitterLinkage().getScreenName()) %></a>
        : <%= Helper.readableDate(comment.getCreatedAt()) %>
        <% if (RemoveCommentPermission.check(comment, event, user)) { %>
            <a href="#" title="コメントを削除" onclick="removeComment(this, '<%= h(comment.getId()) %>')">[x]</a>
        <% } %></p>
        <% if (comment.isHTML()) { %>
            <p><%= Helper.cleanupHTML(comment.getComment()) %></p>
        <% } else { %>
            <p><%= h(comment.getComment()) %></p>
        <% } %>
    </div>
    <script>
    function removeComment(anchorElem, commentId) {
        var spinner = partakeUI.spinner(anchorElem);
        if (!spinner.is(":hidden"))
            return;

        spinner.show();
        if (window.confirm('メッセージを削除しようとしています。この操作は取り消せません。本当に削除しますか？')) {
            partake.event.removeComment(commentId)
            .always(function () {
                spinner.hide();
            })
            .done(function (json) {
                $('#comment-' + commentId).remove();
            })
            .fail(function (xhr) {
                try {
                    var json = $.parseJSON(xhr.responseText);
                    alert(json.reason);
                } catch (e) {
                    alert('レスポンスが JSON 形式ではありません。');
                }
            });
        }
    }
    </script>
<% } %>

<div class="comment-form">
<% if (user != null) { %>
    <form class="spinner-container">
        <%= Helper.tokenTags() %>
        <strong>コメント</strong><br />
        <textarea id="commentForm-commentEdit" name="comment"></textarea><br />
        <input id="comment-form-submit" type="button" class="btn btn-primary" value="コメントを投稿"  />
    </form>
    <script>
    function postComment() {
        var spinner = partakeUI.spinner(document.getElementById('comment-form-submit'));
        var eventId = '<%= h(event.getId()) %>';
        var comment = $('#commentForm-commentEdit').val();

        spinner.show();
        $('#commentForm-commentEdit').attr('disabled', '');
        $('#comment-form-submit').attr('disabled', '');

        partake.event.postComment(eventId, comment)
        .always(function (xhr) {
            spinner.hide();
            $('#commentForm-commentEdit').removeAttr('disabled');
            $('#comment-form-submit').removeAttr('disabled');
        })
        .done(function (json) {
            // TODO: Recreating comment board is better.
            location.reload();
        })
        .fail(function (xhr) {
            try {
                var json = $.parseJSON(xhr.responseText);
                alert(json.reason);
            } catch (e) {
                alert('レスポンスが JSON 形式ではありません。');
            }
        });
    }
    $('#comment-form-submit').click(postComment);
    </script>
<% } else { %>
    <p>コメントを投稿するにはログインしてください。</p>
<% } %>
</div>

<script type="text/javascript" src="/js/tiny_mce_jquery/jquery.tinymce.js"></script>
<script type="text/javascript">
$(function() {
    var initArg = {
        // Location of TinyMCE script
        script_url: "/js/tiny_mce_jquery/tiny_mce.js",

        theme: "advanced",
        language: "ja",
        width: "300",

        theme_advanced_toolbar_location: "top",
        theme_advanced_toolbar_align:  "left",
        theme_advanced_statusbar_location: "bottom",
        theme_advanced_resizing: true
    };

    var width = $(window).width();
    console.log(width);
    if (width <= 480) {
        initArg.initial_type = "phone";
        initArg.width = "" + (width - 40);

        initArg.theme_advanced_buttons1 = "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull";
        initArg.theme_advanced_buttons2 = "formatselect,fontselect,fontsizeselect";
        initArg.theme_advanced_buttons3 = "";
    } else {
        if (width < 980)
            initArg.width = "476";
        else
            initArg.width = "620";

        initArg.plugins = "inlinepopups,searchreplace,spellchecker,style,table,xhtmlxtras";

        initArg.theme_advanced_buttons1 = "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect";
        initArg.theme_advanced_buttons2 = "cut,copy,paste,|,bullist,numlist,|,outdent,indent,blockquote,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor";
        initArg.theme_advanced_buttons3 = "tablecontrols,|,hr,|,sub,sup,|,styleprops,spellchecker";
    }

    $('#commentForm-commentEdit').tinymce(initArg);

    $(window).resize(function() {
        console.log("resize");
        console.log(initArg.width);
        console.log($(window).width());
        if (initArg.initial_type != "phone")
            return;
        var width = $(window).width();

        var ifr = $('#commentForm-commentEdit_ifr');
        if (width <= 480)
            ifr.width(width - 40);
        else if (width < 980)
            ifr.width(476);
        else
            ifr.width(620);
    });
});
</script>
