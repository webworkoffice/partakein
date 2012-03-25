package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.ImageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;
import in.partake.service.PartakeService;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class CreateAPI extends AbstractEventEditAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        Event embryo = new Event();
        embryo.setOwnerId(user.getId());

        Boolean draft = getBooleanParameter("draft");
        if (draft == null || draft)
            embryo.setPreview(true);
        else
            embryo.setPreview(false);
        embryo.setCreatedAt(TimeUtil.getCurrentDate());

        List<EventRelation> relations = new ArrayList<EventRelation>();
        JSONObject invalidParameters = new JSONObject();
        updateEventFromParameter(user, embryo, invalidParameters);
        updateEventRelationFromParameter(user, relations, invalidParameters);
        if (!invalidParameters.isEmpty())
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);

        String eventId = new CreateTransaction(user, embryo, relations).execute();

        // private でなければ Lucene にデータ挿入して検索ができるようにする
        embryo.setId(eventId);
        if (!embryo.isPrivate() && !embryo.isPreview()) {
            IEventSearchService searchService = PartakeService.get().getEventSearchService();
            searchService.create(embryo);
        }
        
        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);

        return renderOK(obj);
    }
}

class CreateTransaction extends Transaction<String> {
    private UserEx user;
    private Event embryo;
    private List<EventRelation> relations;

    public CreateTransaction(UserEx user, Event embryo, List<EventRelation> relations) {
        this.user = user;
        this.embryo = embryo;
        this.relations = relations;
    }

    @Override
    protected String doExecute(PartakeConnection con) throws DAOException, PartakeException {
        if (embryo.getForeImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, embryo.getForeImageId(), user))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);
        }

        if (embryo.getBackImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, embryo.getBackImageId(), user))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);            
        }

        String eventId = EventDAOFacade.create(con, embryo);
        EventDAOFacade.setEventRelations(con, eventId, relations);

        return eventId;
    }
}
