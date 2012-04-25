package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

import java.util.Map;
import java.util.UUID;

public class ShowParticipantsAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private EventEx event;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        ParticipantsListTransaction transaction = new ParticipantsListTransaction(user, eventId);
        transaction.execute();

        event = transaction.getEvent();
        ticketHolderListMap = transaction.getTicketHolderListMap();

        return render("events/participants/show.jsp");
    }

    public EventEx getEvent() {
        return event;
    }

    public Map<UUID, EventTicketHolderList> getTicketHolderListMap() {
        return ticketHolderListMap;
    }
}
