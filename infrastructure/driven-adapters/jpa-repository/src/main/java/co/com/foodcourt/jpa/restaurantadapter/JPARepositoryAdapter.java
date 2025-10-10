package co.com.foodcourt.jpa.restaurantadapter;

import co.com.foodcourt.jpa.common.LogConstants;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.helper.AdapterOperations;
import co.com.foodcourt.model.restaurant.Restaurant;
import co.com.foodcourt.model.restaurant.gateways.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class JPARepositoryAdapter extends AdapterOperations<Restaurant, RestaurantEntity, Long, JPARepository>
implements RestaurantRepository
{

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Restaurant.class));
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {


        RestaurantEntity restaurantEntity = super.mapper.map(restaurant, RestaurantEntity.class);
        log.info(LogConstants.SAVE_RESTAURANT.getMessage(),restaurant.getName());
        restaurantEntity.setOwnerId(restaurant.getOwner().getUserId());
        RestaurantEntity restaurantSaved = repository.save(restaurantEntity);
        log.info(LogConstants.RESTAURANT_SAVED.getMessage(),restaurantEntity.getRestaurantId());

        return toEntity(restaurantSaved);
    }
}
