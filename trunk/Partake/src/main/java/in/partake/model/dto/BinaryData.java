package in.partake.model.dto;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
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
        this.data = src.data != null ? Arrays.copyOf(src.data, src.data.length) : null;
    }
    
    // ----------------------------------------------------------------------
    // equals / hashCode
    
    // TODO: should be implemented.
    
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
