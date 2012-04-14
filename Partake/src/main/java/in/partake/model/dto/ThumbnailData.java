package in.partake.model.dto;

import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class ThumbnailData extends PartakeModel<ThumbnailData> {
    private String id;
    private String userId;
    private String type;
    private byte[] data;
    private Date   createdAt;

    public ThumbnailData(String id, String userId, String type, byte[] data, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
    }

    public ThumbnailData(ThumbnailData src) {
        this(src.id, src.userId, src.type, src.data, src.createdAt);
    }

    public ThumbnailData(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.optString("userId");
        this.type = obj.getString("type");
        if (obj.containsKey("createdAt"))
            this.createdAt = new Date(obj.getLong("createdAt"));

        // We don't create data from JSONObject.
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    // ----------------------------------------------------------------------
    // equals / hashCode

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ThumbnailData)) { return false; }

        ThumbnailData lhs = this;
        ThumbnailData rhs = (ThumbnailData) obj;

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
