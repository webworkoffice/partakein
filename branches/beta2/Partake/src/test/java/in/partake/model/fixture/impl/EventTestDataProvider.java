package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

/**
 * Event のテストデータを作成します。
 * @author shinyak
 *
 */
public class EventTestDataProvider extends TestDataProvider<Event> {
    public static final String UNIQUE_IDENTIFIER = "dlasjfjdlkhvfiaxh";
    public static final String JAPANESE_IDENTIFIER = "音無小鳥";

    @Override
    public Event create() {
        return create(0, "", 0);
    }

    @Override
    public Event create(long pkNumber, String pkSalt, int objNumber) {
        Date now = new Date(1L);
        Date beginDate = now;
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event("eventId" + pkSalt + pkNumber, "DUMMY EVENT" + objNumber, "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", "userId", null, true, "passcode", false, false, now, now);

        UUID uuid = new UUID(pkNumber, ("event" + pkSalt).hashCode());
        event.setId(uuid.toString());
        return event;
    }

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
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventAccess dao = daos.getEventAccess();
        dao.truncate(con);

        Date now = new Date();
        Date late = new Date(now.getTime() + 1000 * 3600);

        dao.put(con, new Event(DEFAULT_EVENT_ID, "short-id", "title", "summary", "category",
                late, late, late, 0, "url", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, null, false, false,
                now, now, -1));

        dao.put(con, new Event(PRIVATE_EVENT_ID, "short-id", "title", "summary", "category",
                late, late, late, 0, "url", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, true, "passcode", false, false,
                now, now, -1));

        dao.put(con, new Event(JAPANESE_EVENT_ID, "short-id", "title", "summary", "category",
                late, late, late, 0, "url", "place",
                "address", "unique identifier -- " + JAPANESE_IDENTIFIER, "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, "passcode", false, false,
                now, now, -1));

        dao.put(con, new Event(UNIQUEIDENTIFIER_EVENT_ID, "short-id", "title", "summary", "category",
                late, late, late, 0, "url", "place",
                "address", "unique identifier -- " + UNIQUE_IDENTIFIER, "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, "passcode", false, false,
                now, now, -1));
    }
}
