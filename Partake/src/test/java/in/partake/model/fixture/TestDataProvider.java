package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;

import java.util.UUID;

public abstract class TestDataProvider<T> {
    // Users
    public static final String INVALID_USER_ID = new UUID(1, -1).toString();

    public static final String DEFAULT_USER_ID = new UUID(1, 0).toString();
    public static final String DEFAULT_TWITTER_ID = "0";
    public static final String DEFAULT_TWITTER_SCREENNAME = "testUser";
    public static final String DEFAULT_USER_OPENID_IDENTIFIER = "http://www.example.com/ident";
    public static final String DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER = "http://www.example.com/alternative";

    public static final String DEFAULT_ANOTHER_USER_ID = new UUID(1, 1).toString();
    public static final String DEFAULT_ANOTHER_TWITTER_ID = "1";
    public static final String DEFAULT_ANOTHER_TWITTER_SCREENNAME = "testUser2";
    
    public static final String USER_WITHOUT_PREF_ID = new UUID(1, 2).toString();
    public static final String USER_WITHOUT_PREF_TWITTER_ID = "2";
    public static final String USER_WITHOUT_PREF_SCREENNAME = "testUser3";

    public static final String ADMIN_USER_ID = new UUID(1, 8).toString();
    public static final String ADMIN_USER_TWITTER_ID = "8";
    public static final String ADMIN_USER_SCREENNAME = "partakein";    

    public static final String EVENT_OWNER_ID = new UUID(1, 10).toString();
    public static final String EVENT_OWNER_TWITTER_ID = "10";
    public static final String EVENT_OWNER_TWITTER_SCREENNAME = "eventOwner";

    public static final String EVENT_EDITOR_ID = new UUID(1, 20).toString();
    public static final String EVENT_EDITOR_TWITTER_ID = "20";
    public static final String EVENT_EDITOR_TWITTER_SCREENNAME = "eventEditor";

    public static final String EVENT_COMMENTOR_ID = new UUID(1, 30).toString();
    public static final String EVENT_COMMENTOR_TWITTER_ID = "30";
    public static final String EVENT_COMMENTOR_TWITTER_SCREENNAME = "eventEditor";

    public static final String EVENT_ENROLLED_USER_ID = new UUID(1, 40).toString();
    public static final String EVENT_ENROLLED_USER_TWITTER_ID = "40";
    public static final String EVENT_ENROLLED_USER_TWITTER_SCREENNAME = "eventEnrolledUser";

    public static final String EVENT_RESERVED_USER_ID = new UUID(1, 50).toString();
    public static final String EVENT_RESERVED_USER_TWITTER_ID = "50";
    public static final String EVENT_RESERVED_USER_TWITTER_SCREENNAME = "eventReservedUser";

    public static final String EVENT_CANCELLED_USER_ID = new UUID(1, 60).toString();
    public static final String EVENT_CANCELLED_USER_TWITTER_ID = "60";
    public static final String EVENT_CANCELLED_USER_TWITTER_SCREENNAME = "eventCancelledUser";

    public static final String EVENT_UNRELATED_USER_ID = new UUID(1, 70).toString();
    public static final String EVENT_UNRELATED_USER_TWITTER_ID = "70";
    public static final String EVENT_UNRELATED_USER_TWITTER_SCREENNAME = "eventUnrelatedUser";

    public static final String ATTENDANCE_PRESENT_USER_ID = new UUID(1, 80).toString();
    public static final String ATTENDANCE_PRESENT_USER_TWITTER_ID = "80";
    public static final String ATTENDANCE_PRESENT_USER_TWITTER_SCREENNAME = "attendancePresentUser";

    public static final String ATTENDANCE_ABSENT_USER_ID = new UUID(1, 81).toString();
    public static final String ATTENDANCE_ABSENT_USER_TWITTER_ID = "81";
    public static final String ATTENDANCE_ABSENT_USER_TWITTER_SCREENNAME = "attendanceAbsentUser";

    public static final String ATTENDANCE_UNKNOWN_USER_ID = new UUID(1, 82).toString();
    public static final String ATTENDANCE_UNKNOWN_USER_TWITTER_ID = "82";
    public static final String ATTENDANCE_UNKNOWN_USER_TWITTER_SCREENNAME = "attendanceUnknownUser";

    // Events
    public static final String INVALID_EVENT_ID = new UUID(2, -1).toString();
    public static final String DEFAULT_EVENT_ID = new UUID(2, 0).toString();
    public static final String PRIVATE_EVENT_ID = new UUID(2, 10).toString();
    public static final String JAPANESE_EVENT_ID = new UUID(2, 20).toString();
    public static final String UNIQUEIDENTIFIER_EVENT_ID = new UUID(2, 30).toString();

    // Event Comments
    public static final String INVALID_COMMENT_ID = new UUID(4, -1).toString();
    public static final String OWNER_COMMENT_ID = new UUID(4, 1).toString();
    public static final String EDITOR_COMMENT_ID = new UUID(4, 2).toString();
    public static final String COMMENTOR_COMMENT_ID = new UUID(4, 3).toString();
    public static final String UNRELATED_USER_COMMENT_ID = new UUID(4, 4).toString();

    // Images
    public static final String EVENT_FOREIMAGE_ID = new UUID(3, 1).toString();
    public static final String EVENT_BACKIMAGE_ID = new UUID(3, 2).toString();

    public static final String[] IMAGE_OWNED_BY_DEFAULT_USER_ID = new String[] {
        new UUID(3, 10).toString(), new UUID(3, 11).toString(), new UUID(3, 12).toString(), new UUID(3, 13).toString(), new UUID(3, 14).toString(),
        new UUID(3, 15).toString(), new UUID(3, 16).toString(), new UUID(3, 17).toString(), new UUID(3, 18).toString(), new UUID(3, 19).toString(),
    };
    // IMAGE_OWNED_BY_DEFAULT_USER_ID contains DEFAULT_IMAGE_ID.
    public static final String DEFAULT_IMAGE_ID = IMAGE_OWNED_BY_DEFAULT_USER_ID[0];

    // Calendar Id
    public static final String DEFAULT_CALENDAR_ID = new UUID(5, 1).toString();
    
    // TODO: Name should be more descriptive.
    public abstract T create();
    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException;
}
