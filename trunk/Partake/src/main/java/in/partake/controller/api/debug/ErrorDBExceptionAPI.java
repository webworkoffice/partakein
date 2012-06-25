package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class ErrorDBExceptionAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * DAOException が不意に起こった場合のテスト。
     * @return
     */
    @Override
    protected String doExecute() throws DAOException {
        throw new DAOException();
    }

}
