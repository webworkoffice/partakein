package in.partake.model.dao;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;

import org.junit.Before;
import org.junit.Test;

public abstract class EventAccessTestCaseBase extends AbstractDaoTestCaseBase {
	private IEventAccess dao;

	@Before
	public void setup() throws DAOException {
	    super.setup();
	    
		dao = getFactory().getEventAccess();
	}

	@Test
	public void loadAllEventKeys() throws DAOException {
		PartakeConnection con = getPool().getConnection();

		try {
			for (KeyIterator iter = dao.getAllEventKeys(con); iter.hasNext();) {
				iter.next();
			}
		} finally {
			con.invalidate();
		}
	}
}
