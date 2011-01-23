package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.resource.PartakeProperties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EventCassandraDaoTest extends CassandraDaoTestBase {
	private CassandraConnectionPool pool;
	private EventCassandraDao dao;
	
	@Before
	public void setup() throws DAOException {
		if (!PartakeProperties.get().getDAOFactoryClassName().endsWith("CassandraDAOFactory")) {
			Assert.fail("This test doesn't run because properties file isn't ready.");
		}

		pool = new CassandraConnectionPool();
		dao = (EventCassandraDao) new CassandraDAOFactory().getEventAccess();
	}

	@Test
	public void loadAllEventKeys() throws DAOException {
        PartakeConnection con = pool.getConnection();

        try {
			for (KeyIterator iter = dao.getAllEventKeys(con); iter.hasNext();) {
				iter.next();
			}
        } finally {
        	con.invalidate();
        }
	}
}