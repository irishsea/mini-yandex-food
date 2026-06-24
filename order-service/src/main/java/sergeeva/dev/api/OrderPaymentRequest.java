package sergeeva.dev.api;

import sergeeva.dev.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
