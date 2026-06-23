package sergeeva.dev.domain;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import sergeeva.dev.api.CreatePaymentRequestDto;
import sergeeva.dev.api.CreatePaymentResponseDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(CreatePaymentRequestDto requestDto);

    CreatePaymentResponseDto toResponseDto(PaymentEntity paymentEntity);
}
