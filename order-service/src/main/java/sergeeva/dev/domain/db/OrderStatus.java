package sergeeva.dev.domain.db;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PAYMENT_FAILED,
    DELIVERY_ASSIGNED,
    DELIVERED
}
