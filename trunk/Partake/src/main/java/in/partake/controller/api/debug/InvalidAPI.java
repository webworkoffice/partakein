package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class InvalidAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * 常に <code>{ "result": "error", "reason": "intentional invalid response" }</code> を返す。
     * ステータスは 400 を返す。
     */
    @Override
    protected String doExecute() throws DAOException {
        return renderInvalid(UserErrorCode.INTENTIONAL_USER_ERROR);
    }

}
