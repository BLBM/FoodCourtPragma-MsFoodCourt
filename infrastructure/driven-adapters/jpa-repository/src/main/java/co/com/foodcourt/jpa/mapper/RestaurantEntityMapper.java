package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.mapstruct.Mapper;

@Mapper
public interface RestaurantEntityMapper {
    Restaurant toDomain(RestaurantEntity entity);
    RestaurantEntity toEntity(Restaurant domain);
}
