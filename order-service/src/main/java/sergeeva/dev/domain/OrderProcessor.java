package sergeeva.dev.domain;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
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
import sergeeva.dev.kafka.DeliveryEvent;
import sergeeva.dev.kafka.OrderPaidEvent;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

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

        CreatePaymentResponseDto paymentResponseDto = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(orderId)
                .paymentMethod(paymentRequest.paymentMethod())
                .amount(entity.getTotalAmount()).build());

        if (!PaymentStatus.PAYMENT_SUCCEEDED.equals(paymentResponseDto.paymentStatus())) {
            entity.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        } else {
            entity.setOrderStatus(OrderStatus.PAID);
        }

        var saved = repository.save(entity);

        sendEventToKafka(saved, paymentResponseDto);

        return saved;
    }

    private void sendEventToKafka(OrderEntity saved, CreatePaymentResponseDto paymentResponseDto) {
        kafkaTemplate.send(orderPaidTopic,
                        saved.getId(),
                        OrderPaidEvent.builder()
                                .orderId(saved.getId())
                                .amount(saved.getTotalAmount())
                                .paymentMethod(paymentResponseDto.paymentMethod())
                                .paymentId(paymentResponseDto.paymentId())
                                .build())
                .thenAccept(result ->
                        log.info("Order Paid event sent: id={}", saved.getId()));
    }

    public void processDeliveryAssigned(DeliveryEvent event) {
        var order = getById(event.orderId());
        if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
            processIncorrectDeliveryState(order);
            return;
        }

        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        repository.save(order);
        log.info("Order delivery assigned processed: orderId={}", order.getId());
    }

    private void processIncorrectDeliveryState(OrderEntity order) {
        if (order.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)) {
            log.info("Order delivery already processed: orderId={}", order.getId());
        } else {
            log.error("Trying to assign delivery but order have incorrect state: state={}", order.getId());
        }
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