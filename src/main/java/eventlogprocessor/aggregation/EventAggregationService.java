package eventlogprocessor.aggregation;

import eventlogprocessor.dto.EventStatisticsResponse;
import eventlogprocessor.dto.UserCount;
import eventlogprocessor.model.Event;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class EventAggregationService {

    private int validEvents = 0;
    private int invalidLines = 0;

    private final Map<UUID, Integer> eventPerUser = new HashMap<>();
    private final Map<String, Integer> eventPerAction = new HashMap<>();

    private BigDecimal totalPurchase = BigDecimal.ZERO;
    private BigDecimal maxPurchase = BigDecimal.ZERO;
    private int purchaseCount = 0;

    public void addValidEvent(Event event){
        validEvents++;

        eventPerUser.merge(event.getUserId(), 1, Integer::sum);
        eventPerAction.merge(event.getAction(), 1, Integer::sum);

        if(event.getAction().equals("purchase")){
            BigDecimal amount = event.getAmount();
            if (amount != null) {
                totalPurchase = totalPurchase.add(amount);
                if (amount.compareTo(maxPurchase) > 0) {
                    maxPurchase = amount;
                }
                purchaseCount++;
            }
        }
    }

    public void addInvalidEvent(){
        invalidLines++;
    }

    public EventStatisticsResponse build() {
        EventStatisticsResponse stats = new EventStatisticsResponse();
        stats.setTotalValidEvents(validEvents);
        stats.setTotalInvalidLines(invalidLines);
        stats.setEventPerUser(eventPerUser);
        stats.setEventPerAction(eventPerAction);
        stats.setTotalPurchaseAmount(totalPurchase);
        if (purchaseCount > 0){
            stats.setAveragePurchaseAmount(totalPurchase.divide(BigDecimal.valueOf(purchaseCount), 2, RoundingMode.HALF_UP));
            stats.setLargestPurchase(maxPurchase);
        }else {
            stats.setAveragePurchaseAmount(BigDecimal.ZERO);
            stats.setLargestPurchase(BigDecimal.ZERO);
        }

        List<UserCount> top3Users = findTop3Users();

        stats.setTopUsers(top3Users);
        stats.setMostActiveUser(top3Users.isEmpty() ? null : top3Users.getFirst().getUserId());

        return stats;
    }

    private List<UserCount> findTop3Users() {
        return eventPerUser.entrySet().stream()
                .map(e -> new UserCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(UserCount::getEventCount)
                        .reversed()
                        .thenComparing(user -> user.getUserId().toString()))
                .limit(3)
                .toList();
    }
}
