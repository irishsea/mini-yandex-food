package sergeeva.dev.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sergeeva.dev.domain.OrderProcessor;
import sergeeva.dev.domain.db.OrderEntity;
import sergeeva.dev.domain.db.OrderEntityMapper;
import sergeeva.dev.http.order.CreateOrderRequestDto;
import sergeeva.dev.http.order.OrderDto;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public @NonNull ResponseEntity<OrderDto> create(@RequestBody CreateOrderRequestDto request) {
        log.info("Created order, request:{}", request);
        OrderEntity order = orderProcessor.create(request);
        return ResponseEntity.ok(orderEntityMapper.toOrderDto(order));
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

    @PostMapping("/{id}/pay")
    public @NonNull ResponseEntity<OrderDto> payOrder(@PathVariable Long id,
                                                      @RequestBody OrderPaymentRequest request) {
        log.info("Make payment for order, id:{}, request:{}", id, request);
        OrderEntity order = orderProcessor.processPayment(id, request);
        return ResponseEntity.ok(orderEntityMapper.toOrderDto(order));
    }
}
