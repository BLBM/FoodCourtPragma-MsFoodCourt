package co.com.foodcourt.jpa.restaurantadapter;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends CrudRepository<RestaurantEntity, Long>, QueryByExampleExecutor<RestaurantEntity> {
}
