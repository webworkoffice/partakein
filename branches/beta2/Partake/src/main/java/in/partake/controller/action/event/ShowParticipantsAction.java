package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class ShowParticipantsAction extends AbstractPartakeAction {
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

        return render("events/participants/show.jsp");
    }
    
    public EventEx getEvent() {
        return event;
    }
    
    public ParticipationList getParticipationList() {
        return participationList;
    }
}
