package sergeeva.dev.domain;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sergeeva.dev.api.OrderPaymentRequest;
import sergeeva.dev.domain.db.OrderEntity;
import sergeeva.dev.domain.db.OrderEntityMapper;
import sergeeva.dev.domain.db.OrderItemEntity;
import sergeeva.dev.domain.db.OrderJpaRepository;
import sergeeva.dev.external.PaymentHttpClient;
import sergeeva.dev.http.order.CreateOrderRequestDto;
import sergeeva.dev.http.order.OrderStatus;
import sergeeva.dev.http.payment.CreatePaymentRequestDto;
import sergeeva.dev.http.payment.CreatePaymentResponseDto;
import sergeeva.dev.http.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;

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

    public OrderEntity processPayment(Long orderId,
                                      OrderPaymentRequest paymentRequest) {
        var entity = getById(orderId);
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entity with id `%s` not found".formatted(orderId));
        }

        if (!OrderStatus.PENDING_PAYMENT.equals(entity.getOrderStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot pay not payment pending order, '%s'".formatted(orderId));
        }

        CreatePaymentResponseDto responseDto = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(orderId)
                .paymentMethod(paymentRequest.paymentMethod())
                .amount(entity.getTotalAmount()).build());

        if (!PaymentStatus.PAYMENT_SUCCEEDED.equals(responseDto.paymentStatus())) {
            entity.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        } else {
            entity.setOrderStatus(OrderStatus.PAID);
        }

        return repository.save(entity);
    }

    /**
     * Метод-заглушка, имитирующий подсчет стоимости заказа на стороннем сервисе.
     */
    private static void calculatePricingAndSetToOrder(OrderEntity entity) {
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