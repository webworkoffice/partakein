package in.partake.model.dao.cassandra;

import in.partake.model.dao.AbstractConnectionTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

public class CassandraKeyColumnDataIteratorTest extends AbstractConnectionTestCaseBase {
    // MASTER TABLE
    private static final String PREFIX_BASE = "ckcdi:id:";
    private static final String KEYSPACE = "Keyspace1";
    private static final String COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CL_W = ConsistencyLevel.ALL;

    @Test
    public void iterateEmpty() throws DAOException {
        Assume.assumeTrue(PartakeProperties.get().usesCassandra());

        String prefix = PREFIX_BASE + "itempty:" + Util.getTimeString(System.currentTimeMillis()) + ":";
        CassandraTableDescription desc = new CassandraTableDescription(prefix, KEYSPACE, COLUMNFAMILY, CL_R, CL_W);
        CassandraConnection con = (CassandraConnection) getPool().getConnection();

        try {
            ColumnOrSuperColumnKeyMapper<Tuple> mapper = new FakeMapper(con);
            con.beginTransaction();
            for (CassandraKeyColumnDataIterator<Tuple> iter = new CassandraKeyColumnDataIterator<Tuple>(con, desc, mapper); iter.hasNext(); ) {
                Assert.fail();
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToIterateOne() throws Exception {
        Assume.assumeTrue(PartakeProperties.get().usesCassandra());

        String prefix = PREFIX_BASE + "itone:" + Util.getTimeString(System.currentTimeMillis()) + ":";
        CassandraTableDescription desc = new CassandraTableDescription(prefix, KEYSPACE, COLUMNFAMILY, CL_R, CL_W);
        CassandraConnection con = (CassandraConnection) getPool().getConnection();
        try {
            Client client = con.getClient();

            String key = prefix + "id";
            {
                List<Mutation> mutations = new ArrayList<Mutation>();
                mutations.add(CassandraDaoUtils.createMutation("name", "value", con.getAcquiredTime()));	        
                client.batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
            }

            int numFound = 0;
            CassandraKeyColumnDataIterator<Tuple> it = new CassandraKeyColumnDataIterator<Tuple>(con, desc, new FakeMapper(con));
            while (it.hasNext()) {
                Tuple t = it.next();
                if (t == null) { continue; }

                Assert.assertEquals("id", t.key);
                Assert.assertEquals("name", t.name);
                Assert.assertEquals("value", t.value);

                ++numFound;
            }

            Assert.assertEquals(1, numFound);
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToIterateMany() throws Exception {
        Assume.assumeTrue(PartakeProperties.get().usesCassandra());

        String prefix = PREFIX_BASE + "itmany:" + Util.getTimeString(System.currentTimeMillis()) + ":";
        CassandraTableDescription desc = new CassandraTableDescription(prefix, KEYSPACE, COLUMNFAMILY, CL_R, CL_W);
        CassandraConnection con = (CassandraConnection) getPool().getConnection();
        try {
            Client client = con.getClient();

            for (int i = 0; i < 10; ++i) {
                String key = prefix + i;
                List<Mutation> mutations = new ArrayList<Mutation>();
                for (int j = 0; j < 10; ++j) {
                    mutations.add(CassandraDaoUtils.createMutation("name" + j, "value" + j, con.getAcquiredTime()));            
                }
                client.batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
            }

            int numFound = 0;
            CassandraKeyColumnDataIterator<Tuple> it = new CassandraKeyColumnDataIterator<Tuple>(con, desc, new FakeMapper(con));
            while (it.hasNext()) {
                Tuple t = it.next();
                if (t == null) { continue; }
                ++numFound;
            }

            Assert.assertEquals(100, numFound);
        } finally {
            con.invalidate();
        }	    
    }

    // ----------------------------------------------------------------------

    private static class Tuple {
        public String key;
        public String name;
        public String value;
    }

    private static class FakeMapper extends ColumnOrSuperColumnKeyMapper<Tuple> {
        public FakeMapper(CassandraConnection connection) {
            super(connection, null);
        }

        @Override
        public Tuple map(ColumnOrSuperColumn cosc, String key) throws DAOException {
            Tuple tuple = new Tuple();
            tuple.key = key;
            tuple.name = string(cosc.getColumn().getName());
            tuple.value = string(cosc.getColumn().getValue());
            return tuple;
        }

        @Override
        public ColumnOrSuperColumn unmap(Tuple t, long time) throws DAOException {
            throw new UnsupportedOperationException();
        }
    }
}
