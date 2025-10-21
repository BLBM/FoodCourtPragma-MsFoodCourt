package co.com.foodcourt.api.dto;

import java.time.LocalDateTime;

public record UpdateOrderResponse(
        Long orderId,
        String status,
        Long clientId,
        LocalDateTime creationDate,
        LocalDateTime deliveredDate
) {
}
