package in.partake.model.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class EventAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventAccess, Event, String> {
	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getEventAccess());
	}

	@Override
	protected Event create(long pkNumber, String pkSalt, int objNumber) {
        Date now = new Date(1L);
        Date beginDate = now;
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event("eventId" + pkSalt + pkNumber, "DUMMY EVENT" + objNumber, "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", "userId", null, true, "passcode", false, false, now, now);
        event.setId("eventId" + pkSalt + pkNumber);
        return event;	    
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
            String screenNames[] = new String[]{
                    null, // 0
                    "",   // 1
                    "A",  // 2
                    "A,B,C", // 3 
                    "  A  ", // 4 
                    
                    "  A  ,  B  ,  C  ", // 5
                    "  AA, B A, A", // 6
                    "   A,   B   A  , C   ", // 7
                    " B, B, B", // 8
                    " C " // 9
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
                }
            }
            
            {
                List<Event> targetEvents = dao.findByScreenName(con, "A");
                List<String> actual = new ArrayList<String>();
                for (Event event : targetEvents) {
                    if (event == null) { continue; }
                    actual.add(event.getId());
                }
                
                List<String> expected = Arrays.asList(new String[] {
                        originalEventIds[2], originalEventIds[3], originalEventIds[4], originalEventIds[5], originalEventIds[6], originalEventIds[7]
                });

                Collections.sort(actual);
                Collections.sort(expected);
                
                Assert.assertEquals(expected, actual);
                
            }
            
            
        } finally {
            con.invalidate();
        }
    }

    
    
    private Event createEvent(String eventId, String userId) {
        Date beginDate = PDate.getCurrentDate().getDate();
        Date now = PDate.getCurrentDate().getDate();
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, false, now, now);
        event.setId(eventId);
        return event;
    }
}
