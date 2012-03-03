package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class EventNewAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        ensureLogin();
        return render("events/new.jsp");
    }
}
