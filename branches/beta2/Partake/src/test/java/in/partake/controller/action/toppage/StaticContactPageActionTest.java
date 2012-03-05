package in.partake.controller.action.toppage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticContactPageActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy("/termofuse");
        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultSuccess(proxy);
        
        StaticContactPageAction action = (StaticContactPageAction) proxy.getAction();
        Assert.assertEquals("termofuse.jsp", action.getLocation());
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/termofuse");

        proxy.execute();
        assertResultSuccess(proxy);
        
        StaticContactPageAction action = (StaticContactPageAction) proxy.getAction();
        Assert.assertEquals("termofuse.jsp", action.getLocation());
    }
}
