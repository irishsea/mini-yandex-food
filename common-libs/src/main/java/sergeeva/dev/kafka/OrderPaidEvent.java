package sergeeva.dev.kafka;

import lombok.Builder;
import sergeeva.dev.http.payment.PaymentMethod;

import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
