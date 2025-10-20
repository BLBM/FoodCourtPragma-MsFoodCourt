package co.com.foodcourt.usecase.dish;

import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.util.ValidateDish;
import co.com.foodcourt.usecase.util.ValidateUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DishUseCase {

    private  final DishRepository dishRepository;
    private  final RestaurantRepository restaurantRepository;

    public Dish saveDish(Dish dish,Long ownerId){
        ValidateDish.validateDish(dish);

        Restaurant restaurant= restaurantRepository.getRestaurantById(dish.getRestaurant().getRestaurantId());

        ValidateUser.validateOwner(ownerId,restaurant.getOwner().getUserId());

        Category category = Category.builder().categoryId(dish.getCategory().getCategoryId()).build();

        dish.setActive(true);
        dish.setRestaurant(restaurant);
        dish.setCategory(category);

        return  dishRepository.saveDish(dish);
    }

    public Dish updateDish(Long dishId,Dish partialDish, Long ownerId){
        Dish existingDish = dishRepository.findById(dishId);

        Restaurant restaurant= restaurantRepository.getRestaurantById(existingDish.getRestaurant().getRestaurantId());

        ValidateUser.validateOwner(ownerId,restaurant.getOwner().getUserId());

        if (partialDish.getPrice() != null) {
            ValidateDish.validatePrice(partialDish.getPrice());
            existingDish.setPrice(partialDish.getPrice());
        }
        if (partialDish.getDescription() != null) {
            existingDish.setDescription(partialDish.getDescription());
        }
        if (partialDish.getActive() != null) {
            existingDish.setActive(partialDish.getActive());
        }

        return dishRepository.saveDish(existingDish);
    }

    
    public List<Dish> getAllDishesByRestaurant(Long restaurantId, Long categoryId) {
        if (restaurantId == null) {
            throw new ValidationException(ValidationMessages.INVALID_RESTAURANT_PARAM.getMessage());
        }
        List<Dish> dishes = (categoryId == null)
                ? dishRepository.findByRestaurantId(restaurantId)
                : dishRepository.findByRestaurantIdAndCategoryId(restaurantId, categoryId);

        if (dishes.isEmpty()) {
            throw new DishNotFoundException(ValidationMessages.DISHES_NOT_FOUND_FOR_RESTAURANT.getMessage() + restaurantId);
        }
        return dishes;
    }

}
