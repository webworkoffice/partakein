package in.partake.controller.action.admin;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AdminPageActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testWithAdminLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/admin/");
        loginAs(proxy, TestDataProvider.USER_ADMIN_ID);

        proxy.execute();
        assertResultSuccess(proxy);
        
        AdminPageAction action = (AdminPageAction) proxy.getAction();
        Assert.assertEquals("admin/index.jsp", action.getLocation());
    }

    @Test
    public void testWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/admin/");
        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/admin/");

        proxy.execute();
        assertResultLoginRequired(proxy);        
    }
}
