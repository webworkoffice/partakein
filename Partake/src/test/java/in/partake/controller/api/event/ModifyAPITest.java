package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.EventEx;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ModifyAPITest extends APIControllerTest {

    @Test
    public void testToModify() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);

        EventEx event = loadEventEx(DEFAULT_EVENT_ID);
        event.setTitle("MODIFIED");
        setEventParameters(proxy, event);

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getTitle(), is("MODIFIED"));
    }

    @Test
    public void testToModifyWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/modify");

        EventEx event = loadEventEx(DEFAULT_EVENT_ID);
        setEventParameters(proxy, event);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToModifyWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);

        EventEx event = loadEventEx(DEFAULT_EVENT_ID);
        setEventParameters(proxy, event);
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    private void setEventParameters(ActionProxy proxy, EventEx event) {
        if (event.getId() != null)
            addParameter(proxy, "eventId", event.getId());
        if (event.isDraft())
            addParameter(proxy, "draft", String.valueOf(event.isDraft()));

        // Basic Information
        addParameter(proxy, "title", event.getTitle());
        addParameter(proxy, "summary", event.getSummary());
        addParameter(proxy, "category", event.getCategory());
        addParameter(proxy, "beginDate", String.valueOf(event.getBeginDate().getTime()));
        if (event.getEndDate() != null)
            addParameter(proxy, "endDate", String.valueOf(event.getEndDate().getTime()));
        if (event.getUrl() != null)
            addParameter(proxy, "url", event.getUrl());
        if (event.getPlace() != null)
            addParameter(proxy, "place", event.getPlace());
        if (event.getAddress() != null)
            addParameter(proxy, "address", event.getAddress());
        addParameter(proxy, "description", event.getDescription());
        if (event.getHashTag() != null)
            addParameter(proxy, "hashTag", event.getHashTag());
        if (event.getPasscode() != null)
            addParameter(proxy, "passcode", event.getPasscode());
        if (event.getManagerScreenNames() != null)
            addParameter(proxy, "editors", event.getManagerScreenNames());
        if (event.getForeImageId() != null)
            addParameter(proxy, "foreImageId", event.getForeImageId());
        if (event.getBackImageId() != null)
            addParameter(proxy, "backImageId", event.getBackImageId());

        // event relations
        if (event.getEventRelations() != null && !event.getEventRelations().isEmpty()) {
            int size = event.getEventRelations().size();
            String[] relatedEventIds = new String[size];
            String[] relatedEventRequired = new String[size];
            String[] relatedEventPriority = new String[size];

            for (int i = 0; i < event.getEventRelations().size(); ++i) {
                EventRelation relation = event.getEventRelations().get(i);
                relatedEventIds[i] = relation.getEventId();
                relatedEventRequired[i] = String.valueOf(relation.isRequired());
                relatedEventPriority[i] = String.valueOf(relation.hasPriority());
            }

            addParameter(proxy, "relatedEventID[]", relatedEventIds);
            addParameter(proxy, "relatedEventRequired[]", relatedEventRequired);
            addParameter(proxy, "relatedEventPriority[]", relatedEventPriority);
        }
    }
}
