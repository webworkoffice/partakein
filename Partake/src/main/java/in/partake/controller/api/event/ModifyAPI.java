package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

public class ModifyAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        checkCSRFToken();

        String eventId = getValidEventIdParameter();
        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        throw new RuntimeException("Not implemented yet");
        
//        Event embryo = new Event();
//        embryo.setOwnerId(user.getId());
//        
//        Boolean draft = getBooleanParameter("draft");
//        if (draft == null || draft)
//            embryo.setPreview(true);
//        else
//            embryo.setPreview(false);
//        embryo.setCreatedAt(TimeUtil.getCurrentDate());
//        
//        JSONObject invalidParameters = new JSONObject();
//        if (!updateEventFromParameter(user, embryo, invalidParameters))
//            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, invalidParameters);
//        
//        String eventId = DeprecatedEventDAOFacade.get().create(embryo, null, null);
//        
//        JSONObject obj = new JSONObject();
//        obj.put("eventId", eventId);
//        
//        return renderOK(obj);

//
//          EventService.get().update(event, eventEmbryo,
//                  foreImageEmbryo != null || removingForeImage, foreImageEmbryo,
//                  backImageEmbryo != null || removingBackImage, backImageEmbryo);
//          EventService.get().setEventRelations(event.getId(), eventRelations);
//
//          addActionMessage("イベント情報が変更されました。");
//          this.eventId = event.getId();
//          return SUCCESS;        
    }
}
