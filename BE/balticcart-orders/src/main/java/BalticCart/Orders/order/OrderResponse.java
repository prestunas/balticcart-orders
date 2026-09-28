package BalticCart.Orders.order;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        Instant createdAt,
        OrderStatus status
) {

    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getCreatedAt(),
                order.getStatus()
        );
    }
}
