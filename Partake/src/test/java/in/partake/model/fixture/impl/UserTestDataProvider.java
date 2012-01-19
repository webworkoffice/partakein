package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dto.User;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

public class UserTestDataProvider extends TestDataProvider<User> {
    
    @Override
    public User create() {
        return create(0, "", 0);
    }
    
    @Override
    public User create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, ("user" + pkSalt).hashCode());
        return new User(id.toString(), 1, new Date(0), "calendarId" + objNumber);
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
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IUserAccess dao = factory.getUserAccess();
        dao.truncate(con);
        
        // testUser という id の user がいることを保証する。
        dao.put(con, new User(USER_ID1, 1, new Date(), null)); 
        dao.put(con, new User(USER_ID2, 2, new Date(), null)); 
        dao.put(con, new User(USER_ID3, 3, new Date(), null)); 

        // TODO 上記3ユーザと何が違うのか、UserID命名の意図をコメント
        // TODO ユーザ名をTestDataProviderに定数として定義
        dao.put(con, new User("openid-remove-0", 1000, new Date(), null));
        dao.put(con, new User("openid-remove-1", 1001, new Date(), null));
        dao.put(con, new User("openid-remove-2", 1002, new Date(), null));
        dao.put(con, new User("openid-remove-3", 1003, new Date(), null));
    }
}
