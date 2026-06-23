package sergeeva.dev.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sergeeva.dev.domain.OrderProcessor;
import sergeeva.dev.domain.db.OrderEntity;
import sergeeva.dev.domain.db.OrderEntityMapper;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public @NonNull OrderDto create(@RequestBody CreateOrderRequestDto request) {
        log.info("Created order, request:{}", request);
        OrderEntity order = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(order);
    }

    @GetMapping("{id}")
    public @NonNull ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        var order = orderProcessor.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Updating order with id:{}", order.getId());
        return ResponseEntity.ok(orderEntityMapper.toOrderDto(order));
    }

}
