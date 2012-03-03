package in.partake.controller.action.mypage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.UserService;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class MypageActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy("/mypage");
        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultSuccess(proxy);
        
        MypageAction action = (MypageAction) proxy.getAction();

        assertEquals(UserService.get().getUserPreference(TestDataProvider.USER_ID1), action.getPreference());
        assertEquals(UserService.get().getOpenIDIdentifiers(TestDataProvider.USER_ID1), action.getOpenIds());
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/mypage");

        proxy.execute();        
        assertResultLoginRequired(proxy);
    }
}
