package in.partake.util;

import java.io.Serializable;

public class KeyValuePair implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String key;
	public String value;
	public long timestamp;
	
	public KeyValuePair() {
		
	}
	
	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public KeyValuePair(String key, String value, long timestamp) {
		this.key = key;
		this.value = value;
		this.timestamp = timestamp;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValie(String value) {
		this.value = value;
	}
}
