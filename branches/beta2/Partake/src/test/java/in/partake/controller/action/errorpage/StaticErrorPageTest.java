package in.partake.controller.action.errorpage;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.ServerErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticErrorPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/error");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/error");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testAccessWithErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy("/error");
        addParameter(proxy, "errorCode", ServerErrorCode.INTENTIONAL_ERROR.getErrorCode());
        proxy.execute();
        assertResultSuccess(proxy);

        StaticErrorPageAction action = (StaticErrorPageAction) proxy.getAction();

        assertThat(action.getServerErrorCode(), is(ServerErrorCode.INTENTIONAL_ERROR));
    }

    @Test
    public void testAccessWithInvalidErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy("/error");
        addParameter(proxy, "errorCode", "hogehoge");
        proxy.execute();
        assertResultSuccess(proxy);

        StaticErrorPageAction action = (StaticErrorPageAction) proxy.getAction();
        assertThat(action.getServerErrorCode(), is(nullValue()));
    }

}
