<%@page import="in.partake.view.util.Helper"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@page import="in.partake.model.dto.auxiliary.EventCategory"%>
<%@page import="in.partake.base.KeyValuePair"%>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<div id="title" class="control-group">
    <label class="control-label" for="title">タイトル <span class="label label-important">必須</span></label>
    <div class="controls">
        <input type="text" name="title" class="span7" placeholder="タイトル" />
        <p class="help-block">イベントのタイトルです。100 文字以内で記述します。</p>
    </div>
</div>
<div id="summary" class="control-group">
    <label class="control-label" for="summary">概要</label>
    <div class="controls">
    	<input type="text" name="summary" class="span7" placeholder="概要" />
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
		$('category').val('<%= Helper.h(EventCategory.CATEGORIES.get(0).getKey()) %>'); 
		</script>
	</div>
</div>        
<div id="description" class="control-group">
	<label class="control-label" for="description">説明 <span class="label label-important">必須</span></label>
	<div class="controls">
		<textarea name="description"></textarea>
		<p class="help-block">イベントの説明を記述します。(HTML などを含めて50000文字まで)</p>
	</div>
</div>
<div id="beginDate" class="control-group">
	<label class="control-label">開催日時 <span class="label label-important">必須</span></label>
    <div class="controls form-inline">
       	<input type="text" id="beginDataInput" name="beginDate" class="span2" placeholder="YYYY-MM-DD HH:MM" />
	</div>	
	<script>
	$('#beginDataInput').datetimepicker({ dateFormat: 'yy-mm-dd' });
	</script>
</div>
<div id="endDate" class="control-group">
    <label class="control-label">終了日時</label>
    <div class="controls">				
		<label class="checkbox">
			<input type="checkbox" id="usesEndDate" name="usesEndDate" />
			終了日時を設定する
        </label>
	</div>
	<div class="controls form-inline">
       	<input type="text" id="endDataInput" name="endDate" class="span2" placeholder="YYYY-MM-DD HH:MM" />
    </div>
	<script>
	$('#endDataInput').datetimepicker({
		dateFormat: 'yy-mm-dd',
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
			<input type="checkbox" id="usesDeadline" name="usesDeadline" />
			締め切りを設定する
       	</label>
	</div>
	<div class="controls form-inline">
       	<input type="text" id="deadlineInput" name="deadline" class="span2" placeholder="YYYY-MM-DD HH:MM" />
		<p class="help-block"> 締め切り以後は参加／不参加が変更できなくなります。設定しない場合、開始日時が締め切りとなります。</p>
	</div>
	<script>
	$('#deadlineInput').datetimepicker({ dateFormat: 'yy-mm-dd' });
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
		<input type="text" name="capacity" class="span7" />
		<p class="help-block">定員を超える参加表明者は補欠者として扱われます。0 をいれると定員なしの意味になります。</p>
	</div>
</div>
<div id="foreImageId" class="control-group">
	<label class="control-label" for="foreImage">掲載画像</label>
	<div class="controls form-inline">
		<label class="checkbox"><input type="checkbox" name="foreImage" />掲載する</label>
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
	        <li class="span2"><img src="http://placehold.it/260x180" alt=""></li>
        </ul>
		<p>新しく画像を選択します。</p>
   	</div>
</div>
<div class="control-group">
   	<label class="control-label" for="backImage">背景画像</label>
	<div class="controls form-inline">
		<label class="checkbox"><input type="checkbox" id="backImage" name="backImage" />掲載する</label>
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
<div class="control-group">
   	<label class="control-label">会場</label>
    <div class="controls">
    	<input type="text" id="place" name="place" class="span7" />
    	<p class="help-block">会場名を設定します。</p>
    </div>
</div>
<div class="control-group">
   	<label class="control-label">住所</label>
   	<div class="controls">
   		<input type="text" id="address" name="address" class="span7" />
   		<p class="help-block">住所を正確に入力すると、google の地図を表示できます。</p>
   	</div>
</div>
<div class="control-group">
   	<label class="control-label">URL</label>
  	<div class="controls">
   		<input type="text" id="url" name="url" class="span7" />
   		<p class="help-block">参考 URL を設定します。</p>
   	</div>
</div>
<div class="control-group">
   	<label class="control-label">ハッシュタグ</label>
   	<div class="controls">
   		<input type="text" id="hashTag" name="hashTag" class="span7" />
		<p class="help-block">twitter で用いる公式ハッシュタグを設定できます。# から始まる英数字、日本語、アンダースコアなどを含む文字列が使用できます。</p>
   	</div>
</div>

<h3 class="switchHat"><a title="関連イベント　,　複数の管理者">▼ 詳細な設定&nbsp;&nbsp;（関連イベントや編集者を設定できます。）</a></h3>

<div class="switchDetail">

<div class="control-group">
	<label for="secret" class="control-label">非公開設定</label>
	<div class="controls">
		<label class="checkbox"><input type="checkbox" id="secret" name="secret"/>非公開にする</label>
		<p class="help-block">非公開設定にすると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</p>
	</div>
</div>
<div class="control-group">
	<label for="passcode" class="control-label">パスコード</label>
	<div class="controls">
		<input type="text" id="passcode" name="passcode" class="text-input" />
	</div>
</div>
<script>
function checkPasscode() {
    if ($('#secret').is(':checked')) {
        $('#passcode').attr('disabled', null);
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

<div class="control-group">
	<label for="editors" class="control-label">編集者</label>
	<div class="controls">
    	<input type="text" id="editors" name="editors" class="span7" />
        <p class="help-block">自分以外にも編集者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。編集者はイベント削除以外のことを行うことが出来ます。</p>
        <p class="help-block">例： user1, user2, user3</p>
	</div>
</div>

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

