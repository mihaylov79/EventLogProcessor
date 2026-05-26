package eventlogprocessor.aggregation;

import eventlogprocessor.dto.EventStatisticsResponse;
import eventlogprocessor.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventAggregationServiceUTest {

    @Test
    void addEvent_validEvent_shouldIncrementValidEvents() {
       EventAggregationService service = new EventAggregationService();

       UUID user1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID user2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        Event event1 = new Event();
        event1.setUserId(user1);
        event1.setAction("login");

        Event event2 = new Event();
        event2.setUserId(user1);
        event2.setAction("purchase");
        event2.setAmount(new BigDecimal("10.50"));

        Event event3 = new Event();
        event3.setUserId(user2);
        event3.setAction("purchase");
        event3.setAmount(new BigDecimal("20.00"));

        service.addValidEvent(event1);
        service.addValidEvent(event2);
        service.addValidEvent(event3);
        service.addInvalidEvent();

        EventStatisticsResponse stats = service.build();

        assertEquals(3, stats.getTotalValidEvents());
        assertEquals(1, stats.getTotalInvalidLines());
        assertEquals(2, stats.getEventPerUser().get(user1));
        assertEquals(1, stats.getEventPerUser().get(user2));
        assertEquals(1, stats.getEventPerAction().get("login"));
        assertEquals(2, stats.getEventPerAction().get("purchase"));

        assertEquals(new BigDecimal("30.50"), stats.getTotalPurchaseAmount());
        assertEquals(new BigDecimal("15.25"), stats.getAveragePurchaseAmount());
        assertEquals(new BigDecimal("20.00"), stats.getLargestPurchase());

    }

    @Test
    void build_sets_topUsers_and_mostActiveUser() {
       EventAggregationService service = new EventAggregationService();

        UUID user1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID user2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID user3 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

        Event event1 = new Event(); event1.setUserId(user1); event1.setAction("view");
        Event event2 = new Event(); event2.setUserId(user2); event2.setAction("click");
        Event event3 = new Event(); event3.setUserId(user3); event3.setAction("login");
        Event event4 = new Event(); event4.setUserId(user1); event4.setAction("login");

        service.addValidEvent(event1);
        service.addValidEvent(event2);
        service.addValidEvent(event3);
        service.addValidEvent(event4);

        EventStatisticsResponse stats = service.build();

        assertEquals(user1, stats.getMostActiveUser());
        assertEquals(3, stats.getTopUsers().size());
        assertEquals(user1, stats.getTopUsers().getFirst().getUserId());
    }
}
