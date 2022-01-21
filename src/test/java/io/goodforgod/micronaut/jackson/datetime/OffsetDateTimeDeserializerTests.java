package io.goodforgod.micronaut.jackson.datetime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 26.04.2021
 */
@MicronautTest
class OffsetDateTimeDeserializerTests extends Assertions {

    static class User {

        private String name;
        private OffsetDateTime value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public OffsetDateTime getValue() {
            return value;
        }

        public void setValue(OffsetDateTime value) {
            this.value = value;
        }
    }

    private static final OffsetDateTime TIME = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
    private static final String VALUE = "1970-01-01T00:00:00.000Z";

    private static final OffsetDateTime TIME_MOSCOW = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("+03:00"));
    private static final String VALUE_MOSCOW = "1970-01-01T03:00:00.000+03:00";

    @Inject
    private ObjectMapper mapper;

    @Test
    void serializationIsValidForIso() throws JsonProcessingException {
        final User user = new User();
        user.setName("Bob");
        user.setValue(TIME);

        final String json = mapper.writeValueAsString(user);
        assertNotNull(json);
        assertTrue(json.contains("\"value\":\"" + VALUE + "\""), json);
    }

    @Test
    void deserializationIsValidForIso() throws JsonProcessingException {
        final String json = "{\"name\":\"Bob\",\"value\":\"" + VALUE + "\"}";

        final User user = mapper.readValue(json, User.class);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
        assertEquals(TIME, user.getValue());
    }

    @Test
    void serializationIsValidForIsoMoscow() throws JsonProcessingException {
        final User user = new User();
        user.setName("Bob");
        user.setValue(TIME_MOSCOW);

        final String json = mapper.writeValueAsString(user);
        assertNotNull(json);
        assertTrue(json.contains("\"value\":\"" + VALUE_MOSCOW + "\""), json);
    }

    @Test
    void deserializationIsValidForIsoMoscow() throws JsonProcessingException {
        final String json = "{\"name\":\"Bob\",\"value\":\"" + VALUE_MOSCOW + "\"}";

        final User user = mapper.readValue(json, User.class);
        assertNotNull(user);
        assertEquals("Bob", user.getName());
        assertEquals(TIME_MOSCOW, user.getValue());
    }

    @Test
    void deserializationFails() {
        final String json = "{\"name\":\"Bob\",\"value\":\"1970-01-01 03:00\"}";

        try {
            mapper.readValue(json, User.class);
            fail("Should not happen");
        } catch (JsonProcessingException e) {
            assertTrue(e.getCause() instanceof DateTimeException);
        }
    }
}
