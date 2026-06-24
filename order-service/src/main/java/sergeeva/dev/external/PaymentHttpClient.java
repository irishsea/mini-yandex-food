package sergeeva.dev.external;

import org.springframework.web.bind.annotation.RequestBody;import org.springframework.web.service.annotation.HttpExchange;import org.springframework.web.service.annotation.PostExchange;import sergeeva.dev.http.payment.CreatePaymentRequestDto;import sergeeva.dev.http.payment.CreatePaymentResponseDto;

@HttpExchange (
        accept = "application/json",
        contentType = "application/json"
)
public interface PaymentHttpClient {

    @PostExchange
    CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto requestDto);
}
