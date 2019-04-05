package com.cvent.enumdeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Test enum with no JsonCreator
 */
@JsonDeserialize(using = EnumDeserializer.class)
public enum TestEnumNoJsonCreator {
    One(1),
    Two(2),
    Three(3);

    private final int value;
    TestEnumNoJsonCreator(int value) {
        this.value = value;
    }
}
