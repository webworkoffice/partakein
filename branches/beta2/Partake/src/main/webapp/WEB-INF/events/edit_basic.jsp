<%@page import="in.partake.base.KeyValuePair"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
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
    <title>イベントを編集します</title>
</head>
<body class="with-sub-nav"
    <% if (!StringUtils.isBlank(event.getBackImageId())) { %>
        style="background-image: url(/images/<%= h(event.getBackImageId()) %>)"
    <% } %>
>

<jsp:include page="/WEB-INF/internal/header.jsp" flush="true" />
<div class="container">

<jsp:include page="/WEB-INF/events/_edit_manage_navigation.jsp" flush="true">
    <jsp:param name="NAVIGATION" value="basic" />
</jsp:include>

<script>
var eventId = '<%= event.getId() %>';

// TODO: Move these methods to partake-util.js
function enclosingForm(elem) {
    while (elem) {
        if (elem instanceof HTMLFormElement)
            return elem;
        elem = elem.parentNode;
    }

    return null;
}

function removeSuffix(str, suffix) {
    var idx;
    if ((idx = str.lastIndexOf(suffix)) != -1)
        return str.substring(0, idx);

    return null;
}

$(function() {
    $('.edit-button').click(function(e) {
        var id = removeSuffix($(this).attr("id"), "-edit");
        $('#' + id + '-form').show();
        $('#' + id + '-show').hide();
    });
    $('.edit-cancel-button').click(function(e) {
        var form = $(enclosingForm(this));
        var id = removeSuffix(form.attr("id"), "-form");
        $('#' + id + '-form').hide();
        $('#' + id + '-show').show();
    });
    $('.edit-input').keypress(function(e) {
        if (e.which != 13) // (Enter = 13)
            return true;

        var form = enclosingForm(this);
        var id = $(form).attr('id');
        var prefix = removeSuffix(id, '-form');
        $('#' + prefix + '-submit').click();
        return false;
    });
});
</script>

<div class="event-body">

