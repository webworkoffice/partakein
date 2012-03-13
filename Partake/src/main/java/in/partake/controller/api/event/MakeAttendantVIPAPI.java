package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditParticipantsPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

public class MakeAttendantVIPAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();
        String userId = getValidUserIdParameter();
        Boolean vip = getBooleanParameter("vip");
        if (vip == null)
            return renderInvalid(UserErrorCode.INVALID_MISSING_VIP);

        MakeAttendantVIPTransaction transaction = new MakeAttendantVIPTransaction(user, userId, eventId, vip);
        transaction.execute();
        
        return renderOK();
    }
}

class MakeAttendantVIPTransaction extends Transaction<Void> {
    private UserEx user;
    private String vipUserId;
    private String eventId;
    private boolean vip;
    
    public MakeAttendantVIPTransaction(UserEx user, String vipUserId, String eventId, boolean vip) {
        this.user = user;
        this.vipUserId = vipUserId;
        this.eventId = eventId;
        this.vip = vip;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        PartakeDAOFactory factory = DBService.getFactory();

        Event event = factory.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);
        
        Enrollment enrollment = factory.getEnrollmentAccess().find(con, new EnrollmentPK(vipUserId, eventId));
        if (enrollment == null)
            throw new PartakeException(UserErrorCode.INVALID_ATTENDANT_EDIT);
        
        Enrollment newEnrollment = new Enrollment(enrollment);
        newEnrollment.setVIP(vip);
        factory.getEnrollmentAccess().put(con, newEnrollment);
        
        return null;
    }
}
