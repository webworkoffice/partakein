package in.partake.model.dao.cassandra;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraKeyColumnDataIteratorTest extends AbstractDaoTestCaseBase {

	@BeforeClass
	public static void setUpOnce() {
		PartakeProperties.get().reset("cassandra");
		reset();
	}

	@AfterClass
	public static void tearDownOnce() {
		PartakeProperties.get().reset();
		reset();
	}

	@Before
	public void setup() throws DAOException {
		// truncate all Users for testing.
		// TODO 現在の実装ではsetup内でtruncateを実行していないので、このテストは失敗する。CassandraDao#removeAllDataのコメントアウトを外してから実行すること。
		super.setup(getFactory().getUserAccess()); 
	}

	@Test
	public void iterateEmpty() throws DAOException {
		CassandraTableDescription desc = new CassandraTableDescription(
				UserCassandraDao.USERS_PREFIX,
				UserCassandraDao.USERS_KEYSPACE,
				UserCassandraDao.USERS_COLUMNFAMILY,
				UserCassandraDao.USERS_CL_R,
				UserCassandraDao.USERS_CL_W);
		CassandraConnection con = (CassandraConnection) getPool().getConnection();

		try {
			ColumnOrSuperColumnKeyMapper<Object> mapper = new FakeMapper(con, getFactory());
			con.beginTransaction();
			CassandraKeyColumnDataIterator<Object> iter;
			for (iter = new CassandraKeyColumnDataIterator<Object>(con, desc, mapper); iter.hasNext();) {
				Assert.fail();
			}
			con.commit();
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void iterateOne() throws DAOException {
		CassandraTableDescription desc = new CassandraTableDescription(
				UserCassandraDao.USERS_PREFIX,
				UserCassandraDao.USERS_KEYSPACE,
				UserCassandraDao.USERS_COLUMNFAMILY,
				UserCassandraDao.USERS_CL_R,
				UserCassandraDao.USERS_CL_W);
		IUserAccess userDao = getFactory().getUserAccess();
		CassandraConnection con = (CassandraConnection) getPool().getConnection();

		try {
			ColumnOrSuperColumnKeyMapper<Object> mapper = new FakeMapper(con, getFactory());
			con.beginTransaction();
			CassandraKeyColumnDataIterator<Object> iter;
			for (iter = new CassandraKeyColumnDataIterator<Object>(con, desc, mapper); iter.hasNext();) {
				Assert.fail();
			}

			String userId = userDao.getFreshId(con);
			User user = new User(userId, 0, new Date(), "calendarId");
			userDao.put(con, user);

			int found = 0;
			for (iter = new CassandraKeyColumnDataIterator<Object>(con, desc, mapper); iter.hasNext();) {
				iter.next();
				++found;
			}
			con.commit();
			Assert.assertEquals(counfColumnsOf(User.class), found);
		} finally {
			con.invalidate();
		}
	}

	private int counfColumnsOf(Class<?> clazz) {
		int count = 0;
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(javax.persistence.Column.class)) {
				++count;
			}
		}
		return count;
	}

	private static class FakeMapper extends ColumnOrSuperColumnKeyMapper<Object> {
		public FakeMapper(CassandraConnection connection, PartakeDAOFactory factory) {
			super(connection, factory);
		}

		@Override
		public Object map(ColumnOrSuperColumn cosc, String key) throws DAOException {
			return null;
		}

		@Override
		public ColumnOrSuperColumn unmap(Object t, long time) throws DAOException {
			return null;
		}
	}
}
