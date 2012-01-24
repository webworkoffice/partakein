package in.partake.model.fixture;

import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;

public abstract class TestDataProvider<T> {
    public static final String INVALID_USER_ID = new UUID(1, -1).toString();
    public static final String USER_ID1 = new UUID(1, 1).toString();
    public static final String USER_ID2 = new UUID(1, 2).toString();
    public static final String USER_ID3 = new UUID(1, 3).toString();

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
    
    public abstract T create();
    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException;
}
