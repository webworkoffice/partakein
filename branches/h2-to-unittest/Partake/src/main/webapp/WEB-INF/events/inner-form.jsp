<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<div class="control-group">
    <label class="control-label" for="title">タイトル <span class="label label-important">必須</span></label>
    <div class="controls">
        <s:textfield id="title" name="title" cssClass="span7" placeholder="タイトル" />
        <p class="help-block">イベントのタイトルです。100 文字以内で記述します。</p>
    </div>
</div>
<div class="control-group">
    <label class="control-label" for="summary">概要</label>
    <div class="controls">
    	<s:textfield id="summary" name="summary" cssClass="span7" placeholder="概要" />
        <p class="help-block">イベントを一言で表す概要です。100 文字以内で記述します。</p>
    </div>
</div>
<div class="control-group">
	<label class="control-label">カテゴリ</label>
	<div class="controls">
		<s:select id="category" name="category" list="categories" listKey="key" listValue="value" />
	</div>
</div>        
<div class="control-group">
	<label class="control-label" for="description">説明 <span class="label label-important">必須</span></label>
	<div class="controls">
		<s:textarea id="description" name="description" cssClass="text-input" />
		<p class="help-block">イベントの説明を記述します。(HTML などを含めて50000文字まで)</p>
	</div>
</div>
<div class="control-group">
	<label class="control-label">開催日時 <span class="label label-important">必須</span></label>
    <div class="controls form-inline">
		<s:select id="syear" cssClass="span1" name="syear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="syear">年</label>
		<s:select id="smonth" cssClass="span1" name="smonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="smonth">月</label>
		<s:select id="sday" cssClass="span1" name="sday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="sday">日</label>
		<s:select id="shour" cssClass="span1" name="shour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="shour">時</label>
		<s:select id="smin" cssClass="span1" name="smin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="smin">分</label>
    </div>
</div>
<div class="control-group">
    <label class="control-label">終了日時</label>
    <div class="controls">				
		<label class="checkbox">
			<s:checkbox id="usesEndDate" name="usesEndDate" />
			終了日時を設定する
        </label>
	</div>
	<div class="controls form-inline">
		<s:select id="eyear" cssClass="span1" name="eyear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="eyear">年</label>
		<s:select id="emonth" cssClass="span1" name="emonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="emonth">月</label>
		<s:select id="eday" cssClass="span1" name="eday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="eday">日</label>
		<s:select id="ehour" cssClass="span1" name="ehour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="ehour">時</label>
		<s:select id="emin" cssClass="span1" name="emin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="emin">分</label>
    </div>
</div>
<div class="control-group">
    <label class="control-label">申込締切</label>
    <div class="controls">				
		<label class="checkbox">
			<s:checkbox id="usesDeadline" name="usesDeadline" />
			締め切りを設定する
       	</label>
	</div>
	<div class="controls form-inline">
		<s:select id="dyear" cssClass="span1" name="dyear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="dyear">年</label>
		<s:select id="dmonth" cssClass="span1" name="dmonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="dmonth">月</label>
		<s:select id="dday" cssClass="span1" name="dday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="dday">日</label>
		<s:select id="dhour" cssClass="span1" name="dhour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="dhour">時</label>
		<s:select id="dmin" cssClass="span1" name="dmin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="dmin">分</label>
		<p class="help-block"> 締め切り以後は参加／不参加が変更できなくなります。設定しない場合、開始日時が締め切りとなります。</p>
	</div>
</div>
<div class="control-group">
   	<label class="control-label">定員</label>
   	<div class="controls">
		<s:textfield id="capacity" name="capacity" cssClass="span7" />
		<p class="help-block">定員を超える参加表明者は補欠者として扱われます。0 をいれると定員なしの意味になります。</p>
	</div>
</div>
<div class="control-group">
	<label class="control-label" for="foreImage">掲載画像</label>
	<div class="controls">
	    <s:if test="%{foreImageId == null}">
	        <s:file name="foreImage" id="foreImage" label="File" />
	    </s:if>
	    <s:else>
	    	<s:checkbox name="removingForeImage" id="removingForeImage" />画像を消去する
	    	<s:file name="foreImage" id="foreImage" label="File"/>
	    </s:else>
		<p class="help-block">画像を設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</p>
   	</div>
