package in.partake.model.dao.postgres9;

import java.util.Date;

public class Postgres9Entity {
    /** UUID */
    private String id;
    /** Schema type */
    private String schema;
    /** Entity Body */
    private byte[] body;
    /** Optional Body */
    private byte[] opt;
    /** Time created at */
    private Date createdAt;
    /** Time modified at */
    private Date modifiedAt;
    
    public Postgres9Entity(String id, String schema, byte[] body, byte[] opt, Date createdAt) {
        this(id, schema, body, opt, createdAt, null);
    }
    
    public Postgres9Entity(String id, String schema, byte[] body, byte[] opt, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.schema = schema;
        this.body = body;
        this.opt = opt;
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

    public byte[] getOpt() {
        return opt;
    }

    public int getOptLength() {
        return opt.length;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getModifiedAt() {
        return modifiedAt;
    }
}
