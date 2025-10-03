package co.com.foodcourt.jpa.restaurantadapter;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.helper.AdapterOperations;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JPARepositoryAdapter extends AdapterOperations<Restaurant, RestaurantEntity, String, JPARepository>
implements RestaurantRepository
{

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Restaurant.class));
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity restaurantEntity = super.mapper.map(restaurant, RestaurantEntity.class);

        RestaurantEntity restaurantSaved = repository.save(restaurantEntity);

        return toEntity(restaurantSaved);
    }
}
