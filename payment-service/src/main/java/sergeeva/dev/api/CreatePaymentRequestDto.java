package sergeeva.dev.api;

import lombok.Builder;
import sergeeva.dev.domain.PaymentMethod;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
