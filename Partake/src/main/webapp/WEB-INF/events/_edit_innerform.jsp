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
	EventEx event = null;
	if (request.getAttribute(Constants.ATTR_ACTION) instanceof EventEditAction) {
		EventEditAction action = (EventEditAction) request.getAttribute(Constants.ATTR_ACTION);
		event = action.getEvent();
	}
%>

<form id="event-form" class="form-horizontal">

<input type="hidden" id="draft" name="draft" value="true" />
<% if (event != null) { %>
	<input type="hidden" name="eventId" value="<%= h(event.getId()) %>" />
<% } %>

<div id="title" class="control-group">
    <label class="control-label" for="title">タイトル <span class="label label-important">必須</span></label>
    <div class="controls">
        <input type="text" name="title" class="span7" placeholder="タイトル" value="<%= event != null ? h(event.getTitle()) : "" %>" />
        <p class="help-block">イベントのタイトルです。100 文字以内で記述します。</p>
    </div>
</div>
<div id="summary" class="control-group">
    <label class="control-label" for="summary">概要</label>
    <div class="controls">
    	<input type="text" name="summary" class="span7" placeholder="概要" value="<%= event != null ? h(event.getSummary()) : "" %>"/>
        <p class="help-block">イベントを一言で表す概要です。100 文字以内で記述します。</p>
    </div>
</div>
<div id="category" class="control-group">
	<label class="control-label">カテゴリ</label>
	<div class="controls">
		<select name="category">
		<% for (KeyValuePair kv : EventCategory.CATEGORIES) { %>
			<option value="<%= h(kv.getKey()) %>"><%= kv.getValue() %></option>
		<% } %>
		</select>
		<script>
		<% if (event != null && event.getCategory() != null && EventCategory.isValidCategoryName(event.getCategory())) { %>
			$('category').val('<%= h(event.getCategory()) %>');
		<% } else { %>
			$('category').val('<%= h(EventCategory.CATEGORIES.get(0).getKey()) %>');
		<% } %>
		</script>
	</div>
</div>        
<div id="description" class="control-group">
	<label class="control-label" for="description">説明 <span class="label label-important">必須</span></label>
	<div class="controls">
		<textarea name="description"><%= event != null ? Helper.cleanupHTML(event.getDescription()) : "" %></textarea>
		<p class="help-block">イベントの説明を記述します。(HTML などを含めて50000文字まで)</p>
	</div>
</div>
<div id="beginDate" class="control-group">
	<label class="control-label">開催日時 <span class="label label-important">必須</span></label>
    <div class="controls form-inline">
       	<input type="text" id="beginDataInput" name="beginDate" class="span2" 
       	       placeholder="YYYY-MM-DD HH:MM"
       	       value="<%= event != null ? TimeUtil.formatForEvent(event.getBeginDate()) : TimeUtil.formatForEvent(TimeUtil.oneDayAfter(TimeUtil.getCurrentDate())) %>" />
	</div>	
	<script>
	$('#beginDataInput').datetimepicker({
		dateFormat: 'yy-mm-dd'
	});
	</script>
</div>
<div id="endDate" class="control-group">
    <label class="control-label">終了日時</label>
    <div class="controls">				
		<label class="checkbox">
			<input type="checkbox" id="usesEndDate" name="usesEndDate" <%= event != null && event.getEndDate() != null ? "checked" : "" %>/>
			終了日時を設定する
        </label>
	</div>
	<div class="controls form-inline">
       	<input type="text" id="endDataInput" name="endDate" class="span2"
			   placeholder="YYYY-MM-DD HH:MM"
       		   value="<%= event != null && event.getEndDate() != null ? TimeUtil.formatForEvent(event.getEndDate()) : TimeUtil.formatForEvent(TimeUtil.oneDayAfter(TimeUtil.getCurrentDate())) %>" />
    </div>
	<script>
	$('#endDataInput').datetimepicker({
		dateFormat: 'yy-mm-dd'
	});
    function checkEndDate() {
        if ($("#usesEndDate").is(":checked")) {
            $("#endDataInput").removeAttr('disabled');
        } else {
            $("#endDataInput").attr('disabled', '');
        }
    }
    checkEndDate();
    $("#usesEndDate").change(checkEndDate);
	</script>
