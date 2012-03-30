package in.partake.controller.action.event;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class EventNewActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testNew() throws Exception {
        ActionProxy proxy = getActionProxy("/events/new");
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy, "events/new.jsp");

        EventNewAction action = (EventNewAction) proxy.getAction();
        assertThat(action.getEvent().getId(), is(nullValue()));
    }

    @Test
    public void testNewWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/events/new");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testNewWithInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy("/events/new");
        loginAs(proxy, DEFAULT_USER_ID);

        proxy.execute();
        // assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }


}
