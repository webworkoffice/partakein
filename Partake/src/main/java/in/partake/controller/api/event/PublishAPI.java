package in.partake.controller.api.event;

import org.apache.commons.lang.StringUtils;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

public class PublishAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();
        
        Event event = EventService.get().getEventById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        if (!StringUtils.equals(event.getOwnerId(), user.getId()))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);
        
        if (!event.isPreview())
            return renderInvalid(UserErrorCode.EVENT_ALREADY_PUBLISHED);
        
        EventService.get().publishEvent(event);
        return renderOK();
    }
}
