package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.MessageService;
import in.partake.resource.UserErrorCode;

import org.apache.commons.lang.StringUtils;

public class SendMessageAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        String eventId = getParameter("eventId");
        if (eventId == null)
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String message = getParameter("message");
        if (StringUtils.isBlank(message))
            return renderInvalid(UserErrorCode.MISSING_MESSAGE);

        try {
            MessageService.get().sendMessage(user, eventId, message);
            return renderOK();
        } catch (PartakeException e) {
            return renderException(e);
        }
    }
}
