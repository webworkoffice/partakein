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

public abstract class CommentAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getCommentAccess());
    }
    
    @Test
    public void testToAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            String commentId = factory.getCommentAccess().getFreshId(con);
            
            Comment original = new Comment(commentId, "eventId", "userId", "comment content", new Date());
            factory.getCommentAccess().addComment(con, original);
            
            Comment target = factory.getCommentAccess().getComment(con, commentId);
            con.commit();
            
            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertFalse(original.isFrozen());
            Assert.assertEquals(original, target);
            Assert.assertNotSame(original, target);

        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddGetLongComment() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 1024 * 1024; ++i) {
                builder.append((char)('A' + (i % 26)));
            }
            
            con.beginTransaction();
            String commentId = factory.getCommentAccess().getFreshId(con);
            Comment original = new Comment(commentId, "eventId", "userId", builder.toString(), new Date());
            factory.getCommentAccess().addComment(con, original);
            
            Comment target = factory.getCommentAccess().getComment(con, commentId);
            con.commit();
            
            Assert.assertTrue(target.isFrozen());
            Assert.assertEquals(original, target);
        } finally {
            con.invalidate();
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void testToAddWithoutId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            Comment original = new Comment(null, "eventId", "userId", "comment content", new Date());
            factory.getCommentAccess().addComment(con, original);
            con.commit();
            
            Assert.fail();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToAddDeleteGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            String commentId; 
            Comment original = new Comment("eventId", "userId", "comment content", new Date());
            
            {
                con.beginTransaction();
                commentId = factory.getCommentAccess().getFreshId(con);
                original.setId(commentId);
                
                factory.getCommentAccess().addComment(con, original);
                con.commit();
            }

            {
                con.beginTransaction();
                factory.getCommentAccess().removeComment(con, commentId);
                con.commit();
            }

            Comment target;
            {
                con.beginTransaction();
                target = factory.getCommentAccess().getComment(con, commentId);
                con.commit();
            }

            Assert.assertNull(target);
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToAddDeleteAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            String commentId;
            Comment original = new Comment("eventId", "userId", "comment content", new Date());
            {
                con.beginTransaction();
                commentId = factory.getCommentAccess().getFreshId(con);
                original.setId(commentId);
                
                factory.getCommentAccess().addComment(con, original);
                
                con.commit();
            }

            {
                con.beginTransaction();
                factory.getCommentAccess().removeComment(con, commentId);
                con.commit();
            }

            {
                con.beginTransaction();
                factory.getCommentAccess().addComment(con, original);
                con.commit();
            }

            Comment target;
            {
                con.beginTransaction();
                target = factory.getCommentAccess().getComment(con, commentId);
                con.commit();
            }

            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertEquals(original, target);
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToGetByEventId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            String prefix = factory.getCommentAccess().getFreshId(con);
            
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    Comment original = new Comment(prefix + "commentId-" + i + "-" + j, prefix + "eventId" + i, "userId", "comment content", PDate.getCurrentDate().getDate());
                    factory.getCommentAccess().addComment(con, original);
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
                factory.getCommentAccess().addComment(con, original);
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
