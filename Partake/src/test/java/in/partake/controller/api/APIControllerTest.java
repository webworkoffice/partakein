package in.partake.controller.api;

import in.partake.controller.PartakeControllerTestCase;
import in.partake.resource.PartakeProperties;

import java.io.IOException;

import org.junit.BeforeClass;

import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionProxy;


public abstract class APIControllerTest extends PartakeControllerTestCase {

    /**
     * TODO: とりあえず JPA を指定します。後で変更できるようにする。
     */
    @BeforeClass
    public static void setUpOnce() throws Exception {
        PartakeProperties.get().reset("jpa");        
        createDefaultFixtures();
    }    

    /**
     * proxy から JSON を取得する。取得できなかった場合は null を返す。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected String getJSONString(ActionProxy proxy) throws Exception {
        return response.getContentAsString();
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
}
