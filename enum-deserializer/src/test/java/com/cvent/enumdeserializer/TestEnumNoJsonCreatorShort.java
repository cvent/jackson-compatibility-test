package com.cvent.enumdeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Test enum with JsonCreator taking int
 */
@JsonDeserialize(using = EnumDeserializer.class)
public enum TestEnumNoJsonCreatorShort {
    One((short) 1),
    Two((short) 2),
    Three((short) 3);

    private final short value;
    TestEnumNoJsonCreatorShort(short value) {
        this.value = value;
    }
}
