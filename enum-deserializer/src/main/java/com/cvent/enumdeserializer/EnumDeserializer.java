package com.cvent.enumdeserializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Deserializer for doing hacky things to get enums to deserialize from a bunch of different formats using a bunch of
 * different jackson versions
 */
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    private static final Logger LOG = LoggerFactory.getLogger(EnumDeserializer.class);

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // should never run because createContextual returns a different deserializer with information about the
        // specific property/class being deserialized
        throw new UnsupportedOperationException(
                "this deserializer can only run in contextual mode (see createContextual method) because it needs " +
                        "to inspect the enum that it will deserialize to"
        );
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        if (property != null) {
            // Determine type based on the type of the property being deserialized
            return new EnumDeserializerImpl(property.getType().getRawClass());
        }
        try {
            // Root-level object, can only determine the type in jackson-databind>=2.5
            return new EnumDeserializerImpl(ctxt.getContextualType().getRawClass());
        } catch (NoSuchMethodError ex) {
            throw new RuntimeException(
                    "can't support deserializing enums as root-level objects in jackson-databind<2.5. " +
                    "Upgrade library or deserialize as a property in another object instead.");
        }
    }

    /**
     * Deserializer for one specific enum type
     * @param <T> the enum type to deserializer
     */
    private static class EnumDeserializerImpl<T extends Enum<?>> extends JsonDeserializer<T> {

        private final Class<T> targetClass;
        private LongFunction<T> createFromInt;
        private Function<String, T> createFromString;

        EnumDeserializerImpl(Class<T> targetClass) {
            super();
            this.targetClass = targetClass;
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken curr = jp.getCurrentToken();

            if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
                String value = jp.getText();
                return constructFromString(value);
            } else if (curr == JsonToken.VALUE_NUMBER_INT) {
                long value = jp.getLongValue();
                return constructFromInt(value);
            } else if (curr == JsonToken.VALUE_NULL) {
                return null;
            } else {
                throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "expected string or int for enum");
            }
        }

        private T constructFromString(String value) {
            if (value.matches("^\\d*")) {
                // starts with a number, can't be the name of the enum, try parsing as a number instead
                int intValue = Integer.parseInt(value);
                return constructFromInt(intValue);
            }
            if (this.createFromString == null) {
                this.createFromString = findCreateFromString();
            }
            return this.createFromString.apply(value);
        }

        private T constructFromInt(long value) {
            if (this.createFromInt == null) {
                this.createFromInt = findCreateFromInt();
            }
            return this.createFromInt.apply(value);
        }

        private Function<String, T> findCreateFromString() {
            // Try to find JsonCreator method first
            Method stringCreator = findStringCreatorMethod();
            if (stringCreator != null) {
                return (String value) -> {
                    try {
                        return (T) stringCreator.invoke(null, value);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                };
            }
            // don't know how to create from string, try to match by name instead
            T[] enums = targetClass.getEnumConstants();
            return (String value) -> Arrays.stream(enums)
                    .filter(e -> e.name().equals(value)).findFirst().orElse(null);
        }

        private Stream<Method> findCreatorMethods() {
            return Arrays.stream(targetClass.getMethods())
                    .filter(method -> method.getAnnotation(JsonCreator.class) != null &&
                            Modifier.isStatic(method.getModifiers()) &&
                            targetClass.isAssignableFrom(method.getReturnType()));
        }

        private Method findStringCreatorMethod() {
            return findCreatorMethods()
                    .filter(method -> {
                        Class<?>[] params = method.getParameterTypes();
                        return params.length == 1 && params[0].isAssignableFrom(String.class);
                    })
                    .findFirst()
                    .orElse(null);
        }

        private LongFunction<T> findCreateFromInt() {
            // Try to find JsonCreator method first
            LongFunction<T> intCreator = findIntCreatorMethod();
            if (intCreator != null) {
                return intCreator;
            }
            // Look for a single integral field in the class as fallback
            LongFunction<T> onlyIntField = findOnlyIntField();
            if (onlyIntField != null) {
                return onlyIntField;
            }
            throw new IllegalArgumentException("couldn't figure out any deserialization method from int for class " +
                    targetClass.getCanonicalName());
        }

        private LongFunction<T> findIntCreatorMethod() {
            return findCreatorMethods().<LongFunction<T>>map(method -> {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) {
                    return null;
                }
                if (params[0].isAssignableFrom(long.class)) {
                    return (long value) -> {
                        try {
                            return (T) method.invoke(null, (long) value);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    };
                } else if (params[0].isAssignableFrom(int.class)) {
                    return (long value) -> {
                        try {
                            return (T) method.invoke(null, (int) value);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    };
                } else if (params[0].isAssignableFrom(short.class)) {
                    return (long value) -> {
                        try {
                            return (T) method.invoke(null, (short) value);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    };
                }
                return null;
            }).filter(creator -> creator != null).findFirst().orElse(null);
        }

        private LongFunction<T> findOnlyIntField() {
            List<Field> fields = Arrays.stream(targetClass.getDeclaredFields())
                    .filter(field -> field.getType().isAssignableFrom(short.class) ||
                            field.getType().isAssignableFrom(int.class) ||
                            field.getType().isAssignableFrom(long.class))
                    .limit(2)
                    .collect(Collectors.toList());
            if (fields.size() == 1) {
                Field field = fields.get(0);
                field.setAccessible(true);
                T[] enums = targetClass.getEnumConstants();
                return (long value) -> {
                    for (T e : enums) {
                        try {
                            if (field.getLong(e) == value) {
                                return e;
                            }
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    return null;
                };
            }
            if (LOG.isDebugEnabled()) {
                if (fields.size() == 0) {
                    LOG.debug("no integral fields on the enum class");
                } else {
                    LOG.debug("multiple integral fields on the enum class, couldn't determine which one to use: " +
                            fields.stream().map(Field::getName).collect(Collectors.joining(", ")));
                }
            }
            return null;
        }
    }
}
