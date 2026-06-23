package sergeeva.dev.api;
import sergeeva.dev.domain.db.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

public record OrderDto(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemDto> items
) {
}