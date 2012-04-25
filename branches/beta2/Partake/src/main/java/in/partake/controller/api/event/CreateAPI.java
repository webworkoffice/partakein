package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.ImageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

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
        embryo.setPreview(optBooleanParameter("draft", true));
        embryo.setCreatedAt(TimeUtil.getCurrentDateTime());

        List<EventRelation> relations = new ArrayList<EventRelation>();
        List<EventTicket> tickets = new ArrayList<EventTicket>();
        JSONObject invalidParameters = new JSONObject();
        updateEventFromParameter(user, embryo, invalidParameters);
        updateTicketsFromParameter(user, tickets, invalidParameters);
        updateEventRelationFromParameter(user, relations, invalidParameters);
        if (!invalidParameters.isEmpty())
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);

        String eventId = new CreateTransaction(user, embryo, relations).execute();

        // private でなければ Lucene にデータ挿入して検索ができるようにする
        embryo.setId(eventId);
        if (!embryo.isPrivate() && !embryo.isPreview()) {
            IEventSearchService searchService = PartakeApp.getEventSearchService();
            searchService.create(embryo, tickets);
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
    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        if (embryo.getForeImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, daos, embryo.getForeImageId(), user))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);
        }

        if (embryo.getBackImageId() != null) {
            if (!ImageDAOFacade.checkImageOwner(con, daos, embryo.getBackImageId(), user))
                throw new PartakeException(UserErrorCode.INVALID_IMAGE_OWNER);
        }

        // TODO: relations はここに渡って来る前に set されているべきでは？
        embryo.setRelations(relations);
        String eventId = EventDAOFacade.create(con, daos, embryo);

        return eventId;
    }
}