</div>
<div id="deadline" class="control-group">
    <label class="control-label">申込締切</label>
    <div class="controls">
		<label class="checkbox">
			<input type="checkbox" id="usesDeadline" name="usesDeadline" <%= event != null && event.getDeadline() != null ? "checked" : "" %>/>
			締め切りを設定する
       	</label>
	</div>
	<div class="controls form-inline">
       	<input type="text" id="deadlineInput" name="deadline" class="span2"
       	       placeholder="YYYY-MM-DD HH:MM"
       		   value="<%= event != null  && event.getDeadline() != null? TimeUtil.formatForEvent(event.getDeadline()) : TimeUtil.formatForEvent(TimeUtil.oneDayAfter(TimeUtil.getCurrentDate())) %>" />
		<p class="help-block"> 締め切り以後は参加／不参加が変更できなくなります。設定しない場合、開始日時が締め切りとなります。</p>
	</div>
	<script>
	$('#deadlineInput').datetimepicker({
		dateFormat: 'yy-mm-dd'
	});
    function checkDeadline() {
        if ($("#usesDeadline").is(":checked")) {
            $("#deadlineInput").removeAttr('disabled');
        } else {
            $("#deadlineInput").attr('disabled', '');
        }
    }
    checkDeadline();
    $("#usesDeadline").change(checkDeadline);
	</script>    
</div>
<div id="capacity" class="control-group">
   	<label class="control-label">定員</label>
   	<div class="controls">
		<input type="text" name="capacity" class="span7" value="<%= event != null ? String.valueOf(event.getCapacity()) : "" %>"/>
		<p class="help-block">定員を超える参加表明者は補欠者として扱われます。0 をいれると定員なしの意味になります。</p>
	</div>
</div>
<div id="foreImageId" class="control-group">
	<label class="control-label" for="foreImage">掲載画像</label>
	<div class="controls form-inline">
		<label class="checkbox"><input type="checkbox" name="foreImage" />掲載する</label>
		<input type="hidden" id="fore-image-id-input" name="foreImageId" />
		<p class="help-block">画像を設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</p>
	</div>
	<script>
	$('input[name="foreImage"]').change(function() {
		var v = $(this).is(':checked');
		if (v)
			$('#fore-image-chooser').fadeIn("fast");
		else
			$('#fore-image-chooser').fadeOut("fast");
	});
	</script>
	<div id="fore-image-chooser" class="controls" style="display:none">
		<p>現在次の画像が選択されています。</p>
		<ul class="thumbnails">
	        <li class="span2"><img id="selected-fore-image" src="http://placehold.it/260x180" alt=""></li>
        </ul>
        <p><input id="select-new-foreground-image" type="button" class="btn" value="新しく画像を選択します" /></p>
		<script>
			$('#select-new-foreground-image').click(function() {
				$('#image-upload-dialog').attr('purpose', 'foreground');
				$('#image-upload-dialog').modal('show');
			});
		</script>
   	</div>
</div>
<div id="backImageId" class="control-group">
   	<label class="control-label" for="backImage">背景画像</label>
	<div class="controls form-inline">
		<label class="checkbox"><input type="checkbox" name="backImage" />掲載する</label>
		<p class="help-block">画像を設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</p>
	</div>
	<script>
	$('input[name="backImage"]').change(function() {
		var v = $(this).is(':checked');
		if (v)
			$('#back-image-chooser').fadeIn("fast");
		else
			$('#back-image-chooser').fadeOut("fast");
	});
	</script>
	<div id="back-image-chooser" class="controls" style="display:none">
		<p>現在次の画像が選択されています。</p>
		<ul class="thumbnails">
	        <li class="span2"><img src="http://placehold.it/260x180" alt=""></li>
        </ul>
		<p>新しく画像を選択します。</p>
   	</div>
</div>
<div id="place" class="control-group">
   	<label class="control-label">会場</label>
    <div class="controls">
    	<input type="text" name="place" class="span7" value="<%= event != null ? h(event.getPlace()) : "" %>" />
    	<p class="help-block">会場名を設定します。</p>
    </div>
