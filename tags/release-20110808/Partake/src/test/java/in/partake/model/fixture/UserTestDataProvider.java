package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.User;

import java.util.Date;

public class UserTestDataProvider extends TestDataProvider {
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
