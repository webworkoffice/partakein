package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dto.User;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserTestDataProvider extends TestDataProvider<User> {

    @Override
    public User create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, ("user" + pkSalt).hashCode());
        return new User(id.toString(), "screenName" + objNumber, "http://www.example.com/");
    }

    @Override
    public List<User> createSamples() {
        List<User> array = new ArrayList<User>();
        array.add(new User("id", "screenName", "http://www.example.com/"));
        array.add(new User("id1", "screenName", "http://www.example.com/"));
        array.add(new User("id", "screenName1", "http://www.example.com/"));
        array.add(new User("id", "screenName", "http://www.example.com/hoge"));
        return array;
    }

    /**
     * <p>以下のtest用データがDatastoreにあることを保証します。
     * <ul>
     * <li>{@link TestDataProvider#USER_ID1}〜{@link TestDataProvider#USER_ID3}のUserIDを持つユーザ3つ
     * <li>{@link TestDataProvider#USER_ID1}〜{@link TestDataProvider#USER_ID3}のUserIDを持つユーザ3つ
     * </ul>
     * @param con Datastoreへの接続
     * @param factory DAOファクトリクラスのインスタンス
     * @throws DAOException
     */
    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserAccess dao = daos.getUserAccess();
        dao.truncate(con);

        for (int i = 0; i < DEFAULT_USER_IDS.length; ++i)
            dao.put(con, new User(DEFAULT_USER_IDS[i], DEFAULT_USER_TWITTER_SCREENNAME[i], "http://www.example.com/"));

        dao.put(con, new User(DEFAULT_USER_ID, DEFAULT_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(DEFAULT_ANOTHER_USER_ID, DEFAULT_ANOTHER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(ADMIN_USER_ID, ADMIN_USER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(USER_WITHOUT_PREF_ID, USER_WITHOUT_PREF_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(USER_WITH_PRIVATE_PREF_ID, USER_WITH_PRIVATE_PREF_SCREENNAME, "http://www.example.com/"));

        dao.put(con, new User(EVENT_OWNER_ID, EVENT_OWNER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_EDITOR_ID, EVENT_EDITOR_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_COMMENTOR_ID, EVENT_COMMENTOR_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_ENROLLED_USER_ID, EVENT_ENROLLED_USER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_VIP_ENROLLED_USER_ID, EVENT_VIP_ENROLLED_USER_TWITTER_SCREENNAME, "http://www.example.com/"));

        dao.put(con, new User(EVENT_RESERVED_USER_ID, EVENT_RESERVED_USER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_CANCELLED_USER_ID, EVENT_CANCELLED_USER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(EVENT_UNRELATED_USER_ID, EVENT_UNRELATED_USER_TWITTER_SCREENNAME, "http://www.example.com/"));

        dao.put(con, new User(ATTENDANCE_PRESENT_USER_ID, ATTENDANCE_PRESENT_USER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(ATTENDANCE_ABSENT_USER_ID, ATTENDANCE_ABSENT_USER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(ATTENDANCE_UNKNOWN_USER_ID, ATTENDANCE_UNKNOWN_USER_TWITTER_SCREENNAME, "http://www.example.com/"));

        dao.put(con, new User(DEFAULT_SENDER_ID, DEFAULT_SENDER_TWITTER_SCREENNAME, "http://www.example.com/"));
        dao.put(con, new User(DEFAULT_RECEIVER_ID, DEFAULT_RECEIVER_TWITTER_SCREENNAME, "http://www.example.com/"));

    }
}
