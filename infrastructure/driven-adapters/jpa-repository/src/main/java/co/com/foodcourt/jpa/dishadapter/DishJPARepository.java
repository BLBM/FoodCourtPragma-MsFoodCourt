package co.com.foodcourt.jpa.dishadapter;

import co.com.foodcourt.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DishJPARepository extends JpaRepository<DishEntity, Long>{


    @Query(DishQueries.FIND_BY_RESTAURANT_NAME)
    List<DishEntity> findByRestaurantId(@Param("restaurantId") Long restaurantName);

    @Query(DishQueries.FIND_BY_RESTAURANT_NAME_AND_CATEGORY_ID)
    List<DishEntity> findByRestaurantIdAndCategoryId(
            @Param("restaurantId") Long restaurantName,
            @Param("categoryId") Long categoryId);
}
