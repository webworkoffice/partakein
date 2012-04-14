package in.partake.model.dto.auxiliary;

public enum UserMessageDelivery {
    NOT_DELIVERY,
    INQUEUE,
    SUCCESS,
    FAIL;

    public static UserMessageDelivery safeValueOf(String str) {
        if ("notDelivery".equalsIgnoreCase(str))
            return NOT_DELIVERY;
        if ("inqueue".equalsIgnoreCase(str))
            return INQUEUE;
        if ("success".equalsIgnoreCase(str))
            return SUCCESS;
        if ("fail".equalsIgnoreCase(str))
            return FAIL;

        return NOT_DELIVERY;
    }
}
