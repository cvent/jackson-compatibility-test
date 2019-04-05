package com.cvent.enumdeserializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;

/**
 * Test enum with JsonCreator taking string
 */
@JsonDeserialize(using = EnumDeserializer.class)
public enum TestEnumJsonCreatorString {
    One(1),
    Two(2),
    Three(3);

    private final int value;
    TestEnumJsonCreatorString(int value) {
        this.value = value;
    }

    @JsonCreator
    public static TestEnumJsonCreatorString fromString(String value) {
        return Arrays.stream(values()).filter(v -> v.name().equals(value)).findFirst().orElse(null);
    }
}
