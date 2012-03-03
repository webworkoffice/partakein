package in.partake.model.dao.postgres9;

import java.util.Date;

public class Postgres9Entity {
    /** UUID */
    private String id;
    /** Version type */
    private int version;
    /** Entity Body */
    private byte[] body;
    /** Optional Body. Will be used in BinaryEntity and CacheEntity. */
    private byte[] opt;
    /** Time index updated at. */
    private Date updatedAt;
    
    public Postgres9Entity(String id, int version, byte[] body, byte[] opt, Date updatedAt) {
        this.id = id;
        this.version = version;
        this.body = body;
        this.opt = opt;
        this.updatedAt = updatedAt;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
