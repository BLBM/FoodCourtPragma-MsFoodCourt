package co.com.foodcourt.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull
        Long restaurantId,
        @NotNull
        List<DishRequest> dishes
) {
    public record DishRequest(
            @NotNull
            Long dishId,
            @NotNull
            Integer quantity
    ) {}
}