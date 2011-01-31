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
import org.apache.cassandra.thrift.SuperColumn;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

public class ColumnIteratorTestForSuperColumn extends AbstractDaoTestCaseBase {
    private final String PREFIX = "columniteratortest:supercolumn:id:";
    private static final String KEYSPACE = "Keyspace1";
    private static final String COLUMNFAMILY = "Super1";
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
                    
                    SuperColumn superColumn = cosc.getSuper_column();
                    Assert.assertEquals(s, string(superColumn.getName()));
                    for (Column column : superColumn.getColumns()) {
                        if ("v1".equals(string(column.getName()))) {
                            Assert.assertEquals(s, string(column.getValue()));
                        } else if ("v2".equals(string(column.getName()))) {
                            Assert.assertEquals(s, string(column.getValue()));
                        } else if ("v3".equals(string(column.getName()))) {
                            Assert.assertEquals(s, string(column.getValue()));
                        }
                    }
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
            final int n = 1001;
            
            // create
            String id = PREFIX + UUID.randomUUID().toString();
            createColumns(con, id, n);

            {
                ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
                int i = 0;
                while (it.hasNext()) {
                    String s = String.format("%08d", i++);
                    ColumnOrSuperColumn cosc = it.next();
                    
                    SuperColumn superColumn = cosc.getSuper_column();
                    Assert.assertEquals(s, string(superColumn.getName()));
                    for (Column column : superColumn.getColumns()) {
                        if ("v1".equals(string(column.getName()))) {
                            column.setValue(bytes("v1"));
                        } else if ("v2".equals(string(column.getName()))) {
                            column.setValue(bytes("v2"));
                        } else if ("v3".equals(string(column.getName()))) {
                            column.setValue(bytes("v3"));
                        }
                    }
                    
                    it.update(cosc);
                }
                Assert.assertEquals(n, i);
            }
               
            {
                ColumnIterator it = new ColumnIterator(con, null, KEYSPACE, PREFIX + id, COLUMNFAMILY, false, CL_R, CL_W);
                int i = 0;
                while (it.hasNext()) {
                    String s = String.format("%08d", i++);
                    ColumnOrSuperColumn cosc = it.next();
                    
                    SuperColumn superColumn = cosc.getSuper_column();
                    Assert.assertEquals(s, string(superColumn.getName()));
                    for (Column column : superColumn.getColumns()) {
                        if ("v1".equals(string(column.getName()))) {
                            Assert.assertEquals("v1", string(column.getValue()));
                        } else if ("v2".equals(string(column.getName()))) {
                            Assert.assertEquals("v2", string(column.getValue()));
                        } else if ("v3".equals(string(column.getName()))) {
                            Assert.assertEquals("v3", string(column.getValue()));
                        }
                    }
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
           final int n = 1001;
           
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
            addSuperColumn(con.getClient(), id, s, s, con.getAcquiredTime());
        }
    }
    
    private void addSuperColumn(Client client, String id, String name, String value, long time) throws DAOException {
        try {
            String key = PREFIX + id;
            
            SuperColumn superColumn = new SuperColumn();
            superColumn.setName(bytes(name));
            superColumn.addToColumns(new Column(bytes("v1"), bytes(value), time));
            superColumn.addToColumns(new Column(bytes("v2"), bytes(value), time));
            superColumn.addToColumns(new Column(bytes("v3"), bytes(value), time));
            
            List<Mutation> mutations = new ArrayList<Mutation>();
            mutations.add(CassandraDaoUtils.createSuperColumnMutation(superColumn));
            client.batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}
