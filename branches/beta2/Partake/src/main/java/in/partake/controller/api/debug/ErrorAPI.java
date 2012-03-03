package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

public class ErrorAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * 常に <code>{ "result": "error", "reason": "intentional error response" }</code> を返す。
     * ステータスは 500 を返す。 
     */
    @Override
    protected String doExecute() throws DAOException {
        return renderError(ServerErrorCode.INTENTIONAL_ERROR);
    }
}
