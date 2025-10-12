package co.com.foodcourt.model.plate.gateways;

import co.com.foodcourt.model.plate.Dish;

public interface DishRepository {
    Dish save(Dish dish);
}
