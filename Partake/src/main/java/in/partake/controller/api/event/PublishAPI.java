package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;
import in.partake.service.IEventSearchService;
import in.partake.service.PartakeService;

import org.apache.commons.lang.StringUtils;

public class PublishAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        Event event = new PublishTransaction(user, eventId).execute();
        IEventSearchService searchService = PartakeService.get().getEventSearchService();
        if (event.isPrivate())
            searchService.remove(event.getId());
        else
            searchService.create(event);

        return renderOK();
    }
}

class PublishTransaction extends Transaction<Event> {
    private UserEx user;
    private String eventId;

    public PublishTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
    }

    @Override
    protected Event doExecute(PartakeConnection con) throws DAOException, PartakeException {
        Event event = DBService.getFactory().getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        if (!StringUtils.equals(event.getOwnerId(), user.getId()))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        if (!event.isPreview())
            throw new PartakeException(UserErrorCode.EVENT_ALREADY_PUBLISHED);

        event.setPreview(false);
        DBService.getFactory().getEventAccess().put(con, event);

        // Event Activity に挿入
        IEventActivityAccess eaa = DBService.getFactory().getEventActivityAccess();
        EventActivity activity = new EventActivity(eaa.getFreshId(con), event.getId(), "イベントが更新されました : " + event.getTitle(), event.getDescription(), event.getCreatedAt());
        eaa.put(con, activity);

        return event;
    }
}
