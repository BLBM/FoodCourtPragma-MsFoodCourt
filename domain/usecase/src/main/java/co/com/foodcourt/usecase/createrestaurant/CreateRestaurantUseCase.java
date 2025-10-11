package co.com.foodcourt.usecase.createrestaurant;

import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import co.com.foodcourt.model.user.User;
import co.com.foodcourt.model.user.gateways.UserRepository;
import co.com.foodcourt.usecase.util.ValidateRestaurant;
import co.com.foodcourt.usecase.util.ValidateUser;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class CreateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public Restaurant saveRestaurant(Restaurant restaurant) {
        ValidateRestaurant.validateRestaurant(restaurant);

        User owner = userRepository.getUserById(restaurant.getOwner().getUserId());
        ValidateUser.validateUser(owner);

        Restaurant savedRestaurant =  restaurantRepository.saveRestaurant(restaurant);
        savedRestaurant.setOwner(owner);
        return savedRestaurant;
    }
}
