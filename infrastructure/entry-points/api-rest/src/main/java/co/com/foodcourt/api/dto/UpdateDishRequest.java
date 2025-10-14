package co.com.foodcourt.api.dto;

public record UpdateDishRequest(
        Integer price,
        String description
) {
}
