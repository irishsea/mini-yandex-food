package sergeeva.dev.api;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity,
        String name
) {}
