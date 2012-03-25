package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventRemovePermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;
import in.partake.service.impl.LuceneService;

public class RemoveAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();        
        String eventId = getValidEventIdParameter();

        new RemoveEventTransaction(user, eventId).execute();
        // EventSearcher からデータを抜く
        LuceneService.get().removeDocument(eventId);

        return renderOK();
    }
}

class RemoveEventTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    
    public RemoveEventTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        IEventAccess dao = DBService.getFactory().getEventAccess();
                
        Event event = dao.find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        
        if (!EventRemovePermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        dao.remove(con, event.getId());
        return null;
    }
}