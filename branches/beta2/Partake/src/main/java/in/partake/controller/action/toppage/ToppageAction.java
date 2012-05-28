package in.partake.controller.action.toppage;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class ToppageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    protected String doExecute() throws DAOException, PartakeException {
        return render("index.jsp");
    }
}

