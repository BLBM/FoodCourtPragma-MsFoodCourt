package co.com.foodcourt.api.dto;

import java.time.LocalDateTime;

public record CreateOrderResponse (
    Long orderId,
    Long restaurantId,
    Long clientId,
    String status,
    LocalDateTime creationDate
){}
