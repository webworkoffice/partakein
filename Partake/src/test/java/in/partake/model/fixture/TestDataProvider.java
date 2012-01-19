package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;

public abstract class TestDataProvider<T> {
    public static final String INVALID_USER_ID = "invalidUserId";
    public static final String USER_ID1 = "testUser1";
    public static final String USER_ID2 = "testUser2";
    public static final String USER_ID3 = "testUser3";

    public static final String INVALID_EVENT_ID = "invalidEventId";
    public static final String EVENT_ID1 = "event1";
    public static final String EVENT_ID2 = "event2";
    public static final String EVENT_ID3 = "event3";

    public static final String EVENT_PRIVATE_ID1 = "event-private-id1";
    public static final String EVENT_PRIVATE_ID2 = "event-private-id2";
    public static final String EVENT_PRIVATE_ID3 = "event-private-id3";
    
    public abstract T create();
    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException;
}
