package com.cvent.enumdeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Test enum with JsonCreator taking int
 */
@JsonDeserialize(using = EnumDeserializer.class)
public enum TestEnumNoJsonCreatorLong {
    One(1L),
    Two(2L),
    Three(3L);

    private final long value;
    TestEnumNoJsonCreatorLong(long value) {
        this.value = value;
    }
}
