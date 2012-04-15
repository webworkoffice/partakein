package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.resource.UserErrorCode;

import org.apache.commons.lang.StringUtils;

public class PostCommentAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;
    public static final int MAX_COMMENT_LENGTH = 10000;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String comment = getParameter("comment");
        if (StringUtils.isBlank(comment))
            return renderInvalid(UserErrorCode.MISSING_COMMENT);
        if (comment.length() > MAX_COMMENT_LENGTH)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        Comment embryo = new Comment(null, eventId, user.getId(), comment, true, TimeUtil.getCurrentDateTime());
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
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, commentEmbryo.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        commentEmbryo.setId(daos.getCommentAccess().getFreshId(con));
        daos.getCommentAccess().put(con, commentEmbryo);

        // TODO: コメント消したときにこれも消したいか？　まずいコメントが feed され続けるのは問題となりうるか？
        IEventActivityAccess eaa = daos.getEventActivityAccess();
        UserEx user = UserDAOFacade.getUserEx(con, daos, commentEmbryo.getUserId());
        String title = user.getScreenName() + " さんがコメントを投稿しました";
        String content = commentEmbryo.getComment();
        eaa.put(con, new EventActivity(eaa.getFreshId(con), commentEmbryo.getEventId(), title, content, TimeUtil.toDate(commentEmbryo.getCreatedAt())));

        return null;
    }
}
