package in.partake.model.dao.postgres9;

import java.util.Date;

public class Postgres9Entity {
    /** UUID */
    private String id;
    /** Version type */
    private int version;
    /** Entity Body */
    private byte[] body;
    /** Optional Body. Will be used in BinaryEntity. */
    private byte[] opt;
    /** Time created at */
    private Date createdAt;
    /** Time modified at */
    private Date modifiedAt;
    
    public Postgres9Entity(String id, int version, byte[] body, byte[] opt, Date createdAt) {
        this(id, version, body, opt, createdAt, null);
    }
    
    public Postgres9Entity(String id, int version, byte[] body, byte[] opt, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.version = version;
        this.body = body;
        this.opt = opt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
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
