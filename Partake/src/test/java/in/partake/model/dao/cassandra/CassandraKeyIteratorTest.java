package in.partake.model.dao.cassandra;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraKeyIteratorTest extends AbstractDaoTestCaseBase {
    private final String PREFIX = "keyiteratortest:id:";
    private static final String KEYSPACE = "Keyspace1";
    private static final String COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CL_W = ConsistencyLevel.ALL;
    
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("cassandra");
        reset();
    }

    @AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        reset();
    }
    
    @Before
    public void setup() throws DAOException {
        super.setup(null);
    }
    
    @Test
    public void testToIterateSmall() throws DAOException {
        testToIterate(0);
        testToIterate(1);
        testToIterate(100);
    }
    
    @Test
    public void testToIterateLarge() throws DAOException {
        testToIterate(999);
        testToIterate(1000);
        testToIterate(1001);
        testToIterate(10001);
    }
    
    private void testToIterate(int n) throws DAOException {
        CassandraConnection con = (CassandraConnection) getPool().getConnection();
        String prefix = PREFIX + UUID.randomUUID().toString();
        
        try {
            // create keys
            createKeys(con, prefix, n);
            
            // iterate
            CassandraKeyIterator it = new CassandraKeyIterator(con, KEYSPACE, prefix, COLUMNFAMILY, CL_R);
            int i = 0;
            while (it.hasNext()) {
                String s = String.format("%08d", i++);
                String t = it.next();
                Assert.assertEquals(s, t);
            }
            Assert.assertEquals(n, i);
        } finally {
            con.invalidate();
        }
    }

    private void createKeys(CassandraConnection con, String prefix, int n) throws DAOException {
        try {
            for (int i = 0; i < n; ++i) {
                String key = prefix + String.format("%08d", i);
                List<Mutation> mutations = new ArrayList<Mutation>(); 
                mutations.add(CassandraDaoUtils.createMutation("key", "value", con.getAcquiredTime()));
                con.getClient().batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

}