</div>
<div id="address" class="control-group">
   	<label class="control-label">住所</label>
   	<div class="controls">
   		<input type="text" name="address" class="span7" value="<%= event != null ? h(event.getAddress()) : "" %>"/>
   		<p class="help-block">住所を正確に入力すると、google の地図を表示できます。</p>
   	</div>
</div>
<div id="url" class="control-group">
   	<label class="control-label">URL</label>
  	<div class="controls">
   		<input type="text" name="url" class="span7" value="<%= event != null ? h(event.getUrl()) : "" %>" />
   		<p class="help-block">参考 URL を設定します。</p>
   	</div>
</div>
<div id="hashTag" class="control-group">
   	<label class="control-label">ハッシュタグ</label>
   	<div class="controls">
   		<input type="text" name="hashTag" class="span7" value="<%= event != null ? h(event.getHashTag()) : "" %>" />
		<p class="help-block">twitter で用いる公式ハッシュタグを設定できます。# から始まる英数字、日本語、アンダースコアなどを含む文字列が使用できます。</p>
   	</div>
</div>

<h3 class="switchHat"><a title="関連イベント　,　複数の管理者">▼ 詳細な設定&nbsp;&nbsp;（関連イベントや編集者を設定できます。）</a></h3>

<div class="switchDetail">

<div id="secret" class="control-group">
	<label for="secret" class="control-label">非公開設定</label>
	<div class="controls">
		<label class="checkbox"><input type="checkbox" name="secret" <%= event != null && event.isPrivate() ? "checked" : "" %> />非公開にする</label>
		<p class="help-block">非公開設定にすると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</p>
	</div>
</div>
<div id="passcode" class="control-group">
	<label for="passcode" class="control-label">パスコード</label>
	<div class="controls">
		<input type="text" name="passcode" value="<%= event != null ? h(event.getPasscode()) : "" %>"/>
	</div>
</div>
<script>
function checkPasscode() {
    if ($('#secret').is(':checked')) {
        $('#passcode').removedAttr('disabled');
    } else {
        $('#passcode').attr('disabled', '');                
    }
}
checkPasscode();
$('#secret').change(checkPasscode);
</script>

<div class="control-group">
	<label for="secret" class="control-label">関連イベント</label>
	<div class="controls">
		<table class="table">
			<thead>
				<tr><th>イベント ID</th><th>登録必須</th><th>優先参加</th></tr>
			</thead>
			<tbody>
				<tr>
					<td><input type="text"     id="relatedEventID1"       name="relatedEventID1" /></td>
					<td><input type="checkbox" id="relatedEventRequired1" name="relatedEventRequired1" /></td>
					<td><input type="checkbox" id="relatedEventPriority1" name="relatedEventPriority1" /></td>
				</tr>
				<tr>
					<td><input type="text"     id="relatedEventID2"       name="relatedEventID2" /></td>
					<td><input type="checkbox" id="relatedEventRequired2" name="relatedEventRequired2" /></td>
					<td><input type="checkbox" id="relatedEventPriority2" name="relatedEventPriority2" /></td>
				</tr>
				<tr>
					<td><input type="text"     id="relatedEventID3"       name="relatedEventID3" /></td>
					<td><input type="checkbox" id="relatedEventRequired3" name="relatedEventRequired3" /></td>
					<td><input type="checkbox" id="relatedEventPriority3" name="relatedEventPriority3" /></td>
				</tr>
			</tbody>
		</table>
		<p class="help-block">関連イベントを設定できます。</p>
		<p class="help-block">登録必須にチェックすると、そのイベントに登録されていなければこのイベントに登録することは出来ません。</p>
		<p class="help-block">優先参加にチェックすると、そのイベントに登録している方は優先的にこのイベントに参加することが出来ます。</p>
		<p class="help-block">イベント ID とは、 http://partake.in/events/{ID} の {ID} の部分の文字列です。<%-- TODO use in.partake.toppath from properties file --%></p>
	</div>
</div>

