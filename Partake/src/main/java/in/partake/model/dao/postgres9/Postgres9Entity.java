package in.partake.model.dao.postgres9;

import java.util.Date;

public class Postgres9Entity {
    /** UUID */
    private String id;
    /** Schema type */
    private String schema;
    /** Entity Body */
    private byte[] body;
    /** Time created at */
    private Date createdAt;
    /** Time modified at */
    private Date modifiedAt;
    
    public Postgres9Entity(String id, String schema, byte[] body, Date createdAt) {
        this(id, schema, body, createdAt, null);
    }
    
    public Postgres9Entity(String id, String schema, byte[] body, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.schema = schema;
        this.body = body;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public String getSchema() {
        return schema;
    }
    
    public byte[] getBody() {
        return body;
    }
    
    public int getBodyLength() {
        return body.length;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getModifiedAt() {
        return modifiedAt;
    }
}
