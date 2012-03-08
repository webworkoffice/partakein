package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.RemoveCommentPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

public class RemoveCommentAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String commentId = getValidCommentIdParameter();

        new RemoveCommentTransaction(user, commentId).execute();        
        return renderOK();
    }
}

class RemoveCommentTransaction extends Transaction<Void> {
    private String commentId;
    private UserEx user;
    
    public RemoveCommentTransaction(UserEx user, String commentId) {
        this.user = user;
        this.commentId = commentId;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        PartakeDAOFactory factory = DBService.getFactory();
        
        Comment comment = factory.getCommentAccess().find(con, commentId); 
        if (comment == null)
            throw new PartakeException(UserErrorCode.INVALID_COMMENT_ID);

        Event event = factory.getEventAccess().find(con, comment.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_COMMENT_ID);
        
        if (!RemoveCommentPermission.check(comment, event, user))
            throw new PartakeException(UserErrorCode.COMMENT_REMOVAL_FORBIDDEN);
        
        factory.getCommentAccess().remove(con, commentId);
        return null;
    }
}
