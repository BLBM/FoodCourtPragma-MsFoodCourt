package co.com.foodcourt.jpa.dishadapter;

import co.com.foodcourt.jpa.common.ErrorConstants;
import co.com.foodcourt.jpa.common.LogConstants;
import co.com.foodcourt.jpa.entity.CategoryEntity;
import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.jpa.helper.AdapterOperations;
import co.com.foodcourt.jpa.mapper.DishEntityMapper;
import co.com.foodcourt.model.plate.Dish;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.model.plate.gateways.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class DishJPARepositoryAdapter   extends AdapterOperations<Dish, DishEntity, Long, DishJPARepository>
        implements DishRepository{

        private final DishEntityMapper dishEntityMapper;

        public DishJPARepositoryAdapter(DishJPARepository repository, ObjectMapper mapper,DishEntityMapper dishEntityMapper) {
                     super(repository, mapper, d -> mapper.map(d, Dish.class));
                     this.dishEntityMapper = dishEntityMapper;
        }

    @Override
    public Dish saveDish(Dish dish) {
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

    @Override
    public Dish findById(Long id) {
        return repository.findById(id)
                .map(dishEntityMapper::toDomain)
                .orElseThrow(()-> new DishNotFoundException(ErrorConstants.DISH_NOT_FOUND.getMessage()));
    }

}



