package in.partake.controller.api.debug;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class SuccessIfLoginAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    /**
     * login していれば success と同じ挙動をする。
     * そうでなければ loginRequired を返し、HTTP status は 401 を返す。
     * 401 は WWW-Authentication をふくまねばならないので、とりあえず OAuth を入れておく。
     * @return
     */
    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user != null) {
            return renderOK();
        } else {
            return renderLoginRequired();
        }
    }    
}
