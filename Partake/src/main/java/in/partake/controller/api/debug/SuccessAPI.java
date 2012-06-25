package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class SuccessAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * 常に <code>{ "result": "ok" }</code> を返す。
     * HTTP status は 200 を返す。
     * @return
     */    
    @Override
    protected String doExecute() throws DAOException {
        return renderOK();
    }
}
