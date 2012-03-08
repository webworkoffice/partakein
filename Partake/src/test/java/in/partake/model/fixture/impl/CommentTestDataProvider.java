package in.partake.model.fixture.impl;

import java.util.Date;
import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dto.Comment;
import in.partake.model.fixture.TestDataProvider;

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
        
        dao.put(con, new Comment(COMMENT_ID1, EVENT_ID1, USER_ID1, "comment", false, new Date()));
        dao.put(con, new Comment(COMMENT_ID2, EVENT_ID2, USER_ID3, "comment", false, new Date()));
    }

}
