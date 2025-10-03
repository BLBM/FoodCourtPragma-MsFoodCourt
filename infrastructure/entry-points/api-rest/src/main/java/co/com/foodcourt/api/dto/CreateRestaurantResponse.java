package co.com.foodcourt.api.dto;

public record CreateRestaurantResponse(
        String name,
        String owner,
        String phone
) {

}