</div>        
<div class="control-group">
   	<label class="control-label" for="backImage">背景画像</label>
   	<div class="controls">
		<s:if test="%{backImageId == null}">
			<s:file name="backImage" id="backImage" label="File"/>
		</s:if>
		<s:else>
	    	<s:checkbox name="removingBackImage" id="removingBackImage" />背景画像を消去する
	    	<s:file name="backImage" id="backImage" label="File"/>
		</s:else>        	
		<p class="help-block">背景画像を設定できます。(png, gif, jpeg 画像のみが送信できます)</p>
    </div>
</div>
<div class="control-group">
   	<label class="control-label">会場</label>
    <div class="controls">
    	<s:textfield id="place" name="place" cssClass="span7" />
    	<p class="help-block">会場名を設定します。</p>
    </div>
</div>
<div class="control-group">
   	<label class="control-label">住所</label>
   	<div class="controls">
   		<s:textfield id="address" name="address"  cssClass="span7" />
   		<p class="help-block">住所を正確に入力すると、google の地図を表示できます。</p>
   	</div>
</div>
<div class="control-group">
   	<label class="control-label">URL</label>
  	<div class="controls">
   		<s:textfield id="url" name="url" cssClass="span7" />
   		<p class="help-block">参考 URL を設定します。</p>
   	</div>
</div>
<div class="control-group">
   	<label class="control-label">ハッシュタグ</label>
   	<div class="controls">
   		<s:textfield id="hashTag" name="hashTag" cssClass="span7" />
		<p class="help-block">twitter で用いる公式ハッシュタグを設定できます。# から始まる英数字、日本語、アンダースコアなどを含む文字列が使用できます。</p>
   	</div>
</div>

<h3 class="switchHat"><a title="関連イベント　,　複数の管理者">▼ 詳細な設定&nbsp;&nbsp;（関連イベントや編集者を設定できます。）</a></h3>

<div class="switchDetail">

<div class="control-group">
	<label for="secret" class="control-label">非公開設定</label>
	<div class="controls">
		<label><s:checkbox id="secret" name="secret"/>非公開にする</label>
		<p class="help-block">非公開設定にすると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</p>
	</div>
</div>
<div class="control-group">
	<label for="passcode" class="control-label">パスコード</label>
	<div class="controls">
		<s:textfield id="passcode" name="passcode" cssClass="text-input" />
	</div>
</div>

<div class="control-group">
	<label for="secret" class="control-label">関連イベント</label>
	<div class="controls">
		<table class="table">
			<thead>
				<tr><th>イベント ID</th><th>登録必須</th><th>優先参加</th></tr>
			</thead>
			<tbody>
				<tr>
					<td><s:textfield id="relatedEventID1"       name="relatedEventID1" cssClass="text-input" /></td>
					<td><s:checkbox  id="relatedEventRequired1" name="relatedEventRequired1" /></td>
					<td><s:checkbox  id="relatedEventPriority1" name="relatedEventPriority1" /></td>
				</tr>
				<tr>
					<td><s:textfield id="relatedEventID2"       name="relatedEventID2" cssClass="text-input" /></td>
					<td><s:checkbox  id="relatedEventRequired2" name="relatedEventRequired2" /></td>
					<td><s:checkbox  id="relatedEventPriority2" name="relatedEventPriority2" /></td>
				</tr>
				<tr>
					<td><s:textfield id="relatedEventID3"       name="relatedEventID3" cssClass="text-input" /></td>
					<td><s:checkbox  id="relatedEventRequired3" name="relatedEventRequired3" /></td>
					<td><s:checkbox  id="relatedEventPriority3" name="relatedEventPriority3" /></td>
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
	<label for="managers" class="control-label">編集者</label>
	<div class="controls">
    	<s:textfield id="managers" name="managers" cssClass="span7" />
        <p class="help-block">自分以外にも編集者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。編集者はイベント削除以外のことを行うことが出来ます。</p>
        <p class="help-block">例： user1, user2, user3</p>
	</div>
</div>

</div>
