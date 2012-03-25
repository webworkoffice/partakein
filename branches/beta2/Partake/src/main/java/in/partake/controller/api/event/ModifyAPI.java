package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.ImageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class ModifyAPI extends AbstractEventEditAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        boolean draft = optBooleanParameter("draft", true);
        String eventId = getValidEventIdParameter();

        Event embryo = new Event();
        embryo.setPreview(draft);
        embryo.setCreatedAt(TimeUtil.getCurrentDate());

        List<EventRelation> relations = new ArrayList<EventRelation>();
        JSONObject invalidParameters = new JSONObject();
        updateEventFromParameter(user, embryo, invalidParameters);
        updateEventRelationFromParameter(user, relations, invalidParameters);
        if (!invalidParameters.isEmpty())
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);

        new ModifyTransaction(user, eventId, embryo, relations).execute();

        // private でなければ Lucene にデータ挿入して検索ができるようにする
        embryo.setId(eventId);
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        if (embryo.isPrivate() || embryo.isPreview())
            searchService.remove(eventId);
        else if (searchService.hasIndexed(eventId))
            searchService.update(embryo);
        else
            searchService.create(embryo);

        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);
        return renderOK(obj);
    }
}

class ModifyTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private Event embryo;
    private List<EventRelation> relations;

    public ModifyTransaction(UserEx user, String eventId, Event embryo, List<EventRelation> relations) {
        this.user = user;
        this.eventId = eventId;
        this.embryo = embryo;
        this.relations = relations;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        if (embryo.getForeImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, daos, embryo.getForeImageId(), user) ||
                    !StringUtils.equals(embryo.getForeImageId(), event.getForeImageId()))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);
        }

        if (embryo.getBackImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, daos, embryo.getBackImageId(), user) ||
                    !StringUtils.equals(embryo.getBackImageId(), event.getBackImageId()))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);
        }

        boolean draft = embryo.isPreview() && event.isPreview();
        embryo.setId(event.getId());
        embryo.setPreview(draft);
        embryo.setOwnerId(event.getOwnerId());
        embryo.setCreatedAt(event.getCreatedAt());
        embryo.setModifiedAt(TimeUtil.getCurrentDate());

        EventDAOFacade.modify(con, daos, embryo);
        EventDAOFacade.setEventRelations(con, daos, eventId, relations);

        // private でなければ Lucene にデータ挿入して検索ができるようにする
        if (!embryo.isPrivate() && !embryo.isPreview()) {
            IEventSearchService searchService = PartakeApp.getEventSearchService();
            searchService.create(embryo);
        }

        return null;
    }
}
