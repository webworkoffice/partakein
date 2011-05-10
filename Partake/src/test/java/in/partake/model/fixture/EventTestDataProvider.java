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
	/**
	 * <p>以下のtest用データがDatastoreにあることを保証します。
	 * <ul>
	 * <li>{@link TestDataProvider#EVENT_ID1}〜{@link TestDataProvider#EVENT_ID3}のEventIDを持つ公開イベント3つ
	 * <li>{@link TestDataProvider#EVENT_PRIVATE_ID1}〜{@link TestDataProvider#EVENT_PRIVATE_ID3}のEventIDを持つ非公開イベント3つ
	 * </ul>
	 * @param con Datastoreへの接続
	 * @param factory DAOファクトリクラスのインスタンス
	 * @throws DAOException
	 */
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IEventAccess dao = factory.getEventAccess();
        dao.truncate(con);
        
        Date now = new Date();
        Date late = new Date(now.getTime() + 1000 * 3600);
        
        dao.put(con, new Event(EVENT_ID1, "short-id1", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_ID2, "short-id2", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_ID3, "short-id3", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", false, null, false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_PRIVATE_ID1, "short-private-id1", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_PRIVATE_ID2, "short-private-id2", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
                               now, now, -1));

        dao.put(con, new Event(EVENT_PRIVATE_ID3, "short-private-id3", "title", "summary", "category", 
                               late, late, late, 0, "url", "place",
                               "address", "description", "#hashTag", USER_ID1, null,
                               "foreImageId", "backImageId", true, "passcode", false, false,
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
