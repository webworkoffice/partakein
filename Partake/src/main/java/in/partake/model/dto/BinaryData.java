package in.partake.model.dto;

import java.util.Arrays;

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
    @Column(length=10000)
    private String type;
    @Column @Lob
    private byte[] data;

    public BinaryData() {
        this(null, null, null);
    }

    public BinaryData(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public BinaryData(String id, String type, byte[] data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public BinaryData(BinaryData src) {
        this.id = src.id;
        this.type = src.type;
        this.data = src.data;
    }

    public BinaryData(JSONObject obj) {
        this.id = obj.getString("id");
        this.type = obj.getString("type");

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
        if (!(obj instanceof BinaryData)) { return false; }

        BinaryData lhs = this;
        BinaryData rhs = (BinaryData) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id))         { return false; }
        if (!ObjectUtils.equals(lhs.type, rhs.type))     { return false; }
        if (!Arrays.equals(lhs.data, rhs.data))          { return false; }

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
        obj.put("type", type);
        return obj;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setType(String type) {
        checkFrozen();
        this.type = type;
    }

    public void setData(byte[] data) {
        checkFrozen();
        this.data = data;
    }
}
