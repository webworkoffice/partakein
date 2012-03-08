package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class ModifyAPI extends AbstractEventEditAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();
        
        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        Event embryo = new Event(event);
        boolean draft = embryo.isPreview() && optBooleanParameter("draft", true);
        embryo.setPreview(draft);
        embryo.setModifiedAt(TimeUtil.getCurrentDate());
        
        List<EventRelation> relations = new ArrayList<EventRelation>();
        JSONObject invalidParameters = new JSONObject();
        updateEventFromParameter(user, embryo, invalidParameters);
        updateEventRelationFromParameter(user, relations, invalidParameters);
        
        if (!invalidParameters.isEmpty())
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);
        
        DeprecatedEventDAOFacade.get().update(embryo);
        DeprecatedEventDAOFacade.get().setEventRelations(event.getId(), relations);
        
        JSONObject obj = new JSONObject();
        obj.put("eventId", embryo.getId());
        return renderOK(obj);
    }
}
