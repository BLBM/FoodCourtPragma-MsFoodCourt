package co.com.foodcourt.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantRequest(
    @NotBlank(message = "name is required")
    String name,
    @NotBlank(message = "address is required")
    String address,
    @NotNull(message = "ownerId is required")
    Long ownerId,
    @NotBlank(message = "urlLogo is required")
    String urlLogo,
    @NotBlank(message = "phone is required")
    String phone,
    @NotNull(message = "nit is required")
    Long nit
) {
}
