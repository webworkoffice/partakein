package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class CassandraTableDescription {
    public final String prefix;
    public final String keyspace;
    public final String columnFamily;
    public final ConsistencyLevel readConsistency;
    public final ConsistencyLevel writeConsistency;

    public CassandraTableDescription(String prefix, String keyspace, String columnFamily, ConsistencyLevel readConsistency, ConsistencyLevel writeConsistency) {
        this.prefix = prefix;
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
        this.readConsistency = readConsistency;
        this.writeConsistency = writeConsistency;
    }
}
