package in.partake.model.dto.auxiliary;

public enum AttendanceStatus {
    UNKNOWN,
    PRESENT,
    ABSENT;
    
    private static final AttendanceStatus SAFE_VALUE = UNKNOWN;
    
    public static AttendanceStatus safeValueOf(String str) {
        if (str == null) { return SAFE_VALUE; }
        if ("".equals(str)) { return SAFE_VALUE; }
        
        if ("present".equalsIgnoreCase(str)) { return PRESENT; }
        if ("absent".equalsIgnoreCase(str))  { return ABSENT; }
        if ("unknown".equalsIgnoreCase(str)) { return UNKNOWN; }
        
        try {
            return valueOf(str);
        } catch (IllegalArgumentException e) {
            return SAFE_VALUE;
        }
    }
    
    public static boolean isValueOf(String str) {
        if ("present".equalsIgnoreCase(str)) { return true; }
        if ("absent".equalsIgnoreCase(str))  { return true; }
        if ("unknown".equalsIgnoreCase(str)) { return true; }

        return false;
    }
}
