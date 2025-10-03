package co.com.foodcourt.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
    @NotBlank(message = "name is required")
    String name,
    @NotBlank(message = "address is required")
    String address,
    @NotBlank(message = "ownerId is required")
    Long ownerId,
    @NotBlank(message = "urlLogo is required")
    String urlLogo,
    @NotBlank(message = "nit is required")
    Long nit
) {
}
