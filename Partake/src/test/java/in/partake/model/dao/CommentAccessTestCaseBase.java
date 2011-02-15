package in.partake.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import in.partake.model.dto.Comment;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class CommentAccessTestCaseBase extends AbstractDaoTestCaseBase<ICommentAccess, Comment, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getCommentAccess());
    }
    
    @Override
    protected Comment create(long pkNumber, String pkSalt, int objNumber) {
        return new Comment(pkSalt + pkNumber, "eventId", "userId", "comment content", new Date(objNumber));
    }
    
    @Test
    public void testToFindtByEventId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            String prefix = factory.getCommentAccess().getFreshId(con);
            
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    Comment original = new Comment(prefix + "commentId-" + i + "-" + j, prefix + "eventId" + i, "userId", "comment content", PDate.getCurrentDate().getDate());
                    factory.getCommentAccess().put(con, original);
                }
            }

            for (int i = 0; i < 10; ++i) {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId" + i);
                
                List<String> strs = new ArrayList<String>();
                while (it.hasNext()) {
                    Comment comment = it.next();
                    String id = comment.getId();
                    if (id == null) { continue; } 
                    strs.add(id);                                        
                }
                Assert.assertEquals(10, strs.size());
                Collections.sort(strs);
                
                for (int j = 0; j < 10; ++j) {
                    Assert.assertEquals(prefix + "commentId-" + i + "-" + j, strs.get(j));
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testToUpdateByIterator() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            
            String prefix = factory.getCommentAccess().getFreshId(con);
            
            // create 
            for (int i = 0; i < 10; ++i) {
                Comment original = new Comment(prefix + "commentId-" + i, prefix + "eventId", "userId", "comment content", PDate.getCurrentDate().getDate());
                factory.getCommentAccess().put(con, original);
            }
            
            // update
            {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                while (it.hasNext()) {
                    Comment comment = it.next();
                    
                    Comment updated = new Comment(comment);
                    updated.setComment("New comment!");
                    
                    it.update(updated);
                }
            }
            // get them
            {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                while (it.hasNext()) {
                    Comment comment = it.next();
                    Assert.assertEquals("New comment!", comment.getComment());
                }                
            }
            
            con.commit();            
        } finally {
            con.invalidate();
        }
    }

    
}
