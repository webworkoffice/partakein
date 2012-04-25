package in.partake.controller.api.event;

import java.util.UUID;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditParticipantsPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

public class MakeAttendantVIPAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        UUID ticketId = getValidTicketIdParameter();
        String userId = getValidUserIdParameter();
        Boolean vip = getBooleanParameter("vip");
        if (vip == null)
            return renderInvalid(UserErrorCode.INVALID_MISSING_VIP);

        MakeAttendantVIPTransaction transaction = new MakeAttendantVIPTransaction(user, userId, ticketId, vip);
        transaction.execute();

        return renderOK();
    }
}

class MakeAttendantVIPTransaction extends Transaction<Void> {
    private UserEx user;
    private String vipUserId;
    private UUID ticketId;
    private boolean vip;

    public MakeAttendantVIPTransaction(UserEx user, String vipUserId, UUID ticketId, boolean vip) {
        this.user = user;
        this.vipUserId = vipUserId;
        this.ticketId = ticketId;
        this.vip = vip;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
    	EventTicket ticket = daos.getEventTicketAccess().find(con, ticketId);
    	if (ticket == null)
    		throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        Event event = daos.getEventAccess().find(con, ticket.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_TICKET_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        Enrollment enrollment = daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticketId, vipUserId);
        if (enrollment == null)
            throw new PartakeException(UserErrorCode.INVALID_ATTENDANT_EDIT);

        Enrollment newEnrollment = new Enrollment(enrollment);
        newEnrollment.setVIP(vip);
        daos.getEnrollmentAccess().put(con, newEnrollment);

        return null;
    }
}
