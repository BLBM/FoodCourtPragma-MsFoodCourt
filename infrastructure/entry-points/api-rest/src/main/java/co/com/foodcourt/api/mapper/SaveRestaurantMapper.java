package co.com.foodcourt.api.mapper;

import co.com.foodcourt.api.dto.CreateRestaurantRequest;
import co.com.foodcourt.api.dto.CreateRestaurantResponse;
import co.com.foodcourt.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaveRestaurantMapper {

    SaveRestaurantMapper INSTANCE = Mappers.getMapper(SaveRestaurantMapper.class);

    @Mapping(target = "restaurantId", ignore = true)
    Restaurant toDomain(CreateRestaurantRequest saveRestaurantRequest);

    CreateRestaurantResponse toDto (Restaurant restaurant);
}