<div id="editors" class="control-group">
	<label for="editors" class="control-label">編集者</label>
	<div class="controls">
    	<input type="text" name="editors" class="span7" value="<%= event != null ? h(event.getManagerScreenNames()) : ""%>"/>
        <p class="help-block">自分以外にも編集者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。編集者はイベント削除以外のことを行うことが出来ます。</p>
        <p class="help-block">例： user1, user2, user3</p>
	</div>
</div>
</div>

</form>


<div id="image-upload-dialog" class="modal modal-wider" style="display:none">
	<div class="modal-header">
    	<a class="close" data-dismiss="modal">&times;</a>
    	<h3>画像を選択</h3>
	</div>
  	<div class="modal-body">
		<div class="row">
			<div class="span3">
				<p>新しく画像をアップロード、もしくは過去にアップロードした画像から選択します。</p>
				<p>選択された画像</p>
				<ul class="thumbnails">
					<li class="span3"><img id="selected-image" src="http://placehold.it/260x180" alt=""></li>
				</ul>
				<form enctype="multipart/form-data">
			  		<label for="fileupload"><input type="button" class="btn btn-danger" value="新しく画像をアップロード"/></label>
		  			<%= Helper.tokenTags() %>
					<input id="fileupload" type="file" name="file" class="invisible" />
				</form>
			</div>
			<div class="span6">
				<ul id="thumbnails" class="thumbnails">
					<li class="span2"><a href="#" class="thumbnail"><img src="http://placehold.it/160x120" alt=""></a></li>
					<li class="span2"><a href="#" class="thumbnail"><img src="http://placehold.it/160x120" alt=""></a></li>
					<li class="span2"><a href="#" class="thumbnail"><img src="http://placehold.it/160x120" alt=""></a></li>
					<li class="span2"><a href="#" class="thumbnail"><img src="http://placehold.it/160x120" alt=""></a></li>
					<li class="span2"><a href="#" class="thumbnail"><img src="http://placehold.it/160x120" alt=""></a></li>
		    	</ul>
		    	<div id="image-pagination" class="pagination pagination-centered"></div>			
			</div>
		</div>
    	
  	</div>
  	<div class="modal-footer spinner-container">
	    <a href="#" id="image-upload-dialog-ok" class="btn btn-primary">OK</a>
	    <a href="#" class="btn" data-dismiss="modal">キャンセル</a>
  	</div>
	
	<%-- Since IE does not support XHR File upload, we use iframe trasport technique here... Too bad. --%>
	<script>
	var links = partakeUI.pagination($('#image-pagination'), 1, 100, 6);
	
	$('#image-upload-dialog-ok').click(function() {
		var dialog = $('#image-upload-dialog');
		dialog.modal('hide');
		var imageId = $('#selected-image').attr('imageId');
		if (dialog.attr('purpose') == "foreground" && imageId) {
			$('#fore-image-id-input').val(imageId);
			$('#selected-fore-image').attr('src', '/images/' + imageId);
		} else if (dialog.attr('purpose') == "background" && imageId) {
			$('#back-image-id-input').val(imageId);
			$('#selected-back-image').attr('src', '/images/' + imageId);
		}		
	});
	
	function selectImage(imageId) {
		$('#selected-image').attr('imageId', imageId);
		$('#selected-image').attr('src', '/images/' + imageId);
	}
	
	function deleteImagesIfTooMany() {
		var lis = $('#thumbnails li');
		console.log(lis.length);
		for (var i = 6; i < lis.length; ++i) {
			$(lis.get(i)).remove();	
		}
	}
	
	$('#fileupload').fileupload({
		url: '/api/image/create',
		files: [{name: $('#fileupload').val()}],
        fileInput: $('#fileupload'),
        always: function(e, data) {
        	
        },
		done: function (e, data) {
			var xhr = data.jqXHR;
			try {
				var json = $.parseJSON(xhr.responseText);
				var img = $('<img alt=""/>').attr('src', '/images/' + json.imageId);
				var a = $('<a class="thumbnail"></a>').append(img);
				a.click(function() { selectImage(json.imageId); });
				var li = $('<li class="span2"></li>').append(a);
				$('#thumbnails').prepend(li);
				deleteImagesIfTooMany();
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

