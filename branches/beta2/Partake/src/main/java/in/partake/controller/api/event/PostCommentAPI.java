package in.partake.controller.api.event;

import in.partake.base.PartakeException;
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
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
        String eventId = getValidEventIdParameter();
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String comment = getParameter("comment");
        if (StringUtils.isBlank(comment))
            return renderInvalid(UserErrorCode.MISSING_COMMENT);
        if (comment.length() > 10000) // TODO: Don't put magic number!
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        Comment embryo = new Comment(eventId, user.getId(), comment, true, new Date());
        EventService.get().addComment(embryo);

        return renderOK();
    }
}
