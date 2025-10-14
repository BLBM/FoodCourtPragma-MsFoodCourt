package co.com.foodcourt.jpa.mapper;



import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.model.plate.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DishEntityMapper {

    @Mapping(target = "restaurant", source = "restaurantId")
    @Mapping(target = "category", source = "categoryId")
    Dish toDomain(DishEntity entity);

}

