package in.partake.model.dto.auxiliary;

public enum EnqueteAnswerType {
    TEXT,
    DATETIME,
    CHECKBOX,
    RADIOBUTTON;

    public static EnqueteAnswerType safeValueOf(String v) {
        if ("text".equalsIgnoreCase(v))
            return TEXT;
        if ("datetime".equalsIgnoreCase(v))
            return DATETIME;
        if ("checkbox".equalsIgnoreCase(v))
            return CHECKBOX;
        if ("radiobutton".equalsIgnoreCase(v))
            return RADIOBUTTON;

        return TEXT;
    }
}
