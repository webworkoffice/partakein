package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

public class ColumnIteratorTestForColumn extends AbstractDaoTestCaseBase {
    private final String PREFIX = "columniteratortest:id:";
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
    public void testForColumnIteratorToGetSmall() throws DAOException {
        testForColumnIteratorToGet(0);
        testForColumnIteratorToGet(1);
        testForColumnIteratorToGet(100);
        testForColumnIteratorToGet(999);
        testForColumnIteratorToGet(1000);
        testForColumnIteratorToGet(1001);
    }
    
    @Test
    public void testForColumnIteratorToGetLarge() throws DAOException {
        testForColumnIteratorToGet(10001);
    }
    
    private void testForColumnIteratorToGet(int n) throws DAOException {
        CassandraConnection con = (CassandraConnection) getPool().getConnection();
        try {
            // create
            String id = PREFIX + UUID.randomUUID().toString();
            createColumns(con, id, n);
            
            // get
            {
                ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
                int i = 0;
                while (it.hasNext()) {
                    String s = String.format("%08d", i++);
                    ColumnOrSuperColumn cosc = it.next();
                    Column column = cosc.getColumn();
                    Assert.assertEquals(s, string(column.getName()));
                    Assert.assertEquals(s, string(column.getValue()));
                }
                Assert.assertEquals(n, i);
            }
        }  finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testForColumnIteratorToUpdate() throws DAOException {
        CassandraConnection con = (CassandraConnection) getPool().getConnection();
        try {
            final int n = 100;
            
            // create
            String id = PREFIX + UUID.randomUUID().toString();
            createColumns(con, id, n);

            {
                ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
                int i = 0;
                while (it.hasNext()) {
                    String s = String.format("%08d", i++);
                    ColumnOrSuperColumn cosc = it.next();
                    Column column = cosc.getColumn();
                    Assert.assertEquals(s, string(column.getName()));
                    Assert.assertEquals(s, string(column.getValue()));
                    
                    String t = String.format("%08d", i);
                    column.setValue(bytes(t));
                    it.update(cosc);
                }
                Assert.assertEquals(n, i);
            }
               
            {
                ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
                int i = 0;
                while (it.hasNext()) {
                    String s = String.format("%08d", i);
                    String t = String.format("%08d", i + 1);
                    ++i;
                    
                    ColumnOrSuperColumn cosc = it.next();
                    Column column = cosc.getColumn();
                    Assert.assertEquals(s, string(column.getName()));
                    Assert.assertEquals(t, string(column.getValue()));
                }
                Assert.assertEquals(n, i);
            }
            
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testForColumnIteratorToRemove() throws DAOException {
        CassandraConnection con = (CassandraConnection) getPool().getConnection();    
       try {
           final int n = 100;
           
           // create
           String id = PREFIX + UUID.randomUUID().toString();
           createColumns(con, id, n);

           {
               ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
               int i = 0;
               while (it.hasNext()) {
                   it.next();
                   it.remove();
                   ++i;
               }
               Assert.assertEquals(n, i);
           }
              
           {
               ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
               int i = 0;
               while (it.hasNext()) {
                   it.next();
                   ++i;
               }
               Assert.assertEquals(0, i);
           }
        } finally {
            con.invalidate();
        }
    }

    private void createColumns(CassandraConnection con, String id, int n) throws DAOException {
        for (int i = 0; i < n; ++i) {
            String s = String.format("%08d", i);
            addColumn(con.getClient(), id, s, s, con.getAcquiredTime());
        }
    }
    
    private void addColumn(Client client, String id, String name, String value, long time) throws DAOException {
        try {
            String key = PREFIX + id;
            List<Mutation> mutations = new ArrayList<Mutation>(); 
            mutations.add(CassandraDaoUtils.createMutation(name, value, time));
            client.batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}
