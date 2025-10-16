package co.com.foodcourt.jpa.restaurantadapter;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface JPARepository extends CrudRepository<RestaurantEntity, Long>, QueryByExampleExecutor<RestaurantEntity> {
    List<RestaurantEntity> findAllByOrderByNameAsc();
}
