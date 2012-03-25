package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditParticipantsPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.UserErrorCode;

public class AttendAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String userId = getValidUserIdParameter();
        String eventId = getValidEventIdParameter();

        String status = getParameter("status");
        if (status == null || !AttendanceStatus.isValueOf(status))
            return renderInvalid(UserErrorCode.MISSING_ATTENDANCE_STATUS);

        new AttendTransaction(user, userId, eventId, AttendanceStatus.safeValueOf(status)).execute();
        return renderOK();
    }
}

class AttendTransaction extends Transaction<Void> {
    private UserEx user;
    private String userId;
    private String eventId;
    private AttendanceStatus status;

    public AttendTransaction(UserEx user, String userId, String eventId, AttendanceStatus status) {
        this.user = user;
        this.userId = userId;
        this.eventId = eventId;
        this.status = status;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        updateAttendanceStatus(con, daos);
        return null;
    }

    private void updateAttendanceStatus(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEnrollmentAccess enrollmentAccess = daos.getEnrollmentAccess();

        // We have already checked the event exists, so when no enrollment is found, we throw an "invalid user id"
        // exception here.
        Enrollment enrollment = enrollmentAccess.find(con, new EnrollmentPK(userId, eventId));
        if (enrollment == null)
            throw new PartakeException(UserErrorCode.INVALID_USER_ID);

        Enrollment newEnrollment = new Enrollment(enrollment);
        newEnrollment.setAttendanceStatus(status);
        enrollmentAccess.put(con, newEnrollment);
    }
}
