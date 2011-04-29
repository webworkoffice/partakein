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
        
        Date now = new Date();
        Date late = new Date(now.getTime() + 1000 * 3600);
        
        dao.put(con, new Event(EVENT_ID1, "shortId1", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_ID2, "shortId2", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_ID3, "shortId3", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));


        dao.put(con, new Event("event-search-1", "event-search-1", "title title title", "summary", "computer", 
                late, late, late, 0, "url", "place",
                "address", "unique identifier -- djlkajsd", "#hashTag", USER_ID1, null,
                "foreImageId", "backImageId", false, null, false, false,
                now, now, -1));

        dao.put(con, new Event("event-search-2", "event-search-2", "title title title", "summary", "computer", 
                late, late, late, 0, "url", "place",
                "address", "unique identifier -- xgnasdgb", "#hashTag", USER_ID1, null,
                "foreImageId", "backImageId", false, null, false, false,
                now, now, -1));

        dao.put(con, new Event("event-search-3", "event-search-3", "title title title", "summary", "computer", 
                late, late, late, 0, "url", "place",
                "address", "unique identifier -- 昇竜拳", "#hashTag", USER_ID1, null,
                "foreImageId", "backImageId", false, null, false, false,
                now, now, -1));
                
    }
}
