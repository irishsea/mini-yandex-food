package sergeeva.dev.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sergeeva.dev.http.payment.CreatePaymentRequestDto;
import sergeeva.dev.http.payment.CreatePaymentResponseDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(CreatePaymentRequestDto requestDto);

    CreatePaymentResponseDto toResponseDto(PaymentEntity paymentEntity);
}
