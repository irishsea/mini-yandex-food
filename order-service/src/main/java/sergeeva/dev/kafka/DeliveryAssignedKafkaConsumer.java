package sergeeva.dev.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sergeeva.dev.domain.OrderProcessor;

@Slf4j
@EnableKafka
@Service
@AllArgsConstructor
public class DeliveryAssignedKafkaConsumer {

    private final OrderProcessor orderProcessor;

    @KafkaListener(
            topics = "${delivery-topic}",
            containerFactory = "deliveryEventEventListenerFactory"
    )
    public void listen(DeliveryEvent event) {
        log.info("Received delivery assigned event: {}", event);
        orderProcessor.processDeliveryAssigned(event);
    }

}