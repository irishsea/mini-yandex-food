package sergeeva.dev.api;

import sergeeva.dev.domain.PaymentMethod;
import sergeeva.dev.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long paymentId,
        PaymentStatus paymentStatus,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
