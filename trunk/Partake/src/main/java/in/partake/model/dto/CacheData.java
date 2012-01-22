package in.partake.model.dto;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "CacheData")
public class CacheData extends PartakeModel<CacheData> {
    @Id
    private String id;
    @Column @Lob
    private byte[] data;
    @Column
    private Date   invalidAfter;
    
    public CacheData() {
        
    }
    
    public CacheData(String id, byte[] data, Date invalidAfter) {
        this.id = id;
        this.data = data;
        this.invalidAfter = invalidAfter;
    }
    
    public CacheData(String id, byte[] data, JSONObject obj) {
        this.id = id;
        this.data = data;
        if (obj.containsKey("invalidAfter"))
            this.invalidAfter = new Date(obj.getLong("invalidAfter"));
    }
    
    public CacheData(CacheData cacheData) {
        this.id = cacheData.id;
        if (cacheData.data != null) {
            this.data = Arrays.copyOf(cacheData.data, cacheData.data.length);
        } else {
            this.data = null;
        }
        if (cacheData.invalidAfter != null) {
            this.invalidAfter = (Date) cacheData.invalidAfter.clone();
        } else {
            this.invalidAfter = null;
        }
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    @Override
    public CacheData copy() {
        return new CacheData(this);
    }
    
    public JSONObject toJSONWithoutData() {
        JSONObject obj = new JSONObject();
        obj.put("invalidAfter", invalidAfter.getTime()); 
        return obj;
    }
    
    // ----------------------------------------------------------------------
    // equals
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CacheData)) { return false; }
        
        CacheData lhs = this;
        CacheData rhs = (CacheData) obj;
        
        return ObjectUtils.equals(lhs.id, rhs.id) &&
            Arrays.equals(lhs.data, rhs.data) &&
            ObjectUtils.equals(lhs.invalidAfter, rhs.invalidAfter);
    }
    
    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(id) * 37 * 37 +
            ObjectUtils.hashCode(data) * 37 +
            ObjectUtils.hashCode(invalidAfter);
    }
    
    
    // ----------------------------------------------------------------------
    // accessors
    
    public String getId() {
        return this.id;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public Date getInvalidAfter() {
        return invalidAfter;
    }
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }
    
    public void setData(byte[] data) {
        checkFrozen();
        this.data = data; // TODO: deep copy すべきか、そうでないか。
    }
    
    public void setInvalidAfter(Date date) {
        checkFrozen();
        if (date != null) {
            this.invalidAfter = (Date) date.clone();
        } else {
            this.invalidAfter = null;
        }
    }
}
