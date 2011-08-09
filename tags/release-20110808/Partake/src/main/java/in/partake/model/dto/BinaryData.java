package in.partake.model.dto;

import java.util.Arrays;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

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
        this(null, type, data);
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
    
    public BinaryData(BinaryData src, boolean deepCopy) {
        this.id = src.id;
        this.type = src.type;
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
        
        if (!ObjectUtils.equals(lhs.id, rhs.id))     { return false; }
        if (!ObjectUtils.equals(lhs.type, rhs.type)) { return false; }
        if (!Arrays.equals(lhs.data, rhs.data))      { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(type);
        code = code * 37 + ObjectUtils.hashCode(data);
        
        return code;
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
    
    public void setDate(byte[] data) {
        checkFrozen();
        this.data = data;
    }
}
