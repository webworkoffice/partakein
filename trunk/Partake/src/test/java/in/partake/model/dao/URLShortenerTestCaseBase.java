package in.partake.model.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class URLShortenerTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getURLShortenerAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
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
            factory.getURLShortenerAccess().addShortenedURL(con, "http://www.example.com/", "bitly", tcoShortened);
            
            Assert.assertEquals(bitlyShortened, factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "bitly")); 
            Assert.assertEquals(tcoShortened,   factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/", "tco"));
            String shortened = factory.getURLShortenerAccess().getShortenedURL(con, "http://www.example.com/");
            Assert.assertTrue(bitlyShortened.equals(shortened) || tcoShortened.equals(shortened));
            con.commit();
        } finally {
            con.invalidate();
        }
    }
}
