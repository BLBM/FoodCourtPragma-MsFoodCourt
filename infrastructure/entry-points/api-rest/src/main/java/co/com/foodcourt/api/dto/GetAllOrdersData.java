package co.com.foodcourt.api.dto;

import co.com.foodcourt.model.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetAllOrdersData(
        Long orderId,
        Long clientId,
        LocalDateTime creationDate,
        OrderStatus status,
        String restaurantName,
        List<OrderDishData> dishes
) {
}
