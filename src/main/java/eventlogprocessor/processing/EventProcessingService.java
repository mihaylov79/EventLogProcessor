package eventlogprocessor.processing;

import eventlogprocessor.aggregation.EventAggregationService;
import eventlogprocessor.dto.EventStatisticsResponse;
import eventlogprocessor.model.Event;
import eventlogprocessor.parser.EventParserService;
import eventlogprocessor.validation.EventValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Service
public class EventProcessingService {

    private final EventParserService parser;
    private final EventValidationService validator;
    private final EventAggregationService aggregator;

    @Autowired
    public EventProcessingService(EventParserService parser, EventValidationService validator, EventAggregationService aggregator) {
        this.parser = parser;
        this.validator = validator;
        this.aggregator = aggregator;
    }

    public EventStatisticsResponse process(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    aggregator.addInvalidEvent();
                    continue;
                }
                Optional<Event> eventOpt = parser.parse(line);
                if (eventOpt.isEmpty()) {
                    aggregator.addInvalidEvent();
                    continue;
                }
                Event event = eventOpt.get();
                if (!validator.isValid(event)) {
                    aggregator.addInvalidEvent();
                    continue;
                }

                aggregator.addValidEvent(event);
            }
        }
        return aggregator.build();
    }
}
