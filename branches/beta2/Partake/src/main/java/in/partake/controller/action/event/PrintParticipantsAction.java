package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.EventParticipationListPermission;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.UserErrorCode;

import java.util.List;

public class PrintParticipantsAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private EventEx event;
    private ParticipationList participationList;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        ParticipantsListTransaction transaction = new ParticipantsListTransaction(user, eventId);
        transaction.execute();

        event = transaction.getEvent();
        participationList = transaction.getParticipationList();

        return render("events/participants/print.jsp");
    }

    public EventEx getEvent() {
        return event;
    }

    public ParticipationList getParticipationList() {
        return participationList;
    }
}

class ParticipantsListTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;

    private EventEx event;
    private ParticipationList participationList;

    public ParticipantsListTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        // Only owner can retrieve the participants list.
        if (!EventParticipationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, eventId);
        participationList = event.calculateParticipationList(participations);
        return null;
    }

    public EventEx getEvent() {
        return event;
    }

    public ParticipationList getParticipationList() {
        return participationList;
    }
}
