package sergeeva.dev.domain.db;

import org.mapstruct.*;
import sergeeva.dev.api.CreateOrderRequestDto;
import sergeeva.dev.api.OrderDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrderEntityMapper {

    OrderEntity toEntity(CreateOrderRequestDto requestDto);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);
}