<div class="page-header">
    <h1 id="title-show">
        <span id="title-content"><%= h(event.getTitle()) %></span>
        <span id="title-edit" class="label label-edit edit-button">タイトルを編集</span>
    </h1>
    <form id="title-form" style="display: none;">
        <input type="text" class="span18 edit-input" name="title" id="title-input" value="" placeholder="タイトル：　「お花見」「HTML 勉強会」「武道館ライブ」など">
        <div class="edit-form-buttons">
            <input type="button" value="キャンセル" class="btn edit-cancel-button">
            <input id="title-submit" type="button" value="保存" class="btn edit-save-button">
        </div>
    </form>
    <script>
    $('#title-edit').click(function(e) {
        $('#title-input').val($('#title-content').text());
    });

    $('#title-submit').click(function(e) {
        var form = $(enclosingForm(this));
        var id = removeSuffix(form.attr("id"), "-form");

        partake.event.modify(eventId, { title: $('#title-input').val() })
        .done(function (json) {
            $('#title-content').text($('#title-input').val());
            $('#' + id + '-form').hide();
            $('#' + id + '-show').show();
        })
        .fail(partake.defaultFailHandler);
    });
    </script>

    <p id="summary-show">
        <span id="summary-content"><%= h(event.getSummary()) %></span>
        <span id="summary-edit" class="label label-edit edit-button">概要を編集</span>
    </p>
    <form id="summary-form" style="display: none;">
        <p><input type="text" class="span18 edit-input" name="summary" id="summary-input" value="" placeholder="概要：　「みんなで一緒に飲みましょう！」など"></p>
        <div class="edit-form-buttons">
            <input type="button" value="キャンセル" class="btn edit-cancel-button">
            <input id="summary-submit" type="button" value="保存" class="btn edit-save-button">
        </div>
    </form>
    <script>
    $('#summary-edit').click(function(e) {
        $('#summary-input').val($('#summary-content').text());
    });

    $('#summary-submit').click(function(e) {
        var form = $(enclosingForm(this));
        var id = removeSuffix(form.attr("id"), "-form");

        partake.event.modify(eventId, { summary: $('#summary-input').val() })
        .done(function (json) {
            $('#summary-content').text($('#summary-input').val());
            $('#' + id + '-form').hide();
            $('#' + id + '-show').show();
        })
        .fail(partake.defaultFailHandler);
    });
    </script>

    <p id="category-show">
        <span id="category-content" class="label label-info"><%= h(EventCategory.getReadableCategoryName(event.getCategory())) %></span>
        <span id="category-edit" class="label label-edit edit-button">カテゴリを編集</span>
    </p>
    <form id="category-form" style="display: none;">
        <select id="category-input" name="category">
        <% for (KeyValuePair kv : EventCategory.getCategories()) { %>
            <option value="<%= h(kv.getKey()) %>"><%= kv.getValue() %></option>
        <% } %>
        </select>
        <script>
        <% if (event.getCategory() != null && EventCategory.isValidCategoryName(event.getCategory())) { %>
            $('category').val('<%= h(event.getCategory()) %>');
        <% } else { %>
            $('category').val('<%= h(EventCategory.getCategories().get(0).getKey()) %>');
        <% } %>
        </script>
        <div class="edit-form-buttons">
            <input type="button" value="キャンセル" class="btn edit-cancel-button">
            <input id="category-submit" type="button" value="保存" class="btn edit-save-button">
        </div>
    </form>
    <script>
    $('#category-edit').click(function() {
        $('#category-input option').each(function (i) {
            if ($(this).text() == $('#category-content').text()) {
                $('#category-input').val($(this).val());
                return false;
            }

            return true;
        });
    });

    $('#category-submit').click(function(e) {
        var form = $(enclosingForm(this));
        var id = removeSuffix(form.attr("id"), "-form");

        partake.event.modify(eventId, { category: $('#category-input option:selected').val() })
        .done(function (json) {
            $('#category-content').text($('#category-input option:selected').text());
            $('#' + id + '-form').hide();
            $('#' + id + '-show').show();
        })
        .fail(partake.defaultFailHandler);
    });
    </script>

    <div id="backimage-show">
        <span id="backimage-edit" class="label label-edit edit-button">背景画像を編集</span>
    </div>
    <form id="backimage-form" style="display: none;">
        <input type="hidden" id="backimage-id" value="<%= event.getBackImageId() %>" />
        <div class="event-image">
            <% if (StringUtils.isBlank(event.getBackImageId())) { %>
                <img id="backimage-editimage" src="/images/no-image.png">
            <% } else { %>
                <img id="backimage-editimage" src="/images/<%= h(event.getBackImageId()) %>">
            <% } %>
        </div>

        <div class="edit-back-buttons">
            <input type="button" value="キャンセル" class="btn edit-cancel-button">
            <input id="backimage-remove" type="button" value="画像を削除" class="btn edit-remove-button">
            <input id="backimage-select" type="button" value="画像を選択" class="btn">
        </div>
        <script>
            $('#backimage-edit').click(function() {
            });
            $('#backimage-remove').click(function() {
                partake.event.modify(eventId, { 'backImageId': "" })
                .done(function (json) {
                    $('#backimage-id').val("");
                    $('#backimage-showimage').hide();
                    $('#backimage-editimage').attr('src', '/images/no-image.png');
                    document.body.style.backgroundImage = '';
                    $('#backimage-form').hide();
                    $('#backimage-show').show();
                })
                .fail(partake.defaultFailHanlder);
            });
            $('#backimage-select').click(function() {
                $('#image-upload-dialog').attr('purpose', 'background');
                var imageId = $('#backimage-id').val();
                if (imageId == null || imageId == "" || imageId == "null") {
                    $('#selected-image').attr('src', '/images/no-image.png');
                } else {
                    $('#selected-image').attr('src', '/images/' + imageId);
                }
                $('#image-upload-dialog').modal('show');
            });

            function onBackImageSelected(imageId, dialog) {
                partake.event.modify(eventId, { 'backImageId': imageId })
                .done(function (json) {
                    $('#backimage-id').val(imageId);
                    $('#backimage-showimage').attr('src', '/images/' + imageId);
                    $('#backimage-editimage').attr('src', "/images/" + imageId);
                    document.body.style.backgroundImage = "url(/images/" + imageId + ")";
                    dialog.modal('hide');

                    $('#backimage-showimage').show();
                    $('#backimage-form').hide();
                    $('#backimage-show').show();
                })
                .fail(partake.defaultFailHanlder);
            }
        </script>
    </form>
</div>

