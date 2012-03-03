package in.partake.controller.action.feed;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class FeedListPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        return render("feedlist.jsp");
    }    
}
