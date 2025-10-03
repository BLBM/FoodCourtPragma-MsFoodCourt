package co.com.foodcourt.usecase.createrestaurant;

import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.usecase.util.ValidateRestaurant;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class CreateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    public Restaurant saveRestaurant(Restaurant restaurant) {
        ValidateRestaurant.validateRestaurant(restaurant);
        return restaurantRepository.saveRestaurant(restaurant);
    }
}
