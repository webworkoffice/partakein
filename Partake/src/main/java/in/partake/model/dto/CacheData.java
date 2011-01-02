package in.partake.model.dto;

import java.util.Arrays;
import java.util.Date;

public class CacheData extends PartakeModel<CacheData> {
    private String id;
    private byte[] data;
    private Date   invalidAfter;
    
    public CacheData() {
        
    }
    
    public CacheData(String id, byte[] data, Date invalidAfter) {
        this.id = id;
        this.data = data;
        this.invalidAfter = invalidAfter;
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
