package in.partake.model.dto.auxiliary;

public enum TicketPriceType {
    FREE,
    NONFREE;

    public static TicketPriceType safeValueOf(String key) {
        if ("free".equalsIgnoreCase(key))
            return FREE;
        if ("nonfree".equalsIgnoreCase(key))
            return NONFREE;
        return FREE;
    }
}
