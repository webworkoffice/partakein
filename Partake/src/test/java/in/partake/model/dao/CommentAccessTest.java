package in.partake.model.dao;

import in.partake.base.TimeUtil;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dto.Comment;
import in.partake.model.fixture.impl.CommentTestDataProvider;
import in.partake.service.DBService;
import in.partake.service.TestDatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommentAccessTest extends AbstractDaoTestCaseBase<ICommentAccess, Comment, String> {
    private CommentTestDataProvider provider;
    
    @Before
    public void setup() throws DAOException {
        super.setup(DBService.getFactory().getCommentAccess());
        provider = TestDatabaseService.getTestDataProviderSet().getCommentDataProvider();
    }
    
    @Override
    protected Comment create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
    
    @Test
    // TODO tell about order of the DataIterator's value.
    public void testToFindByEventId() throws Exception {
        PartakeConnection con = pool.getConnection();
        TimeUtil.setCurrentDate(TimeUtil.getCurrentDate());
        
        try {
            con.beginTransaction();
            String prefix = factory.getCommentAccess().getFreshId(con);
            
            String[][] ids = new String[10][10];
            
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    ids[i][j] = UUID.randomUUID().toString();
                    Comment original = new Comment(ids[i][j], prefix + "eventId" + i, "userId", "comment content", false, TimeUtil.getCurrentDate());
                    factory.getCommentAccess().put(con, original);
                    TimeUtil.waitForTick();
                }
            }

            for (int i = 0; i < 10; ++i) {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId" + i);
                try {
                    List<String> strs = new ArrayList<String>();
                    while (it.hasNext()) {
                        Comment comment = it.next();
                        String id = comment.getId();
                        if (id == null) { continue; } 
                        strs.add(id);                                        
                    }
                    Assert.assertEquals(10, strs.size());
                    
                    for (int j = 0; j < 10; ++j) {
                        Assert.assertEquals(ids[i][j], strs.get(j));
                    }
                } finally {
                    it.close();
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testToUpdateByIterator() throws Exception {
        PartakeConnection con = pool.getConnection();
        
        try {
            con.beginTransaction();
            
            String prefix = factory.getCommentAccess().getFreshId(con);
            
            String[] ids = new String[10];
            // create 
            for (int i = 0; i < 10; ++i) {
                ids[i] = UUID.randomUUID().toString();
                Comment original = new Comment(ids[i], prefix + "eventId", "userId", "comment content", false, TimeUtil.getCurrentDate());
                factory.getCommentAccess().put(con, original);
            }
            
            // update
            {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                try {
                    while (it.hasNext()) {
                        Comment comment = it.next();
                        
                        Comment updated = new Comment(comment);
                        updated.setComment("New comment!");
                        
                        it.update(updated);
                    }
                } finally {
                    it.close();
                }
            }
            // get them
            {
                DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, prefix + "eventId");
                try {
                    while (it.hasNext()) {
                        Comment comment = it.next();
                        Assert.assertEquals("New comment!", comment.getComment());
                    }
                } finally {
                    it.close();
                }
            }
            
            con.commit();            
        } finally {
            con.invalidate();
        }
    }

    
}
