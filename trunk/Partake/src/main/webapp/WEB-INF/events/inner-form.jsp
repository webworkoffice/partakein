<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<dl class="event-form">
<dt><label for="title">タイトル<span class="required">(必須)</span></label>:</dt>
	<dd><s:textfield id="title" name="title" cssClass="text-input" /></dd>
<dt><label for="summary">概要</label>:</dt>
	<dd><s:textfield id="summary" name="summary" cssClass="text-input" /><br />
	   <span class="accent">＞</span> 概要は 100 文字以内で記述してください。</dd>
<dt><label for="category">カテゴリ</label>:</dt>
	<dd><s:select id="category" name="category" list="categories" listKey="key" listValue="value"></s:select></dd>
<dt><label for="description">説明</label>:</dt>
	<dd><s:textarea id="description" name="description" cssClass="text-input" /><br />
	<span class="accent">＞</span> イベントの説明を記述します。(HTML などを含めて50000文字まで)</dd>
<dt>開催日時<span class="required">(必須)</span>:</dt>
	<dd><s:select id="syear" name="syear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="syear">年</label>
		<s:select id="smonth" name="smonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="smonth">月</label>
		<s:select id="sday" name="sday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="sday">日</label>
		<s:select id="shour" name="shour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="shour">時</label>
		<s:select id="smin" name="smin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="smin">分</label></dd>
<dt>終了日時:</dt>
	<dd><s:checkbox id="usesEndDate" name="usesEndDate" />終了日時を設定する<br />
		<s:select id="eyear" name="eyear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="eyear">年</label>
		<s:select id="emonth" name="emonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="emonth">月</label>
		<s:select id="eday" name="eday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="eday">日</label>
		<s:select id="ehour" name="ehour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="ehour">時</label>
		<s:select id="emin" name="emin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="emin">分</label><br />
		<span class="accent">＞</span> イベントの終了時刻を設定できます。</dd>
<dt>申込締切:</dt>
	<dd><s:checkbox id="usesDeadline" name="usesDeadline" />締め切りを設定する<br />
		<s:select id="dyear" name="dyear" list="{'2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019'}"></s:select><label for="dyear">年</label>
		<s:select id="dmonth" name="dmonth" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'}"/><label for="dmonth">月</label>
		<s:select id="dday" name="dday" list="{'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'}"/><label for="dday">日</label>
		<s:select id="dhour" name="dhour" list="{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'}" /><label for="dhour">時</label>
		<s:select id="dmin" name="dmin" list="{'0', '5', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55'}"  /><label for="dmin">分</label><br />
		<span class="accent">＞</span> 締め切り以後は参加／不参加が変更できなくなります。設定しない場合、開始日時が締め切りとなります。</dd>
<dt><label for="capacity">定員</label>:</dt>
	<dd><s:textfield id="capacity" name="capacity" cssClass="text-input" /><br />
		<span class="accent">＞</span> 定員を超える参加表明者は補欠者として扱われます。0 をいれると定員なしの意味になります。</dd>
<dt><label for="foreImage">掲載画像</label>:</dt>
    <s:if test="%{foreImageId == null}">
        <dd><s:file name="foreImage" id="foreImage" label="File"/><br />
<div class="pict-link"><img src="<%= request.getContextPath() %>/images/camera.png" alt="link"/><a href="http://www.ashinari.com/" target="_blank" title="[Link]無料の素材屋さん「足成」">
 フリー画像はこちら (別ウインドウ)
</a>
</div>
		<span class="accent">＞</span> 画像を設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</dd>
    </s:if>
    <s:else>
    	<dd><s:checkbox name="removingForeImage" id="removingForeImage" />画像を消去する<br />
    		<s:file name="foreImage" id="foreImage" label="File"/><br />
			<span class="accent">＞</span> 画像を再設定できます。画像は上部に掲載されます。(png, gif, jpeg 画像のみが送信できます)</dd>
    </s:else>
<dt><label for="backImage">背景画像</label>:</dt>
	<s:if test="%{backImageId == null}">
		<dd><s:file name="backImage" id="backImage" label="File"/><br />
		<span class="accent">＞</span> 背景画像を設定できます。(png, gif, jpeg 画像のみが送信できます)</dd>
	</s:if>
	<s:else>
    	<dd><s:checkbox name="removingBackImage" id="removingBackImage" />背景画像を消去する<br />
    		<s:file name="backImage" id="backImage" label="File"/><br />
			<span class="accent">＞</span> 背景画像を再設定できます。(png, gif, jpeg 画像のみが送信できます)</dd>
	</s:else>
<dt><label for="place">会場</label>:</dt>
	<dd><s:textfield id="place" name="place" cssClass="text-input" /></dd>
<dt><label for="address">住所</label>:</dt>
	<dd><s:textfield id="address" name="address"  cssClass="text-input"/><br />
		<span class="accent">＞</span> 住所を正確に入力すると、google の地図を表示できます。</dd>
<dt><label for="url">URL</label>:</dt>
	<dd><s:textfield id="url" name="url" cssClass="text-input" /></dd>
<dt><label for="hashTag">ハッシュタグ</label></dt>
	<dd><s:textfield id="hashTag" name="hashTag" cssClass="text-input" /><br />
		<span class="accent">＞</span> twitter で用いる公式ハッシュタグを設定できます。# から始まる英数字、アンダースコアのみの文字列が使用できます。<br />100 文字まで設定できます。</dd>
<dt><label for="secret">非公開設定</label>:</dt>
	<dd><s:checkbox id="secret" name="secret"/>非公開にする<br />
		<span class="accent">＞</span> 非公開設定にすると、管理者以外の方はイベントの閲覧にパスコードが必要になります。</dd>
<dt><label for="passcode">パスコード</label>:</dt>
	<dd><s:textfield id="passcode" name="passcode" cssClass="text-input" /></dd>
</dl>

<span class="switchHat"><a title="関連イベント　,　複数の管理者">▼ 詳細な設定&nbsp;&nbsp;（関連イベント/自分以外の管理者）</a></span>

<div class="switchDetail rad">
<dl class="event-form">
<dt>関連イベント：</dt>
	<dd>
		<table class="associated-event">
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

        <span class="accent">＞</span>関連イベントを設定できます。<br />
        <span class="accent">＞</span>登録必須にチェックすると、そのイベントに登録されていなければこのイベントに登録することは出来ません。<br />
        <span class="accent">＞</span>優先参加にチェックすると、そのイベントに登録している方は優先的にこのイベントに参加することが出来ます。<br />
        <span class="accent">＞</span>イベント ID とは、 http://partake.in/events/{ID} の {ID} の部分の文字列です。<%-- TODO use in.partake.toppath from properties file --%>
        </dd>
<dt><label for="managers">自分以外の管理者：</label></dt>
    <dd><s:textfield id="managers" name="managers" cssClass="text-input" /><br />
        <span class="accent">＞</span>自分以外にも管理者を指定できます。twitter のショートネームをコンマ区切りで列挙してください。この管理者はイベント削除以外のことを行うことが出来ます。<br />
        <span class="accent">＞</span>例： user1, user2, user3</dd>
</dl>
</div>