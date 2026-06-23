package sergeeva.dev.domain;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sergeeva.dev.api.CreateOrderRequestDto;
import sergeeva.dev.domain.db.OrderEntity;
import sergeeva.dev.domain.db.OrderEntityMapper;
import sergeeva.dev.domain.db.OrderJpaRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;

    public OrderEntity create(CreateOrderRequestDto request) {
        var entity = orderEntityMapper.toEntity(request);
        return repository.save(entity);
    }

    public OrderEntity getById(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return repository.findById(id).orElse(null);
    }

}