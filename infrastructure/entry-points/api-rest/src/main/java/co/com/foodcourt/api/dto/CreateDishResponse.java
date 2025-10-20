package co.com.foodcourt.api.dto;


public record CreateDishResponse(

        String name,
        Integer price,
        String description

) {
}
