package in.partake.model.dto.auxiliary;

public enum MessageDelivery {
    NOT_DELIVERED, // Not queued.
    INQUEUE,       // In queue
    SUCCESS,       // Succeeded sending.
    FAIL;          // Failed sending.

    public static MessageDelivery safeValueOf(String str) {
        if ("notDelivery".equalsIgnoreCase(str))
            return NOT_DELIVERED;
        if ("inqueue".equalsIgnoreCase(str))
            return INQUEUE;
        if ("success".equalsIgnoreCase(str))
            return SUCCESS;
        if ("fail".equalsIgnoreCase(str))
            return FAIL;

        return NOT_DELIVERED;
    }
}
