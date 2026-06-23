package sergeeva.dev.api;

import lombok.Builder;

import java.util.Set;

@Builder
public record CreateOrderRequestDto(
        Long customerId,
        String address,
        Set<OrderItemRequestDto> items
) {}
