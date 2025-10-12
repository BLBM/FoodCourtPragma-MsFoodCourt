package co.com.foodcourt.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDishRequest(


        @NotBlank(message = "name is required")
        String name,
        @NotNull(message = "price is required")
        Integer price,
        @NotNull(message = "restaurantId is required")
        Long restaurantId,
        @NotBlank(message = "description is required")
        String description,
        @NotBlank(message = "urlImage is required")
        String urlImage,
        @NotNull(message = "categoryId is required")
        Long categoryId

) {
}
