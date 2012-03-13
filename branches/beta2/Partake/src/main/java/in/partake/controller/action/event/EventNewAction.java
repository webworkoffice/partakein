package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;

import java.util.ArrayList;

public class EventNewAction extends AbstractEventEditAction {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();        
        event = new EventEx(new Event(), user, null, null, new ArrayList<EventRelationEx>());
        return render("events/new.jsp");
    }
}
