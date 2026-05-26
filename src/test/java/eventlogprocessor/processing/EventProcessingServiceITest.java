package eventlogprocessor.processing;

import eventlogprocessor.dto.EventStatisticsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventProcessingServiceITest {

    @Autowired
    private EventProcessingService eventProcessingService;

    @Test
    void process_mixedInput_producesExpectedStats() throws IOException {
        String input = String.join("\n",
                "{\"timestamp\":\"2026-05-01T10:00:00Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"login\"}",
                "INVALID_LINE",
                "{\"timestamp\":\"2026-05-01T10:01:12Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440001\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"view\",\"articleId\":\"art-900\"}",
                "{\"timestamp\":\"2026-05-01T10:02:05Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440003\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"purchase\",\"amount\":19.99}",
                "{\"timestamp\":\"2026-05-01T10:03:40Z\",\"eventId\":\"NOT-A-UUID\",\"userId\":\"f2f09c5a-88bc-4c3e-8b78-0a91cfa0f332\",\"action\":\"logout\"}"
        );

        InputStream stream= new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        EventStatisticsResponse stats = eventProcessingService.process(stream);

        assertNotNull(stats);
        assertEquals(3, stats.getTotalValidEvents());
        assertEquals(2, stats.getTotalInvalidLines());
        assertEquals(1, stats.getEventPerAction().get("login"));
        assertEquals(1, stats.getEventPerAction().get("view"));
        assertEquals(1, stats.getEventPerAction().get("purchase"));
        assertNull(stats.getEventPerAction().get("logout"));

    }
}
