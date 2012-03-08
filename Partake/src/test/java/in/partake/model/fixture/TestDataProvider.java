package in.partake.model.fixture;

import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;

public abstract class TestDataProvider<T> {
    // TODO: Name should be more descriptive.
    public static final String INVALID_USER_ID = new UUID(1, -1).toString();
    
    public static final String USER_ID1 = new UUID(1, 1).toString();
    public static final String TWITTER_ID1 = "1";
    public static final String TWITTER_SCREENNAME1 = "testUser1";

    public static final String USER_ID2 = new UUID(1, 2).toString();
    public static final String TWITTER_ID2 = "2";
    public static final String TWITTER_SCREENNAME2 = "testUser2";
    
    public static final String USER_ID3 = new UUID(1, 3).toString();
    public static final String TWITTER_ID3 = "3";
    public static final String TWITTER_SCREENNAME3 = "testUser3";

    public static final String USER_ID4 = new UUID(1, 4).toString();
    public static final String TWITTER_ID4 = "4";
    public static final String TWITTER_SCREENNAME4 = "testUser4";

    public static final String USER_ADMIN_ID = new UUID(1, 5).toString();
    public static final String TWITTER_ADMIN_ID = "5";
    public static final String TWITTER_ADMIN_SCREENNAME = "partakein";    

    public static final String INVALID_EVENT_ID = new UUID(2, -1).toString();
    public static final String EVENT_ID1 = new UUID(2, 1).toString();
    public static final String EVENT_ID2 = new UUID(2, 2).toString();
    public static final String EVENT_ID3 = new UUID(2, 3).toString();

    public static final String EVENT_PRIVATE_ID1 = new UUID(2, 11).toString();
    public static final String EVENT_PRIVATE_ID2 = new UUID(2, 12).toString();
    public static final String EVENT_PRIVATE_ID3 = new UUID(2, 13).toString();

    public static final String EVENT_SEARCH_ID1 = new UUID(2, 21).toString();
    public static final String EVENT_SEARCH_ID2 = new UUID(2, 22).toString();
    public static final String EVENT_SEARCH_ID3 = new UUID(2, 23).toString();
    
    public static final String EVENT_REMOVE_ID0 = new UUID(2, 30).toString();
    public static final String EVENT_REMOVE_ID1 = new UUID(2, 31).toString();
    public static final String EVENT_REMOVE_ID2 = new UUID(2, 32).toString();
    public static final String EVENT_REMOVE_ID3 = new UUID(2, 33).toString();
    
    public static final String IMAGE_ID1 = new UUID(3, 1).toString();

    public static final String COMMENT_INVALID_ID = new UUID(4, 0).toString();
    public static final String COMMENT_ID1 = new UUID(4, 1).toString();
    public static final String COMMENT_ID2 = new UUID(4, 2).toString();
    
    public abstract T create();
    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException;
}
