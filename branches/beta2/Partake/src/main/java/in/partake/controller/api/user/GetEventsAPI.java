package in.partake.controller.api.user;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class GetEventsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String userId = getValidUserIdParameter();

        // TODO:
        throw new RuntimeException("Not implemented yet.");
    }
}
