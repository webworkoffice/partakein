package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class EnrollmentAccessTest extends AbstractDaoTestCaseBase<IEnrollmentAccess, Enrollment, String> {
    @Rule
    public TestName name = new TestName();

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEnrollmentAccess());
    }

    @Override
    protected Enrollment create(long pkNumber, String pkSalt, int objNumber) {
        return new Enrollment(
                "id" + pkSalt + pkNumber,
                "userId" + pkSalt + pkNumber,
                "eventId" + pkSalt + pkNumber,
                "comment" + objNumber,
                ParticipationStatus.ENROLLED,
                false,
                ModificationStatus.CHANGED,
                AttendanceStatus.UNKNOWN,
                new Date(1L));
    }

    @Test
    public void testGetEmptyPaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String eventId = "eventId" + System.currentTimeMillis();
                List<Enrollment> list = dao.findByEventId(con, eventId);
                Assert.assertTrue(list.isEmpty());

                return null;
            }
        }.execute();
    }

    @Test
    public void testPutAndGetPaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                ParticipationStatus status = ParticipationStatus.ENROLLED;

                String id = UUID.randomUUID().toString();
                String eventId = UUID.randomUUID().toString();
                String userId = UUID.randomUUID().toString();

                Event event = createEvent(eventId, userId);
                con.beginTransaction();
                event.setId(eventId);
                daos.getEventAccess().put(con, event);
                daos.getUserAccess().put(con, new User(userId, 0, new Date(), null));

                dao.put(con, new Enrollment(id, userId, eventId, "", ParticipationStatus.ENROLLED, false, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));

                List<Enrollment> list = dao.findByEventId(con, eventId);
                con.commit();

                Assert.assertEquals(1, list.size());
                Enrollment storedParticipation = list.get(0);
                Assert.assertNotNull(storedParticipation);
                Assert.assertEquals(userId, storedParticipation.getUserId());
                Assert.assertEquals(ModificationStatus.CHANGED, storedParticipation.getModificationStatus());
                Assert.assertEquals(status, storedParticipation.getStatus());
                // TODO Auto-generated method stub
                return null;
            }
        }.execute();
    }

    @Test
    public void testPutAndUpdatePaticipationList() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                String id = UUID.randomUUID().toString();
                String eventId = UUID.randomUUID().toString();
                String userId  = UUID.randomUUID().toString();
                Event event = createEvent(eventId, userId);

                // create
                {
                    con.beginTransaction();
                    event.setId(eventId);
                    daos.getEventAccess().put(con, event);
                    daos.getUserAccess().put(con, new User(userId, 0, new Date(), null));
                    dao.put(con, new Enrollment(id, userId, eventId, "", ParticipationStatus.ENROLLED, false, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));
                    con.commit();
                }

                // update
                {
                    con.beginTransaction();
                    List<Enrollment> storedList = dao.findByEventId(con, eventId);
                    Enrollment storedParticipation = storedList.get(0);
                    Assert.assertNotNull(storedParticipation);
                    ModificationStatus newStatus = ModificationStatus.NOT_ENROLLED;
                    Assert.assertFalse(newStatus.equals(storedParticipation.getModificationStatus()));
                    Enrollment newStoredParticipation = new Enrollment(storedParticipation);
                    newStoredParticipation.setModificationStatus(ModificationStatus.CHANGED);
                    dao.put(con, newStoredParticipation);
                    con.commit();
                }

                // get
                {
                    con.beginTransaction();
                    List<Enrollment> updatedList = dao.findByEventId(con, eventId);
                    Assert.assertEquals(1, updatedList.size());
                    Enrollment updatedParticipation = updatedList.get(0);
                    Assert.assertEquals(userId, updatedParticipation.getUserId());
                    Assert.assertEquals(ModificationStatus.CHANGED, updatedParticipation.getModificationStatus());
                    Assert.assertEquals(ParticipationStatus.ENROLLED, updatedParticipation.getStatus());
                    con.commit();
                }

                return null;
            }
        }.execute();
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
