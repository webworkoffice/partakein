package in.partake.model.dto;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "BinaryData")
@Cacheable(false)
public class BinaryData extends PartakeModel<BinaryData> {
    @Id
    private String id;
    private String userId;
    @Column(length=10000)
    private String type;
    @Column @Lob
    private byte[] data;
    private Date   createdAt;

    public BinaryData() {
        this(null, null, null, null, null);
    }

    public BinaryData(String userId, String type, byte[] data, Date createdAt) {
        this.userId = userId;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
    }

    public BinaryData(String id, String userId, String type, byte[] data, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
    }

    public BinaryData(BinaryData src) {
        this.id = src.id;
        this.userId = src.userId;
        this.type = src.type;
        this.data = src.data;
        this.createdAt = src.createdAt;
    }

    public BinaryData(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.optString("userId");
        this.type = obj.getString("type");
        if (obj.containsKey("createdAt"))
            this.createdAt = new Date(obj.getLong("createdAt"));

        // We don't create data from JSONObject.
    }

    public BinaryData(BinaryData src, boolean deepCopy) {
        this.id = src.id;
        this.type = src.type;
        this.userId = src.userId;
        this.createdAt = src.createdAt;
        if (deepCopy) {
            this.data = src.data != null ? Arrays.copyOf(src.data, src.data.length) : null;
        } else {
            this.data = src.data;
        }
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public BinaryData copy() {
        return new BinaryData(this);
    }

    // ----------------------------------------------------------------------
    // equals / hashCode

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryData)) { return false; }

        BinaryData lhs = this;
        BinaryData rhs = (BinaryData) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id))         { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.type, rhs.type))     { return false; }
        if (!Arrays.equals(lhs.data, rhs.data))          { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;
        code = code * 37 + ObjectUtils.hashCode(id);
        return code;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("userId", userId);
        obj.put("type", type);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());

        return obj;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setType(String type) {
        checkFrozen();
        this.type = type;
    }

    public void setData(byte[] data) {
        checkFrozen();
        this.data = data;
    }
    
    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}
