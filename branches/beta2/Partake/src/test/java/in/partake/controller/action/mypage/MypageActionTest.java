package in.partake.controller.action.mypage;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class MypageActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy("/mypage");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);
        
        MypageAction action = (MypageAction) proxy.getAction();

        assertThat(action.getPreference(), is(UserPreference.getDefaultPreference(TestDataProvider.DEFAULT_USER_ID)));
        assertThat(action.getOpenIds(), hasItem(TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER));
        assertThat(action.getOpenIds(), hasItem(TestDataProvider.DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER));
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/mypage");

        proxy.execute();        
        assertResultLoginRequired(proxy);
    }
}
