package co.com.foodcourt.api.dto;

public record GetAllDishesData(
        String name,
        String description,
        int price,
        String categoryName
) {
}
