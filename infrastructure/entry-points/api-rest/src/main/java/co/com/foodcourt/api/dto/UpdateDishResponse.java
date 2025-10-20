package co.com.foodcourt.api.dto;

public record UpdateDishResponse(
        String name,
        Integer price,
        String description,
        Boolean active
) {
}
