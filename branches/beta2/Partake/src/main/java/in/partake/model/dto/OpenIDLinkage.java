package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "OpenIDLinkages")
public class OpenIDLinkage extends PartakeModel<OpenIDLinkage> {
    @Id @Column(length = 4096)
    private String id;
    @Column
    private String userId;

    public OpenIDLinkage() {

    }

    public OpenIDLinkage(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public OpenIDLinkage(OpenIDLinkage linkage) {
        this.id = linkage.id;
        this.userId = linkage.userId;
    }

    public OpenIDLinkage(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.getString("userId");
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("userId", userId);

        return obj;
    }

    // -----------------------------------------------------------------------------
    //

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OpenIDLinkage)) { return false; }

        OpenIDLinkage lhs = this;
        OpenIDLinkage rhs = (OpenIDLinkage) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(userId);

        return code;
    }

    // -----------------------------------------------------------------------------
    //

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }
}
