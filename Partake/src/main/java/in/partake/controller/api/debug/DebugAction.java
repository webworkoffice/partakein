package in.partake.controller.api.debug;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;


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
     * 常に <code>{ "result": "error", "reason": "intentional error" }</code> を返す。
     * ステータスは 500 を返す。 
     */
    public String error() {
        return renderError("intentional error");
    }
}
