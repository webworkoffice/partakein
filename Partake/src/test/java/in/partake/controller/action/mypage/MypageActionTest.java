package in.partake.controller.action.mypage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.fixture.TestDataProvider;

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

        assertEquals(DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.USER_ID1), action.getPreference());
        assertEquals(DeprecatedUserDAOFacade.get().getOpenIDIdentifiers(TestDataProvider.USER_ID1), action.getOpenIds());
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/mypage");

        proxy.execute();        
        assertResultLoginRequired(proxy);
    }
}
