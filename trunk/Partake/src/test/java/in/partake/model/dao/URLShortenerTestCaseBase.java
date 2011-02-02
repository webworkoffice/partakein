package in.partake.model.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class URLShortenerTestCaseBase extends AbstractDaoTestCaseBase {
    private IURLShortenerAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getURLShortenerAccess());
        dao = getFactory().getURLShortenerAccess();
    }
    
    @Test
    public void testToCreateAndGet() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String shortened = "http://bit.ly/example";
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "bitly", shortened);
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "bitly")); 
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToCreateAndGetLongURL() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String shortened = "http://bit.ly/example";
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "bitly", shortened);
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "bitly")); 
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    public void testToCreateAndGet2() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String shortened = "http://bit.ly/example";
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "bitly", shortened);
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/")); 
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToCreateAndGet3() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String bitlyShortened = "http://bit.ly/example";
            String tcoShortened   = "http://t.co/example";
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "bitly", bitlyShortened);
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "tco", tcoShortened);
            
            Assert.assertEquals(bitlyShortened, factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "bitly")); 
            Assert.assertEquals(tcoShortened,   factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "tco"));
            String shortened = factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/");
            Assert.assertTrue(bitlyShortened.equals(shortened) || tcoShortened.equals(shortened));
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    
    @Test
    public void testToAddRemoveGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            String original  = "http://www.example.com/" + System.currentTimeMillis();
            String shortened = "http://bit.ly/example/" + System.currentTimeMillis();

            {
                con.beginTransaction();
                dao.addShortenedURL(con, original, "bitly", shortened);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeShortenedURL(con, original);
                con.commit();
            }
            
            {
                con.beginTransaction();
                String target = dao.getShortenedURL(con, original);
                con.commit();
                
                Assert.assertNull(target);
            }
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    public void testToAddRemoveAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            String original  = "http://www.example.com/" + System.currentTimeMillis();
            String shortened = "http://bit.ly/example/" + System.currentTimeMillis();

            {
                con.beginTransaction();
                dao.addShortenedURL(con, original, "bitly", shortened);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeShortenedURL(con, original);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.addShortenedURL(con, original, "bitly", shortened);
                con.commit();
            }
            
            {
                con.beginTransaction();
                String target = dao.getShortenedURL(con, original);
                con.commit();
                
                Assert.assertNotNull(target);
                Assert.assertEquals(shortened, target);
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddRemoveAddGet2() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            String original  = "http://www.example.com/" + System.currentTimeMillis();
            String shortened = "http://bit.ly/example/" + System.currentTimeMillis();

            {
                con.beginTransaction();
                dao.addShortenedURL(con, original, "bitly", shortened);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeShortenedURL(con, original, "bitly");
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.addShortenedURL(con, original, "bitly", shortened);
                con.commit();
            }
            
            {
                con.beginTransaction();
                String target = dao.getShortenedURL(con, original);
                con.commit();
                
                Assert.assertNotNull(target);
                Assert.assertEquals(shortened, target);
            }
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    public void testToGetNull() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            String postfix = String.valueOf(System.currentTimeMillis());
            con.beginTransaction();
            Assert.assertNull(dao.getShortenedURL(con, "http://www.example.com/" + postfix, "bitly")); 
            Assert.assertNull(dao.getShortenedURL(con, "http://www.example.com/" + postfix, "tco")); 
            Assert.assertNull(dao.getShortenedURL(con, "http://www.example.com/" + postfix, "google")); 
            con.commit();
        } finally {
            con.invalidate();
        }
    }
}
