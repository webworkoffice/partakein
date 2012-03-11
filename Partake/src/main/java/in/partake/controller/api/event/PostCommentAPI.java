package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Comment;
import in.partake.model.dto.EventActivity;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class PostCommentAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;
    public static final int MAX_COMMENT_LENGTH = 10000;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();        
        String eventId = getValidEventIdParameter();

        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String comment = getParameter("comment");
        if (StringUtils.isBlank(comment))
            return renderInvalid(UserErrorCode.MISSING_COMMENT);
        if (comment.length() > MAX_COMMENT_LENGTH)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        Comment embryo = new Comment(eventId, user.getId(), comment, true, new Date());
        new PostCommentTransaction(embryo).execute();

        return renderOK();
    }
}

class PostCommentTransaction extends Transaction<Void> {
    private Comment commentEmbryo;
    
    public PostCommentTransaction(Comment embryo) {
        this.commentEmbryo = embryo;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        PartakeDAOFactory factory = DBService.getFactory();
        
        commentEmbryo.setId(factory.getCommentAccess().getFreshId(con));
        factory.getCommentAccess().put(con, commentEmbryo);

        // TODO: コメント消したときにこれも消したいか？　まずいコメントが feed され続けるのは問題となりうるか？
        IEventActivityAccess eaa = factory.getEventActivityAccess();
        UserEx user = UserDAOFacade.getUserEx(con, commentEmbryo.getUserId());
        String title = user.getScreenName() + " さんがコメントを投稿しました";
        String content = commentEmbryo.getComment();
        eaa.put(con, new EventActivity(eaa.getFreshId(con), commentEmbryo.getEventId(), title, content, commentEmbryo.getCreatedAt()));
        
        return null;
    }    
}
