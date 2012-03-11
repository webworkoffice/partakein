package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.fixture.TestDataProvider;

/**
 * 
 * @author shinyak
 *
 */
public class CalendarLinkageTestDataProvider extends TestDataProvider<CalendarLinkage> {
    public static final byte[] BYTE1_CONTENT = new byte[] { 1, 2, 3 };  
    
    @Override
    public CalendarLinkage create() {
        throw new RuntimeException("Not implemented yet.");
    }
    
    @Override
    public CalendarLinkage create(long pkNumber, String pkSalt, int objNumber) {
        throw new RuntimeException("Not implemented yet.");
    }
    
    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ICalendarLinkageAccess dao = factory.getCalendarAccess();
        dao.truncate(con);
        
        dao.put(con, new CalendarLinkage(DEFAULT_CALENDAR_ID, DEFAULT_USER_ID));
    }
}
