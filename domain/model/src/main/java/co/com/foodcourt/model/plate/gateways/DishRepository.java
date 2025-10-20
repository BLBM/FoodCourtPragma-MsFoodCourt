package co.com.foodcourt.model.plate.gateways;

import co.com.foodcourt.model.plate.Dish;

import java.util.List;

public interface DishRepository {
    Dish saveDish(Dish dish);

    Dish findById(Long dishId);

    List<Dish> findByRestaurantId(Long restaurantId);

    List<Dish> findByRestaurantIdAndCategoryId(Long restaurantId, Long CategoryId);

}
