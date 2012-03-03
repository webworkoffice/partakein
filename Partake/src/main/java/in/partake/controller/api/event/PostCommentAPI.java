package in.partake.controller.api.event;

import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Comment;
import in.partake.resource.UserErrorCode;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class PostCommentAPI extends AbstractPartakeAPI {
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
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String comment = getParameter("comment");
        if (StringUtils.isEmpty(comment))
            return renderInvalid(UserErrorCode.MISSING_COMMENT);

        Comment embryo = new Comment(eventId, user.getId(), comment, true, new Date());
        EventService.get().addComment(embryo);

        return renderOK();
    }
}
