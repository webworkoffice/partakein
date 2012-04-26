package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserTicketTestDataProvider extends TestDataProvider<UserTicket> {

    @Override
    public UserTicket create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserTicket(uuid.toString(), "userId" + objNumber, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime());
    }

    @Override
    public List<UserTicket> createSamples() {
        List<UserTicket> array = new ArrayList<UserTicket>();
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id1", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId1", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 1), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment1", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment1", "eventId", ParticipationStatus.RESERVED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.ABSENT, new DateTime(0L)));
        array.add(new UserTicket("id", "userId", new UUID(0, 0), "comment", "eventId", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.PRESENT, new DateTime(1L)));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserTicketAccess dao = daos.getEnrollmentAccess();

        dao.truncate(con);
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_VIP_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, true,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.RESERVED, false,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_CANCELLED_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.CANCELLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));

        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_PRESENT_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_ABSENT_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.ABSENT, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                ATTENDANCE_UNKNOWN_USER_ID, DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, TimeUtil.getCurrentDateTime()));

        // Enrollment for private event
        dao.put(con, new UserTicket(UUID.randomUUID().toString(),
                EVENT_ENROLLED_USER_ID, PRIVATE_EVENT_TICKET_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false,
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, TimeUtil.getCurrentDateTime()));
    }
}
