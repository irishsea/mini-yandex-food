package sergeeva.dev.domain;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sergeeva.dev.api.CreateOrderRequestDto;
import sergeeva.dev.domain.db.*;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;

    public OrderEntity create(CreateOrderRequestDto request) {
        var entity = orderEntityMapper.toEntity(request);
        calculatePricingAndSetToOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return repository.save(entity);
    }

    public OrderEntity getById(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return repository.findById(id).orElse(null);
    }

    /**
     * Метод-заглушка, имитирующий подсчет стоимости заказа на стороннем сервисе.
     */
    private void calculatePricingAndSetToOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item : entity.getItems()) {
            var randomPrice = ThreadLocalRandom.current().nextDouble(100, 5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));

            totalPrice = item.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(totalPrice);
        }
        entity.setTotalAmount(totalPrice);
    }

}