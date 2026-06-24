package sergeeva.dev.http.order;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity,
        String name
) {
    //TODO валидация количества айтемов
}
