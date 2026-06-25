package sergeeva.dev.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sergeeva.dev.kafka.DeliveryEvent;
import sergeeva.dev.kafka.OrderPaidEvent;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryEntityRepository repository;
    private final KafkaTemplate<Long, DeliveryEvent> kafkaTemplate;

    @Value("${delivery-topic}")
    private String deliveryTopic;

    public void processOrderPaid(OrderPaidEvent event) {
        var orderId = event.orderId();
        var found = repository.findByOrderId(orderId);
        if (found.isPresent()) {
            log.info("found order delivery was already assigned: delivery={}", found.get());
            return;
        }

        var assignedDelivery = assignDelivery(orderId);
        sendDeliveryAssignedEvent(assignedDelivery);
    }

    /**
     * заглушка вызова сервиса по назначению курьера
     */
    private DeliveryEntity assignDelivery(Long orderId) {
        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("courier-" + ThreadLocalRandom.current().nextInt(100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 45));
        log.info("saved order delivery was assigned: delivery={}", entity);

        return repository.save(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        kafkaTemplate.send(
                deliveryTopic,
                assignedDelivery.getOrderId(),
                DeliveryEvent.builder()
                        .courierName(assignedDelivery.getCourierName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        ).thenAccept(result -> {
            log.info("Delivery assigned event sent: deliveryId={}", assignedDelivery.getId());
        });
    }
}
