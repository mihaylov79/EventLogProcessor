package eventlogprocessor.parser;

import eventlogprocessor.model.Event;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;



public class EventParserServiceUTest {

    private final EventParserService parser = new EventParserService();

    @Test
    void parse_validEvent_shouldReturnEvent() {
        String line = "{\"eventId\":\"123e4567-e89b-12d3-a456-426614174000\",\"userId\":\"123e4567-e89b-12d3-a456-426614174001\",\"action\":\"click\",\"timestamp\":\"2024-06-01T12:00:00Z\"}";
        Optional<Event> result = parser.parse(line);
        assertTrue(result.isPresent());
        Event event = result.get();
        assertEquals("123e4567-e89b-12d3-a456-426614174000", event.getEventId().toString());
        assertEquals("123e4567-e89b-12d3-a456-426614174001", event.getUserId().toString());
        assertEquals("click", event.getAction());
        assertEquals("2024-06-01T12:00:00Z",event.getTimestamp());
    }

    @Test
    void parse_invalidJson_returnsEmptyOptional() {
        String line = "INVALID_LINE";
        Optional<Event> result = parser.parse(line);
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_invalidJsonFields_returnsEventWithNullActionAndEventId() {
        String line = "{\"invalidField\":\"value\"}";
        Optional<Event> result = parser.parse(line);
        assertTrue(result.isPresent());
        assertNull(result.get().getAction());
        assertNull(result.get().getEventId());
    }
}
