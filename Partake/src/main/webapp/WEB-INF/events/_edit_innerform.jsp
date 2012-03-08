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
		<label class="checkbox"><input id="fore-image-checkbox" type="checkbox" name="foreImage" <%= event != null && event.getForeImageId() != null ? "checked" : "" %>/>掲載する</label>
		<input type="hidden" id="fore-image-id-input" name="foreImageId" value="<%= event != null ? event.getForeImageId() : "" %>" />
		<p class="help-block">画像を設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</p>
	</div>
	<div id="fore-image-chooser" class="controls" style="display:none">
		<ul class="thumbnails"><li class="span2">
		    <img id="selected-fore-image"
	        	 src="<%= event != null && event.getForeImageId() != null ? "/images/" + event.getForeImageId() : "/images/no-image.png" %>"
	             alt="">
        </li></ul>
        <p><input id="select-new-foreground-image" type="button" class="btn" value="新しく画像を選択します" /></p>
		<script>
			$('#select-new-foreground-image').click(function() {
				$('#image-upload-dialog').attr('purpose', 'foreground');
				var imageId = $('#fore-image-id-input').val();
				if (!imageId || imageId == "") {
					$('#selected-image').attr('src', '/images/no-image.png');
					$('#selected-image').removeAttr('imageId');
				} else {
					$('#selected-image').attr('src', '/images/' + imageId);
					$('#selected-image').attr('imageId', imageId);
				}
				$('#image-upload-dialog').modal('show');
			});
		</script>
   	</div>
   	<script>
   	function updateForeImageChooser() {
		var v = $('#fore-image-checkbox').is(':checked');
		if (v)
			$('#fore-image-chooser').fadeIn("fast");
		else
			$('#fore-image-chooser').fadeOut("fast");   	
   	}
	$('input[name="foreImage"]').change(updateForeImageChooser);
	updateForeImageChooser();
	</script>
   	
</div>

<div id="backImageId" class="control-group">
   	<label class="control-label" for="backImage">背景画像</label>
	<div class="controls form-inline">
		<label class="checkbox"><input id="back-image-checkbox" type="checkbox" name="backImage" <%= event != null && event.getBackImageId() != null ? "checked" : "" %>/>掲載する</label>
		<input type="hidden" id="back-image-id-input" name="backImageId" value="<%= event != null ? event.getBackImageId() : "" %>" />
		<p class="help-block">画像を設定できます。画像は上部に背景にされます。(png, gif, jpeg 画像のみが送信できます)</p>
	</div>
	<div id="back-image-chooser" class="controls" style="display:none">
		<ul class="thumbnails"><li class="span2">
	        <img id="selected-back-image"
	        	 src="<%= event != null && event.getBackImageId() != null ? "/images/" + event.getBackImageId() : "/images/no-image.png" %>"
	             alt="">
        </li></ul>
        <p><input id="select-new-background-image" type="button" class="btn" value="新しく画像を選択します" /></p>
		<script>
			$('#select-new-background-image').click(function() {
				$('#image-upload-dialog').attr('purpose', 'background');
				var imageId = $('#back-image-id-input').val();
				if (!imageId || imageId == "") {
					$('#selected-image').attr('src', '/images/no-image.png');
					$('#selected-image').removeAttr('imageId');
				} else {
					$('#selected-image').attr('src', '/images/' + imageId);
					$('#selected-image').attr('imageId', imageId);
				}
				$('#image-upload-dialog').modal('show');
			});
		</script>
   	</div>
   	<script>
   	function updateBackImageChooser() {
		var v = $('#back-image-checkbox').is(':checked');
		if (v)
			$('#back-image-chooser').fadeIn("fast");
		else
			$('#back-image-chooser').fadeOut("fast");   		
   	}
   	$('input[name="backImage"]').change(updateBackImageChooser);
   	updateBackImageChooser();
	</script>
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

<div id="reletedEvents" class="control-group">
	<label class="control-label">関連イベント</label>
	<div class="controls">
		<table class="table">
			<colgroup>
    			<col class="span1" /><col class="span4" /><col class="span1" /><col class="span1" />
			</colgroup>
			<thead>
				<tr><th></th><th>イベント ID</th><th>必須</th><th>優先</th></tr>
			</thead>
			<tbody id="related-event-tbody">
				<tr>
					<td><i class="icon-plus-sign vertical-middle"></i> <i class="icon-minus-sign vertical-middle"></i></td>
					<td><input type="text"     name="relatedEventID[]" class="span4" /></td>
					<td><input type="checkbox" name="relatedEventRequired[]" /></td>
					<td><input type="checkbox" name="relatedEventPriority[]" /></td>
				</tr>
			</tbody>
		</table>
		<script>
		function updateRelatedEvent() {
			$('#related-event-tbody .icon-plus-sign').unbind('click');
			$('#related-event-tbody .icon-minus-sign').unbind('click');
			$('#related-event-tbody .icon-plus-sign').click(onClickPlusSign);
			$('#related-event-tbody .icon-minus-sign').click(onClickMinusSign);
			if ($('#related-event-tbody tr').size() > 1)
				$('#related-event-tbody .icon-minus-sign').show();
			else
				$('#related-event-tbody .icon-minus-sign').hide();
		}
		updateRelatedEvent();
		function onClickPlusSign(e) {
			var newTr = $('<tr></tr>');
			newTr.html('<td><i class="icon-plus-sign vertical-middle"></i> <i class="icon-minus-sign vertical-middle"></i></td>' +
					'<td><input type="text"     name="relatedEventID[]" class="span4" /></td>' +
					'<td><input type="checkbox" name="relatedEventRequired[]" /></td>' +
					'<td><input type="checkbox" name="relatedEventPriority[]" /></td>');
			var tr = e.srcElement.parentNode.parentNode;
			console.log(tr);
			$(tr).after(newTr);
			updateRelatedEvent();
		}
		function onClickMinusSign(e) {
			var tr = e.srcElement.parentNode.parentNode;
			$(tr).remove();
			updateRelatedEvent();
		}
		</script>
		<p class="help-block">関連イベントを設定できます。</p>
		<p class="help-block">「必須」にチェックすると、そのイベントに登録されていなければこのイベントに登録することは出来ません。</p>
		<p class="help-block">「優先」にチェックすると、そのイベントに登録している方は優先的にこのイベントに参加することが出来ます。</p>
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
					<li class="span3"><img id="selected-image" src="/images/no-image.png" alt=""></li>
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
	
	$('#image-upload-dialog').on('shown', function() {
		console.log('hogehoge');
	});
	
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

