package eventlogprocessor.parser;

import eventlogprocessor.model.Event;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventParserService {

    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<Event> parse(String line) {
        try {
            JsonNode node = mapper.readTree(line);

            Event event = new Event();
            event.setTimestamp(getText(node, "timestamp"));
            event.setEventId(parseUUID(node, "eventId"));
            event.setUserId(parseUUID(node, "userId"));
            event.setAction(getText(node, "action"));

            event.setArticleId(getText(node, "articleId"));
            event.setTarget(getText(node, "target"));
            event.setAmount(parseBigDecimal(node));

            return Optional.of(event);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private  String getText(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        String value = node.get(field).asText();
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private UUID parseUUID(JsonNode node, String field) {
        String text = getText(node, field);
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(JsonNode node) {
        String text = getText(node, "amount");
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
