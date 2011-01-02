package in.partake.model.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BinaryData extends PartakeModel<BinaryData> {
    @Id
    private String id;
    
    private String type;
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
