package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

public class ErrorDBAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * データベースエラー。
     * ステータスは 500 を返す。
     */
    @Override
    protected String doExecute() throws DAOException {

        return renderError(ServerErrorCode.DB_ERROR);
    }

}
