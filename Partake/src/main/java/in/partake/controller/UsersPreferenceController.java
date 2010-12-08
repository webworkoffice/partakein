package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.service.UserService;

public class UsersPreferenceController extends PartakeActionSupport {
	private boolean profilePublic;
	private boolean receivingTwitterMessage;
	private boolean tweetingAttendanceAutomatically;
	
	public String showPreference() {
		try {
			User user = (User)session.get(Constants.ATTR_USER);
			if (user == null) { return LOGIN; }
			
			UserPreference pref = UserService.get().getUserPreference(user.getId()); 
			
			profilePublic = pref.isProfilePublic();
			receivingTwitterMessage = pref.isReceivingTwitterMessage();
			tweetingAttendanceAutomatically = pref.tweetsAttendanceAutomatically();
			
			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	public String setPreference() {
		try {
		    UserEx user = getLoginUser();
			if (user == null) { return LOGIN; }
			
			UserPreference embryo = new UserPreference(profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);
			UserService.get().setUserPreference(user.getId(), embryo);
			
			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	// --------------------------------------------------
	
	public boolean isProfilePublic() {
		return this.profilePublic;
	}
	
	public boolean isReceivingTwitterMessage() {
		return this.receivingTwitterMessage;
	}
	
	public boolean isTweetingAttendanceAutomatically() {
		return this.tweetingAttendanceAutomatically;
	}
	
	public void setProfilePublic(boolean profilePublic) {
		this.profilePublic = profilePublic;
	}
	
	public void setReceivingTwitterMessage(boolean receivingTwitterMessage) {
		this.receivingTwitterMessage = receivingTwitterMessage;
	}
	
	public void setTweetingAttendanceAutomatically(boolean tweetingAttendanceAutomatically) {
		this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
	}
}
