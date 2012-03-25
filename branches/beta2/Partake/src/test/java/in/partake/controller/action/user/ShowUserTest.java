package in.partake.controller.action.user;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ShowUserTest extends AbstractPartakeControllerTest {
    @Test
    public void testShowDefaultUser() throws Exception {
        ActionProxy proxy = getActionProxy("/users/" + TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getUser().getId(), is(TestDataProvider.DEFAULT_USER_ID));
        assertThat(action.getLocation(), is("users/show.jsp"));
   }

    @Test
    public void testShowPrivatePrefUser() throws Exception {
        ActionProxy proxy = getActionProxy("/users/" + TestDataProvider.USER_WITH_PRIVATE_PREF_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ShowAction action = (ShowAction) proxy.getAction();
        assertThat(action.getUser(), is(nullValue()));
        assertThat(action.getLocation(), is("users/private.jsp"));
   }
}
