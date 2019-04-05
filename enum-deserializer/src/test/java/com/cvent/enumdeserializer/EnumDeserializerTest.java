package com.cvent.enumdeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * A unit test for EnumDeserializer
 *
 * @author dholsopple@cvent.com
 */
@RunWith(Parameterized.class)
public class EnumDeserializerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {TestEnumJsonCreatorInt.class, TestEnumJsonCreatorInt.One},
                {TestEnumJsonCreatorString.class, TestEnumJsonCreatorString.One},
                {TestEnumNoJsonCreator.class, TestEnumNoJsonCreator.One},
                {TestEnumJsonCreatorShort.class, TestEnumJsonCreatorShort.One},
                {TestEnumNoJsonCreatorShort.class, TestEnumNoJsonCreatorShort.One},
                {TestEnumNoJsonCreatorLong.class, TestEnumNoJsonCreatorLong.One}
        });
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<?> enumClass;
    private final Object enumOneVariant;

    public EnumDeserializerTest(Class<?> enumClass, Object enumOneVariant) {
        this.enumClass = enumClass;
        this.enumOneVariant = enumOneVariant;
    }

    @Test
    public void testDeserializeFromInt() throws Exception {
        assertEquals(this.enumOneVariant, OBJECT_MAPPER.readValue("1", this.enumClass));
    }

    @Test
    public void testDeserializeFromIntString() throws Exception {
        assertEquals(this.enumOneVariant, OBJECT_MAPPER.readValue("\"1\"", this.enumClass));
    }

    @Test
    public void testDeserializeFromName() throws Exception {
        assertEquals(this.enumOneVariant, OBJECT_MAPPER.readValue("\"One\"", this.enumClass));
    }

    @Test
    public void testDeserializeFromNull() throws Exception {
        assertNull(OBJECT_MAPPER.readValue("null", this.enumClass));
    }
}
