package in.partake.model.dto.auxiliary;

public enum ParticipationStatus {
	ENROLLED() { // 参加します
		public boolean isEnrolled() { return true; }
	},		
	RESERVED() { // 多分参加します(仮登録)
		public boolean isEnrolled() { return true; }
	},		
	CANCELLED() { // 参加をキャンセルします
		public boolean isEnrolled() { return false; }
	},
	NOT_ENROLLED() { // そもそも参加をしていません
		public boolean isEnrolled() { return false; }
	}
	;		
	
    private static ParticipationStatus SAFE_VALUE = NOT_ENROLLED;
    
    public static ParticipationStatus safeValueOf(String str) {
    	if (str == null) { return SAFE_VALUE; }
    	if ("".equals(str)) { return SAFE_VALUE; }
    	
    	try {
    		return valueOf(str.toUpperCase());
    	} catch (IllegalArgumentException e) {
    		return SAFE_VALUE;
    	}
    }
	
    // ----------------------------------------------------------------------
	public abstract boolean isEnrolled();	
}


