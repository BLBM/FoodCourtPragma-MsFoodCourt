package co.com.foodcourt.model.restaurant.gateways;

import co.com.foodcourt.model.restaurant.Restaurant;

import java.util.List;

public interface RestaurantRepository {
        Restaurant saveRestaurant(Restaurant restaurant);
        Restaurant getRestaurantById(Long restaurantId);
        List<Restaurant> findAllOrderedByNameAsc();

}
