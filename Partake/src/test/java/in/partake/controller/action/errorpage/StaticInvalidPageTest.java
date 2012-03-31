package in.partake.controller.action.errorpage;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticInvalidPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testAccessWithErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");
        addParameter(proxy, "errorCode", UserErrorCode.INTENTIONAL_USER_ERROR.getErrorCode());
        proxy.execute();
        assertResultSuccess(proxy);

        StaticInvalidPageAction action = (StaticInvalidPageAction) proxy.getAction();

        assertThat(action.getUserErrorCode(), is(UserErrorCode.INTENTIONAL_USER_ERROR));
    }

    @Test
    public void testAccessWithInvalidErrorCode() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");
        addParameter(proxy, "errorCode", "hogehoge");
        proxy.execute();
        assertResultSuccess(proxy);

        StaticInvalidPageAction action = (StaticInvalidPageAction) proxy.getAction();
        assertThat(action.getUserErrorCode(), is(nullValue()));
    }
}
