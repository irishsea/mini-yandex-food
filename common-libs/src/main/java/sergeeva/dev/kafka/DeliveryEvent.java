package sergeeva.dev.kafka;

import lombok.Builder;

@Builder
public record DeliveryEvent(
        Long orderId,
        String courierName,
        Integer etaMinutes
) {}
