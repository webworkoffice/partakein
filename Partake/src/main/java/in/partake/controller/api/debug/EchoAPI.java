package in.partake.controller.api.debug;

import net.sf.json.JSONObject;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class EchoAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * data を読んで、それを echo して返す。
     * data があれば 200 を返し、なければ 400 を返す。
     * @return
     */
    @Override
    protected String doExecute() throws DAOException {
        String data = getParameter("data");
        if (data == null) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        }
        
        JSONObject obj = new JSONObject();
        obj.put("data", data);
        
        return renderOK(obj);
    }
}
