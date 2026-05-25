package eventlogprocessor.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Event {

    private String timestamp;

    private UUID eventId;

    private UUID userId;

    private String action;

    private String articleId;

    private String target;

    private BigDecimal amount;
}
