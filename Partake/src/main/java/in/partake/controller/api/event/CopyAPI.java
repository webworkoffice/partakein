package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import net.sf.json.JSONObject;

public class CopyAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        String eventId = getValidEventIdParameter();
        String newEventId = new CopyTransaction(user, eventId).execute();

        JSONObject obj = new JSONObject();
        obj.put("eventId", newEventId);
        return renderOK(obj);
    }
}

class CopyTransaction extends Transaction<String> {
    private UserEx user;
    private String eventId;

    public CopyTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return EventDAOFacade.copy(con, daos, user, eventId);
    }
}
