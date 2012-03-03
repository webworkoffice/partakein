package in.partake.model.dao;

import java.util.HashSet;
import java.util.Set;

import in.partake.base.TimeUtil;
import in.partake.model.dao.access.IAccess;
import in.partake.model.dto.PartakeModel;
import in.partake.service.DBService;
import in.partake.service.TestDatabaseService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Dao のテストケースのベース。
 * 
 * @author shinyak
 *
 */
public abstract class AbstractDaoTestCaseBase<DAO extends IAccess<T, PK>, T extends PartakeModel<T>, PK> extends AbstractConnectionTestCaseBase {
    protected DAO dao;
    
    @Deprecated
    protected PartakeConnectionPool getPool() {
        return DBService.getPool();
    }
    
    @Deprecated
    protected PartakeDAOFactory getFactory() {
        return DBService.getFactory();
    }
    
    // setup() should be implemented in each test case.
    @Before
    protected abstract void setup() throws DAOException;
    
    protected void setup(DAO dao) throws DAOException {
        // remove the current data
        TimeUtil.resetCurrentDate();
        this.dao = dao;
        
        if (dao == null)
            return;
        
        // truncate all data.
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            dao.truncate(con);
            con.commit();
        } finally {
            con.invalidate();
        }        
    }
    
    // 同じ (pkNumber, pkSalt) なら同じ結果を返すようにする。
    // TODO: We should use TestDataProvider instead.
    protected abstract T create(long pkNumber, String pkSalt, int objNumber);
    
    // ------------------------------------------------------------
    
    @Test
    public final void testToCreate() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                T t1 = create(0, "create", 0);
                T t2 = create(i, "create", j);
                
                if (i == 0 && j == 0) {
                    Assert.assertEquals(t1, t2);
                } else {
                    Assert.assertFalse(t1.equals(t2));
                }
                
                if (i == 0) {
                    Assert.assertEquals(t1.getPrimaryKey(), t2.getPrimaryKey());
                } else {
                    Assert.assertFalse(t1.getPrimaryKey().equals(t2.getPrimaryKey()));
                }
            }
        }
    }
    
    
    
    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutFind() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            con.beginTransaction();
            T t1 = create(System.currentTimeMillis(), "putfind", 0);
            dao.put(con, t1);
            con.commit();
            
            con.beginTransaction();
            T t2 = dao.find(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            Assert.assertEquals(t1, t2);
            Assert.assertNotSame(t1, t2);
            Assert.assertFalse(t1.isFrozen());
            Assert.assertTrue(t2.isFrozen());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutFindInTransaction() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            con.beginTransaction();
            T t1 = create(System.currentTimeMillis(), "putfindintran", 0);
            dao.put(con, t1);

            T t2 = dao.find(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            Assert.assertEquals(t1, t2);     
            Assert.assertFalse(t1.isFrozen());
            Assert.assertTrue(t2.isFrozen());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutPutFind() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            long time = System.currentTimeMillis();
            
            con.beginTransaction();
            T t1 = create(time, "putputfind", 0); 
            dao.put(con, t1);
            con.commit();

            TimeUtil.waitForTick();
            
            con.beginTransaction();
            T t2 = create(time, "putputfind", 1); 
            dao.put(con, t2);
            con.commit();
            
            Assert.assertEquals(t1.getPrimaryKey(), t2.getPrimaryKey());

            con.beginTransaction();
            T t3 = dao.find(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            Assert.assertFalse(t1.equals(t3));
            Assert.assertEquals(t2, t3);            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutRemoveFind() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            TimeUtil.waitForTick();
            
            con.beginTransaction();
            T t1 = create(System.currentTimeMillis(), "putremovefind", 0); 
            dao.put(con, t1);
            con.commit();
            
            TimeUtil.waitForTick();
            
            con.beginTransaction();
            dao.remove(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            T t2 = dao.find(con, (PK) t1.getPrimaryKey());
            Assert.assertNull(t2);            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public final void testToPutRemovePutFind() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            con.beginTransaction();
            T t1 = create(System.currentTimeMillis(), "putremovefind", 0); 
            dao.put(con, t1);
            con.commit();
            
            TimeUtil.waitForTick();
            
            con.beginTransaction();
            dao.remove(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            TimeUtil.waitForTick();

            con.beginTransaction();
            dao.put(con, t1);
            con.commit();

            T t2 = dao.find(con, (PK) t1.getPrimaryKey());
            
            Assert.assertEquals(t1, t2);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    @SuppressWarnings("unchecked")        
    public final void testToRemoveInvalidObject() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            T t1 = create(System.currentTimeMillis(), "removeinvalid", 0);
            
            con.beginTransaction();
            dao.remove(con, (PK) t1.getPrimaryKey());
            con.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }        
    }

    @Test
    @SuppressWarnings("unchecked")    
    public final void testToFindWithInvalidId() throws Exception {
        PartakeConnection con = getPool().getConnection();

        try {
            T t1 = create(System.currentTimeMillis(), "findInvalid", 0);
            
            con.beginTransaction();
            T t = dao.find(con, (PK) t1.getPrimaryKey());
            con.commit();
            
            Assert.assertNull(t);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }                
    }

    @Test
    public final void testToIterate() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            Set<T> created = new HashSet<T>();
            for (int i = 0; i < 3; ++i) {
                T t = create(System.currentTimeMillis(), String.valueOf(i), i);
                created.add(t);
                
                con.beginTransaction();
                dao.put(con, t);
                con.commit();
            }
            
            int count = 0;
            DataIterator<T> it = dao.getIterator(con);
            try {
                while (it.hasNext()) {
                    T t = it.next();
                    if (t == null) { continue; }
                    ++count;
                    Assert.assertTrue(created.contains(t));
                }
            } finally {
                it.close();
            }
            Assert.assertEquals(3, count);
        } finally {
            con.invalidate();
        }
    }
    

}
