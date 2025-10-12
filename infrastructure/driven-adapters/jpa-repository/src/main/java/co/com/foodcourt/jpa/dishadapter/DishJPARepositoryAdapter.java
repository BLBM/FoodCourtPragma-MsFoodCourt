package co.com.foodcourt.jpa.dishadapter;

import co.com.foodcourt.jpa.common.LogConstants;
import co.com.foodcourt.jpa.entity.CategoryEntity;
import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.helper.AdapterOperations;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DishJPARepositoryAdapter   extends AdapterOperations<Dish, DishEntity, Long, DishJPARepository>
        implements DishRepository{

        public DishJPARepositoryAdapter(DishJPARepository repository, ObjectMapper mapper) {
                     super(repository, mapper, d -> mapper.map(d, Dish.class));
        }

    @Override
    public Dish save(Dish dish) {
        log.info(LogConstants.SAVE_DISH.getMessage(), dish.getName());

        DishEntity dishEntity = super.mapper.map(dish, DishEntity.class);

        dishEntity.setRestaurantId(RestaurantEntity.builder()
                .restaurantId(dish.getRestaurant().getRestaurantId())
                .build());

        dishEntity.setCategoryId(CategoryEntity.builder()
                .categoryId(dish.getCategory().getCategoryId())
                .build());

        DishEntity savedEntity = repository.save(dishEntity);
        log.info(LogConstants.DISH_SAVED.getMessage(), savedEntity.getName());

        return super.mapper.map(savedEntity, Dish.class);
    }

}



