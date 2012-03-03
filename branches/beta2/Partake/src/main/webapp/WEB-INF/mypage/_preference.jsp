<%@page import="in.partake.model.dto.UserPreference"%>
<%@page import="in.partake.controller.action.mypage.MypageAction"%>
<%@page import="in.partake.resource.PartakeProperties"%>
<%@page import="in.partake.view.util.Helper"%>
<%@page import="java.util.List"%>
<%@page import="in.partake.model.UserEx"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="in.partake.resource.Constants"%>
<%@page import="static in.partake.view.util.Helper.h"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<%
	UserEx user = (UserEx)request.getSession().getAttribute(Constants.ATTR_USER);
	MypageAction action = (MypageAction) request.getAttribute(Constants.ATTR_ACTION);
	UserPreference pref = action.getPreference();
	List<String> associatedOpenIds = action.getOpenIds();
%>

<h2>各種設定</h2>
<div class="row"><form class="form-horizontal"><fieldset>
	<div class="control-group">
    	<label class="control-label">設定項目</label>
        <div class="controls">
            <label class="checkbox">
            	<input type="checkbox" id="receivingTwitterMessage" name="receivingTwitterMessage" <%= pref.isReceivingTwitterMessage() ? "checked" : "" %> />
				twitter 経由のリマインダーを受け取る (default:受け取る)
            </label>
            <label class="checkbox">
            	<input type="checkbox" id="profilePublic" name="profilePublic" <%= pref.isProfilePublic() ? "checked" : "" %>/>
				マイページを他人にも公開する (default：公開)
            </label>
            <label class="checkbox">
            	<input type="checkbox" id="tweetingAttendanceAutomatically" name="tweetingAttendanceAutomatically" <%= pref.tweetsAttendanceAutomatically() ? "checked" : "" %>/>
	 			イベントに参加するとき、自動的に参加をつぶやく (default：つぶやかない)
            </label>
            <p class="spinner-container">
	            <input id="setPreferenceButton" type="button" value="この設定を保存する" />            
	            <span id="setPreferenceMessage" class="text-info"></span>
            </p>
            <script>
              	function callSetPreference() {
              		var spinner = partakeUI.spinner(document.getElementById('setPreferenceButton'));
              		var receivingTwitterMessage = $('#receivingTwitterMessage').is(':checked');
              		var profilePublic = $('#profilePublic').is(':checked');
              		var tweetingAttendanceAutomatically = $('#tweetingAttendanceAutomatically').is(':checked');
              		
              		spinner.show();
              		$('#setPreferenceButton').attr('disabled', '');
              		partake.setPreference(receivingTwitterMessage, profilePublic, tweetingAttendanceAutomatically).success(function(json) {
              			$('#setPreferenceMessage').hide();
              			$('#setPreferenceMessage').text("設定を保存しました。");
              			$('#setPreferenceMessage').fadeIn("fast");
              			$('#setPreferenceButton').removeAttr("disabled");
            			spinner.hide();
            		}).error(function(xhr) {
              			$('#setPreferenceMessage').hide();
              			$('#setPreferenceMessage').text("設定の保存に失敗しました。: " + json.reason);
              			$('#setPreferenceMessage').fadeIn("fast");
        				$('#setPreferenceButton').removeAttr("disabled");
        				spinner.hide();
              		});
              	};
              	
              	$('#setPreferenceButton').click(callSetPreference);
            </script>            
        </div>
    </div>
</fieldset></form></div>
