package co.com.foodcourt.usecase.dish;

import co.com.foodcourt.model.category.Category;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.common.ValidationMessages;
import co.com.foodcourt.usecase.exception.ValidationException;
import co.com.foodcourt.usecase.util.ValidateDish;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class DishUseCase {

    private  final DishRepository dishRepository;
    private  final RestaurantRepository restaurantRepository;

    public Dish saveDish(Dish dish,Long ownerId){
        ValidateDish.validateDish(dish);

        Restaurant restaurant= restaurantRepository.getRestaurantById(dish.getRestaurant().getRestaurantId());

        if(!restaurant.getOwner().getUserId().equals(ownerId)){
            throw new ValidationException(ValidationMessages.INVALID_OWNER_OF_RESTAURANT.getMessage());
        }

        Category category = Category.builder().categoryId(dish.getCategory().getCategoryId()).build();

        dish.setActive(true);
        dish.setRestaurant(restaurant);
        dish.setCategory(category);

        return  dishRepository.save(dish);
    }

}
