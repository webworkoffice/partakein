package in.partake.resource;

@SuppressWarnings("nls")
public final class Constants {
    // twitter settings    
	public static final String TWITTER_APPLICATION_NAME  = "Partake";
    
    public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
    public static final String TWITTER_ACCESS_TOKEN_URL  = "http://twitter.com/oauth/access_token";
    public static final String TWITTER_AUTHORIZE_URL 	 = "http://twitter.com/oauth/authorize";

    public static final String ANTISAMY_POLICY_FILE_RELATIVE_LOCATION = "/WEB-INF/antisamy/antisamy-myspace-1.4.1.xml"; 
        
    // TODO: なんでこれ形式が統一されてないの？
    
    // Session attribute keys
    public static final String ATTR_ACTION = "action-model"; // 
    
    public static final String ATTR_USER = "user";			// User (logged in user)
    public static final String ATTR_SHOWING_USER = "user_showing"; // user (for show())
    public static final String ATTR_ERRORMSGS = "errormsg";	// List<String>
    
    // 
    public static final String ATTR_EVENTSET = "eventset";
    public static final String ATTR_EVENT = "event";
    public static final String ATTR_RECENT_EVENTS = "recent_events";
    
    public static final String ATTR_OWNED_EVENTSET = "ownedeventset";
    public static final String ATTR_ENROLLED_EVENTSET = "enrolledeventset";
    public static final String ATTR_FINISHED_EVENTSET = "finishedeventset";

    public static final String ATTR_ENROLLED_PARTICIPATIONS = "ENROLLED_PARTICIPANTS";
    public static final String ATTR_SPARE_PARTICIPATIONS = "SPARE_PARTICIPANTS";
    public static final String ATTR_CANCELLED_PARTICIPATIONS = "CANCELLED_PARTICIPANTS";
    
    public static final String ATTR_REDIRECTURL = "redirectURL";

    public static final String ATTR_PARTICIPATION_STATUS = "participationstatus";
    public static final String ATTR_NOTIFICATION_STATUS = "notificationstatus";
    
    public static final String ATTR_DEADLINE_OVER = "deadline_over";
    
    public static final String ATTR_COMMENTSET = "commentset";

    public static final String ATTR_SEARCH_RESULT = "search_result";
    
    public static final String ATTR_CURRENT_URL = "CURRENT_URL";
    
    public static final String ATTR_OPENID_DISCOVERY_INFORMATION = "OPENID_DISC";
    public static final String ATTR_OPENID_PURPOSE = "OPENID_PURPOSE";
    
    public static final String ATTR_WARNING_MESSAGE = "WARNING_MESSAGE";
    
    
    private Constants() {
    	// prevent from instantiation.
    }
}
