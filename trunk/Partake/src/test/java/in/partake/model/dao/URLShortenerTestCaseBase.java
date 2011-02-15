package in.partake.model.dao;

import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class URLShortenerTestCaseBase extends AbstractDaoTestCaseBase<IURLShortenerAccess, ShortenedURLData, ShortenedURLDataPK> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getURLShortenerAccess());
    }
    
    @Override
    protected ShortenedURLData create(long pkNumber, String pkSalt, int objNumber) {
        return new ShortenedURLData("http://www.example.com/" + pkSalt + pkNumber, "bitly", "http://examp.le/" + objNumber);
    }
    
    @Test
    public void testToCreateAndGet() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String shortened = "http://bit.ly/example";
            factory.getURLShortenerAccess().put(con, new ShortenedURLData("http://www.example.com/", "bitly", shortened));
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().find(con, new ShortenedURLDataPK("http://www.example.com/", "bitly")).getShortenedURL()); 
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
            factory.getURLShortenerAccess().put(con, new ShortenedURLData("http://www.example.com/", "bitly", shortened));
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().find(con, new ShortenedURLDataPK("http://www.example.com/", "bitly")).getShortenedURL()); 
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
            factory.getURLShortenerAccess().put(con, new ShortenedURLData("http://www.example.com/", "bitly", shortened));
            Assert.assertEquals(shortened, factory.getURLShortenerAccess().findByURL(con, "http://www.example.com/").getShortenedURL()); 
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
            factory.getURLShortenerAccess().put(con, new ShortenedURLData("http://www.example.com/", "bitly", bitlyShortened));
            factory.getURLShortenerAccess().put(con, new ShortenedURLData("http://www.example.com/", "tco", tcoShortened));
            
            Assert.assertEquals(bitlyShortened, factory.getURLShortenerAccess().find(con, new ShortenedURLDataPK("http://www.example.com/", "bitly")).getShortenedURL()); 
            Assert.assertEquals(tcoShortened,   factory.getURLShortenerAccess().find(con, new ShortenedURLDataPK("http://www.example.com/", "tco")).getShortenedURL());
            ShortenedURLData shortened = factory.getURLShortenerAccess().findByURL(con, "http://www.example.com/");
            Assert.assertTrue(bitlyShortened.equals(shortened.getShortenedURL()) || tcoShortened.equals(shortened.getShortenedURL()));
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
                dao.put(con, new ShortenedURLData(original, "bitly", shortened));
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeByURL(con, original);
                con.commit();
            }
            
            {
                con.beginTransaction();
                ShortenedURLData target = dao.findByURL(con, original);
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
                dao.put(con, new ShortenedURLData(original, "bitly", shortened));
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                dao.removeByURL(con, original);
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                dao.put(con, new ShortenedURLData(original, "bitly", shortened));
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                ShortenedURLData target = dao.findByURL(con, original);
                con.commit();
                
                Assert.assertNotNull(target);
                Assert.assertEquals(shortened, target.getShortenedURL());
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
                dao.put(con, new ShortenedURLData(original, "bitly", shortened));
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                dao.remove(con, new ShortenedURLDataPK(original, "bitly"));
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                dao.put(con, new ShortenedURLData(original, "bitly", shortened));
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                String target = dao.findByURL(con, original).getShortenedURL();
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
            Assert.assertNull(dao.find(con, new ShortenedURLDataPK("http://www.example.com/" + postfix, "bitly"))); 
            Assert.assertNull(dao.find(con, new ShortenedURLDataPK("http://www.example.com/" + postfix, "tco"))); 
            Assert.assertNull(dao.find(con, new ShortenedURLDataPK("http://www.example.com/" + postfix, "google"))); 
            con.commit();
        } finally {
            con.invalidate();
        }
    }
}
