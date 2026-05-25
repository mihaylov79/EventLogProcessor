package eventlogprocessor.validation;

import eventlogprocessor.model.Event;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Service
public class EventValidationService {
    private static final Set<String> ACTIONS = Set.of("login", "logout", "view", "click", "purchase");

    public boolean isValid(Event event){
        if(event ==null) return false;

        if(event.getTimestamp() == null || !isValidTimestamp(event.getTimestamp())) return false;
        if(event.getEventId() == null) return false;
        if(event.getUserId() == null) return false;
        if(event.getAction() == null || !ACTIONS.contains(event.getAction())) return false;

        return switch (event.getAction()) {
            case "view" -> event.getArticleId() != null;
            case "click" -> event.getTarget() != null;
            case "purchase" -> event.getAmount() != null && event.getAmount().compareTo(BigDecimal.ZERO) > 0;
            case "login", "logout" -> true;
            default -> false;
        };
    }

    private boolean isValidTimestamp(String timestamp){
        try {
            Instant.parse(timestamp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