<div class="row">
    <div class="span8 pull-right">
        <div id="side-information">
            <jsp:include page="/WEB-INF/events/_edit_side_information.jsp" flush="true" />
        </div>
    </div>

    <div class="span16">
        <div id="foreimage-show">
            <div class="event-image">
                <% if (StringUtils.isBlank(event.getForeImageId())) { %>
                    <img id="foreimage-showimage" src="/images/no-image.png" style="display: none">
                <% } else { %>
                    <img id="foreimage-showimage" src="/images/<%= h(event.getForeImageId()) %>">
                <% } %>
            </div>
            <span id="foreimage-edit" class="label label-edit edit-button">この位置への掲載画像を編集</span>
        </div>
        <form id="foreimage-form" style="display: none;">
            <input type="hidden" id="foreimage-id" value="<%= event.getForeImageId() %>" />
            <div class="event-image">
                <% if (StringUtils.isBlank(event.getForeImageId())) { %>
                    <img id="foreimage-editimage" src="/images/no-image.png">
                <% } else { %>
                    <img id="foreimage-editimage" src="/images/<%= h(event.getForeImageId()) %>">
                <% } %>
            </div>

            <div class="edit-form-buttons">
                <input type="button" value="キャンセル" class="btn edit-cancel-button">
                <input id="foreimage-remove" type="button" value="画像を削除" class="btn edit-remove-button">
                <input id="foreimage-select" type="button" value="画像を選択" class="btn">
            </div>
            <script>
                $('#foreimage-remove').click(function() {
                    partake.event.modify(eventId, { 'foreImageId': "" })
                    .done(function (json) {
                        $('#foreimage-id').val("");
                        $('#foreimage-editimage').attr('src', '/images/no-image.png');
                        $('#foreimage-showimage').hide();
                        $('#foreimage-form').hide();
                        $('#foreimage-show').show();
                    })
                    .fail(partake.defaultFailHanlder);
                });
                $('#foreimage-select').click(function() {
                    $('#image-upload-dialog').attr('purpose', 'foreground');
                    var imageId = $('#foreimage-id').val();
                    if (imageId == null || imageId == "" || imageId == "null") {
                        $('#foreimage-editimage').attr('src', '/images/no-image.png');
                        $('#selected-image').attr('src', '/images/no-image.png');
                    } else {
                        $('#foreimage-editimage').attr('src', '/images/' + imageId);
                        $('#selected-image').attr('src', '/images/' + imageId);
                    }
                    $('#image-upload-dialog').modal('show');
                });

                function onForeImageSelected(imageId, dialog) {
                    partake.event.modify(eventId, { 'foreImageId': imageId })
                    .done(function (json) {
                        $('#foreimage-id').val(imageId);
                        $('#foreimage-editimage').attr('src', '/images/' + imageId);
                        $('#foreimage-showimage').attr('src', '/images/' + imageId);
                        dialog.modal('hide');

                        $('#foreimage-showimage').show();
                        $('#foreimage-form').hide();
                        $('#foreimage-show').show();
                    })
                    .fail(partake.defaultFailHanlder);
                }
            </script>
        </form>

        <div id="description-show">
            <h3>イベント説明 <span id="description-edit" class="label label-edit edit-button">イベント説明を編集</span></h3>
            <div id="description-content" style="min-height: 200px;">
                <%= Helper.cleanupHTML(event.getDescription()) %>
            </div>
        </div>
        <form id="description-form" style="display: none;">
            <h3>イベント説明</h3>
            <textarea id="description-input" name="description"><%= Helper.cleanupHTML(event.getDescription()) %></textarea>
            <div class="description-form-buttons">
                <input type="button" value="キャンセル" class="btn edit-cancel-button">
                <input id="description-submit" type="button" value="保存" class="btn edit-save-button">
            </div>
        </form>
        <script>
        $('#description-edit').click(function() {
        });

        $('#description-submit').click(function(e) {
            var form = $(enclosingForm(this));
            var id = removeSuffix(form.attr("id"), "-form");

            partake.event.modify(eventId, { description: $('#description-input').html() })
            .done(function (json) {
                $('#description-content').html($('#description-input').html());
                $('#' + id + '-form').hide();
                $('#' + id + '-show').show();
            })
            .fail(partake.defaultFailHandler);
        });
        </script>
    </div>
</div>


