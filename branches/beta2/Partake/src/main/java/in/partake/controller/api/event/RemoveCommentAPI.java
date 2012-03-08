package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.CommentEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

public class RemoveCommentAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String commentId = getValidCommentIdParameter();

        // TODO: These code should be in transaction.
        CommentEx comment = DeprecatedEventDAOFacade.get().getCommentExById(commentId);

        // Only an event owner or a user who posted the comment can remove the comment. 
        if (comment.getUser().getId().equals(user.getId()) || comment.getEvent().hasPermission(user, UserPermission.EVENT_REMOVE_COMMENT)) {
            DeprecatedEventDAOFacade.get().removeComment(commentId);
            return renderOK();
        }

        return renderForbidden(UserErrorCode.COMMENT_REMOVAL_FORBIDDEN);
    }
}
