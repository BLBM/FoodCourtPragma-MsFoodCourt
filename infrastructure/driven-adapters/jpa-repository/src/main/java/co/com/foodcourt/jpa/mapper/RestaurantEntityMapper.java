package co.com.foodcourt.jpa.mapper;

import co.com.foodcourt.jpa.entity.RestaurantEntity;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantEntityMapper {
    Restaurant toDomain(RestaurantEntity entity);
    RestaurantEntity toEntity(Restaurant domain);
}
