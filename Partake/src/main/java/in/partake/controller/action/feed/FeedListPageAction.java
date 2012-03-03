package in.partake.controller.action.feed;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.dao.DAOException;

public class FeedListPageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
    
    public String execute() throws DAOException {
        return render("feedlist.jsp");
    }    
}
