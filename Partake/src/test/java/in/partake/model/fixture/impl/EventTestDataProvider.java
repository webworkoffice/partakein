package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
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
    public Event create(long pkNumber, String pkSalt, int objNumber) {
        DateTime now = new DateTime(objNumber);
        DateTime beginDate = now;

        UUID uuid = new UUID(pkNumber, ("event" + pkSalt).hashCode());
        UUID ownerId = new UUID(objNumber, objNumber);
        Event event = new Event(uuid.toString(), "title", "summary", "category", beginDate, null,
                "url", "place", "address", "description", "hashTag", ownerId.toString(), null, null, null,
                true, "passcode", false, false, new ArrayList<EventRelation>(), now, now, 0);
        return event;
    }

    @Override
    public List<Event> createSamples() {
        List<Event> array = new ArrayList<Event>();
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id1", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "titl1e", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summ1ary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "ca1tegory", new DateTime(1), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(1), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "ur1l", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "pl1ace", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "addre1ss", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "descr1iption", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "ha1shTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "own1erId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "manager1ScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "fo1reImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "bac1kImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", false, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode1", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", true, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, true, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, null, new DateTime(0), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(1), new DateTime(0), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(1), 0));
        array.add(new Event("id", "title", "summary", "category", new DateTime(0), new DateTime(0), "url", "place", "address", "description", "hashTag", "ownerId", "managerScreenNames", "foreImageId", "backImageId", true, "passcode", false, false, new ArrayList<EventRelation>(), new DateTime(0), new DateTime(0), 1));
        return array;
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

        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime late = new DateTime(now.getTime() + 1000 * 3600);
        String category = EventCategory.getCategories().get(0).getKey();

        dao.put(con, new Event(DEFAULT_EVENT_ID, "title", "summary", category,
                late, late, "http://www.example.com/", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, null, false, false,
                new ArrayList<EventRelation>(),
                now, now, -1));

        dao.put(con, new Event(PRIVATE_EVENT_ID, "title", "summary", category,
                late, late, "http://www.example.com/", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, true, "passcode", false, false,
                new ArrayList<EventRelation>(),
                now, now, -1));

        dao.put(con, new Event(JAPANESE_EVENT_ID, "title", "summary", category,
                late, late, "http://www.example.com/", "place",
                "address", "unique identifier -- " + JAPANESE_IDENTIFIER, "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, "passcode", false, false,
                new ArrayList<EventRelation>(),
                now, now, -1));

        dao.put(con, new Event(UNIQUEIDENTIFIER_EVENT_ID, "title", "summary", category,
                late, late, "http://www.example.com/", "place",
                "address", "unique identifier -- " + UNIQUE_IDENTIFIER, "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, "passcode", false, false,
                new ArrayList<EventRelation>(),
                now, now, -1));

        dao.put(con, new Event(UNPUBLISHED_EVENT_ID, "title", "summary", category,
                late, late, "http://www.example.com/", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, null, true, false,
                new ArrayList<EventRelation>(),
                now, now, -1));
    }
}
