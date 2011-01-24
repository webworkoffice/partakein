package in.partake.model.dto;

/**
 * 最後に行われたParticipationStatusの変更内容を表す
 */
public enum LastParticipationStatus {
	/**
	 * 他ユーザのキャンセルなどの要因によって、正式な参加者に昇格した
	 */
    ENROLLED,
    /**
     * 正式な参加者から補欠に降格した
     */
    NOT_ENROLLED,
    /**
     * ユーザの意志でステータスが変更された
     * リマインダを送信する必要はない
     */
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
