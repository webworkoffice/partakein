package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class ErrorExceptionAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * RuntimeException が不意に起こった場合の対応をテスト
     * @return
     */
    @Override
    protected String doExecute() throws DAOException {
        throw new RuntimeException("Some Error");
    }
}
