package in.partake.model.fixture;

import java.util.Date;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Event;

/**
 * Event のテストデータを作成します。
 * @author shinyak
 *
 */
public class EventTestDataProvider extends TestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IEventAccess dao = factory.getEventAccess();
        dao.truncate(con);
        
        dao.put(con, new Event(EVENT_ID1, "shortId1", "title", "summary", "category", 
                               new Date(), new Date(), new Date(), 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
                               new Date(), new Date(), -1));

        dao.put(con, new Event(EVENT_ID2, "shortId2", "title", "summary", "category", 
                               new Date(), new Date(), new Date(), 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
                               new Date(), new Date(), -1));

        dao.put(con, new Event(EVENT_ID3, "shortId3", "title", "summary", "category", 
                               new Date(), new Date(), new Date(), 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
                               new Date(), new Date(), -1));

    }
}
