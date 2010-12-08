<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>


<s:hidden name="parentEventId" id="parentEventId" />
<dl class="event-form">
<dt><label for="title">タイトル<span class="required">(必須)</span></label>:</dt>
	<dd><s:textfield id="title" name="title" cssClass="text-input" /></dd>
<dt><label for="summary">概要</label>:</dt>
	<dd><s:textfield id="summary" name="summary" cssClass="text-input" /><br />
	   <span class="accent">＞</span> 概要は 100 文字以内で記述してください。</dd>
<dt><label for="category">カテゴリー</label>:</dt>
	<dd><s:select id="category" name="category" list="categories" listKey="key" listValue="value"></s:select></dd>
<dt>開始日時(必須):</dt>
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
<dt><label for="place">会場</label>:</dt>
	<dd><s:textfield id="place" name="place" cssClass="text-input" /></dd>
<dt><label for="address">住所</label>:</dt>
	<dd><s:textfield id="address" name="address"  cssClass="text-input"/><br />
		<span class="accent">＞</span> 住所を正確に入力すると、google の地図を表示できます。</dd>
<dt><label for="url">URL</label>:</dt>
	<dd><s:textfield id="url" name="url" cssClass="text-input" /></dd>
<dt><label for="havingPriority">優先参加権</label></dt>
	<dd><s:checkbox id="havingPriority" name="havingPriority" />このサブイベントに参加する人は親イベントへの優先参加権を得ます<br />
		<span class="accent">＞</span> このサブイベントの参加者は、親イベントに優先的に参加できるようになります。</dd>
<dt><label for="parentRequired"></label></dt>
	<dd><s:checkbox id="parentRequired" name="parentRequired" />このサブイベントに参加する人は親イベントへ参加していなければなりません<br />
		<span class="accent">＞</span> 親イベントの参加者のみがこのサブイベントの参加権利を得ます。(親イベントをキャンセルすると、このイベントへの参加もキャンセルされます。)</dd>
</dl>