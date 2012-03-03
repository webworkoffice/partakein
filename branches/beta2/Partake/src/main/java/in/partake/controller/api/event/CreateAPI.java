package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
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
        embryo.setPreview(true);
        embryo.setCreatedAt(TimeUtil.getCurrentDate());
        
        JSONObject invalidParameters = new JSONObject();
        if (!updateEventFromParameter(user, embryo, invalidParameters))
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);
        
        String eventId = EventService.get().create(embryo, null, null);
        
        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);
        
        return renderOK(obj);
    }
}
