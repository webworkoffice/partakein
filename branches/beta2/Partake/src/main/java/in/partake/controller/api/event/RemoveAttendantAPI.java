package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditParticipantsPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.UserErrorCode;

public class RemoveAttendantAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();
        String userId = getValidUserIdParameter();

        new RemoveAttendantTransaction(user, eventId, userId).execute();
        return renderOK();
    }
}

class RemoveAttendantTransaction extends Transaction<Void>
{
    private UserEx user;
    private String eventId;
    private String userId;

    public RemoveAttendantTransaction(UserEx user, String eventId, String userId) {
        this.user = user;
        this.eventId = eventId;
        this.userId = userId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventAccess eventDao = daos.getEventAccess();
        Event event = eventDao.find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!EventEditParticipantsPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        daos.getEnrollmentAccess().remove(con, new EnrollmentPK(userId, eventId));
        return null;
    }
}
