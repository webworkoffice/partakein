package in.partake.controller.api;

import in.partake.controller.PartakeControllerTestCase;
import in.partake.resource.Constants;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionProxy;


public abstract class APIControllerTest extends PartakeControllerTestCase {

    /**
     * proxy から JSON を取得する。取得できなかった場合は null を返す。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected String getJSONString(ActionProxy proxy) throws Exception {
        // response.setCharacterEncoding("UTF-8") すればgetContentAsStringでも正常に取得できるはずだが、
        // このメソッドがresponseの状態を破壊するのは不適と考えて自前でbyte[]->Stringの変換を行っている
        byte[] streamData = response.getContentAsByteArray();
        if (streamData == null) {
            return null;
        } else {
            return new String(streamData, "UTF-8");
        }
    }

    /**
     * proxy から JSON を取得する。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected JSONObject getJSON(ActionProxy proxy) throws Exception {
        String str = getJSONString(proxy);
        System.out.println(str);
        return JSONObject.fromObject(str);
    }

    // ----------------------------------------------------------------------
    
    protected void assertResultOK(ActionProxy proxy) throws Exception {
        Assert.assertEquals(200, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        Assert.assertEquals(400, response.getStatus());        

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
    
    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        // status code should be 401.
        Assert.assertEquals(401, response.getStatus());
        
        // header should contain WWW-authenticate.
        String authenticate = (String) response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(authenticate);
        Assert.assertTrue(authenticate.contains("OAuth"));
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("auth", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        Assert.assertEquals(403, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("forbidden", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    protected void assertResultError(ActionProxy proxy) throws Exception {
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    /**
     * PARTAKE API標準のDateフォーマットをparseするためのフォーマットを作成する。
     * このメソッドが返すインスタンスはスレッドセーフではないため、他スレッドとの共用は避けること。
     * @return API標準のDateフォーマット
     */
    protected DateFormat createDateFormat() {
        // TODO Localeは外部ファイルなどで設定可能にする
        return new SimpleDateFormat(Constants.JSON_DATE_FORMAT, Locale.getDefault());
    }
}
