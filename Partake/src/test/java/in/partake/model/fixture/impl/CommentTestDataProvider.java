package in.partake.model.fixture.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dto.Comment;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

public class CommentTestDataProvider extends TestDataProvider<Comment> {

    @Override
    public Comment create() {
        return new Comment();
    }

    @Override
    public Comment create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("comment" + pkSalt).hashCode());
        String eventId = new UUID(pkNumber, ("comment" + pkSalt).hashCode()).toString();
        String userId  = new UUID(objNumber, "user".hashCode()).toString();
        String comment = "";
        boolean isHTML = false;
        Date createdAt = new Date(objNumber);
        return new Comment(uuid.toString(), eventId, userId, comment, isHTML, createdAt); 
    }

    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ICommentAccess dao = factory.getCommentAccess();
        dao.truncate(con);
        
        Date now = TimeUtil.getCurrentDate();
        
        dao.put(con, new Comment(OWNER_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_OWNER_ID, "comment", false, now));
        dao.put(con, new Comment(EDITOR_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_EDITOR_ID, "comment", false, now));
        dao.put(con, new Comment(COMMENTOR_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_COMMENTOR_ID, "comment", false, now));
        dao.put(con, new Comment(UNRELATED_USER_COMMENT_ID, DEFAULT_EVENT_ID, EVENT_UNRELATED_USER_ID, "comment", false, now));        
    }

}
