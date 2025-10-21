package co.com.foodcourt.jpa.mapper;



import co.com.foodcourt.jpa.entity.DishEntity;
import co.com.foodcourt.model.plate.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DishEntityMapper {

    @Mapping(target = "restaurant", source = "restaurantId")
    @Mapping(target = "category", source = "categoryId")
    Dish toDomain(DishEntity entity);

}

