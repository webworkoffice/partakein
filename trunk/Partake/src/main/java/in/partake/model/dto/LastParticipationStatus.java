package in.partake.model.dto;

public enum LastParticipationStatus {
    ENROLLED,
    NOT_ENROLLED,
    CHANGED;
    
    private static LastParticipationStatus SAFE_VALUE = CHANGED;
    
    public static LastParticipationStatus safeValueOf(String str) {
    	if (str == null) { return SAFE_VALUE; }
    	if ("".equals(str)) { return SAFE_VALUE; }
    	
    	try {
    		return valueOf(str);
    	} catch (IllegalArgumentException e) {
    		return SAFE_VALUE;
    	}
    }
}
