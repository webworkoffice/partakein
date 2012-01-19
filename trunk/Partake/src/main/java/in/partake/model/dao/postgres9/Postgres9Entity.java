package in.partake.model.dao.postgres9;

import java.util.Date;

public class Postgres9Entity {
    private String id;           // UUID
    private String schema;       // schema
    private String data;         // Data
    private Date createdAt;      // time created at
    private Date modifiedAt;     // time modified at
    
    public Postgres9Entity(String id, String schema, String data, Date createdAt) {
        this(id, schema, data, createdAt, null);
    }
    
    public Postgres9Entity(String id, String schema, String data, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.schema = schema;
        this.data = data;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public String getSchema() {
        return schema;
    }
    
    public String getData() {
        return data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getModifiedAt() {
        return modifiedAt;
    }
}
