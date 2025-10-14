package co.com.foodcourt.model.plate.gateways;

import co.com.foodcourt.model.plate.Dish;

public interface DishRepository {
    Dish saveDish(Dish dish);

    Dish findById(Long dishId);

    Dish save(Dish dish);
}
