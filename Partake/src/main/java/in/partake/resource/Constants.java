package in.partake.resource;

import java.util.UUID;

@SuppressWarnings("nls")
public final class Constants {
    // twitter settings
    public static final String TWITTER_APPLICATION_NAME  = "Partake";
    public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
    public static final String TWITTER_ACCESS_TOKEN_URL  = "http://twitter.com/oauth/access_token";
    public static final String TWITTER_AUTHORIZE_URL 	 = "http://twitter.com/oauth/authorize";

    public static final String ANTISAMY_POLICY_FILE_RELATIVE_LOCATION = "/antisamy-partake-from-myspace-1.4.1.xml";

    public static final UUID DEMO_ID = UUID.fromString("ff24c3ad-98b6-4fe3-b2be-68d220d6a866");

    // Session Attribute
    public static final String ATTR_USER = "user";          // User (logged in user)

    // Request Attribute
    public static final String ATTR_ACTION = "actionModel"; //
    public static final String ATTR_PARTAKE_SESSION = "sessionToken";
    public static final String ATTR_REDIRECTURL = "redirectURL";
    public static final String ATTR_CURRENT_URL = "currentURL";
    public static final String ATTR_NO_HEADER_MESSAGES = "NO_HEADER_MESSAGES";
    @Deprecated
    public static final String ATTR_PARTAKE_API_SESSION_TOKEN = ATTR_PARTAKE_SESSION;

    // TODO: We'd like to use 'long' value as is.
    @Deprecated
    public static final String JSON_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    private Constants() {
        // Prevents from instantiation.
    }
}
