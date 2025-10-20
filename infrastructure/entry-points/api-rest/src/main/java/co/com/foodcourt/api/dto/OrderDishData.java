package co.com.foodcourt.api.dto;

public record OrderDishData(
       Long dishId,
       String dishName,
       Integer quantity
) {
}
