package eventlogprocessor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class EventStatisticsResponse {

    private int totalValidEvents;

    private int totalInvalidLines;

    private Map<String, Integer> eventPerAction;

    private Map<UUID, Integer> eventPerUser;

    private BigDecimal totalPurchaseAmount;

    private BigDecimal averagePurchaseAmount;

    private BigDecimal largestPurchase;

    private UUID mostActiveUser;

    private List<UserCount> topUsers;
}
