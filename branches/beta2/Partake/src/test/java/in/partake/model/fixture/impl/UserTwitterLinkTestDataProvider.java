package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;

public class UserTwitterLinkTestDataProvider extends TestDataProvider<UserTwitterLink> {
    @Override
    public UserTwitterLink create(long pkNumber, String pkSalt, int objNumber) {
        return new UserTwitterLink("twitterId" + pkNumber + pkSalt, "screenName", "name", "accessToken",
                "accessTokenSecret", "profileImageURL", "userId" + objNumber);
    }

    @Override
    public List<UserTwitterLink> createSamples() {
        List<UserTwitterLink> array = new ArrayList<UserTwitterLink>();
        array.add(new UserTwitterLink("twitterId", "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId1", "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName1", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName", "name1", "accessToken", "accessTokenSecret", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName", "name", "accessToken1", "accessTokenSecret", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName", "name", "accessToken", "accessTokenSecret1", "profileImageURL", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL1", "userId"));
        array.add(new UserTwitterLink("twitterId", "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId1"));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserTwitterLinkAccess dao = daos.getTwitterLinkageAccess();
        dao.truncate(con);


        for (int i = 0; i < DEFAULT_USER_IDS.length; ++i)
            dao.put(con, new UserTwitterLink(DEFAULT_USER_TWITTER_IDS[i], DEFAULT_USER_TWITTER_SCREENNAME[i], "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", DEFAULT_USER_IDS[i]));

        dao.put(con, new UserTwitterLink(DEFAULT_TWITTER_ID, DEFAULT_TWITTER_SCREENNAME, "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", DEFAULT_USER_ID));
        dao.put(con, new UserTwitterLink(DEFAULT_ANOTHER_TWITTER_ID, DEFAULT_ANOTHER_TWITTER_SCREENNAME, "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", DEFAULT_ANOTHER_USER_ID));
        dao.put(con, new UserTwitterLink(ADMIN_USER_TWITTER_ID, ADMIN_USER_SCREENNAME, "testUser 2", "accessToken", "accessTokenSecret", "http://www.example.com/", ADMIN_USER_ID));
        dao.put(con, new UserTwitterLink(USER_WITHOUT_PREF_TWITTER_ID, USER_WITHOUT_PREF_SCREENNAME, "testUser 3", "accessToken", "accessTokenSecret", "http://www.example.com/", USER_WITHOUT_PREF_ID));
        dao.put(con, new UserTwitterLink(USER_WITH_PRIVATE_PREF_TWITTER_ID, USER_WITHOUT_PREF_SCREENNAME, "testUser 3", "accessToken", "accessTokenSecret", "http://www.example.com/", USER_WITH_PRIVATE_PREF_ID));

        dao.put(con, new UserTwitterLink(EVENT_OWNER_TWITTER_ID, EVENT_OWNER_TWITTER_SCREENNAME, "testUser 3", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_OWNER_ID));
        dao.put(con, new UserTwitterLink(EVENT_EDITOR_TWITTER_ID, EVENT_EDITOR_TWITTER_SCREENNAME, "testUser 4", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_EDITOR_ID));
        dao.put(con, new UserTwitterLink(EVENT_COMMENTOR_TWITTER_ID, EVENT_COMMENTOR_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_COMMENTOR_ID));
        dao.put(con, new UserTwitterLink(EVENT_ENROLLED_USER_TWITTER_ID, EVENT_ENROLLED_USER_TWITTER_SCREENNAME, "testUser 4", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_ENROLLED_USER_ID));
        dao.put(con, new UserTwitterLink(EVENT_VIP_ENROLLED_USER_TWITTER_ID, EVENT_VIP_ENROLLED_USER_TWITTER_SCREENNAME, "testUser 4", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_VIP_ENROLLED_USER_ID));

        dao.put(con, new UserTwitterLink(EVENT_RESERVED_USER_TWITTER_ID, EVENT_RESERVED_USER_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_RESERVED_USER_ID));
        dao.put(con, new UserTwitterLink(EVENT_CANCELLED_USER_TWITTER_ID, EVENT_CANCELLED_USER_TWITTER_SCREENNAME, "testUser 4", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_CANCELLED_USER_ID));
        dao.put(con, new UserTwitterLink(EVENT_UNRELATED_USER_TWITTER_ID, EVENT_UNRELATED_USER_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", EVENT_UNRELATED_USER_ID));
        dao.put(con, new UserTwitterLink(ATTENDANCE_PRESENT_USER_TWITTER_ID, ATTENDANCE_PRESENT_USER_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", ATTENDANCE_PRESENT_USER_ID));
        dao.put(con, new UserTwitterLink(ATTENDANCE_ABSENT_USER_TWITTER_ID, ATTENDANCE_ABSENT_USER_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", ATTENDANCE_ABSENT_USER_ID));
        dao.put(con, new UserTwitterLink(ATTENDANCE_UNKNOWN_USER_TWITTER_ID, ATTENDANCE_UNKNOWN_USER_TWITTER_SCREENNAME, "partakein", "accessToken", "accessTokenSecret", "http://www.example.com/", ATTENDANCE_UNKNOWN_USER_ID));

        dao.put(con, new UserTwitterLink(DEFAULT_SENDER_TWITTER_ID, DEFAULT_SENDER_TWITTER_SCREENNAME, "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", DEFAULT_SENDER_ID));
        dao.put(con, new UserTwitterLink(DEFAULT_RECEIVER_TWITTER_ID, DEFAULT_RECEIVER_TWITTER_SCREENNAME, "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", DEFAULT_RECEIVER_ID));

    }

}
