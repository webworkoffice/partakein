package in.partake.controller.api.event;

import java.util.ArrayList;
import java.util.List;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

public class CreateAPI extends AbstractEventEditAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
                
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
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
        
        String eventId = DeprecatedEventDAOFacade.get().create(embryo, null, null);
        DeprecatedEventDAOFacade.get().setEventRelations(eventId, relations);
        
        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);
        
        return renderOK(obj);
    }
}
