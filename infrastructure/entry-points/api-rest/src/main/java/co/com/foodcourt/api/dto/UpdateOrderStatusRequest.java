package co.com.foodcourt.api.dto;

public record UpdateOrderStatusRequest(
        String newStatus,
        String pin
) {
}
