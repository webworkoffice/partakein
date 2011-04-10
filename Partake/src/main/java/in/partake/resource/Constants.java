package in.partake.resource;

@SuppressWarnings("nls")
public final class Constants {
    // twitter settings    
	public static final String TWITTER_APPLICATION_NAME  = "Partake";
    
    public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
    public static final String TWITTER_ACCESS_TOKEN_URL  = "http://twitter.com/oauth/access_token";
    public static final String TWITTER_AUTHORIZE_URL 	 = "http://twitter.com/oauth/authorize";

    public static final String ANTISAMY_POLICY_FILE_RELATIVE_LOCATION = "/antisamy-partake-from-myspace-1.4.1.xml"; 

    // TODO: なんでこれ形式が統一されてないの？
    // TODO: あと、Attribute で渡して JSP で表示というのがださ過ぎるのでなんとかしたい。
    
    // Session attribute keys
    public static final String ATTR_ACTION = "action-model"; // 
    
    public static final String ATTR_USER = "user";			// User (logged in user)
    public static final String ATTR_SHOWING_USER = "user_showing"; // user (for show())
    public static final String ATTR_ERRORMSGS = "errormsg";	// List<String>
    
    public static final String ATTR_CSRF_PREVENTION = "csrf_prevention_token";
    public static final String ATTR_PARTAKE_TOKEN = "partake.token";
    public static final String ATTR_PARTAKE_ONETIME_TOKEN = "partake.onetime.token";
    
    /** error が発生した場合の詳細を説明するために用いる */
    public static final String ATTR_ERROR_DESCRIPTION = "ERROR_DESCRIPTION";
    
    // 
    public static final String ATTR_EVENTSET = "eventset";
    public static final String ATTR_EVENT = "event";
    public static final String ATTR_RECENT_EVENTS = "recent_events";
    
    public static final String ATTR_OWNED_EVENTSET = "ownedeventset";
    public static final String ATTR_ENROLLED_EVENTSET = "enrolledeventset";
    public static final String ATTR_FINISHED_EVENTSET = "finishedeventset";

    public static final String ATTR_PARTICIPATIONLIST = "PARTICIPATION_LIST";
    public static final String ATTR_REMINDER_STATUS = "REMINDER_STATUS";
    
    public static final String ATTR_REDIRECTURL = "redirectURL";

    public static final String ATTR_PARTICIPATION_STATUS = "PARTICIPATION_STATUS";
    
    public static final String ATTR_DEADLINE_OVER = "DEADLINE_OVER";
    
    public static final String ATTR_COMMENTSET = "COMMENTSET";
    public static final String ATTR_MESSAGESET = "MESSAGESET";
    
    public static final String ATTR_SEARCH_RESULT = "SEARCH_RESULT";
    
    public static final String ATTR_CURRENT_URL = "CURRENT_URL";
    
    public static final String ATTR_OPENID_DISCOVERY_INFORMATION = "OPENID_DISC";
    public static final String ATTR_OPENID_PURPOSE = "OPENID_PURPOSE";
    
    public static final String ATTR_WARNING_MESSAGE = "WARNING_MESSAGE";
    public static final String ATTR_ERROR_MESSAGE = "ERROR_MESSAGE";
    
    public static final String ATTR_REQUIRED_EVENTS = "REQUIRED_EVENTS";
    public static final String ATTR_EVENT_RELATIONS = "ATTR_EVENT_RELATIONS";
    
    public static final String ATTR_NO_HEADER_MESSAGES = "NO_HEADER_MESSAGES";
    
    public static final String ATTR_QUESTIONNAIRES = "QUESTIONNAIRES";
    
    private Constants() {
    	// prevent from instantiation.
    }
}
