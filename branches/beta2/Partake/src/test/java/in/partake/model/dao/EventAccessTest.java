package in.partake.model.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.partake.base.TimeUtil;
import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.service.TestDatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventAccessTest extends AbstractDaoTestCaseBase<IEventAccess, Event, String> {
    private EventTestDataProvider provider;
    
	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getEventAccess());
        provider = TestDatabaseService.getTestDataProviderSet().getEventProvider();
	}

	@Override
	protected Event create(long pkNumber, String pkSalt, int objNumber) {
	    return provider.create(pkNumber, pkSalt, objNumber);
	}
	
	@Test
	public void testToGetFreshId() throws DAOException {
	    PartakeConnection con = getPool().getConnection();

        try {
            Set<String> visited = new HashSet<String>(); 
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                String id = dao.getFreshId(con);
                Assert.assertFalse(visited.contains(id));
                visited.add(id);
            }
            con.commit();
        } finally {
            con.invalidate();
        }
	}
    
    @Test
    public void testToFindByOwnerId() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId-getbyowner-" + System.currentTimeMillis();
            Set<String> eventIds = new HashSet<String>();
            
            for (int i = 0; i < 10; ++i) {                
                String eventId;
                
                Event original = createEvent(null, userId);
                {
                    con.beginTransaction();
                    eventId = dao.getFreshId(con);
                    original.setId(eventId);
                    
                    dao.put(con, original);
                    con.commit();
                    
                    eventIds.add(eventId);
                }
            }
            
            List<Event> targetEvents = dao.findByOwnerId(con, userId);
            Set<String> targetEventIds = new HashSet<String>();
            for (Event e : targetEvents) {
                targetEventIds.add(e.getId());
            }
            
            Assert.assertEquals(eventIds, targetEventIds);
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToFindByInvalidOwner() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId-getbyowner-" + System.currentTimeMillis();
            String invalidUserId = "userId-invalid-" + System.currentTimeMillis();
            
            Set<String> eventIds = new HashSet<String>();
            
            for (int i = 0; i < 10; ++i) {                
                String eventId;
                
                Event original = createEvent(null, userId);
                {
                    con.beginTransaction();
                    eventId = dao.getFreshId(con);
                    original.setId(eventId);
                    
                    dao.put(con, original);
                    con.commit();
                    
                    eventIds.add(eventId);
                }
            }
            
            List<Event> targetEvents = dao.findByOwnerId(con, invalidUserId);
            
            Assert.assertNotNull(targetEvents);
            Assert.assertTrue(targetEvents.isEmpty());
        } finally {
            con.invalidate();
        }
    }
    
    
    @Test
    public void testToFindByScreenName() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId-screenname-" + System.currentTimeMillis();
            Set<String> expectedEventIds = new HashSet<String>();
            
            String screenNames[] = new String[]{
                    null,
                    "",
                    "A",
                    "A,B,C",
                    "  A  ",
                    
                    "  A  ,  B  ,  C  ",
                    "  AA, B A, A",
                    "   A,   B   A  , C   ",
                    " B, B, B",
                    " C "
            };
            String[] originalEventIds = new String[10];
            
            // event 作成
            for (int i = 0; i < 10; ++i) {                
                Event original = createEvent(null, userId);
                original.setManagerScreenNames(screenNames[i]);
                {
                    con.beginTransaction();
                    String eventId = dao.getFreshId(con);
                    originalEventIds[i] = eventId;
                    
                    original.setId(eventId);
                    
                    dao.put(con, original);
                    con.commit();
                    
                    if (original.isManager("A")) {
                        expectedEventIds.add(eventId);
                    }
                }
            }
            
            {
                List<Event> targetEvents = dao.findByScreenName(con, "A");
                Set<String> targetEventIds = new HashSet<String>();
                for (Event e : targetEvents) {
                    targetEventIds.add(e.getId());
                }
                
                Assert.assertEquals(expectedEventIds, targetEventIds);
                
            }
            
            
        } finally {
            con.invalidate();
        }
    }

    
    
    private Event createEvent(String eventId, String userId) {
        Date beginDate = TimeUtil.getCurrentDate();
        Date now = TimeUtil.getCurrentDate();
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, false, now, now);
        event.setId(eventId);
        return event;
    }
}
