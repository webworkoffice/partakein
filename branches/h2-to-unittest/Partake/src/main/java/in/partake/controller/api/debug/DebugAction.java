package in.partake.controller.api.debug;

import net.sf.json.JSONObject;
import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;


/**
 * render 系がきちんと動作しているかどうかを確認する。
 * これはクライアントのテストにも使えるので、ユーザーにも公開する。
 */
public class DebugAction extends PartakeAPIActionSupport {

    private static final long serialVersionUID = 1L;

    /**
     * 常に <code>{ "result": "ok" }</code> を返す。
     * HTTP status は 200 を返す。
     * @return
     */
    public String success() {
        return renderOK();
    }
    
    
    /**
     * data を読んで、それを echo して返す。
     * data があれば 200 を返し、なければ 400 を返す。
     * @return
     */
    public String echo() {
        String data = getParameter("data");
        if (data == null) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        }
        
        JSONObject obj = new JSONObject();
        obj.put("data", data);
        
        return renderOK(obj);
    }
    
    /**
     * login していれば success と同じ挙動をする。
     * そうでなければ loginRequired を返し、HTTP status は 401 を返す。
     * 401 は WWW-Authentication をふくまねばならないので、とりあえず OAuth を入れておく。
     * @return
     */
    public String successIfLogin() {
        UserEx user = getLoginUser();
        if (user != null) {
            return renderOK();
        } else {
            return renderLoginRequired();
        }
    }

    /**
     * 常に <code>{ "result": "error", "reason": "intentional invalid response" }</code> を返す。
     * ステータスは 400 を返す。
     */
    public String invalid() {
        return renderInvalid(UserErrorCode.INTENTIONAL_USER_ERROR);
    }

    /**
     * 常に <code>{ "result": "error", "reason": "intentional error response" }</code> を返す。
     * ステータスは 500 を返す。 
     */
    public String error() {
        return renderError(ServerErrorCode.INTENTIONAL_ERROR);
    }

    /**
     * RuntimeException が不意に起こった場合の対応をテスト
     * @return
     */
    public String errorException() {
        throw new RuntimeException("Some Error");
    }
    
    /**
     * データベースエラー。
     * ステータスは 500 を返す。
     */
    public String errorDB() {
        return renderError(ServerErrorCode.DB_ERROR);
    }
    
    /**
     * DAOException が不意に起こった場合のテスト。
     * @return
     */
    public String errorDBException() throws DAOException {
        throw new DAOException();
    }
}
