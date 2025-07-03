package de.l.codefor.wateringsync.to;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum WaterType {

    RAINWATER(1),
    SERVICEWATER(2),
    TAPWATER(3),
    RIVERWATER(4),
    OTHER_WATER(5),
    NOT_SPECIFIED(6);

    private final int code;

    WaterType(int code) {
        this.code = code;
    }

    static WaterType getByCode(int code) {
        return Arrays.stream(WaterType.values()).filter(wt -> wt.code == code).findAny().orElse(null);
    }

    @JsonValue
    public int toValue() {
        return this.code;
    }
}
