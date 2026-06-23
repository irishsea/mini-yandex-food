package sergeeva.dev.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sergeeva.dev.domain.PaymentEntityMapper;
import sergeeva.dev.domain.PaymentEntityRepository;
import sergeeva.dev.domain.PaymentStatus;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityRepository repository;
    private final PaymentEntityMapper mapper;

    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {
        var found = repository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.warn("Payment already exists for orderId={}", request.orderId());
            return mapper.toResponseDto(found.get());
        }

        var entity = mapper.toEntity(request);
        /**
         * Имитация удачного совершения платежа.
         */
        entity.setPaymentStatus(PaymentStatus.PAYMENT_SUCCEEDED);
        return mapper.toResponseDto(repository.save(entity));
    }

}