<div id="image-upload-dialog" class="modal modal-wider" style="display:none">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>画像を選択</h3>
    </div>
    <div class="modal-body">
        <div class="row">
            <div class="span6">
                <p>新しく画像をアップロード、もしくは過去にアップロードした画像から選択します。</p>
                <p>選択された画像</p>
                <ul class="thumbnails">
                    <li class="span6"><img id="selected-image" src="/images/no-image.png" alt=""></li>
                </ul>
                <form enctype="multipart/form-data">
                    <label for="fileupload"><input type="button" class="btn btn-danger invisible-if-ie" value="新しく画像をアップロード"/></label>
                    <input id="fileupload" type="file" name="file" class="invisible-if-not-ie" />
                </form>
            </div>
            <div class="span12">
                <ul id="image-upload-dialog-thumbnails" class="thumbnails">
                </ul>
                <div id="image-pagination" class="pagination pagination-centered"></div>
            </div>
        </div>

    </div>
    <div class="modal-footer spinner-container">
        <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
        <a href="#" id="image-upload-dialog-ok" class="btn btn-primary">OK</a>
    </div>

    <%-- Since IE does not support XHR File upload, we use iframe trasport technique here... Too bad. --%>
    <script>
    var cachedTotalImageCount = 0;

    function onSelectImage(imageId) {
        $('#selected-image').attr('imageId', imageId);
        $('#selected-image').attr('src', '/images/' + imageId);
    }

    function createImageHTML(imageId) {
        var img = $('<img alt=""/>').attr('src', '/images/' + imageId);
        var a = $('<a class="thumbnail"></a>').append(img);
        a.click(function() { onSelectImage(imageId); });
        var li = $('<li class="span4"></li>').append(a);

        return li;
    }

    function updatePagination(currentPageNum, totalImageCount) {
        var pagination = $('#image-pagination');
        pagination.empty();

        var pages = partakeUI.pagination(pagination, currentPageNum, totalImageCount, 6);
        for (var i = 0; i < pages.length; ++i) {
            pages[i].anchor.click((function(pageNum) {
                return function() {
                    showImages(pageNum);
                };
            })(pages[i].pageNum));
        }
    }

    function updateImageList(imageIds) {
        var thumbnails = $('#image-upload-dialog-thumbnails');
        thumbnails.empty();

        for (var i = 0; i < imageIds.length; ++i) {
            var li = createImageHTML(imageIds[i]);
            $('#image-upload-dialog-thumbnails').append(li);
        }
    }

    function showImages(pageNum) {
        partake.account.getImages((pageNum - 1) * 6, 6)
        .done(function(json) {
            cachedTotalImageCount = json.count;
            updateImageList(json.imageIds);
            updatePagination(pageNum, json.count);
        });
    }

    $('#image-upload-dialog').on('shown', function() {
        showImages(1);
    });

    $('#image-upload-dialog-ok').click(function() {
        var dialog = $('#image-upload-dialog');
        var imageId = $('#selected-image').attr('imageId');
        if (dialog.attr('purpose') == "foreground" && imageId) {
            onForeImageSelected(imageId, dialog);
        } else if (dialog.attr('purpose') == "background" && imageId) {
            onBackImageSelected(imageId, dialog);
        }
    });

    $('#fileupload').fileupload({
        url: '/api/image/create',
        files: [{name: $('#fileupload').val()}],
        fileInput: $('#fileupload'),

        formData: function (form) {
            var result = form.serializeArray();
            result.push({ name: 'sessionToken', value: '<%= Helper.getSessionToken() %>' });
            result.push({ name: 'limit', value: '6' });
            if (jQuery.browser.msie)
                result.push({ name: 'ensureTextPlain', value: 'true'});
            return result;
        },

        always: function(e, data) {
        },

        done: function (e, data) {
            var xhr = data.jqXHR;
            try {
                var json = $.parseJSON(xhr.responseText || data.result.text());
                cachedTotalImageCount += 1;
                updateImageList(json.imageIds);
                updatePagination(1, cachedTotalImageCount);
            } catch (e) {
                alert('レスポンスが JSON 形式ではありません。');
            }
        }
    });
    </script>
</div>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/tiny_mce_jquery/jquery.tinymce.js"></script>
<script type="text/javascript">
$(function() {
    $('textarea').tinymce({
        // Location of TinyMCE script
        script_url: "<%= request.getContextPath() %>/js/tiny_mce_jquery/tiny_mce.js",

        theme: "advanced",
        language: "ja",
        plugins: "inlinepopups,searchreplace,spellchecker,style,table,xhtmlxtras",

        theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect",
        theme_advanced_buttons2: "cut,copy,paste,|,search,replace,|,undo,redo,|,bullist,numlist,|,outdent,indent,blockquote,|,link,unlink,anchor,image,cleanup,help,code,|,forecolor,backcolor",
        theme_advanced_buttons3: "tablecontrols,|,hr,|,cite,abbr,acronym,del,ins,|,sub,sup,|,styleprops,spellchecker",
        theme_advanced_toolbar_location: "top",
        theme_advanced_toolbar_align: "left",
        theme_advanced_statusbar_location: "bottom",
        theme_advanced_resizing: true
    });
});
</script>

</div>

</div>
<jsp:include page="/WEB-INF/internal/footer.jsp" />
</body>
</html>
