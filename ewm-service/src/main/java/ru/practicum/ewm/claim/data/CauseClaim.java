package ru.practicum.ewm.claim.data;

public enum CauseClaim {

    BEHAVIOR("BEHAVIOR"),
    CONTENT("CONTENT"),
    FAKE("FAKE"),
    PERSONAL_DATA("PERSONAL_DATA"),
    SPAM("SPAM");
    private final String value;

    CauseClaim(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static CauseClaim getEnum(String value) {
        for (CauseClaim v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
