package in.partake.controller.action.toppage;

import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticTermOfUsePageActionTest extends ActionControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy("/termofuse");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        StaticTermOfUsePageAction action = (StaticTermOfUsePageAction) proxy.getAction();
        Assert.assertEquals("termofuse.jsp", action.getLocation());
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/termofuse");

        proxy.execute();
        assertResultSuccess(proxy);

        StaticTermOfUsePageAction action = (StaticTermOfUsePageAction) proxy.getAction();
        Assert.assertEquals("termofuse.jsp", action.getLocation());
    }
}
