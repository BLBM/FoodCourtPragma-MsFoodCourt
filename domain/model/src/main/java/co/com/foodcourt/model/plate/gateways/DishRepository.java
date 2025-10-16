package co.com.foodcourt.model.plate.gateways;

import co.com.foodcourt.model.plate.Dish;

import java.util.List;

public interface DishRepository {
    Dish saveDish(Dish dish);

    Dish findById(Long dishId);

    List<Dish> findByRestaurantName(String restaurantName);

    List<Dish> findByRestaurantNameAndCategoryId(String restaurantName, Long CategoryId);

}
