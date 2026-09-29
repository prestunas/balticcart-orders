package BalticCart.Orders.order;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record OrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        Instant createdAt,
        OrderStatus status,
        boolean needsAttention
) {

    public static OrderResponse from(CustomerOrder order) {
        Instant now = Instant.now();
        boolean attention = (order.getStatus() == OrderStatus.NEW || order.getStatus() == OrderStatus.PROCESSING)
                && now.isAfter(order.getCreatedAt().plus(24, ChronoUnit.HOURS));
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getCreatedAt(),
                order.getStatus(),
                attention
        );
    }
}
