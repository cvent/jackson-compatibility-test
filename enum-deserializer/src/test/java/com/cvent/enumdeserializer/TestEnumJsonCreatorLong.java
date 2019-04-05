package com.cvent.enumdeserializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Test enum with JsonCreator taking int
 */
@JsonDeserialize(using = EnumDeserializer.class)
public enum TestEnumJsonCreatorLong {
    One,
    Two,
    Three;

    private final int decoyField = 1;
    private final int decoyField2 = 2;

    @JsonCreator
    public static TestEnumJsonCreatorLong fromInt(long value) {
        switch ((int) value) {
            case 1: return One;
            case 2: return Two;
            case 3: return Three;
            default: return null;
        }
    }
}
