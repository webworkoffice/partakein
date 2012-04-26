package in.partake.model.dto.auxiliary;

public enum EnqueteAnswerType {
    TEXT("text"),
    TEXTAREA("textArea"),
    DATETIME("datetime"),
    CHECKBOX("checkbox"),
    RADIOBUTTON("radiobutton");

    private String value;

    private EnqueteAnswerType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static EnqueteAnswerType safeValueOf(String v) {
        for (EnqueteAnswerType type : values()) {
            if (type.toString().equalsIgnoreCase(v))
                return type;
        }

        return TEXT;
    }
}
