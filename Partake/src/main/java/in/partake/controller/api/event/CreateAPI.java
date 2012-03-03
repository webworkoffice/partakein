package in.partake.controller.api.event;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class CreateAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
}
